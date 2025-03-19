import { getApiUrl, formatShortUrl } from '../config/api';

export const shortenUrl = async (url: string): Promise<string> => {
  const response = await fetch(getApiUrl('SHORTEN'), {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: `url=${encodeURIComponent(url)}`,
  });

  if (!response.ok) {
    const errorText = await response.text();
    console.error('Server error:', errorText);
    throw new Error(`Ошибка при сокращении URL: ${response.status}`);
  }

  const shortCode = await response.text();
  return formatShortUrl(shortCode);
}; 