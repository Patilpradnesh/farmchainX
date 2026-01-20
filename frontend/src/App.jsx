import { Navigate, Route, Routes } from 'react-router-dom'
import Navbar from './components/Navbar'
import ProtectedRoute from './components/ProtectedRoute'
import Login from './pages/Login'
import Register from './pages/Register'
import Unauthorized from './pages/Unauthorized'
import FarmerProfile from './pages/FarmerProfile'
import CropNew from './pages/CropNew'
import CropDetails from './pages/CropDetails'
import NotFound from './pages/NotFound'
import PendingApproval from './pages/PendingApproval'
import ApprovedFarmerRoute from './components/ApprovedFarmerRoute'
import FarmerDashboard from './pages/FarmerDashboard'
import AdminHome from './pages/AdminHome'
import Footer from './components/Footer'

export default function App() {
  return (
    <div className="min-h-screen bg-base-200">
      <div className="pointer-events-none fixed inset-0 opacity-[0.35] [background:radial-gradient(80%_60%_at_50%_0%,hsl(var(--p))_0%,transparent_60%),radial-gradient(60%_40%_at_80%_20%,hsl(var(--s))_0%,transparent_55%),radial-gradient(70%_50%_at_20%_10%,hsl(var(--a))_0%,transparent_60%)]" />

      <div className="relative">
        <Navbar />
        <main className="max-w-6xl mx-auto px-4 py-8">
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/unauthorized" element={<Unauthorized />} />

          <Route element={<ProtectedRoute />}>
            <Route path="/pending-approval" element={<PendingApproval />} />
            <Route path="/crop/:id" element={<CropDetails />} />
          </Route>

          <Route element={<ApprovedFarmerRoute />}>
            <Route path="/dashboard" element={<FarmerDashboard />} />
            <Route path="/crops/new" element={<CropNew />} />
          </Route>

          <Route element={<ProtectedRoute roles={['FARMER']} />}>
            <Route path="/farmer/profile" element={<FarmerProfile />} />
          </Route>

          <Route element={<ProtectedRoute roles={['ADMIN']} />}>
            <Route path="/admin" element={<AdminHome />} />
          </Route>

          <Route path="*" element={<NotFound />} />
        </Routes>
        </main>
        <Footer />
      </div>
    </div>
  )
}
