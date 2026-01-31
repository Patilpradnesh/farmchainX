import { useEffect, useMemo, useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { Eye, EyeOff, LogIn, ShieldCheck } from 'lucide-react'
import { useAuth } from '../context/useAuth'
import { getDashboardPathForUser } from '../utils/routes'

export default function Login() {
  const navigate = useNavigate()
  const location = useLocation()
  const { login, refreshMe } = useAuth()

  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [showPassword, setShowPassword] = useState(false)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    const stored = sessionStorage.getItem('auth_error')
    if (stored) {
      sessionStorage.removeItem('auth_error')
      setError(String(stored))
    }
  }, [])

  const canSubmit = useMemo(() => {
    return email.trim().length > 3 && password.length >= 4 && !loading
  }, [email, password, loading])

  const onSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)

    try {
      await login({ email: email.trim(), password })
      const me = await refreshMe()
      const from = location?.state?.from
      const safeFrom =
        typeof from === 'string' &&
        from.startsWith('/') &&
        !from.startsWith('/login') &&
        !from.startsWith('/register')

      const to = safeFrom ? from : getDashboardPathForUser(me)
      navigate(to, { replace: true })
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
    <div className="min-h-[calc(100vh-4rem)] bg-base-200">
      <div className="mx-auto w-full max-w-6xl px-4 py-10">
        <div className="mx-auto w-full max-w-md">
          <div className="text-center mb-6">
            <div className="inline-flex items-center gap-2 rounded-full border border-base-300 bg-base-100 px-3 py-1 text-sm text-base-content/70">
              <ShieldCheck size={16} className="text-primary" />
              Trusted access
            </div>
            <h1 className="mt-4 text-3xl font-bold tracking-tight">Welcome back</h1>
            <p className="mt-2 text-base-content/70">
              Sign in to manage crops and verify traceability.
            </p>

            {/* TEMP: visibility check (remove later) */}
            <div id="red-test-box" className="mt-4 rounded-xl bg-red-600 text-white px-4 py-3 text-sm font-semibold">
              RED TEST BOX — if you can see this, UI changes are reflecting.
            </div>
          </div>

          <form className="card fx-card border-t-4 border-t-primary" onSubmit={onSubmit} noValidate>
            <div className="card-body gap-5 p-8">
              <div className="form-control">
                <label className="label">
                  <span className="label-text font-medium">Email</span>
                </label>
                <input
                  className="input input-bordered input-lg"
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="you@example.com"
                  autoComplete="email"
                  inputMode="email"
                  required
                />
              </div>

              <div className="form-control">
                <label className="label">
                  <span className="label-text font-medium">Password</span>
                </label>
                <div className="join w-full">
                  <input
                    className="input input-bordered input-lg join-item w-full"
                    type={showPassword ? 'text' : 'password'}
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="Enter your password"
                    autoComplete="current-password"
                    aria-invalid={error ? 'true' : 'false'}
                    aria-describedby={error ? 'login-error' : undefined}
                    required
                  />
                  <button
                    className="btn join-item btn-outline"
                    type="button"
                    onClick={() => setShowPassword((s) => !s)}
                    aria-label={showPassword ? 'Hide password' : 'Show password'}
                  >
                    {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                  </button>
                </div>
              </div>

              {error ? (
                <div id="login-error" className="alert alert-error" role="alert" aria-live="polite">
                  <span>{error}</span>
                </div>
              ) : null}

              <button className="btn btn-primary btn-lg" type="submit" disabled={!canSubmit}>
                <LogIn size={18} />
                {loading ? 'Logging in…' : 'Login'}
              </button>

              <div className="text-sm text-base-content/70 text-center">
                New farmer?{' '}
                <Link className="link link-primary" to="/register">
                  Register
                </Link>
              </div>
            </div>
          </form>

          <p className="mt-4 text-xs text-base-content/60 text-center">
            FarmXChain helps you track crop batches with tamper-evident records.
          </p>
        </div>
      </div>
    </div>
  )
}
