// src/analytics/components/charts/TopEventsLeaderboard.jsx
import React, { useState } from 'react';
import { 
  BarChart, 
  Bar, 
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  Legend,
  ResponsiveContainer
} from 'recharts';
import { BaseChart } from './BaseChart';
import { useTopEventsData } from '../../hooks/useTopEventsData';
import { useAnalytics } from '../../contexts/AnalyticsContext';
import { formatCurrency, formatNumber } from '../../utils/formatters';
import { CHART_COLORS } from '../../utils/colors';

export const TopEventsLeaderboard = ({ 
  limit = 10,
  height = 400,
  className = "" 
}) => {
  const { data, isLoading, error } = useTopEventsData({ limit });
  const { role, eventId } = useAnalytics();
  
  const getChartTitle = () => {
    if (role === 'ADMIN') {
      return 'Top Events - System Wide';
    } else if (eventId) {
      return 'Ticket Type Performance';
    } else {
      return 'My Top Performing Events';
    }
  };
  
  const CustomTooltip = ({ active, payload, label }) => {
    if (active && payload && payload.length) {
      return (
        <div className="bg-white p-3 shadow-lg border rounded-lg">
          <p className="font-medium text-gray-900 mb-2">{label}</p>
          {payload.map((entry, index) => (
            <p key={index} style={{ color: entry.color }} className="text-sm">
              {entry.name}: {entry.name.includes('Revenue') ? formatCurrency(entry.value) : formatNumber(entry.value)}
            </p>
          ))}
        </div>
      );
    }
    return null;
  };
  
  return (
    <div className={`bg-white rounded-lg shadow-lg p-6 ${className}`}>
      <h3 className="text-xl font-semibold mb-6">{getChartTitle()}</h3>
      
      <BaseChart 
        isLoading={isLoading} 
        error={error} 
        data={data}
        emptyMessage="No events data available"
      >
        <ResponsiveContainer width="100%" height={height}>
          <BarChart 
            data={data} 
            layout="vertical"
            margin={{ top: 20, right: 30, left: 100, bottom: 20 }}
          >
            <CartesianGrid strokeDasharray="3 3" horizontal={false} />
            <XAxis 
              type="number"
              tickFormatter={role === 'ADMIN' || !eventId ? formatCurrency : formatNumber}
            />
            <YAxis 
              type="category" 
              dataKey={role === 'ADMIN' || !eventId ? "eventName" : "ticketType"}
              width={90}
              tick={{ fontSize: 12 }}
            />
            <Tooltip content={<CustomTooltip />} />
            <Legend />
            
            {role === 'ADMIN' ? (
              <Bar 
                dataKey="revenue" 
                name="Revenue"
                fill={CHART_COLORS.primary}
                radius={[0, 4, 4, 0]}
              />
            ) : eventId ? (
              <Bar 
                dataKey="ticketsSold" 
                name="Tickets Sold"
                fill={CHART_COLORS.success}
                radius={[0, 4, 4, 0]}
              />
            ) : (
              <Bar 
                dataKey="revenue" 
                name="Revenue"
                fill={CHART_COLORS.primary}
                radius={[0, 4, 4, 0]}
              />
            )}
          </BarChart>
        </ResponsiveContainer>
      </BaseChart>
    </div>
  );
};