import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { UserPlus, Sprout, MapPin, Wheat } from 'lucide-react'
import { registerFarmer } from '../api/auth'

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
    <div className="min-h-[calc(100vh-10rem)] grid lg:grid-cols-2 gap-8 items-center">
      <div className="hidden lg:block">
        <div className="max-w-xl">
          <div className="badge badge-secondary badge-outline">Farmer Registration</div>
          <h1 className="mt-4 text-5xl font-extrabold tracking-tight leading-tight">Create your account</h1>
          <p className="mt-3 text-lg text-base-content/70">
            Register as a farmer and submit your details for admin approval.
          </p>

          <div className="mt-8 grid gap-4">
            <Hint icon={<Sprout size={18} />} title="On-chain traceability" text="Crops are registered and can be verified later." />
            <Hint icon={<MapPin size={18} />} title="Origin included" text="Store farm location as part of the record." />
            <Hint icon={<Wheat size={18} />} title="Crop info" text="Capture primary crop type for your profile." />
          </div>
        </div>
      </div>

      <div className="w-full">
        <div className="mx-auto w-full max-w-lg">
          <div className="mb-6">
            <h2 className="text-3xl font-bold tracking-tight">Create your account</h2>
            <p className="text-base-content/70">
              Farmers require admin approval before accessing the dashboard.
            </p>
          </div>

          <form className="card bg-base-100 shadow-sm border border-base-200" onSubmit={onSubmit}>
            <div className="card-body gap-4">
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div className="form-control">
                <label className="label">
                  <span className="label-text">Name</span>
                </label>
                <input
                  className="input input-bordered"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  placeholder="Your name"
                  required
                />
              </div>

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
                  required
                />
              </div>
            </div>

            <div className="form-control">
              <label className="label">
                <span className="label-text">Password</span>
              </label>
              <input
                className="input input-bordered"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Create a password"
                autoComplete="new-password"
                required
              />
            </div>

            <div className="rounded-box border border-base-300 bg-base-200 p-4">
              <div className="font-semibold mb-3">Farmer details</div>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div className="form-control">
                  <label className="label">
                    <span className="label-text">Farm Location</span>
                  </label>
                  <input
                    className="input input-bordered"
                    value={farmLocation}
                    onChange={(e) => setFarmLocation(e.target.value)}
                    placeholder="e.g., Pune, Maharashtra"
                    required
                  />
                </div>

                <div className="form-control">
                  <label className="label">
                    <span className="label-text">Primary Crop</span>
                  </label>
                  <input
                    className="input input-bordered"
                    value={cropType}
                    onChange={(e) => setCropType(e.target.value)}
                    placeholder="e.g., Wheat"
                    required
                  />
                </div>
              </div>
            </div>

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

            <button className="btn btn-primary" type="submit" disabled={loading}>
              <UserPlus size={18} />
              {loading ? 'Creating…' : 'Register'}
            </button>

            <p className="text-sm text-base-content/70">
              Already have an account?{' '}
              <Link className="link link-primary" to="/login">
                Login
              </Link>
            </p>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}

function Hint({ icon, title, text }) {
  return (
    <div className="flex items-start gap-3">
      <div className="mt-0.5 flex h-9 w-9 items-center justify-center rounded-xl bg-secondary/10 text-secondary">
        {icon}
      </div>
      <div>
        <div className="font-semibold">{title}</div>
        <div className="text-sm text-base-content/70">{text}</div>
      </div>
    </div>
  )
}
