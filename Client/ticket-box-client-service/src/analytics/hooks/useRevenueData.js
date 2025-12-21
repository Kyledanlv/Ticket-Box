// src/analytics/hooks/useRevenueData.js
import { useQuery } from '@tanstack/react-query';
import { useAnalytics } from '../contexts/AnalyticsContext';
import { analyticsApiService } from '../api/analyticsApi';

export const useRevenueData = () => {
  const { role, organizerId, eventId, timeRange } = useAnalytics();
  
  return useQuery({
    queryKey: ['revenue', role, organizerId, eventId, timeRange],
    queryFn: async () => {
      let response;
      
      if (role === 'ADMIN') {
        response = await analyticsApiService.getSystemRevenue(timeRange);
      } else if (eventId) {
        response = await analyticsApiService.getEventRevenue(eventId, timeRange);
      } else if (organizerId) {
        response = await analyticsApiService.getOrganizerRevenue(organizerId, timeRange);
      } else {
        throw new Error('Invalid analytics context');
      }
      
      // Transform backend response to match frontend format
      return transformRevenueData(response);
    },
    staleTime: 1000 * 60 * 5, // 5 minutes
    refetchOnWindowFocus: false,
    retry: 2
  });
};

// Transform backend response to frontend format
const transformRevenueData = (backendData) => {
  if (!backendData?.chartData) {
    return [];
  }
  
  return backendData.chartData.map(point => ({
    date: point.date,
    revenue: point.revenue,
    ticketsSold: point.ticketsSold,
    orderCount: point.orderCount
  }));
};