import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { LogOut, User, LayoutDashboard, BedDouble, Users, CalendarCheck, CreditCard } from 'lucide-react';
import { AuthService } from '../services/api';

/**
 * Top navigation bar component.
 * Displays app logo, main navigation links, and user actions.
 * Only visible when user is authenticated.
 */
const Navbar = () => {
    const navigate = useNavigate();
    // Check if user is logged in
    const token = localStorage.getItem('token');

    /**
     * Handles user logout.
     * Clears authentication and redirects to login.
     */
    const handleLogout = () => {
        AuthService.logout();
    };

    // Don't render navbar if user is not logged in
    if (!token) return null;

    return (
        <nav className="bg-indigo-600 text-white shadow-lg">
            <div className="max-w-7xl mx-auto px-4">
                <div className="flex justify-between h-16 items-center">
                    {/* Logo and navigation links */}
                    <div className="flex items-center space-x-8">
                        {/* App logo */}
                        <Link to="/" className="text-xl font-bold flex items-center gap-2">
                            <LayoutDashboard size={24} />
                            HMS
                        </Link>
                        
                        {/* Main navigation (hidden on mobile) */}
                        <div className="hidden md:flex space-x-4">
                            <Link to="/" className="hover:text-indigo-200 flex items-center gap-1">
                                <LayoutDashboard size={18} /> Dashboard
                            </Link>
                            <Link to="/rooms" className="hover:text-indigo-200 flex items-center gap-1">
                                <BedDouble size={18} /> Rooms
                            </Link>
                            <Link to="/guests" className="hover:text-indigo-200 flex items-center gap-1">
                                <Users size={18} /> Guests
                            </Link>
                            <Link to="/bookings" className="hover:text-indigo-200 flex items-center gap-1">
                                <CalendarCheck size={18} /> Bookings
                            </Link>
                            <Link to="/payments" className="hover:text-indigo-200 flex items-center gap-1">
                                <CreditCard size={18} /> Payments
                            </Link>
                        </div>
                    </div>
                    
                    {/* User info and logout */}
                    <div className="flex items-center space-x-4">
                        {/* User display */}
                        <div className="flex items-center gap-2">
                            <User size={20} />
                            <span>Admin</span>
                        </div>
                        
                        {/* Logout button */}
                        <button
                            onClick={handleLogout}
                            className="bg-indigo-700 hover:bg-indigo-800 px-3 py-1 rounded flex items-center gap-2 transition"
                        >
                            <LogOut size={18} /> Logout
                        </button>
                    </div>
                </div>
            </div>
        </nav>
    );
};

export default Navbar;