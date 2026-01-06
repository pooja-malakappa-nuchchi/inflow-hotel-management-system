import React from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { CheckCircle, XCircle, AlertCircle, Info, X } from 'lucide-react';

/**
 * Snackbar notification component.
 * Displays temporary toast messages with auto-dismiss functionality.
 * 
 * @param {string} message - Message text to display
 * @param {string} type - Notification type: 'success', 'error', 'warning', or 'info'
 * @param {boolean} isOpen - Controls visibility
 * @param {function} onClose - Callback when notification is closed
 * @param {number} duration - Auto-dismiss duration in milliseconds (default: 3000)
 */
const Snackbar = ({ message, type = 'info', isOpen, onClose, duration = 3000 }) => {
    /**
     * Auto-dismiss timer effect.
     * Closes snackbar after specified duration.
     */
    React.useEffect(() => {
        if (isOpen && duration > 0) {
            const timer = setTimeout(() => {
                onClose();
            }, duration);
            // Cleanup timer on unmount
            return () => clearTimeout(timer);
        }
    }, [isOpen, duration, onClose]);

    /**
     * Returns appropriate icon based on notification type.
     */
    const getIcon = () => {
        switch (type) {
            case 'success':
                return <CheckCircle className="w-5 h-5" />;
            case 'error':
                return <XCircle className="w-5 h-5" />;
            case 'warning':
                return <AlertCircle className="w-5 h-5" />;
            default:
                return <Info className="w-5 h-5" />;
        }
    };

    /**
     * Returns color classes based on notification type.
     */
    const getColors = () => {
        switch (type) {
            case 'success':
                return 'bg-emerald-500 text-white';
            case 'error':
                return 'bg-rose-500 text-white';
            case 'warning':
                return 'bg-amber-500 text-white';
            default:
                return 'bg-indigo-500 text-white';
        }
    };

    return (
        <AnimatePresence>
            {isOpen && (
                <motion.div
                    initial={{ opacity: 0, y: -50, x: '-50%' }}
                    animate={{ opacity: 1, y: 0, x: '-50%' }}
                    exit={{ opacity: 0, y: -50, x: '-50%' }}
                    className={`fixed top-4 left-1/2 z-50 flex items-center gap-3 px-6 py-3 rounded-lg shadow-2xl ${getColors()}`}
                >
                    {/* Type icon */}
                    {getIcon()}
                    
                    {/* Message text */}
                    <span className="font-medium">{message}</span>
                    
                    {/* Close button */}
                    <button
                        onClick={onClose}
                        className="ml-2 hover:bg-white/20 rounded-full p-1 transition"
                    >
                        <X className="w-4 h-4" />
                    </button>
                </motion.div>
            )}
        </AnimatePresence>
    );
};

export default Snackbar;