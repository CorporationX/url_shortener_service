import axios from 'axios';

// Базовый URL теперь относительный, так как Nginx проксирует запросы
const api = axios.create({
    baseURL: process.env.REACT_APP_API_URL,
    headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
    }
});

export const shortenUrl = async (url: string): Promise<string> => {
    const response = await api.post('/shorten', `url=${encodeURIComponent(url)}`);
    return response.data;
};

export const getOriginalUrl = async (hash: string): Promise<string> => {
    const response = await api.get(`/${hash}`);
    return response.data;
}; 