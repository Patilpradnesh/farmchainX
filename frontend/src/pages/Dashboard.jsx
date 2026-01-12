import { useEffect, useState } from 'react'
import api from '../api/axios'

export default function Dashboard() {
  const [farmers, setFarmers] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    let active = true

    const fetchFarmers = async () => {
      setLoading(true)
      setError('')

      try {
        const res = await api.get('/api/auth/farmers')
        const data = Array.isArray(res.data) ? res.data : []

        if (active) {
          setFarmers(data)
        }
      } catch (err) {
        const message =
          err?.response?.data?.message ||
          err?.message ||
          'Failed to load farmers.'

        if (active) {
          setError(String(message))
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
  }, [])

  return (
    <div className="page">
      <h1 className="page__title">Dashboard</h1>

      {loading ? <p className="muted">Loading farmers…</p> : null}
      {error ? <p className="alert">{error}</p> : null}

      {!loading && !error ? (
        <div className="card">
          <div className="tableWrap">
            <table className="table">
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
                    <td colSpan={5} className="muted">
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
                      <td>{f.status ?? 'N/A'}</td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      ) : null}
    </div>
  )
}
