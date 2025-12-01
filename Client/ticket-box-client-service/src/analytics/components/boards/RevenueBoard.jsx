// src/analytics/components/boards/RevenueBoard.jsx
import React from 'react';
import { RevenueLineChart } from '../charts/RevenueLineChart';
import { TopEventsLeaderboard } from '../charts/TopEventsLeaderBoard';
import { useAnalytics } from '../../contexts/AnalyticsContext';
import { TrendingUp, BarChart3 } from 'lucide-react';

export const RevenueBoard = ({ 
  className = "" 
}) => {
  const { role } = useAnalytics();

  return (
    <div className={`bg-white rounded-lg shadow-lg p-6 ${className}`}>
      <div className="flex items-center gap-2 mb-6">
        <TrendingUp className="h-6 w-6 text-green-500" />
        <h3 className="text-xl font-semibold">
          {role === 'ADMIN' ? 'System Revenue Analytics' : 'My Revenue Analytics'}
        </h3>
      </div>

      <div className="grid grid-cols-1 xl:grid-cols-2 gap-6">
        <RevenueLineChart />
        
        <div className="flex flex-col">
          <div className="flex items-center gap-2 mb-4">
            <BarChart3 className="h-5 w-5 text-blue-500" />
            <h4 className="text-lg font-medium">
              {role === 'ADMIN' ? 'Top Events' : 'Event Performance'}
            </h4>
          </div>
          <TopEventsLeaderboard height={350} />
        </div>
      </div>
    </div>
  );
};