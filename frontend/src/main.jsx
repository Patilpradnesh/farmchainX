import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import './index.css'
import App from './App.jsx'
import { AuthProvider } from './context/AuthContext.jsx'
import { ToastProvider } from './components/toast/ToastProvider.jsx'

// Ensure daisyUI theme is applied early to avoid a flash.
const storedMode = localStorage.getItem('themeMode')
if (!storedMode) localStorage.setItem('themeMode', 'light')
document.documentElement.setAttribute(
  'data-theme',
  storedMode === 'dark' ? 'farmxchainDark' : 'farmxchain',
)

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <ToastProvider>
      <AuthProvider>
        <BrowserRouter>
          <App />
        </BrowserRouter>
      </AuthProvider>
    </ToastProvider>
  </StrictMode>,
)
