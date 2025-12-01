// src/analytics/hooks/useKPIData.js
import { useQuery } from '@tanstack/react-query';
import { useAnalytics } from '../contexts/AnalyticsContext';
import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_ANALYTICS_API_URL || 'http://localhost:8081/api/analytics';

export const useKPIData = () => {
  const { role, organizerId, timeRange } = useAnalytics();
  
  return useQuery({
    queryKey: ['kpi', role, organizerId, timeRange],
    queryFn: async () => {
      let endpoint;
      
      if (role === 'ADMIN') {
        endpoint = `${API_BASE_URL}/kpi/system?timeRange=${timeRange}`;
      } else if (organizerId) {
        endpoint = `${API_BASE_URL}/kpi/organizer/${organizerId}?timeRange=${timeRange}`;
      } else {
        throw new Error('Invalid analytics context for KPI');
      }
      
      try {
        const response = await axios.get(endpoint, {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
          }
        });
        
        return transformKPIData(response.data);
      } catch (error) {
        console.error('Error fetching KPI data:', error);
        
        // Fallback to mock data in development
        if (import.meta.env.DEV) {
          console.warn('Using mock data for development');
          return generateMockKPIData(role);
        }
        
        throw error;
      }
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

// Mock data generator for development/fallback
const generateMockKPIData = (role) => {
  const baseData = {
    totalRevenue: Math.floor(Math.random() * 100000000) + 50000000,
    ticketsSold: Math.floor(Math.random() * 5000) + 2000,
    activeEvents: Math.floor(Math.random() * 50) + 20,
    avgOrderValue: Math.floor(Math.random() * 500000) + 200000,
    revenueChange: (Math.random() - 0.3) * 50,
    ticketsSoldChange: (Math.random() - 0.2) * 40,
    conversionRate: Math.random() * 30 + 50 // 50-80% conversion rate
  };
  
  // Adjust based on role
  if (role !== 'ADMIN') {
    baseData.totalRevenue = Math.floor(baseData.totalRevenue / 10);
    baseData.ticketsSold = Math.floor(baseData.ticketsSold / 10);
    baseData.activeEvents = Math.floor(baseData.activeEvents / 5);
    baseData.avgOrderValue = Math.floor(baseData.avgOrderValue / 2);
  }
  
  return baseData;
};