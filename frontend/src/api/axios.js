import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
})

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers = config.headers || {}
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error),
)

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error?.response?.status

    if (status === 401) {
      localStorage.removeItem('token')

      // Friendly message for the next page load.
      sessionStorage.setItem(
        'auth_error',
        'Your session has expired. Please login again.',
      )

      // Keep it framework-agnostic (works outside React render cycle)
      if (window.location.pathname !== '/login') {
        window.location.assign('/login')
      }
    }

    if (status === 403) {
      sessionStorage.setItem(
        'auth_error',
        'Access denied (403). You don’t have permission to perform this action.',
      )

      if (window.location.pathname !== '/unauthorized') {
        window.location.assign('/unauthorized')
      }
    }

    return Promise.reject(error)
  },
)

export default api
