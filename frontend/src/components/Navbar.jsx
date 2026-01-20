import { Link, NavLink, useNavigate } from 'react-router-dom'
import { Menu, Sprout, LogOut, LayoutDashboard, User, PlusCircle } from 'lucide-react'
import { useAuth } from '../context/useAuth'
import RoleBadge from './RoleBadge'
import { getDashboardPathForUser } from '../utils/routes'
import ThemeToggle from './ThemeToggle'

function initials(nameOrEmail) {
  const src = String(nameOrEmail || '').trim()
  if (!src) return 'U'
  const parts = src.split(/\s+/).filter(Boolean)
  const letters = (parts.length >= 2 ? parts[0][0] + parts[1][0] : src[0]).toUpperCase()
  return letters.slice(0, 2)
}

function MenuLink({ to, icon, children }) {
  return (
    <li>
      <NavLink
        to={to}
        className={({ isActive }) =>
          isActive
            ? 'active font-semibold'
            : undefined
        }
      >
        {icon ? <span className="opacity-80">{icon}</span> : null}
        {children}
      </NavLink>
    </li>
  )
}

function PillLink({ to, icon, children }) {
  return (
    <NavLink
      to={to}
      className={({ isActive }) =>
        isActive
          ? 'btn btn-sm btn-primary rounded-full gap-2'
          : 'btn btn-sm btn-ghost rounded-full gap-2'
      }
    >
      {icon}
      {children}
    </NavLink>
  )
}

export default function Navbar() {
  const navigate = useNavigate()
  const { isAuthenticated, user, logout } = useAuth()

  const onLogout = () => {
    logout()
    navigate('/login', { replace: true })
  }

  const role = String(user?.role ?? '').toUpperCase()
  const status = String(user?.status ?? '').toUpperCase()
  const isFarmer = role === 'FARMER'
  const isApprovedFarmer = isFarmer && status === 'APPROVED'

  const dashboardTo = isAuthenticated ? getDashboardPathForUser(user) : '/'
  const brandTo = dashboardTo

  const navItems = (
    <>
      {isAuthenticated ? (
        <>
          <MenuLink to={dashboardTo} icon={<LayoutDashboard size={16} />}>Dashboard</MenuLink>
          {isApprovedFarmer ? <MenuLink to="/crops/new" icon={<PlusCircle size={16} />}>Add Crop</MenuLink> : null}
        </>
      ) : (
        <>
          <MenuLink to="/" icon={<LayoutDashboard size={16} />}>Login</MenuLink>
          <MenuLink to="/register" icon={<PlusCircle size={16} />}>Register</MenuLink>
        </>
      )}
    </>
  )

  return (
    <header className="sticky top-0 z-50 border-b border-base-200 bg-base-100/85 backdrop-blur supports-backdrop-filter:bg-base-100/70">
      <div className="navbar max-w-6xl mx-auto px-4">
        <div className="navbar-start">
          <div className="dropdown">
            <div tabIndex={0} role="button" className="btn btn-ghost lg:hidden" aria-label="Open menu">
              <Menu size={20} />
            </div>
            <ul
              tabIndex={0}
              className="menu menu-sm dropdown-content bg-base-100 rounded-box z-1 mt-3 w-56 p-2 shadow"
            >
              {navItems}
            </ul>
          </div>

          <Link to={brandTo} className="btn btn-ghost text-lg gap-2">
            <Sprout size={18} />
            <span className="font-bold">FarmXChain</span>
          </Link>
        </div>

        <div className="navbar-center hidden lg:flex">
          <div className="flex items-center gap-2">
            {isAuthenticated ? (
              <>
                <PillLink to={dashboardTo} icon={<LayoutDashboard size={16} />}>Dashboard</PillLink>
                {isApprovedFarmer ? (
                  <PillLink to="/crops/new" icon={<PlusCircle size={16} />}>Add crop</PillLink>
                ) : null}
              </>
            ) : (
              <>
                <PillLink to="/" icon={<LayoutDashboard size={16} />}>Login</PillLink>
                <PillLink to="/register" icon={<PlusCircle size={16} />}>Register</PillLink>
              </>
            )}
          </div>
        </div>

        <div className="navbar-end gap-2">
          {isAuthenticated ? (
            <>
              <div className="hidden sm:flex items-center gap-2">
                {user?.role ? <RoleBadge role={user.role} /> : null}
              </div>

              <ThemeToggle />

              <div className="dropdown dropdown-end">
                <button type="button" tabIndex={0} className="btn btn-ghost btn-sm gap-2">
                  <div className="avatar placeholder">
                    <div className="bg-primary text-primary-content rounded-full w-8">
                      <span className="text-xs font-bold">
                        {initials(user?.name || user?.email)}
                      </span>
                    </div>
                  </div>
                  <span className="hidden sm:inline font-semibold">
                    {user?.name || user?.email || 'Account'}
                  </span>
                </button>

                <ul tabIndex={0} className="menu dropdown-content bg-base-100 rounded-box z-1 mt-3 w-56 p-2 shadow">
                  <li className="menu-title">
                    <span className="truncate">{user?.email || 'Signed in'}</span>
                  </li>
                  <li>
                    <Link to={dashboardTo}>
                      <LayoutDashboard size={16} /> Dashboard
                    </Link>
                  </li>
                  {isFarmer ? (
                    <li>
                      <Link to="/farmer/profile">
                        <User size={16} /> Profile
                      </Link>
                    </li>
                  ) : null}
                  <li>
                    <button type="button" onClick={onLogout}>
                      <LogOut size={16} /> Logout
                    </button>
                  </li>
                </ul>
              </div>
            </>
          ) : (
            <>
              <ThemeToggle />
              <Link className="btn btn-ghost btn-sm" to="/">
                Login
              </Link>
              <Link className="btn btn-primary btn-sm" to="/register">
                Register
              </Link>
            </>
          )}
        </div>
      </div>
    </header>
  )
}
