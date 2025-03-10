const API_CONFIG = {
  BASE_URL: process.env.REACT_APP_API_URL || 'http://backend:8080/api',
  BACKEND_PORT: '8080',
  ENDPOINTS: {
    SHORTEN: '/shorten',
  },
};

export const getApiUrl = (endpoint: keyof typeof API_CONFIG.ENDPOINTS): string => {
  return `${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS[endpoint]}`;
};

export const formatShortUrl = (shortCode: string): string => {
  const currentDomain = window.location.hostname;
  return `http://${currentDomain}:${API_CONFIG.BACKEND_PORT}/${shortCode}`;
};

export default API_CONFIG; 