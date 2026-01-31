import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from '../context/useAuth'

function normalizeRole(role) {
  if (!role) return null
  return String(role).replace(/^ROLE_/, '').toUpperCase()
}

function normalizeStatus(status) {
  if (!status) return null
  return String(status).toUpperCase()
}

export default function ProtectedRoute({ roles }) {
  const location = useLocation()
  const { isAuthenticated, user, loading } = useAuth()

  if (loading) {
    return (
      <div className="min-h-[50vh] flex items-center justify-center">
        <div className="flex items-center gap-3 text-base-content/70">
          <span className="loading loading-spinner loading-md text-primary" />
          <span>Loading…</span>
        </div>
      </div>
    )
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace state={{ from: location.pathname }} />
  }

  // If user is pending approval, block protected actions and show a friendly page.
  const status = normalizeStatus(user?.status)
  const currentRole = normalizeRole(user?.role)

  // Admins must be able to access the admin portal to approve others.
  if (status === 'PENDING' && currentRole !== 'ADMIN') {
    const path = location.pathname
    const allowed = path === '/pending-approval'
    if (!allowed) return <Navigate to="/pending-approval" replace />
  }

  const required = Array.isArray(roles) ? roles.map(normalizeRole) : null

  if (required?.length && !required.includes(currentRole)) {
    return <Navigate to="/unauthorized" replace />
  }

  return <Outlet />
}
