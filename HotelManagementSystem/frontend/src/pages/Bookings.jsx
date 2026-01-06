import React, { useEffect, useState } from 'react';
import { BookingService, RoomService, GuestService } from '../services/api';
import { Plus, Calendar, CheckCircle, XCircle, Clock, X, Search, Filter, Trash2, Edit2 } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

/**
 * Bookings management page component.
 * Displays all bookings with create, edit, and delete functionality.
 * Includes dynamic pricing calculation and room availability checking.
 */
const Bookings = () => {
    // State management
    const [bookings, setBookings] = useState([]);
    const [rooms, setRooms] = useState([]);
    const [guests, setGuests] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showModal, setShowModal] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [currentBookingId, setCurrentBookingId] = useState(null);
    
    // Form data for create/edit
    const [formData, setFormData] = useState({
        roomId: '',
        guestId: '',
        email: '',
        checkInDate: '',
        checkOutDate: '',
        totalAmount: '',
        status: 'PENDING'
    });

    // Load bookings and related data on mount
    useEffect(() => {
        fetchBookings();
        fetchRoomsAndGuests();
    }, []);

    /**
     * Fetches all bookings from API.
     */
    const fetchBookings = async () => {
        try {
            const response = await BookingService.getAllBookings();
            setBookings(response.data);
        } catch (error) {
            console.error('Failed to fetch bookings', error);
        } finally {
            setLoading(false);
        }
    };

    /**
     * Fetches rooms and guests data for dropdowns.
     */
    const fetchRoomsAndGuests = async () => {
        try {
            const [roomsRes, guestsRes] = await Promise.all([
                RoomService.getAllRooms(),
                GuestService.getAllGuests()
            ]);
            setRooms(roomsRes.data);
            setGuests(guestsRes.data);
        } catch (error) {
            console.error('Failed to fetch rooms and guests', error);
        }
    };

    /**
     * Opens edit modal with booking data.
     */
    const handleEdit = (booking) => {
        setFormData({
            roomId: booking.room.id,
            guestId: booking.guest.id,
            email: booking.guest.email,
            checkInDate: booking.checkInDate,
            checkOutDate: booking.checkOutDate,
            totalAmount: booking.totalAmount,
            status: booking.status
        });
        setCurrentBookingId(booking.id);
        setIsEditing(true);
        setShowModal(true);
    };

    /**
     * Deletes a booking with confirmation.
     */
    const handleDelete = async (id) => {
        if (window.confirm('Are you sure you want to delete this booking? This action cannot be undone.')) {
            try {
                await BookingService.deleteBooking(id);
                fetchBookings();
            } catch (error) {
                console.error('Failed to delete booking', error);
                alert('Failed to delete booking.');
            }
        }
    };

    /**
     * Opens modal for creating new booking.
     */
    const openAddModal = () => {
        setFormData({
            roomId: '',
            guestId: '',
            email: '',
            checkInDate: '',
            checkOutDate: '',
            totalAmount: '',
            status: 'PENDING'
        });
        setIsEditing(false);
        setCurrentBookingId(null);
        setShowModal(true);
    };

    /**
     * Submits booking form (create or update).
     */
    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const bookingData = {
                room: { id: parseInt(formData.roomId) },
                guest: {
                    id: parseInt(formData.guestId),
                    email: formData.email
                },
                checkInDate: formData.checkInDate,
                checkOutDate: formData.checkOutDate,
                totalAmount: parseFloat(formData.totalAmount),
                status: formData.status
            };

            if (isEditing) {
                await BookingService.updateBooking(currentBookingId, bookingData);
            } else {
                await BookingService.createBooking(bookingData);
            }

            setShowModal(false);
            fetchBookings();
        } catch (error) {
            console.error('Failed to save booking', error);
            alert('Failed to save booking. ' + (error.response?.data?.message || 'Please try again.'));
        }
    };

    /**
     * Handles form input changes.
     */
    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    /**
     * Handles date changes and fetches available rooms.
     */
    const handleDateChange = async (e) => {
        const { name, value } = e.target;
        const updatedFormData = { ...formData, [name]: value };
        setFormData(updatedFormData);

        // Fetch available rooms when both dates are selected
        if (updatedFormData.checkInDate && updatedFormData.checkOutDate) {
            try {
                const response = await RoomService.getAvailableRooms(
                    updatedFormData.checkInDate,
                    updatedFormData.checkOutDate
                );
                setRooms(response.data);

                // Reset room selection if not available
                if (updatedFormData.roomId && !response.data.find(r => r.id === parseInt(updatedFormData.roomId))) {
                    if (!isEditing) {
                        setFormData(prev => ({ ...prev, roomId: '', totalAmount: '' }));
                    }
                }
            } catch (error) {
                console.error('Failed to fetch available rooms', error);
            }
        }
    };

    /**
     * Handles room selection and calculates price.
     */
    const handleRoomChange = (e) => {
        const roomId = e.target.value;
        const selectedRoom = rooms.find(r => r.id === parseInt(roomId));

        // Calculate total price with dynamic pricing
        if (selectedRoom && formData.checkInDate && formData.checkOutDate) {
            const price = calculateEstimatedPrice(selectedRoom, formData.checkInDate, formData.checkOutDate);
            setFormData({
                ...formData,
                roomId: roomId,
                totalAmount: price.toFixed(2)
            });
        } else {
            setFormData({ ...formData, roomId: roomId, totalAmount: '' });
        }
    };

    /**
     * Calculates estimated price with weekend surcharge.
     * Friday and Saturday nights get 20% increase.
     */
    const calculateEstimatedPrice = (room, checkIn, checkOut) => {
        let total = 0;
        let current = new Date(checkIn);
        const end = new Date(checkOut);
        const basePrice = room.price;

        while (current < end) {
            const dayOfWeek = current.getDay();
            let dailyPrice = basePrice;
            // Apply 20% surcharge for Friday (5) and Saturday (6)
            if (dayOfWeek === 5 || dayOfWeek === 6) {
                dailyPrice *= 1.2;
            }
            total += dailyPrice;
            current.setDate(current.getDate() + 1);
        }
        return total;
    };

    /**
     * Handles guest selection and auto-fills email.
     */
    const handleGuestChange = (e) => {
        const guestId = e.target.value;
        const selectedGuest = guests.find(g => g.id === parseInt(guestId));
        setFormData({
            ...formData,
            guestId: guestId,
            email: selectedGuest ? selectedGuest.email : ''
        });
    };

    /**
     * Returns CSS classes for booking status badge.
     */
    const getStatusColor = (status) => {
        switch (status) {
            case 'CONFIRMED': return 'bg-emerald-100 text-emerald-700';
            case 'PENDING': return 'bg-amber-100 text-amber-700';
            case 'CANCELLED': return 'bg-rose-100 text-rose-700';
            default: return 'bg-gray-100 text-gray-700';
        }
    };

    // Loading state
    if (loading) {
        return (
            <div className="flex items-center justify-center h-64">
                <motion.div
                    animate={{ rotate: 360 }}
                    transition={{ duration: 1, repeat: Infinity, ease: "linear" }}
                    className="w-12 h-12 border-4 border-indigo-500 border-t-transparent rounded-full"
                />
            </div>
        );
    }

    return (
        <div className="space-y-8">
            {/* Page header with search and actions */}
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                <div>
                    <h1 className="text-3xl font-bold text-gray-900 tracking-tight">Bookings</h1>
                    <p className="text-gray-500 mt-1">Manage reservations and check-ins</p>
                </div>
                <div className="flex gap-3">
                    {/* Search bar */}
                    <div className="relative">
                        <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={20} />
                        <input
                            type="text"
                            placeholder="Search bookings..."
                            className="pl-10 pr-4 py-2 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-500 w-full md:w-64"
                        />
                    </div>
                    {/* Filter button */}
                    <button className="p-2 border border-gray-200 rounded-xl hover:bg-gray-50 text-gray-600">
                        <Filter size={20} />
                    </button>
                    {/* New booking button */}
                    <button
                        onClick={openAddModal}
                        className="bg-indigo-600 text-white px-4 py-2 rounded-xl flex items-center gap-2 hover:bg-indigo-700 transition shadow-lg shadow-indigo-200 font-medium"
                    >
                        <Plus size={20} /> New Booking
                    </button>
                </div>
            </div>

            {/* Bookings table */}
            <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                <table className="min-w-full divide-y divide-gray-100">
                    <thead className="bg-gray-50/50">
                        <tr>
                            <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Guest</th>
                            <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Room</th>
                            <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Dates</th>
                            <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Amount</th>
                            <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Status</th>
                            <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Actions</th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-100">
                        {bookings.map((booking, index) => (
                            <motion.tr
                                key={booking.id}
                                initial={{ opacity: 0, y: 10 }}
                                animate={{ opacity: 1, y: 0 }}
                                transition={{ delay: index * 0.05 }}
                                className="hover:bg-gray-50/50 transition-colors"
                            >
                                {/* Guest column with avatar */}
                                <td className="px-6 py-4 whitespace-nowrap">
                                    <div className="flex items-center">
                                        <div className="h-8 w-8 rounded-full bg-indigo-100 flex items-center justify-center text-indigo-600 font-bold mr-3">
                                            {booking.guest.name.charAt(0)}
                                        </div>
                                        <div>
                                            <div className="text-sm font-medium text-gray-900">{booking.guest.name}</div>
                                            <div className="text-xs text-gray-500">{booking.guest.email}</div>
                                        </div>
                                    </div>
                                </td>
                                {/* Room column */}
                                <td className="px-6 py-4 whitespace-nowrap">
                                    <div className="text-sm text-gray-900 font-medium">Room {booking.room.roomNumber}</div>
                                    <div className="text-xs text-gray-500">{booking.room.type}</div>
                                </td>
                                {/* Dates column */}
                                <td className="px-6 py-4 whitespace-nowrap">
                                    <div className="flex flex-col space-y-1">
                                        <div className="flex items-center gap-1.5 text-xs text-gray-600">
                                            <Calendar size={14} className="text-gray-400" />
                                            <span>{booking.checkInDate}</span>
                                        </div>
                                        <div className="flex items-center gap-1.5 text-xs text-gray-600">
                                            <Clock size={14} className="text-gray-400" />
                                            <span>{booking.checkOutDate}</span>
                                        </div>
                                    </div>
                                </td>
                                {/* Amount column */}
                                <td className="px-6 py-4 whitespace-nowrap">
                                    <span className="text-sm font-semibold text-gray-900">${booking.totalAmount}</span>
                                </td>
                                {/* Status badge */}
                                <td className="px-6 py-4 whitespace-nowrap">
                                    <span className={`px-2.5 py-0.5 inline-flex text-xs font-medium rounded-full ${getStatusColor(booking.status)}`}>
                                        {booking.status}
                                    </span>
                                </td>
                                {/* Action buttons */}
                                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                                    <div className="flex gap-2">
                                        <button
                                            onClick={() => handleEdit(booking)}
                                            className="p-2 text-gray-400 hover:text-indigo-600 hover:bg-indigo-50 rounded-lg transition-all"
                                            title="Edit Booking"
                                        >
                                            <Edit2 size={18} />
                                        </button>
                                        <button
                                            onClick={() => handleDelete(booking.id)}
                                            className="p-2 text-gray-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition-all"
                                            title="Delete Booking"
                                        >
                                            <Trash2 size={18} />
                                        </button>
                                    </div>
                                </td>
                            </motion.tr>
                        ))}
                    </tbody>
                </table>
            </div>

            {/* Create/Edit Modal */}
            <AnimatePresence>
                {showModal && (
                    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
                        {/* Backdrop */}
                        <motion.div
                            initial={{ opacity: 0 }}
                            animate={{ opacity: 1 }}
                            exit={{ opacity: 0 }}
                            onClick={() => setShowModal(false)}
                            className="absolute inset-0 bg-black/40 backdrop-blur-sm"
                        />
                        {/* Modal content */}
                        <motion.div
                            initial={{ scale: 0.95, opacity: 0, y: 20 }}
                            animate={{ scale: 1, opacity: 1, y: 0 }}
                            exit={{ scale: 0.95, opacity: 0, y: 20 }}
                            className="bg-white rounded-2xl shadow-2xl w-full max-w-md relative z-10 overflow-hidden max-h-[90vh] overflow-y-auto"
                        >
                            {/* Modal header */}
                            <div className="p-6 border-b border-gray-100 flex justify-between items-center bg-gray-50">
                                <h2 className="text-xl font-bold text-gray-800">
                                    {isEditing ? 'Edit Booking' : 'New Booking'}
                                </h2>
                                <button
                                    onClick={() => setShowModal(false)}
                                    className="p-2 hover:bg-gray-200 rounded-full transition-colors text-gray-500"
                                >
                                    <X size={20} />
                                </button>
                            </div>

                            {/* Booking form */}
                            <form onSubmit={handleSubmit} className="p-6 space-y-4">
                                {/* Guest selection */}
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Select Guest</label>
                                    <select
                                        name="guestId"
                                        value={formData.guestId}
                                        onChange={handleGuestChange}
                                        required
                                        className="w-full px-4 py-2 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-white"
                                    >
                                        <option value="">Choose a guest...</option>
                                        {guests.map(guest => (
                                            <option key={guest.id} value={guest.id}>
                                                {guest.name}
                                            </option>
                                        ))}
                                    </select>
                                </div>

                                {/* Guest email */}
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Guest Email</label>
                                    <input
                                        type="email"
                                        name="email"
                                        value={formData.email}
                                        onChange={handleChange}
                                        required
                                        className="w-full px-4 py-2 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-500"
                                        placeholder="Enter guest email"
                                    />
                                </div>

                                {/* Check-in and check-out dates */}
                                <div className="grid grid-cols-2 gap-4">
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-1">Check-in</label>
                                        <input
                                            type="date"
                                            name="checkInDate"
                                            value={formData.checkInDate}
                                            onChange={handleDateChange}
                                            required
                                            min={new Date().toISOString().split('T')[0]}
                                            className="w-full px-4 py-2 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-500"
                                        />
                                    </div>
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-1">Check-out</label>
                                        <input
                                            type="date"
                                            name="checkOutDate"
                                            value={formData.checkOutDate}
                                            onChange={handleDateChange}
                                            required
                                            min={formData.checkInDate || new Date().toISOString().split('T')[0]}
                                            className="w-full px-4 py-2 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-500"
                                        />
                                    </div>
                                </div>

                                {/* Room selection */}
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Select Room</label>
                                    <select
                                        name="roomId"
                                        value={formData.roomId}
                                        onChange={handleRoomChange}
                                        required
                                        disabled={!formData.checkInDate || !formData.checkOutDate}
                                        className="w-full px-4 py-2 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-500 disabled:bg-gray-50 disabled:text-gray-400 bg-white"
                                    >
                                        <option value="">
                                            {(!formData.checkInDate || !formData.checkOutDate)
                                                ? "Select dates first..."
                                                : "Choose a room..."}
                                        </option>
                                        {rooms.map(room => (
                                            <option key={room.id} value={room.id}>
                                                Room {room.roomNumber} - {room.type} (${room.price}/night)
                                            </option>
                                        ))}
                                    </select>
                                </div>

                                {/* Price display */}
                                <div className="bg-gray-50 p-4 rounded-xl border border-gray-100">
                                    <label className="block text-xs font-semibold text-gray-500 uppercase tracking-wider mb-1">Estimated Total</label>
                                    <div className="flex items-baseline gap-1">
                                        <span className="text-2xl font-bold text-indigo-600">
                                            ${formData.totalAmount || '0.00'}
                                        </span>
                                    </div>
                                    <p className="text-xs text-gray-400 mt-1">Includes 20% weekend surcharge</p>
                                </div>

                                {/* Status selection */}
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Status</label>
                                    <select
                                        name="status"
                                        value={formData.status}
                                        onChange={handleChange}
                                        className="w-full px-4 py-2 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-white"
                                    >
                                        <option value="PENDING">Pending</option>
                                        <option value="CONFIRMED">Confirmed</option>
                                        <option value="CANCELLED">Cancelled</option>
                                    </select>
                                </div>

                                {/* Form action buttons */}
                                <div className="flex gap-3 pt-4">
                                    <button
                                        type="button"
                                        onClick={() => setShowModal(false)}
                                        className="flex-1 px-4 py-2 border border-gray-200 text-gray-700 rounded-xl hover:bg-gray-50 transition font-medium"
                                    >
                                        Cancel
                                    </button>
                                    <button
                                        type="submit"
                                        className="flex-1 bg-indigo-600 text-white px-4 py-2 rounded-xl hover:bg-indigo-700 transition font-medium shadow-lg shadow-indigo-200"
                                    >
                                        {isEditing ? 'Save Changes' : 'Create Booking'}
                                    </button>
                                </div>
                            </form>
                        </motion.div>
                    </div>
                )}
            </AnimatePresence>
        </div>
    );
};

export default Bookings;