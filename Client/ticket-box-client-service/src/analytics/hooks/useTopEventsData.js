// src/analytics/hooks/useTopEventsData.js
import { useQuery } from '@tanstack/react-query';
import { useAnalytics } from '../contexts/AnalyticsContext';

export const useTopEventsData = ({ limit = 10 } = {}) => {
  const { role, organizerId, eventId } = useAnalytics();
  
  return useQuery({
    queryKey: ['top-events', role, organizerId, eventId, limit],
    queryFn: async () => {
      await new Promise(resolve => setTimeout(resolve, 800));
      
      if (role === 'ADMIN') {
        return generateSystemTopEvents(limit);
      } else if (eventId) {
        return generateEventTicketComparison(eventId);
      } else {
        return generateOrganizerTopEvents(organizerId, limit);
      }
    },
    staleTime: 1000 * 60 * 5,
    refetchOnWindowFocus: false
  });
};

// Mock data generators
const generateSystemTopEvents = (limit) => {
  const events = ['Music Festival', 'Tech Conference', 'Food Expo', 'Art Exhibition', 'Sports Tournament'];
  return Array.from({ length: limit }, (_, i) => ({
    eventName: `${events[i % events.length]} ${i + 1}`,
    revenue: Math.floor(Math.random() * 50000000) + 10000000,
    ticketsSold: Math.floor(Math.random() * 1000) + 500,
    sellThroughRate: Math.random() * 0.8 + 0.2
  }));
};

const generateOrganizerTopEvents = (organizerId, limit) => {
  const events = ['My Concert', 'Workshop Series', 'Community Event', 'Product Launch'];
  return Array.from({ length: limit }, (_, i) => ({
    eventName: `${events[i % events.length]} ${i + 1}`,
    revenue: Math.floor(Math.random() * 10000000) + 2000000,
    ticketsSold: Math.floor(Math.random() * 500) + 100,
    sellThroughRate: Math.random() * 0.9 + 0.1
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