import { useEffect, useMemo, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { Eye, EyeOff, LogIn, ShieldCheck, Boxes, ScanSearch } from 'lucide-react'
import { useAuth } from '../context/useAuth'
import { getDashboardPathForUser } from '../utils/routes'

export default function Login() {
  const navigate = useNavigate()
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
      const to = getDashboardPathForUser(me)
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
    <div className="min-h-[calc(100vh-10rem)] grid lg:grid-cols-2 gap-8 items-center">
      <div className="hidden lg:block">
        <div className="max-w-xl">
          <div className="badge badge-primary badge-outline">Secure Supply Chain</div>
          <h1 className="mt-4 text-5xl font-extrabold tracking-tight leading-tight">
            FarmXChain
          </h1>
          <p className="mt-3 text-lg text-base-content/70">
            A professional dashboard for crop registration and blockchain-backed traceability.
          </p>

          <div className="mt-8 grid gap-4">
            <Feature icon={<ShieldCheck size={18} />} title="Verified roles" text="Admin and farmer access are enforced by JWT claims." />
            <Feature icon={<Boxes size={18} />} title="Batch tracking" text="Register crop batches with origin and certificate path." />
            <Feature icon={<ScanSearch size={18} />} title="Traceability" text="Open a batch and verify its chain record instantly." />
          </div>
        </div>
      </div>

      <div className="w-full">
        <div className="mx-auto w-full max-w-md">
          <div className="mb-6">
            <h2 className="text-3xl font-bold tracking-tight">Welcome back</h2>
            <p className="text-base-content/70">Sign in to continue.</p>
          </div>

          <form className="card bg-base-100 shadow-sm border border-base-200" onSubmit={onSubmit} noValidate>
            <div className="card-body gap-4">
            <div className="form-control">
              <label className="label">
                <span className="label-text">Email</span>
              </label>
              <input
                className="input input-bordered"
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
                <span className="label-text">Password</span>
              </label>
              <div className="join w-full">
                <input
                  className="input input-bordered join-item w-full"
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

            <button className="btn btn-primary" type="submit" disabled={!canSubmit}>
              <LogIn size={18} />
              {loading ? 'Logging in…' : 'Login'}
            </button>

            <div className="divider">OR</div>

            <p className="text-sm text-base-content/70">
              New user?{' '}
              <Link className="link link-primary" to="/register">
                Create an account
              </Link>
            </p>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}

function Feature({ icon, title, text }) {
  return (
    <div className="flex items-start gap-3">
      <div className="mt-0.5 flex h-9 w-9 items-center justify-center rounded-xl bg-primary/10 text-primary">
        {icon}
      </div>
      <div>
        <div className="font-semibold">{title}</div>
        <div className="text-sm text-base-content/70">{text}</div>
      </div>
    </div>
  )
}
