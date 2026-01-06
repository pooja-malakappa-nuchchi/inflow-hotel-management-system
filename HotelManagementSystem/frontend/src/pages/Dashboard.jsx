import React, { useEffect, useState } from 'react';
import { AnalyticsService } from '../services/api';
import { BarChart, Users, BedDouble, DollarSign, Calendar } from 'lucide-react';

/**
 * Reusable stat card component for displaying metrics.
 * 
 * @param {string} title - Card title
 * @param {string|number} value - Metric value to display
 * @param {Component} icon - Lucide icon component
 * @param {string} color - Tailwind background color class
 */
const StatCard = ({ title, value, icon: Icon, color }) => (
    <div className="bg-white p-6 rounded-lg shadow-md flex items-center space-x-4">
        {/* Icon container with background color */}
        <div className={`p-3 rounded-full ${color}`}>
            <Icon className="text-white" size={24} />
        </div>
        {/* Stat details */}
        <div>
            <p className="text-gray-500 text-sm">{title}</p>
            <p className="text-2xl font-bold text-gray-800">{value}</p>
        </div>
    </div>
);

/**
 * Main dashboard component.
 * Displays key hotel metrics and statistics overview.
 */
const Dashboard = () => {
    const [stats, setStats] = useState(null);

    // Fetch dashboard stats on mount
    useEffect(() => {
        const fetchStats = async () => {
            try {
                const response = await AnalyticsService.getSummary();
                setStats(response.data);
            } catch (error) {
                console.error('Failed to fetch stats', error);
            }
        };
        fetchStats();
    }, []);

    // Loading state
    if (!stats) return <div className="text-center mt-10">Loading dashboard...</div>;

    return (
        <div>
            {/* Page title */}
            <h1 className="text-3xl font-bold text-gray-800 mb-8">Dashboard</h1>

            {/* Key metrics grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
                {/* Total revenue card */}
                <StatCard
                    title="Total Revenue"
                    value={`$${stats.totalRevenue}`}
                    icon={DollarSign}
                    color="bg-green-500"
                />
                {/* Available rooms card */}
                <StatCard
                    title="Available Rooms"
                    value={stats.availableRooms}
                    icon={BedDouble}
                    color="bg-blue-500"
                />
                {/* Today's check-ins card */}
                <StatCard
                    title="Today's Check-ins"
                    value={stats.todayCheckIns}
                    icon={Calendar}
                    color="bg-purple-500"
                />
                {/* Occupancy rate card */}
                <StatCard
                    title="Occupancy Rate"
                    value={`${stats.occupancyRate.toFixed(1)}%`}
                    icon={BarChart}
                    color="bg-orange-500"
                />
            </div>

            {/* Recent activity section */}
            <div className="bg-white p-6 rounded-lg shadow-md">
                <h2 className="text-xl font-bold text-gray-800 mb-4">Recent Activity</h2>
                <p className="text-gray-500">No recent activity to display.</p>
            </div>
        </div>
    );
};

export default Dashboard;