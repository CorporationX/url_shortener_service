import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import toast from 'react-hot-toast';
import { shortenUrl } from '../services/urlService';
import { ClipboardDocumentIcon, ClipboardDocumentCheckIcon } from '@heroicons/react/24/outline';

const MAX_TOASTS = 4;
let activeToasts: string[] = [];

const showToast = (type: 'success' | 'error', message: string) => {
  while (activeToasts.length >= MAX_TOASTS) {
    const oldestToast = activeToasts.shift();
    if (oldestToast) toast.dismiss(oldestToast);
  }

  const toastId = toast[type](message, {
    duration: type === 'error' ? 4000 : 3000,
  });

  activeToasts.push(toastId);
  setTimeout(() => {
    activeToasts = activeToasts.filter(id => id !== toastId);
  }, type === 'error' ? 4000 : 3000);
};

const Home = () => {
  const [url, setUrl] = useState('');
  const [shortUrl, setShortUrl] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [isCopying, setIsCopying] = useState(false);

  useEffect(() => {
    setUrl('');
    setShortUrl('');
    setError('');
    setLoading(false);
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (loading) return;
    
    setLoading(true);
    setError('');
    setShortUrl('');

    try {
      const result = await shortenUrl(url);
      setShortUrl(result);
      showToast('success', 'URL успешно сокращен!');
    } catch (err) {
      console.error('Error:', err);
      showToast('error', 'Ошибка при сокращении URL');
    } finally {
      setLoading(false);
    }
  };

  const copyToClipboard = async () => {
    if (isCopying) return;
    
    setIsCopying(true);
    try {
      await navigator.clipboard.writeText(shortUrl);
      showToast('success', 'Ссылка скопирована в буфер обмена!');
    } catch (err) {
      showToast('error', 'Не удалось скопировать ссылку');
    } finally {
      setIsCopying(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto flex-1 flex flex-col">
      <div className="text-center mb-12">
        <h1 className="text-4xl md:text-5xl font-bold mb-6 bg-gradient-to-r from-primary-400 to-primary-600 bg-clip-text text-transparent">
          Сокращатель ссылок
        </h1>
      </div>

      <div className="w-full max-w-2xl mx-auto">
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="flex flex-col md:flex-row gap-4">
            <input
              type="url"
              value={url}
              onChange={(e) => setUrl(e.target.value)}
              placeholder="Введите длинный URL"
              required
              className="flex-1 px-4 py-3 rounded-lg bg-primary-800/50 border border-primary-700 focus:outline-none focus:border-primary-500 placeholder-gray-400"
            />
            <button
              type="submit"
              disabled={loading}
              className="px-8 py-3 bg-primary-600 rounded-lg font-medium hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 focus:ring-offset-primary-900 disabled:opacity-50 transition-colors md:w-auto w-full"
            >
              {loading ? 'Сокращаем...' : 'Сократить'}
            </button>
          </div>
        </form>

        <AnimatePresence mode="wait">
          {error && (
            <motion.div
              initial={{ opacity: 0, y: -20 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -20 }}
              className="mt-4 p-4 bg-red-500/20 border border-red-500/50 rounded-lg text-red-200"
            >
              {error}
            </motion.div>
          )}

          {shortUrl && (
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: 20 }}
              className="mt-8 p-6 bg-primary-800/50 rounded-lg border border-primary-700"
            >
              <div className="flex flex-col md:flex-row items-center justify-between gap-4">
                <a
                  href={shortUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-primary-400 hover:text-primary-300 break-all"
                >
                  {shortUrl}
                </a>
                <button
                  onClick={copyToClipboard}
                  disabled={isCopying}
                  className="p-2 bg-primary-700 rounded-lg hover:bg-primary-600 focus:outline-none focus:ring-2 focus:ring-primary-500 transition-all transform active:scale-95 disabled:opacity-50"
                  title={isCopying ? 'Копируется...' : 'Копировать в буфер обмена'}
                >
                  {isCopying ? (
                    <ClipboardDocumentCheckIcon className="w-6 h-6 text-green-400" />
                  ) : (
                    <ClipboardDocumentIcon className="w-6 h-6 text-primary-400" />
                  )}
                </button>
              </div>
            </motion.div>
          )}
        </AnimatePresence>
      </div>
    </div>
  );
};

export default Home; 