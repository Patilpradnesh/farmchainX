import { useMemo, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { Eye, EyeOff, UserPlus, ShieldCheck } from 'lucide-react'
import { registerFarmer } from '../api/auth'

export default function Register() {
  const navigate = useNavigate()

  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [showPassword, setShowPassword] = useState(false)
  const [farmLocation, setFarmLocation] = useState('')
  const [cropType, setCropType] = useState('')

  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [loading, setLoading] = useState(false)

  const canSubmit = useMemo(() => {
    return (
      name.trim().length >= 2 &&
      email.trim().length > 3 &&
      password.length >= 4 &&
      farmLocation.trim().length >= 2 &&
      cropType.trim().length >= 2 &&
      !loading
    )
  }, [name, email, password, farmLocation, cropType, loading])

  const onSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setSuccess('')
    setLoading(true)

    try {
      await registerFarmer({ name, email, password, farmLocation, cropType })
      setSuccess('Registration submitted. Admin approval is required before dashboard access.')

      // small pause so user sees success message
      setTimeout(() => {
        navigate('/login', { replace: true })
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
    <div className="min-h-[calc(100vh-4rem)] bg-base-200">
      <div className="mx-auto w-full max-w-6xl px-4 py-10">
        <div className="mx-auto w-full max-w-2xl">
          <div className="text-center mb-8">
            <div className="inline-flex items-center gap-2 rounded-full border border-base-300 bg-base-100 px-3 py-1 text-sm text-base-content/70">
              <ShieldCheck size={16} className="text-primary" />
              Farmer registration
            </div>
            <h1 className="mt-4 text-3xl font-bold tracking-tight">Create your farmer account</h1>
            <p className="mt-2 text-base-content/70">
              Submit your details for admin approval. Once approved, you can add crop batches.
            </p>
          </div>

          <div className="card fx-card border-t-4 border-t-primary">
            <div className="card-body p-8 gap-6">
              <ul className="steps steps-horizontal w-full">
                <li className="step step-primary">Account</li>
                <li className="step step-primary">Farm details</li>
                <li className="step">Approval</li>
              </ul>

              <form className="space-y-6" onSubmit={onSubmit}>
                <section className="space-y-4">
                  <div>
                    <div className="text-lg font-semibold">Step 1: Account</div>
                    <p className="text-sm text-base-content/70">Use a real email address so admins can verify you.</p>
                  </div>

                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                    <div className="form-control">
                      <label className="label">
                        <span className="label-text font-medium">Name</span>
                      </label>
                      <input
                        className="input input-bordered input-lg"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        placeholder="Your full name"
                        required
                      />
                    </div>

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
                        required
                      />
                    </div>
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
                        placeholder="Create a password"
                        autoComplete="new-password"
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
                    <div className="label">
                      <span className="label-text-alt text-base-content/60">Use a strong password to protect your account.</span>
                    </div>
                  </div>
                </section>

                <div className="divider my-0" />

                <section className="space-y-4">
                  <div>
                    <div className="text-lg font-semibold">Step 2: Farm details</div>
                    <p className="text-sm text-base-content/70">These details help admins verify your account and improve traceability.</p>
                  </div>

                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                    <div className="form-control">
                      <label className="label">
                        <span className="label-text font-medium">Farm location</span>
                      </label>
                      <input
                        className="input input-bordered input-lg"
                        value={farmLocation}
                        onChange={(e) => setFarmLocation(e.target.value)}
                        placeholder="e.g., Pune, Maharashtra"
                        required
                      />
                    </div>

                    <div className="form-control">
                      <label className="label">
                        <span className="label-text font-medium">Primary crop</span>
                      </label>
                      <input
                        className="input input-bordered input-lg"
                        value={cropType}
                        onChange={(e) => setCropType(e.target.value)}
                        placeholder="e.g., Wheat"
                        required
                      />
                    </div>
                  </div>
                </section>

                {error ? (
                  <div className="alert alert-error">
                    <span>{error}</span>
                  </div>
                ) : null}
                {success ? (
                  <div className="alert alert-success">
                    <span>{success}</span>
                  </div>
                ) : null}

                <div className="flex flex-col sm:flex-row gap-3 sm:items-center sm:justify-between">
                  <p className="text-sm text-base-content/70">
                    Already have an account?{' '}
                    <Link className="link link-primary" to="/login">
                      Login
                    </Link>
                  </p>
                  <button className="btn btn-primary btn-lg" type="submit" disabled={!canSubmit}>
                    <UserPlus size={18} />
                    {loading ? 'Submitting…' : 'Submit for approval'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
