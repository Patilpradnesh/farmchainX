import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import api from '../api/axios'

export default function Login() {
  const navigate = useNavigate()

  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const onSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)

    try {
      const res = await api.post('/api/auth/login', { email, password })
      const token = typeof res.data === 'string' ? res.data : ''

      if (!token) {
        throw new Error('Login did not return a JWT token')
      }

      localStorage.setItem('token', token)
      navigate('/dashboard', { replace: true })
    } catch (err) {
      const message =
        err?.response?.data?.message ||
        err?.message ||
        'Login failed. Please try again.'
      setError(String(message))
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="page">
      <h1 className="page__title">Login</h1>

      <form className="card form" onSubmit={onSubmit}>
        <label className="field">
          <span className="field__label">Email</span>
          <input
            className="field__input"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="you@example.com"
            required
          />
        </label>

        <label className="field">
          <span className="field__label">Password</span>
          <input
            className="field__input"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Enter your password"
            required
          />
        </label>

        {error ? <p className="alert">{error}</p> : null}

        <button className="btn" type="submit" disabled={loading}>
          {loading ? 'Logging in…' : 'Login'}
        </button>

        <p className="muted">
          New farmer? <Link to="/register">Create an account</Link>
        </p>
      </form>
    </div>
  )
}
