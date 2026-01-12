import { Link, useNavigate } from 'react-router-dom'

export default function Navbar() {
  const navigate = useNavigate()
  const token = localStorage.getItem('token')

  const onLogout = () => {
    localStorage.removeItem('token')
    navigate('/', { replace: true })
  }

  return (
    <header className="navbar">
      <div className="navbar__inner">
        <Link className="navbar__brand" to={token ? '/dashboard' : '/'}>
          FarmXChain
        </Link>
        <nav className="navbar__actions">
          {!token ? (
            <>
              <Link className="btn btn--ghost" to="/">
                Login
              </Link>
              <Link className="btn btn--ghost" to="/register">
                Register
              </Link>
            </>
          ) : (
            <button className="btn" type="button" onClick={onLogout}>
              Logout
            </button>
          )}
        </nav>
      </div>
    </header>
  )
}
