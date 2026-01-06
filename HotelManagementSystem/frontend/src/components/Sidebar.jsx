import React from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { motion } from "framer-motion";
import {
  LayoutDashboard,
  BedDouble,
  Users,
  Calendar,
  CreditCard,
  UserCog,
  Package,
  LogOut,
  Hotel,
} from "lucide-react";

/**
 * Sidebar navigation component.
 * Provides main navigation menu with active state highlighting and logout functionality.
 */
const Sidebar = () => {
  const location = useLocation();
  const navigate = useNavigate();

  // Navigation menu items configuration
  const menuItems = [
    { path: "/dashboard", icon: LayoutDashboard, label: "Dashboard" },
    { path: "/rooms", icon: BedDouble, label: "Rooms" },
    { path: "/guests", icon: Users, label: "Guests" },
    { path: "/bookings", icon: Calendar, label: "Bookings" },
    { path: "/payments", icon: CreditCard, label: "Payments" },
    { path: "/inventory", icon: Package, label: "Inventory" },
    { path: "/staff", icon: UserCog, label: "Staff" },
  ];

  /**
   * Handles user logout with confirmation.
   * Clears authentication tokens and redirects to login.
   */
  const handleLogout = () => {
    if (window.confirm("Are you sure you want to logout?")) {
      // Clear authentication data
      localStorage.removeItem("token");
      localStorage.removeItem("role");
      // Redirect to login page
      navigate("/login");
    }
  };

  /**
   * Checks if current path matches menu item path.
   */
  const isActive = (path) => location.pathname === path;

  return (
    <div className="h-full w-64 bg-white border-r border-gray-200 flex flex-col">
      {/* Logo and brand section */}
      <div className="h-16 px-6 flex items-center border-b border-gray-200">
        <div className="flex items-center gap-3">
          {/* App logo icon */}
          <div className="w-8 h-8 bg-indigo-600 rounded-lg flex items-center justify-center">
            <Hotel className="w-5 h-5 text-white" />
          </div>
          {/* App name */}
          <div>
            <h1 className="text-lg font-bold text-gray-900">Hotel MS</h1>
          </div>
        </div>
      </div>

      {/* Navigation menu items */}
      <nav className="flex-1 overflow-y-auto py-4 px-3">
        <div className="space-y-1">
          {menuItems.map((item, index) => {
            const Icon = item.icon;
            const active = isActive(item.path);

            return (
              <Link key={item.path} to={item.path} className="block">
                <motion.div
                  whileHover={{ x: 4 }}
                  whileTap={{ scale: 0.98 }}
                  className={`
                    flex items-center gap-3 px-3 py-2.5 rounded-lg transition-all duration-200
                    ${
                      active
                        ? "bg-indigo-50 text-indigo-600 font-medium"
                        : "text-gray-700 hover:bg-gray-100"
                    }
                  `}
                >
                  {/* Menu icon */}
                  <Icon className="w-5 h-5" />
                  {/* Menu label */}
                  <span className="text-sm">{item.label}</span>
                  {/* Active indicator */}
                  {active && (
                    <motion.div
                      layoutId="activeTab"
                      className="ml-auto w-1 h-6 bg-indigo-600 rounded-full"
                      transition={{
                        type: "spring",
                        stiffness: 300,
                        damping: 30,
                      }}
                    />
                  )}
                </motion.div>
              </Link>
            );
          })}
        </div>
      </nav>

      {/* Logout button at bottom */}
      <div className="p-3 border-t border-gray-200">
        <motion.button
          whileHover={{ scale: 1.02 }}
          whileTap={{ scale: 0.98 }}
          onClick={handleLogout}
          className="w-full flex items-center justify-center gap-2 px-3 py-2.5 rounded-lg bg-gray-100 hover:bg-gray-200 text-gray-700 transition-colors text-sm font-medium"
        >
          <LogOut className="w-4 h-4" />
          <span>Logout</span>
        </motion.button>
      </div>
    </div>
  );
};

export default Sidebar;