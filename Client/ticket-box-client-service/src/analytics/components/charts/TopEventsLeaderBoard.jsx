// src/analytics/components/charts/TopEventsLeaderBoard.jsx
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

// Hàm cắt ngắn text nếu quá dài
const truncateText = (text, maxLength = 25) => {
  if (!text) return '';
  if (text.length <= maxLength) return text;
  return text.substring(0, maxLength) + '...';
};

// Custom YAxis Label với tooltip
const CustomYAxisTick = ({ x, y, payload }) => {
  const fullText = payload.value;
  const truncatedText = truncateText(fullText, 25);
  
  return (
    <g transform={`translate(${x},${y})`}>
      <title>{fullText}</title>
      <text 
        x={0} 
        y={0} 
        dy={4} 
        textAnchor="end" 
        fill="#666"
        fontSize={11}
        className="cursor-pointer hover:fill-blue-600"
      >
        {truncatedText}
      </text>
    </g>
  );
};

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
  
  // Custom Tooltip hiển thị đầy đủ tên sự kiện
  const CustomTooltip = ({ active, payload }) => {
    if (active && payload && payload.length) {
      const dataPoint = payload[0].payload;
      const fullName = dataPoint.eventName || dataPoint.ticketType || '';
      
      return (
        <div className="bg-white p-4 shadow-xl border rounded-lg max-w-xs">
          <p className="font-semibold text-gray-900 mb-2 break-words">{fullName}</p>
          {payload.map((entry, index) => (
            <p key={index} style={{ color: entry.color }} className="text-sm font-medium">
              {entry.name}: {entry.name.includes('Revenue') ? formatCurrency(entry.value) : formatNumber(entry.value)}
            </p>
          ))}
          {dataPoint.organizerName && (
            <p className="text-xs text-gray-500 mt-1">By: {dataPoint.organizerName}</p>
          )}
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
            margin={{ top: 20, right: 30, left: 150, bottom: 20 }}
            barCategoryGap="15%"
          >
            <CartesianGrid strokeDasharray="3 3" horizontal={false} />
            <XAxis 
              type="number"
              tickFormatter={role === 'ADMIN' || !eventId ? formatCurrency : formatNumber}
              tick={{ fontSize: 11 }}
            />
            <YAxis 
              type="category" 
              dataKey={role === 'ADMIN' || !eventId ? "eventName" : "ticketType"}
              width={140}
              tick={<CustomYAxisTick />}
              interval={0}
            />
            <Tooltip content={<CustomTooltip />} cursor={{ fill: 'rgba(59, 130, 246, 0.1)' }} />
            <Legend wrapperStyle={{ fontSize: '12px' }} />
            
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