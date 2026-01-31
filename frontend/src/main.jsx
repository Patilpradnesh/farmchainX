import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { createBrowserRouter, RouterProvider } from 'react-router-dom'
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

// Minimal router that mounts the existing App component.
const router = createBrowserRouter([
  {
    path: '/',
    element: <App />,
  },
])

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <ToastProvider>
      <AuthProvider>
        <RouterProvider
          router={router}
          future={{ v7_startTransition: true, v7_relativeSplatPath: true }}
        />
      </AuthProvider>
    </ToastProvider>
  </StrictMode>,
)
