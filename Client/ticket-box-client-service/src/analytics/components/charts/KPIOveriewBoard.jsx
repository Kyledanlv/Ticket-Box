// src/analytics/components/boards/KPIOverviewBoard.jsx
import React from 'react';
import { DollarSign, Ticket, Users, TrendingUp } from 'lucide-react';
import { KPICard } from '../charts/KPICard';
import { useKPIData } from '../../hooks/useKPIData';
import { useAnalytics } from '../../contexts/AnalyticsContext';

export const KPIOverviewBoard = ({ config = {}, className = "" }) => {
  const { role } = useAnalytics();
  const { data: kpiData, isLoading } = useKPIData();
  
  const {
    showRevenue = true,
    showTickets = true,
    showEvents = true,
    showAOV = true,
    compact = false
  } = config;

  if (isLoading) {
    return (
      <div className={`bg-white rounded-lg shadow-lg p-6 ${className}`}>
        <div className="animate-pulse">
          <div className="h-6 bg-gray-200 rounded w-1/4 mb-6"></div>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            {[...Array(4)].map((_, i) => (
              <div key={i} className="h-24 bg-gray-200 rounded"></div>
            ))}
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className={`bg-white rounded-lg shadow-lg p-6 ${className}`}>
      <h3 className="text-xl font-semibold mb-6">
        {role === 'ADMIN' ? 'System Overview' : 'Performance Overview'}
      </h3>
      
      <div className={`grid grid-cols-1 ${compact ? 'sm:grid-cols-2' : 'md:grid-cols-2 lg:grid-cols-4'} gap-4`}>
        {showRevenue && (
          <KPICard
            title={role === 'ADMIN' ? "Total Revenue" : "My Revenue"}
            value={kpiData?.totalRevenue || 0}
            format="currency"
            change={kpiData?.revenueChange}
            changePeriod="vs last period"
            icon={DollarSign}
            trend={kpiData?.revenueChange > 0 ? 'up' : 'down'}
            compact={compact}
          />
        )}
        
        {showTickets && (
          <KPICard
            title="Tickets Sold"
            value={kpiData?.ticketsSold || 0}
            change={kpiData?.ticketsSoldChange}
            changePeriod="vs last period"
            icon={Ticket}
            trend={kpiData?.ticketsSoldChange > 0 ? 'up' : 'down'}
            compact={compact}
          />
        )}
        
        {showEvents && (
          <KPICard
            title="Active Events"
            value={kpiData?.activeEvents || 0}
            icon={TrendingUp}
            compact={compact}
          />
        )}
        
        {showAOV && (
          <KPICard
            title="Avg Order Value"
            value={kpiData?.avgOrderValue || 0}
            format="currency"
            icon={Users}
            compact={compact}
          />
        )}
      </div>
    </div>
  );
};