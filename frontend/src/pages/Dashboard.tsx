import React, { useState, useEffect } from 'react';
import axios from 'axios';
import toast from 'react-hot-toast';

interface ShortenedUrl {
  id: string;
  originalUrl: string;
  shortUrl: string;
  createdAt: string;
  clicks: number;
}

const Dashboard = () => {
  const [urls, setUrls] = useState<ShortenedUrl[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [newUrl, setNewUrl] = useState('');

  useEffect(() => {
    fetchUrls();
  }, []);

  const fetchUrls = async () => {
    try {
      // TODO: Implement API call to fetch user's URLs
      // const response = await axios.get('http://localhost:8080/api/urls', {
      //   headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
      // });
      // setUrls(response.data);
    } catch (err) {
      setError('Ошибка при загрузке ссылок');
    } finally {
      setLoading(false);
    }
  };

  const handleShorten = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await axios.post('http://localhost:8080/api/shorten', null, {
        params: { url: newUrl },
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
      });
      setUrls([...urls, response.data]);
      setNewUrl('');
      toast.success('Ссылка успешно сокращена!');
    } catch (err) {
      setError('Ошибка при сокращении ссылки');
      toast.error('Ошибка при сокращении ссылки');
    }
  };

  const copyToClipboard = (url: string) => {
    navigator.clipboard.writeText(url);
    toast.success('Ссылка скопирована в буфер обмена!');
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[60vh]">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary-500"></div>
      </div>
    );
  }

  return (
    <div className="max-w-6xl mx-auto">
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-3xl font-bold bg-gradient-to-r from-primary-400 to-primary-600 bg-clip-text text-transparent">
          Мои ссылки
        </h1>
        <div className="text-gray-300">
          Всего: {urls.length}
        </div>
      </div>

      <div className="bg-primary-900/50 backdrop-blur-lg rounded-xl p-6 shadow-xl border border-primary-700 mb-8">
        <form onSubmit={handleShorten} className="flex gap-4">
          <input
            type="url"
            value={newUrl}
            onChange={(e) => setNewUrl(e.target.value)}
            placeholder="Вставьте вашу длинную ссылку"
            className="flex-1 px-4 py-3 rounded-lg bg-primary-800/50 border border-primary-700 focus:border-primary-500 focus:ring-2 focus:ring-primary-500 outline-none transition-colors text-white placeholder-gray-400"
            required
          />
          <button
            type="submit"
            className="bg-primary-600 hover:bg-primary-700 text-white font-semibold py-3 px-6 rounded-lg transition-colors"
          >
            Сократить
          </button>
        </form>
      </div>

      {error && (
        <div className="p-4 bg-red-500/20 border border-red-500 rounded-lg text-red-400 mb-6">
          {error}
        </div>
      )}

      <div className="space-y-4">
        {urls.map((url) => (
          <div
            key={url.id}
            className="bg-primary-900/50 backdrop-blur-lg rounded-xl p-6 shadow-xl border border-primary-700"
          >
            <div className="flex items-center justify-between">
              <div className="flex-1">
                <a
                  href={url.originalUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-primary-400 hover:text-primary-300 break-all"
                >
                  {url.originalUrl}
                </a>
                <div className="mt-2 flex items-center space-x-4">
                  <a
                    href={url.shortUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="text-white hover:text-primary-300"
                  >
                    {url.shortUrl}
                  </a>
                  <span className="text-gray-400">
                    Переходов: {url.clicks}
                  </span>
                </div>
              </div>
              <button
                onClick={() => copyToClipboard(url.shortUrl)}
                className="ml-4 p-2 text-gray-400 hover:text-white transition-colors"
                title="Копировать"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  className="h-5 w-5"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M8 5H6a2 2 0 00-2 2v11a2 2 0 002 2h10a2 2 0 002-2v-1M8 5a2 2 0 002 2h2a2 2 0 002-2M8 5a2 2 0 012-2h2a2 2 0 012 2m0 0h2a2 2 0 012 2v3m2 4H10m0 0l3-3m-3 3l3 3"
                  />
                </svg>
              </button>
            </div>
          </div>
        ))}

        {urls.length === 0 && (
          <div className="text-center py-12">
            <p className="text-gray-400">
              У вас пока нет сокращенных ссылок. Создайте первую!
            </p>
          </div>
        )}
      </div>
    </div>
  );
};

export default Dashboard; 