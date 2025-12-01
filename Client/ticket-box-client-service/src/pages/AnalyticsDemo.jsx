// src/pages/AnalyticsDemo.jsx
import React, { useState } from 'react';
import { 
  AnalyticsProvider, 
  KPIOverviewBoard, 
  RevenueBoard,
  RevenueLineChart,
  TopEventsLeaderboard 
} from '../analytics';

export const AnalyticsDemo = () => {
  const [role, setRole] = useState('ADMIN');
  const [organizerId, setOrganizerId] = useState('org_123');
  const [eventId, setEventId] = useState('');

  return (
    <div className="min-h-screen bg-gray-100 p-6">
      <div className="max-w-7xl mx-auto">
        <h1 className="text-3xl font-bold mb-6">Analytics Demo</h1>
        
        {/* Controls */}
        <div className="bg-white p-4 rounded-lg shadow-md mb-6">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Role
              </label>
              <select 
                value={role}
                onChange={(e) => setRole(e.target.value)}
                className="w-full p-2 border border-gray-300 rounded-md"
              >
                <option value="ADMIN">Admin</option>
                <option value="ORGANIZER">Organizer</option>
              </select>
            </div>
            
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Organizer ID
              </label>
              <input 
                type="text"
                value={organizerId}
                onChange={(e) => setOrganizerId(e.target.value)}
                className="w-full p-2 border border-gray-300 rounded-md"
                placeholder="org_123"
              />
            </div>
            
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Event ID (optional)
              </label>
              <input 
                type="text"
                value={eventId}
                onChange={(e) => setEventId(e.target.value)}
                className="w-full p-2 border border-gray-300 rounded-md"
                placeholder="event_456"
              />
            </div>
          </div>
        </div>

        {/* Analytics Components */}
        <AnalyticsProvider 
          role={role} 
          organizerId={organizerId}
          eventId={eventId || null}
        >
          <div className="space-y-6">
            {/* KPI Overview */}
            <KPIOverviewBoard />
            
            {/* Revenue Analytics */}
            <RevenueBoard />
            
            {/* Individual Components Demo */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              <RevenueLineChart />
              <TopEventsLeaderboard />
            </div>
          </div>
        </AnalyticsProvider>
        
        {/* Demo Info */}
        <div className="mt-8 bg-blue-50 p-4 rounded-lg">
          <h3 className="text-lg font-semibold mb-2">Demo Features:</h3>
          <ul className="list-disc list-inside space-y-1 text-sm text-gray-700">
            <li><strong>Admin Role:</strong> Xem doanh thu toàn hệ thống, top events hệ thống</li>
            <li><strong>Organizer Role:</strong> Xem doanh thu của riêng họ, top events của họ</li>
            <li><strong>With Event ID:</strong> Xem chi tiết 1 sự kiện cụ thể</li>
            <li><strong>Loading States:</strong> Hiển thị khi đang tải dữ liệu</li>
            <li><strong>Error Handling:</strong> Xử lý lỗi khi có vấn đề</li>
            <li><strong>Responsive:</strong> Hiển thị tốt trên mobile và desktop</li>
          </ul>
        </div>
      </div>
    </div>
  );
};