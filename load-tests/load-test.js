import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Counter, Trend, Gauge } from 'k6/metrics';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';

// ============================================================================
// CUSTOM METRICS
// ============================================================================
const errorRate = new Rate('errors');
const successRate = new Rate('success');
const createUrlCounter = new Counter('create_url_total');
const createSuccessCounter = new Counter('create_url_success');
const createRateLimitCounter = new Counter('create_url_rate_limited');
const redirectCounter = new Counter('redirect_total');
const redirectSuccessCounter = new Counter('redirect_success');
const redirectFailCounter = new Counter('redirect_fail');
const createUrlLatency = new Trend('create_url_latency');
const redirectLatency = new Trend('redirect_latency');
const hashPoolSize = new Gauge('hash_pool_size');

// ============================================================================
// TEST CONFIGURATION
// ============================================================================
export const options = {
  stages: [
    { duration: '1m', target: 5 },      // Warmup: 0→5 VU
    { duration: '2m', target: 10 },     // Ramp up: 5→10 VU
    { duration: '3m', target: 10 },     // Sustained: 10 VU stable
    { duration: '2m', target: 20 },     // Peak: 10→20 VU
    { duration: '2m', target: 20 },     // Peak sustained: 20 VU
    { duration: '1m', target: 5 },      // Ramp down: 20→5 VU
    { duration: '30s', target: 0 },     // Cooldown
  ],

  thresholds: {
    // HTTP metrics
    'http_req_duration': ['p(95)<500', 'p(99)<1000'],
    'http_req_failed': ['rate<0.30'],  // Allow 30% failures (due to rate limiting)

    // Custom metrics
    'create_url_latency': ['p(95)<600', 'p(99)<1000'],
    'redirect_latency': ['p(95)<200', 'p(99)<500'],
    'redirect_success': ['rate>0.80'],  // 80%+ redirects should work
    'errors': ['rate<0.15'],  // Less than 15% real errors
  },
};

// ============================================================================
// GLOBAL STATE
// ============================================================================
const BASE_URL = 'http://localhost:8080';
const createdHashes = [];
let totalCreateAttempts = 0;
let totalCreateSuccesses = 0;
let totalRateLimited = 0;

// ============================================================================
// HELPER FUNCTIONS
// ============================================================================

/**
 * Create a short URL
 * Returns: hash string or null
 */
function createShortUrl() {
  createUrlCounter.add(1);
  totalCreateAttempts++;

  // Generate unique URL
  const timestamp = Date.now();
  const random = Math.floor(Math.random() * 100000);
  const url = `https://loadtest-${timestamp}-${random}.example.com`;

  const payload = JSON.stringify({ url: url });
  const params = {
    headers: { 'Content-Type': 'application/json' },
    timeout: '10s',
  };

  const startTime = new Date().getTime();
  const res = http.post(`${BASE_URL}/url`, payload, params);
  const duration = new Date().getTime() - startTime;

  createUrlLatency.add(duration);

  // Handle response
  if (res.status === 201) {
    try {
      const body = JSON.parse(res.body);

      if (!body.shortUrl) {
        console.error('[CREATE] No shortUrl in response');
        errorRate.add(1);
        return null;
      }

      // Extract hash from shortUrl
      const parts = body.shortUrl.split('/');
      const hash = parts[parts.length - 1];

      if (!hash || hash.length === 0) {
        console.error('[CREATE] Empty hash extracted');
        errorRate.add(1);
        return null;
      }

      // Success!
      createdHashes.push(hash);
      totalCreateSuccesses++;
      createSuccessCounter.add(1);
      successRate.add(1);

      // Log first 5 for debugging
      if (createdHashes.length <= 5) {
        console.log(`[CREATE]  Hash #${createdHashes.length}: ${hash}`);
      }

      return hash;

    } catch (e) {
      console.error('[CREATE] Parse error:', e);
      errorRate.add(1);
      return null;
    }

  } else if (res.status === 429) {
    // Rate limited - expected behavior
    createRateLimitCounter.add(1);
    totalRateLimited++;
    return null;

  } else {
    // Unexpected error
    console.error(`[CREATE] Failed: ${res.status} - ${res.body.substring(0, 100)}`);
    errorRate.add(1);
    return null;
  }
}

/**
 * Test redirect for a given hash
 */
function testRedirect(hash) {
  if (!hash) {
    console.error('[REDIRECT] Called with empty hash');
    return;
  }

  redirectCounter.add(1);

  const startTime = new Date().getTime();
  const res = http.get(`${BASE_URL}/${hash}`, {
    redirects: 0,
    timeout: '10s',
    tags: { name: 'redirect' },
  });
  const duration = new Date().getTime() - startTime;

  redirectLatency.add(duration);

  // Check response
  const isSuccess = res.status === 302 && res.headers['Location'];

  if (isSuccess) {
    redirectSuccessCounter.add(1);
    successRate.add(1);
  } else {
    redirectFailCounter.add(1);
    errorRate.add(1);

    if (res.status === 404) {
      console.error(`[REDIRECT] 404 for hash: ${hash}`);
    } else if (res.status !== 302) {
      console.error(`[REDIRECT] Unexpected status ${res.status} for hash: ${hash}`);
    }
  }
}

/**
 * Get current hash pool size from actuator
 */
function updateHashPoolMetric() {
  try {
    const res = http.get(`${BASE_URL}/actuator/metrics/hash.pool.size`, {
      timeout: '5s',
    });

    if (res.status === 200) {
      const body = JSON.parse(res.body);
      const value = body.measurements[0].value;
      hashPoolSize.add(value);
    }
  } catch (e) {
    // Silently ignore - metric collection is not critical
  }
}

// ============================================================================
// MAIN TEST LOGIC
// ============================================================================
export default function () {
  const scenario = Math.random();

  // Update hash pool metric every 10 iterations
  if (__ITER % 10 === 0) {
    updateHashPoolMetric();
  }

  // Strategy 1: Build up hash collection (40% of time or if we have < 100 hashes)
  if (scenario < 0.4 || createdHashes.length < 100) {
    const hash = createShortUrl();

    if (hash) {
      // Immediately test the redirect (verify it works)
      sleep(0.1);
      testRedirect(hash);
    } else {
      // Creation failed (likely rate limited)
      // Wait longer before next attempt
      sleep(1.0);
    }
  }

  // Strategy 2: Test redirects on existing hashes (50% of time)
  else if (scenario < 0.9) {
    if (createdHashes.length > 0) {
      // Pick random hash
      const idx = Math.floor(Math.random() * createdHashes.length);
      const hash = createdHashes[idx];
      testRedirect(hash);
    } else {
      // No hashes yet, create one
      createShortUrl();
    }
  }

  // Strategy 3: Hotspot testing - popular URLs (10% of time)
  else {
    if (createdHashes.length > 0) {
      // Always use first hash (simulates popular URL)
      testRedirect(createdHashes[0]);
    }
  }

  // Dynamic sleep based on current rate limit status
  const rateLimitRate = totalCreateAttempts > 0
      ? (totalRateLimited / totalCreateAttempts)
      : 0;

  if (rateLimitRate > 0.5) {
    // High rate limiting - slow down
    sleep(Math.random() * 1.5 + 1.0);  // 1.0-2.5s
  } else if (rateLimitRate > 0.3) {
    // Moderate rate limiting
    sleep(Math.random() * 1.0 + 0.5);  // 0.5-1.5s
  } else {
    // Low rate limiting - normal pace
    sleep(Math.random() * 0.5 + 0.3);  // 0.3-0.8s
  }
}

// ============================================================================
// SETUP & TEARDOWN
// ============================================================================
export function setup() {
  console.log('\n' + '='.repeat(80));
  console.log('LOAD TEST STARTING');
  console.log('='.repeat(80));
  console.log(`Target: ${BASE_URL}`);
  console.log(`Duration: ~11.5 minutes`);
  console.log(`Max VUs: 20`);
  console.log('='.repeat(80) + '\n');
}

export function teardown(data) {
  console.log('\n' + '='.repeat(80));
  console.log('LOAD TEST COMPLETED');
  console.log('='.repeat(80));
}

// ============================================================================
// CUSTOM SUMMARY
// ============================================================================
export function handleSummary(data) {
  const createSuccess = totalCreateSuccesses;
  const createAttempts = totalCreateAttempts;
  const rateLimited = totalRateLimited;
  const hashCount = createdHashes.length;

  const redirectTotal = data.metrics.redirect_total?.values?.count || 0;
  const redirectSuccess = data.metrics.redirect_success?.values?.count || 0;
  const redirectFail = data.metrics.redirect_fail?.values?.count || 0;

  const customSummary = '\n' +
      '='.repeat(80) + '\n' +
      'DETAILED TEST RESULTS\n' +
      '='.repeat(80) + '\n\n' +

      '🔹 URL CREATION:\n' +
      `   Total attempts:        ${createAttempts}\n` +
      `   Successful:            ${createSuccess} (${(createSuccess/createAttempts*100).toFixed(1)}%)\n` +
      `   Rate limited:          ${rateLimited} (${(rateLimited/createAttempts*100).toFixed(1)}%)\n` +
      `   Unique hashes created: ${hashCount}\n\n` +

      '🔹 URL REDIRECTS:\n' +
      `   Total attempts:        ${redirectTotal}\n` +
      `   Successful (302):      ${redirectSuccess} (${(redirectSuccess/redirectTotal*100).toFixed(1)}%)\n` +
      `   Failed:                ${redirectFail} (${(redirectFail/redirectTotal*100).toFixed(1)}%)\n\n` +

      '🔹 LATENCY:\n' +
      `   Create URL p(95):      ${data.metrics.create_url_latency?.values?.['p(95)']?.toFixed(2) || 'N/A'}ms\n` +
      `   Create URL p(99):      ${data.metrics.create_url_latency?.values?.['p(99)']?.toFixed(2) || 'N/A'}ms\n` +
      `   Redirect p(95):        ${data.metrics.redirect_latency?.values?.['p(95)']?.toFixed(2) || 'N/A'}ms\n` +
      `   Redirect p(99):        ${data.metrics.redirect_latency?.values?.['p(99)']?.toFixed(2) || 'N/A'}ms\n\n` +

      '🔹 RATE LIMITING ANALYSIS:\n' +
      `   Expected behavior:     10 requests/min per client\n` +
      `   Rate limit hit rate:   ${(rateLimited/createAttempts*100).toFixed(1)}%\n` +
      `   Status:                ${rateLimited > 0 ? ' WORKING' : ' NOT TRIGGERED'}\n\n` +

      '🔹 SYSTEM HEALTH:\n' +
      `   HTTP failures:         ${(data.metrics.http_req_failed?.values?.rate * 100).toFixed(1)}%\n` +
      `   Error rate:            ${(data.metrics.errors?.values?.rate * 100).toFixed(1)}%\n` +
      `   Total requests:        ${data.metrics.http_reqs?.values?.count || 0}\n` +
      `   Requests/sec:          ${data.metrics.http_reqs?.values?.rate?.toFixed(2) || 'N/A'}\n\n` +

      '='.repeat(80) + '\n' +
      'TEST INTERPRETATION:\n' +
      '='.repeat(80) + '\n' +
      `${redirectSuccess > 0 && (redirectSuccess/redirectTotal) > 0.8
          ? ' PASS: System handles redirects correctly (>80% success)\n'
          : ' FAIL: Redirect success rate too low\n'}` +
      `${data.metrics.create_url_latency?.values?.['p(95)'] < 600
          ? ' PASS: Create URL latency within threshold (<600ms p95)\n'
          : ' FAIL: Create URL latency too high\n'}` +
      `${data.metrics.redirect_latency?.values?.['p(95)'] < 200
          ? ' PASS: Redirect latency excellent (<200ms p95)\n'
          : ' FAIL: Redirect latency too high\n'}` +
      `${rateLimited > 0
          ? ' PASS: Rate limiting is active and protecting system\n'
          : ' WARNING: Rate limiting not triggered\n'}` +
      `${hashCount > 50
          ? ' PASS: Successfully created diverse hash pool\n'
          : ' WARNING: Limited hash diversity\n'}` +
      '='.repeat(80) + '\n\n';

  return {
    'stdout': textSummary(data, { indent: '  ', enableColors: true }) + '\n' + customSummary,
    'summary.json': JSON.stringify(data, null, 2),
  };
}