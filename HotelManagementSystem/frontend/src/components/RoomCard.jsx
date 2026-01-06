import React from 'react';
import { BedDouble, DollarSign, Edit2, Trash2 } from 'lucide-react';

/**
 * Room card component for displaying room information.
 * Shows room details with edit and delete actions.
 * 
 * @param {Object} room - Room data object
 * @param {Function} onEdit - Callback when edit button is clicked
 * @param {Function} onDelete - Callback when delete button is clicked
 */
const RoomCard = ({ room, onEdit, onDelete }) => {
    const isAvailable = room.status === 'AVAILABLE';

    /**
     * Returns appropriate styling classes based on room status.
     */
    const getStatusColor = (status) => {
        switch (status) {
            case 'AVAILABLE': 
                return 'bg-green-100 text-green-800';
            case 'BOOKED': 
                return 'bg-red-100 text-red-800';
            case 'MAINTENANCE': 
                return 'bg-gray-100 text-gray-800';
            case 'CLEANING': 
                return 'bg-orange-100 text-orange-800';
            default: 
                return 'bg-gray-100 text-gray-800';
        }
    };

    return (
        <div className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition duration-200">
            {/* Room image placeholder */}
            <div className="h-48 bg-gray-200 flex items-center justify-center">
                <BedDouble size={48} className="text-gray-400" />
            </div>
            
            {/* Room details */}
            <div className="p-4">
                {/* Room number and status */}
                <div className="flex justify-between items-start mb-2">
                    <h3 className="text-xl font-bold text-gray-800">Room {room.roomNumber}</h3>
                    <span className={`px-2 py-1 rounded-full text-xs font-semibold ${getStatusColor(room.status)}`}>
                        {room.status}
                    </span>
                </div>
                
                {/* Room type */}
                <p className="text-gray-600 text-sm mb-4">{room.type} Room</p>
                
                {/* Price and actions */}
                <div className="flex justify-between items-center">
                    {/* Price display */}
                    <div className="flex items-center text-indigo-600 font-bold">
                        <DollarSign size={16} />
                        <span>{room.price}</span>
                        <span className="text-gray-500 text-sm font-normal">/night</span>
                    </div>
                    
                    {/* Action buttons */}
                    <div className="flex gap-2">
                        {/* Edit button */}
                        <button
                            onClick={() => onEdit(room)}
                            className="p-2 text-gray-500 hover:text-indigo-600 hover:bg-indigo-50 rounded-lg transition-colors"
                            title="Edit Room"
                        >
                            <Edit2 size={18} />
                        </button>
                        
                        {/* Delete button */}
                        <button
                            onClick={() => onDelete(room.id)}
                            className="p-2 text-gray-500 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                            title="Delete Room"
                        >
                            <Trash2 size={18} />
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default RoomCard;