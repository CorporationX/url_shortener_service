import http from 'k6/http';

export default function () {
  const url = `https://test-k6-${Math.floor(Math.random() * 100000)}.com`;
  const payload = JSON.stringify({ url: url });
  const params = { 
    headers: { 
      'Content-Type': 'application/json',
      'x-user-id': '1'
    } 
  };

  console.log('=== Testing POST /url ===');
  console.log('Request URL:', url);
  
  const res = http.post('http://localhost:8080/url', payload, params);
  
  console.log('Status:', res.status);
  console.log('Response headers:', JSON.stringify(res.headers, null, 2));
  console.log('Response body:', res.body);
  console.log('Response body length:', res.body ? res.body.length : 0);
  
  if (res.status === 201) {
    try {
      const body = JSON.parse(res.body);
      console.log('Parsed JSON:', JSON.stringify(body, null, 2));
      console.log('shortUrl:', body.shortUrl);
      console.log('shortUrl type:', typeof body.shortUrl);
      
      if (body.shortUrl) {
        const parts = body.shortUrl.split('/');
        console.log('URL parts:', parts);
        const hash = parts[parts.length - 1];
        console.log('Extracted hash:', hash);
        console.log('Hash length:', hash ? hash.length : 0);
        console.log('Hash is empty?', !hash || hash.length === 0);
        
        // Тест редиректа
        if (hash && hash.length > 0) {
          console.log('\n=== Testing GET /' + hash + ' ===');
          const redirectRes = http.get(`http://localhost:8080/${hash}`, {
            redirects: 0,
            tags: { name: 'test-redirect' }
          });
          console.log('Redirect status:', redirectRes.status);
          console.log('Redirect Location:', redirectRes.headers['Location']);
          console.log('Redirect body:', redirectRes.body);
        }
      } else {
        console.error('ERROR: shortUrl is missing or empty!');
      }
    } catch (e) {
      console.error('Parse error:', e);
      console.error('Error message:', e.message);
      console.error('Response body that failed to parse:', res.body);
    }
  } else {
    console.error('ERROR: Expected status 201, got:', res.status);
    console.error('Response body:', res.body);
  }
  
  console.log('\n' + '='.repeat(50) + '\n');
}

export let options = {
  iterations: 5,
  vus: 1,
};

