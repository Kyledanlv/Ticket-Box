// src/analytics/hooks/useRevenueData.js
import { useQuery } from '@tanstack/react-query';
import { useAnalytics } from '../contexts/AnalyticsContext';
import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_ANALYTICS_API_URL || 'http://localhost:8081/api/analytics';

export const useRevenueData = () => {
  const { role, organizerId, eventId, timeRange } = useAnalytics();
  
  return useQuery({
    queryKey: ['revenue', role, organizerId, eventId, timeRange],
    queryFn: async () => {
      let endpoint;
      
      if (role === 'ADMIN') {
        endpoint = `${API_BASE_URL}/revenue/system?timeRange=${timeRange}`;
      } else if (eventId) {
        endpoint = `${API_BASE_URL}/revenue/event/${eventId}?timeRange=${timeRange}`;
      } else if (organizerId) {
        endpoint = `${API_BASE_URL}/revenue/organizer/${organizerId}?timeRange=${timeRange}`;
      } else {
        throw new Error('Invalid analytics context');
      }
      
      try {
        const response = await axios.get(endpoint, {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
          }
        });
        
        // Transform backend response to match frontend format
        return transformRevenueData(response.data);
      } catch (error) {
        console.error('Error fetching revenue data:', error);
        
        // Fallback to mock data in development
        if (import.meta.env.DEV) {
          console.warn('Using mock data for development');
          return generateMockRevenueData(timeRange);
        }
        
        throw error;
      }
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

// Mock data generator for development/fallback
const generateMockRevenueData = (timeRange) => {
  const days = getTimeRangeDays(timeRange);
  const data = [];
  const today = new Date();
  
  for (let i = days; i >= 0; i--) {
    const date = new Date(today);
    date.setDate(date.getDate() - i);
    
    data.push({
      date: date.toISOString(),
      revenue: Math.floor(Math.random() * 5000000) + 1000000,
      ticketsSold: Math.floor(Math.random() * 100) + 20,
      orderCount: Math.floor(Math.random() * 50) + 10
    });
  }
  
  return data;
};

const getTimeRangeDays = (timeRange) => {
  switch (timeRange) {
    case '7d': return 7;
    case '30d': return 30;
    case '90d': return 90;
    case '1y': return 365;
    default: return 30;
  }
};