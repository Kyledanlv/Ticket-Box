// Client/ticket-box-client-service/src/analytics/components/charts/KPICard.jsx
import React from 'react';
import { TrendingUp, TrendingDown } from 'lucide-react';
import { formatCurrency, formatNumber, formatPercentage } from '../../utils/formatters';

export const KPICard = ({
  title,
  value,
  format = 'number',
  change,
  changePeriod,
  icon: Icon,
  trend = 'neutral',
  compact = false
}) => {
  const formatValue = (val) => {
    if (format === 'currency') return formatCurrency(val);
    if (format === 'percentage') return formatPercentage(val);
    return formatNumber(val);
  };

  const getTrendColor = () => {
    if (trend === 'up') return 'text-green-500';
    if (trend === 'down') return 'text-red-500';
    return 'text-gray-500';
  };

  const getTrendIcon = () => {
    if (trend === 'up') return <TrendingUp className="h-4 w-4" />;
    if (trend === 'down') return <TrendingDown className="h-4 w-4" />;
    return null;
  };

  return (
    <div className={`bg-white rounded-lg shadow-md p-${compact ? '4' : '6'} border border-gray-100 hover:shadow-lg transition-shadow`}>
      <div className="flex items-center justify-between mb-3">
        <h4 className="text-sm font-medium text-gray-600">{title}</h4>
        {Icon && (
          <div className="p-2 bg-blue-50 rounded-lg">
            <Icon className="h-5 w-5 text-blue-500" />
          </div>
        )}
      </div>
      
      <div className="mb-2">
        <p className={`${compact ? 'text-2xl' : 'text-3xl'} font-bold text-gray-900`}>
          {formatValue(value)}
        </p>
      </div>
      
      {change !== undefined && (
        <div className={`flex items-center gap-1 text-sm ${getTrendColor()}`}>
          {getTrendIcon()}
          <span className="font-medium">
            {change > 0 ? '+' : ''}{change.toFixed(1)}%
          </span>
          {changePeriod && (
            <span className="text-gray-500 ml-1">{changePeriod}</span>
          )}
        </div>
      )}
    </div>
  );
};