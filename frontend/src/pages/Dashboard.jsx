import { useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import { RefreshCcw, Users, ShieldCheck, Clock } from 'lucide-react'
import { listFarmers, listPendingFarmers } from '../api/farmers'
import RoleBadge from '../components/RoleBadge'
import StatusBadge from '../components/StatusBadge'
import PageHeader from '../components/PageHeader'
import { useAuth } from '../context/useAuth'
import { SkeletonCard } from '../components/Skeleton'
import { useToast } from '../components/toast/useToast'

export default function Dashboard() {
  const { user } = useAuth()
  const toast = useToast()

  const [farmers, setFarmers] = useState([])
  const [pendingCount, setPendingCount] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const role = String(user?.role ?? '').toUpperCase()
  const isAdmin = role === 'ADMIN'
  const isFarmer = role === 'FARMER'
  const isVerified = ['APPROVED', 'VERIFIED', 'ACTIVE'].includes(
    String(user?.status ?? '').toUpperCase(),
  )

  const title = useMemo(() => {
    if (isAdmin) return 'Admin Dashboard'
    if (isFarmer) return 'Farmer Dashboard'
    return 'Dashboard'
  }, [isAdmin, isFarmer])

  useEffect(() => {
    let active = true

    const fetchFarmers = async () => {
      setLoading(true)
      setError('')

      try {
        if (!isAdmin) {
          if (active) {
            setFarmers([])
            setPendingCount(null)
          }
          return
        }

        const [all, pending] = await Promise.allSettled([
          listFarmers(),
          listPendingFarmers(),
        ])

        const allFarmers =
          all.status === 'fulfilled' ? all.value : []

        const pendingFarmers =
          pending.status === 'fulfilled' ? pending.value : null

        if (active) {
          setFarmers(Array.isArray(allFarmers) ? allFarmers : [])
          setPendingCount(
            Array.isArray(pendingFarmers) ? pendingFarmers.length : null,
          )
          toast.success('Dashboard refreshed')
        }
      } catch (err) {
        const message =
          err?.response?.data?.message ||
          err?.message ||
          'Failed to load farmers.'

        if (active) {
          setError(String(message))
          toast.error(String(message))
        }
      } finally {
        if (active) {
          setLoading(false)
        }
      }
    }

    fetchFarmers()

    return () => {
      active = false
    }
  }, [isAdmin, toast])

  const stats = useMemo(() => {
    return {
      pending: pendingCount,
      totalFarmers: farmers.length,
      status: String(user?.status ?? '').toUpperCase() || '—',
    }
  }, [pendingCount, farmers.length, user?.status])

  return (
    <div className="space-y-6">
      <PageHeader
        title={title}
        subtitle={
          <span className="inline-flex items-center gap-2">
            {user?.role ? <RoleBadge role={user.role} /> : null}
            <StatusBadge status={user?.status} />
          </span>
        }
        actions={
          <>
            {isAdmin ? (
              <>
                <Link className="btn btn-ghost btn-sm" to="/admin/farmers">
                  Verify Farmers
                </Link>
                <Link className="btn btn-ghost btn-sm" to="/admin/users">
                  Manage Users
                </Link>
                <button className="btn btn-ghost btn-sm" type="button" onClick={() => window.location.reload()}>
                  <RefreshCcw size={16} />
                  Refresh
                </button>
              </>
            ) : null}
            {isFarmer ? (
              <Link className="btn btn-ghost btn-sm" to="/farmer/profile">
                My Profile
              </Link>
            ) : null}
          </>
        }
      />

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="card bg-base-100 border border-base-200 shadow-sm">
          <div className="card-body">
            <div className="flex items-center gap-3">
              <div className="h-10 w-10 rounded-xl bg-primary/10 text-primary flex items-center justify-center">
                <ShieldCheck size={18} />
              </div>
              <div>
                <div className="text-sm text-base-content/70">Account status</div>
                <div className="text-2xl font-bold">{stats.status}</div>
              </div>
            </div>
          </div>
        </div>

        <div className="card bg-base-100 border border-base-200 shadow-sm">
          <div className="card-body">
            <div className="flex items-center gap-3">
              <div className="h-10 w-10 rounded-xl bg-secondary/10 text-secondary flex items-center justify-center">
                <Users size={18} />
              </div>
              <div>
                <div className="text-sm text-base-content/70">Farmers</div>
                <div className="text-2xl font-bold">{isAdmin ? stats.totalFarmers : '—'}</div>
              </div>
            </div>
          </div>
        </div>

        <div className="card bg-base-100 border border-base-200 shadow-sm">
          <div className="card-body">
            <div className="flex items-center gap-3">
              <div className="h-10 w-10 rounded-xl bg-warning/15 text-warning flex items-center justify-center">
                <Clock size={18} />
              </div>
              <div>
                <div className="text-sm text-base-content/70">Pending approvals</div>
                <div className="text-2xl font-bold">{isAdmin ? stats.pending ?? '—' : '—'}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {isFarmer && !isVerified ? (
        <div className="alert alert-warning">
          <span>
            Your account may be awaiting admin verification. You can still update your profile.
          </span>
        </div>
      ) : null}

      {loading ? (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
          <SkeletonCard />
          <SkeletonCard />
        </div>
      ) : null}
      {error ? (
        <div className="alert alert-error">
          <span>{error}</span>
        </div>
      ) : null}

      {isAdmin && !loading && !error ? (
        <div className="card bg-base-100 shadow-sm border border-base-200">
          <div className="card-body">
            <div className="flex items-center justify-between gap-3 flex-wrap">
              <div className="text-base-content/70">
                Pending verifications: <span className="font-semibold">{pendingCount ?? '—'}</span>
              </div>
              <div className="text-sm text-base-content/60">Recent farmer onboardings</div>
            </div>

            <div className="overflow-x-auto">
              <table className="table table-zebra">
                <thead>
                  <tr>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Farm Location</th>
                    <th>Crop Type</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {farmers.length === 0 ? (
                    <tr>
                      <td colSpan={5} className="text-base-content/70">
                        No farmers found.
                      </td>
                    </tr>
                  ) : (
                    farmers.map((f) => (
                      <tr key={f.id ?? `${f.email}-${f.name}`}>
                        <td>{f.name ?? ''}</td>
                        <td>{f.email ?? ''}</td>
                        <td>{f.farmLocation ?? ''}</td>
                        <td>{f.cropType ?? ''}</td>
                        <td>
                          <StatusBadge status={f.status} />
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      ) : null}

      {!isAdmin ? (
        <div className="card bg-base-100 shadow-sm border border-base-200">
          <div className="card-body">
            <h2 className="card-title">Next</h2>
            <p className="text-base-content/70">
              Your role-based workspace is ready. Next we can add role-specific flows (orders, shipment
              tracking, retail listings).
            </p>
          </div>
        </div>
      ) : null}
    </div>
  )
}
