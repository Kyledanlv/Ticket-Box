// src/analytics/hooks/useTopEventsData.js
import { useQuery } from '@tanstack/react-query';
import { useAnalytics } from '../contexts/AnalyticsContext';
import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_ANALYTICS_API_URL || 'http://localhost:8081/api/analytics';

export const useTopEventsData = ({ limit = 10 } = {}) => {
  const { role, organizerId, eventId } = useAnalytics();
  
  return useQuery({
    queryKey: ['top-events', role, organizerId, eventId, limit],
    queryFn: async () => {
      let endpoint;
      
      if (role === 'ADMIN') {
        endpoint = `${API_BASE_URL}/top-events?limit=${limit}`;
      } else if (organizerId) {
        endpoint = `${API_BASE_URL}/top-events/organizer/${organizerId}?limit=${limit}`;
      } else {
        throw new Error('Invalid analytics context for top events');
      }
      
      try {
        const response = await axios.get(endpoint, {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
          }
        });
        
        return transformTopEventsData(response.data, role, eventId);
      } catch (error) {
        console.error('Error fetching top events data:', error);
        
        // Fallback to mock data in development
        if (import.meta.env.DEV) {
          console.warn('Using mock data for development');
          return generateMockTopEventsData(role, eventId, limit);
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
const transformTopEventsData = (backendData, role, eventId) => {
  if (!backendData?.events) {
    return [];
  }
  
  // For event-specific view, we might want to show ticket types
  if (eventId) {
    // This would come from a different endpoint for ticket breakdown
    return backendData.events.map(event => ({
      ticketType: event.eventName, // or ticket type field
      ticketsSold: event.ticketsSold,
      revenue: event.revenue
    }));
  }
  
  // For admin or organizer view
  return backendData.events.map(event => ({
    eventName: event.eventName,
    revenue: event.revenue,
    ticketsSold: event.ticketsSold,
    sellThroughRate: event.sellThroughRate,
    totalCapacity: event.totalCapacity,
    organizerName: event.organizerName
  }));
};

// Mock data generators
const generateMockTopEventsData = (role, eventId, limit) => {
  if (role === 'ADMIN') {
    return generateSystemTopEvents(limit);
  } else if (eventId) {
    return generateEventTicketComparison(eventId);
  } else {
    return generateOrganizerTopEvents(limit);
  }
};

const generateSystemTopEvents = (limit) => {
  const events = ['Music Festival', 'Tech Conference', 'Food Expo', 'Art Exhibition', 'Sports Tournament'];
  return Array.from({ length: limit }, (_, i) => ({
    eventName: `${events[i % events.length]} ${i + 1}`,
    revenue: Math.floor(Math.random() * 50000000) + 10000000,
    ticketsSold: Math.floor(Math.random() * 1000) + 500,
    sellThroughRate: Math.random() * 0.8 + 0.2,
    totalCapacity: Math.floor(Math.random() * 2000) + 1000,
    organizerName: `Organizer ${i + 1}`
  }));
};

const generateOrganizerTopEvents = (limit) => {
  const events = ['My Concert', 'Workshop Series', 'Community Event', 'Product Launch'];
  return Array.from({ length: limit }, (_, i) => ({
    eventName: `${events[i % events.length]} ${i + 1}`,
    revenue: Math.floor(Math.random() * 10000000) + 2000000,
    ticketsSold: Math.floor(Math.random() * 500) + 100,
    sellThroughRate: Math.random() * 0.9 + 0.1,
    totalCapacity: Math.floor(Math.random() * 800) + 200
  }));
};

const generateEventTicketComparison = (eventId) => {
  const ticketTypes = ['VIP', 'Standard', 'Early Bird', 'Group', 'Student'];
  return ticketTypes.map((type, i) => ({
    ticketType: type,
    ticketsSold: Math.floor(Math.random() * 200) + 50,
    revenue: Math.floor(Math.random() * 5000000) + 1000000
  }));
};