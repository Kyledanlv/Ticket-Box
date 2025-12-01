// src/analytics/components/charts/BaseChart.jsx
import React from 'react';
import { Loader2, AlertCircle } from 'lucide-react';

export const BaseChart = ({ 
  isLoading, 
  error, 
  data, 
  emptyMessage = "No data available",
  children,
  className = "" 
}) => {
  if (isLoading) {
    return (
      <div className={`flex items-center justify-center h-64 ${className}`}>
        <Loader2 className="h-8 w-8 animate-spin text-blue-500" />
        <span className="ml-2 text-gray-600">Loading chart data...</span>
      </div>
    );
  }
  
  if (error) {
    return (
      <div className={`flex flex-col items-center justify-center h-64 text-red-500 ${className}`}>
        <AlertCircle className="h-8 w-8 mb-2" />
        <p className="text-sm">{error.message || 'Failed to load data'}</p>
      </div>
    );
  }
  
  if (!data || data.length === 0) {
    return (
      <div className={`flex items-center justify-center h-64 text-gray-400 ${className}`}>
        <p>{emptyMessage}</p>
      </div>
    );
  }
  
  return <div className={`w-full h-full ${className}`}>{children}</div>;
};