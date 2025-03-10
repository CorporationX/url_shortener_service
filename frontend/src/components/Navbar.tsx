import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Bars3Icon, XMarkIcon } from '@heroicons/react/24/outline';

const Navbar = () => {
  const [isOpen, setIsOpen] = React.useState(false);
  const location = useLocation();
  const navigate = useNavigate();

  const handleNavigation = (path: string) => {
    setIsOpen(false);
    if (location.pathname !== path) {
      navigate(path);
    }
  };

  return (
    <nav className="bg-primary-900/50 backdrop-blur-lg border-b border-primary-700">
      <div className="container mx-auto px-4">
        <div className="flex items-center justify-between h-16">
          <button
            onClick={() => handleNavigation('/')}
            className="flex items-center space-x-2"
          >
            <span className="text-2xl font-bold bg-gradient-to-r from-primary-400 to-primary-600 bg-clip-text text-transparent">
              Linkit
            </span>
          </button>

          {/* Desktop Navigation */}
          <div className="hidden md:flex items-center space-x-8">
            <button
              onClick={() => handleNavigation('/')}
              className={`text-gray-300 hover:text-white transition-colors ${
                location.pathname === '/' ? 'text-white' : ''
              }`}
            >
              Главная
            </button>
          </div>

          {/* Mobile menu button */}
          <div className="md:hidden">
            <button
              onClick={() => setIsOpen(!isOpen)}
              className="text-gray-300 hover:text-white focus:outline-none"
            >
              {isOpen ? (
                <XMarkIcon className="h-6 w-6" />
              ) : (
                <Bars3Icon className="h-6 w-6" />
              )}
            </button>
          </div>
        </div>

        {/* Mobile Navigation */}
        {isOpen && (
          <div className="md:hidden">
            <div className="px-2 pt-2 pb-3 space-y-1">
              <button
                onClick={() => handleNavigation('/')}
                className={`w-full text-left px-3 py-2 rounded-md transition-colors ${
                  location.pathname === '/'
                    ? 'bg-primary-800 text-white'
                    : 'text-gray-300 hover:text-white hover:bg-primary-800'
                }`}
              >
                Главная
              </button>
            </div>
          </div>
        )}
      </div>
    </nav>
  );
};

export default Navbar; 