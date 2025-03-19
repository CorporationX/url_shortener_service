import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';

const Login = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    email: '',
    password: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      // TODO: Implement login API call
      // const response = await axios.post('http://localhost:8080/api/auth/login', formData);
      // localStorage.setItem('token', response.data.token);
      navigate('/dashboard');
    } catch (err) {
      setError('Неверный email или пароль');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-md mx-auto">
      <div className="text-center mb-8">
        <h1 className="text-3xl font-bold mb-2 bg-gradient-to-r from-primary-400 to-primary-600 bg-clip-text text-transparent">
          Вход в систему
        </h1>
        <p className="text-gray-300">
          Войдите, чтобы получить доступ к вашим сокращенным ссылкам
        </p>
      </div>

      <div className="bg-primary-900/50 backdrop-blur-lg rounded-xl p-6 shadow-xl border border-primary-700">
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label htmlFor="email" className="block text-sm font-medium text-gray-300 mb-1">
              Email
            </label>
            <input
              type="email"
              id="email"
              value={formData.email}
              onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              className="w-full px-4 py-3 rounded-lg bg-primary-800/50 border border-primary-700 focus:border-primary-500 focus:ring-2 focus:ring-primary-500 outline-none transition-colors text-white placeholder-gray-400"
              required
            />
          </div>

          <div>
            <label htmlFor="password" className="block text-sm font-medium text-gray-300 mb-1">
              Пароль
            </label>
            <input
              type="password"
              id="password"
              value={formData.password}
              onChange={(e) => setFormData({ ...formData, password: e.target.value })}
              className="w-full px-4 py-3 rounded-lg bg-primary-800/50 border border-primary-700 focus:border-primary-500 focus:ring-2 focus:ring-primary-500 outline-none transition-colors text-white placeholder-gray-400"
              required
            />
          </div>

          {error && (
            <div className="p-4 bg-red-500/20 border border-red-500 rounded-lg text-red-400">
              {error}
            </div>
          )}

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-primary-600 hover:bg-primary-700 text-white font-semibold py-3 px-6 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {loading ? 'Вход...' : 'Войти'}
          </button>
        </form>

        <div className="mt-6 text-center">
          <p className="text-gray-300">
            Нет аккаунта?{' '}
            <Link to="/register" className="text-primary-400 hover:text-primary-300">
              Зарегистрироваться
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Login; 