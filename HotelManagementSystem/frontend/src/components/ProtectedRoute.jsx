import React from 'react';
import { Navigate } from 'react-router-dom';

/**
 * Protected route wrapper component.
 * Checks if user is authenticated before rendering child components.
 * Redirects to login page if no valid token is found.
 * 
 * @param {ReactNode} children - Child components to render if authenticated
 */
const ProtectedRoute = ({ children }) => {
    // Check for authentication token
    const token = localStorage.getItem('token');

    // Redirect to login if not authenticated
    if (!token) {
        return <Navigate to="/login" replace />;
    }

    // Render protected content if authenticated
    return children;
};

export default ProtectedRoute;