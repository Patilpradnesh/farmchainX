import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import api from '../api/axios'

export default function Register() {
  const navigate = useNavigate()

  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [farmLocation, setFarmLocation] = useState('')
  const [cropType, setCropType] = useState('')

  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [loading, setLoading] = useState(false)

  const onSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setSuccess('')
    setLoading(true)

    try {
      await api.post('/api/auth/register/farmer', {
        name,
        email,
        password,
        farmLocation,
        cropType,
      })

      setSuccess('Registration successful. You can now login.')

      // small pause so user sees success message
      setTimeout(() => {
        navigate('/', { replace: true })
      }, 600)
    } catch (err) {
      const message =
        err?.response?.data?.message ||
        err?.message ||
        'Registration failed. Please try again.'
      setError(String(message))
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="page">
      <h1 className="page__title">Farmer Registration</h1>

      <form className="card form" onSubmit={onSubmit}>
        <label className="field">
          <span className="field__label">Name</span>
          <input
            className="field__input"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="Farmer name"
            required
          />
        </label>

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
            placeholder="Create a password"
            required
          />
        </label>

        <label className="field">
          <span className="field__label">Farm Location</span>
          <input
            className="field__input"
            value={farmLocation}
            onChange={(e) => setFarmLocation(e.target.value)}
            placeholder="e.g., Pune, Maharashtra"
            required
          />
        </label>

        <label className="field">
          <span className="field__label">Crop Type</span>
          <input
            className="field__input"
            value={cropType}
            onChange={(e) => setCropType(e.target.value)}
            placeholder="e.g., Wheat"
            required
          />
        </label>

        {error ? <p className="alert">{error}</p> : null}
        {success ? <p className="alert alert--success">{success}</p> : null}

        <button className="btn" type="submit" disabled={loading}>
          {loading ? 'Creating…' : 'Register'}
        </button>

        <p className="muted">
          Already have an account? <Link to="/">Login</Link>
        </p>
      </form>
    </div>
  )
}
