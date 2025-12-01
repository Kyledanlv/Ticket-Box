// src/analytics/hooks/useKPIData.js
import { useQuery } from '@tanstack/react-query';
import { useAnalytics } from '../contexts/AnalyticsContext';

export const useKPIData = () => {
  const { role, organizerId, timeRange } = useAnalytics();
  
  return useQuery({
    queryKey: ['kpi', role, organizerId, timeRange],
    queryFn: async () => {
      await new Promise(resolve => setTimeout(resolve, 600));
      
      const baseData = {
        totalRevenue: Math.floor(Math.random() * 100000000) + 50000000,
        ticketsSold: Math.floor(Math.random() * 5000) + 2000,
        activeEvents: Math.floor(Math.random() * 50) + 20,
        avgOrderValue: Math.floor(Math.random() * 500000) + 200000,
        revenueChange: (Math.random() - 0.3) * 50,
        ticketsSoldChange: (Math.random() - 0.2) * 40
      };
      
      // Điều chỉnh dựa trên role
      if (role !== 'ADMIN') {
        baseData.totalRevenue = Math.floor(baseData.totalRevenue / 10);
        baseData.ticketsSold = Math.floor(baseData.ticketsSold / 10);
        baseData.activeEvents = Math.floor(baseData.activeEvents / 5);
      }
      
      return baseData;
    },
    staleTime: 1000 * 60 * 5,
    refetchOnWindowFocus: false
  });
};