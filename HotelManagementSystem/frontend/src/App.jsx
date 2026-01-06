import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Layout from './components/Layout';
import ProtectedRoute from './components/ProtectedRoute';
import Login from './pages/Login';
import AdminDashboard from './pages/AdminDashboard';
import Rooms from './pages/Rooms';
import Guests from './pages/Guests';
import Bookings from './pages/Bookings';
import Payments from './pages/Payments';
import Staff from './pages/Staff';
import Inventory from './pages/Inventory';

/**
 * Main application component.
 * Defines routing structure with protected and public routes.
 */
function App() {
  return (
    <Router>
      <Routes>
        {/* Public login route */}
        <Route path="/login" element={<Login />} />

        {/* Protected routes wrapped in Layout */}
        <Route path="/" element={
          <ProtectedRoute>
            <Layout />
          </ProtectedRoute>
        }>
          {/* Dashboard route (index) */}
          <Route index element={<AdminDashboard />} />
          
          {/* Feature routes */}
          <Route path="rooms" element={<Rooms />} />
          <Route path="guests" element={<Guests />} />
          <Route path="bookings" element={<Bookings />} />
          <Route path="payments" element={<Payments />} />
          <Route path="staff" element={<Staff />} />
          <Route path="inventory" element={<Inventory />} />
        </Route>

        {/* Catch-all route - redirect to dashboard */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Router>
  );
}

export default App;