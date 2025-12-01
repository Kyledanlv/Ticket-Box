// src/analytics/components/charts/RevenueLineChart.jsx
import React from 'react';
import { 
  LineChart, 
  Line, 
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  Legend,
  ResponsiveContainer 
} from 'recharts';
import { BaseChart } from './BaseChart';
import { useRevenueData } from '../../hooks/useRevenueData';
import { useAnalytics } from '../../contexts/AnalyticsContext';
import { formatCurrency, formatNumber, formatDate } from '../../utils/formatters';
import { CHART_COLORS } from '../../utils/colors';

export const RevenueLineChart = ({ 
  height = 400,
  className = "" 
}) => {
  const { data, isLoading, error } = useRevenueData();
  const { role } = useAnalytics();
  
  const getChartTitle = () => {
    return role === 'ADMIN' ? 'System Revenue Trend' : 'Revenue Trend';
  };
  
  const CustomTooltip = ({ active, payload, label }) => {
    if (active && payload && payload.length) {
      return (
        <div className="bg-white p-3 shadow-lg border rounded-lg">
          <p className="font-medium text-gray-900">{formatDate(label)}</p>
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
      <h3 className="text-xl font-semibold mb-4">{getChartTitle()}</h3>
      
      <BaseChart 
        isLoading={isLoading} 
        error={error} 
        data={data}
        emptyMessage="No revenue data for this period"
      >
        <ResponsiveContainer width="100%" height={height}>
          <LineChart data={data}>
            <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
            <XAxis 
              dataKey="date" 
              tickFormatter={formatDate}
              tick={{ fontSize: 12 }}
            />
            <YAxis 
              yAxisId="left"
              tickFormatter={formatCurrency}
              tick={{ fontSize: 12 }}
            />
            <YAxis 
              yAxisId="right" 
              orientation="right"
              tickFormatter={formatNumber}
              tick={{ fontSize: 12 }}
            />
            <Tooltip content={<CustomTooltip />} />
            <Legend />
            
            <Line 
              yAxisId="left"
              type="monotone" 
              dataKey="revenue" 
              stroke={CHART_COLORS.primary} 
              strokeWidth={2}
              name="Revenue"
              dot={{ fill: CHART_COLORS.primary, strokeWidth: 2, r: 3 }}
            />
            
            <Line 
              yAxisId="right"
              type="monotone" 
              dataKey="ticketsSold" 
              stroke={CHART_COLORS.success} 
              strokeWidth={2}
              name="Tickets Sold"
              dot={{ fill: CHART_COLORS.success, strokeWidth: 2, r: 3 }}
            />
          </LineChart>
        </ResponsiveContainer>
      </BaseChart>
    </div>
  );
};