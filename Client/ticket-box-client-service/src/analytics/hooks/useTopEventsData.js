// src/analytics/hooks/useTopEventsData.js
import { useQuery } from '@tanstack/react-query';
import { useAnalytics } from '../contexts/AnalyticsContext';
import { analyticsApiService } from '../api/analyticsApi';

export const useTopEventsData = ({ limit = 10 } = {}) => {
  const { role, organizerId, eventId } = useAnalytics();
  
  return useQuery({
    queryKey: ['top-events', role, organizerId, eventId, limit],
    queryFn: async () => {
      let response;
      
      if (role === 'ADMIN') {
        response = await analyticsApiService.getTopEvents(limit);
      } else if (organizerId) {
        response = await analyticsApiService.getOrganizerTopEvents(organizerId, limit);
      } else {
        throw new Error('Invalid analytics context for top events');
      }
      
      return transformTopEventsData(response, role, eventId);
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