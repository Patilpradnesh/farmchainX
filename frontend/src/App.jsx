import { Route, Routes } from 'react-router-dom'
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
import AdminFarmerVerification from './pages/AdminFarmerVerification'
import AdminUsers from './pages/AdminUsers'
import Footer from './components/Footer'

function PageContainer({ children }) {
  return <div className="max-w-6xl mx-auto w-full px-4 py-8">{children}</div>
}

export default function App() {
  return (
    <div className="min-h-screen bg-base-200 flex flex-col">
      <div className="relative flex flex-col min-h-screen">
        <Navbar />

        <main className="flex-1 pt-16">
          <Routes>
            {/* Auth (kept full-page, no extra wrapper container) */}
            <Route path="/" element={<Login />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/unauthorized" element={<PageContainer><Unauthorized /></PageContainer>} />

            <Route element={<ProtectedRoute />}>
              <Route path="/pending-approval" element={<PageContainer><PendingApproval /></PageContainer>} />
              <Route path="/crop/:id" element={<PageContainer><CropDetails /></PageContainer>} />
            </Route>

            <Route element={<ApprovedFarmerRoute />}>
              <Route path="/dashboard" element={<PageContainer><FarmerDashboard /></PageContainer>} />
              <Route path="/crops/new" element={<PageContainer><CropNew /></PageContainer>} />
            </Route>

            <Route element={<ProtectedRoute roles={['FARMER']} />}>
              <Route path="/farmer/profile" element={<PageContainer><FarmerProfile /></PageContainer>} />
            </Route>

            <Route element={<ProtectedRoute roles={['ADMIN']} />}>
              <Route path="/admin" element={<PageContainer><AdminHome /></PageContainer>} />
              <Route path="/admin/farmers" element={<PageContainer><AdminFarmerVerification /></PageContainer>} />
              <Route path="/admin/users" element={<PageContainer><AdminUsers /></PageContainer>} />
            </Route>

            <Route path="*" element={<PageContainer><NotFound /></PageContainer>} />
          </Routes>
        </main>

        <Footer />
      </div>
    </div>
  )
}
