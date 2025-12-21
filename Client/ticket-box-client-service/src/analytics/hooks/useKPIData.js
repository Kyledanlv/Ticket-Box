// src/analytics/hooks/useKPIData.js
import { useQuery } from '@tanstack/react-query';
import { useAnalytics } from '../contexts/AnalyticsContext';
import { analyticsApiService } from '../api/analyticsApi';

export const useKPIData = () => {
  const { role, organizerId, timeRange } = useAnalytics();
  
  return useQuery({
    queryKey: ['kpi', role, organizerId, timeRange],
    queryFn: async () => {
      let response;
      
      if (role === 'ADMIN') {
        response = await analyticsApiService.getSystemKPI(timeRange);
      } else if (organizerId) {
        response = await analyticsApiService.getOrganizerKPI(organizerId, timeRange);
      } else {
        throw new Error('Invalid analytics context for KPI');
      }
      
      return transformKPIData(response);
    },
    staleTime: 1000 * 60 * 5,
    refetchOnWindowFocus: false,
    retry: 2
  });
};

// Transform backend response to frontend format
const transformKPIData = (backendData) => {
  return {
    totalRevenue: backendData.totalRevenue || 0,
    ticketsSold: backendData.ticketsSold || 0,
    activeEvents: backendData.activeEvents || 0,
    avgOrderValue: backendData.avgOrderValue || 0,
    revenueChange: backendData.revenueChange || 0,
    ticketsSoldChange: backendData.ticketsSoldChange || 0,
    conversionRate: backendData.conversionRate || 0
  };
};