// src/analytics/api/analyticsApi.js
import axios from 'axios';
import { useAuthStore } from '../../store/useAuthStore';

const API_BASE_URL = import.meta.env.VITE_ANALYTICS_API_URL || 'http://localhost:8081/api/analytics';

// Create axios instance with default config
const analyticsApi = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Request interceptor to add auth token
analyticsApi.interceptors.request.use(
  (config) => {
    const { accessToken } = useAuthStore.getState();
    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`;
      console.log('Analytics API Request:', config.method.toUpperCase(), config.url);
      console.log('Token present:', !!accessToken);
    } else {
      console.warn('No access token available for analytics request');
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
analyticsApi.interceptors.response.use(
  (response) => response.data,
  (error) => {
    console.error('Analytics API Error Details:', {
      status: error.response?.status,
      statusText: error.response?.statusText,
      url: error.config?.url,
      data: error.response?.data
    });
    
    if (error.response?.status === 401 || error.response?.status === 403) {
      console.error('Authentication failed for analytics API');
      // Handle unauthorized - redirect to login
      useAuthStore.getState().logout();
      window.location.href = '/';
    }
    
    return Promise.reject(error);
  }
);

// API Methods
export const analyticsApiService = {
  // Revenue APIs
  getSystemRevenue: (timeRange = '30d') => 
    analyticsApi.get(`/revenue/system`, { params: { timeRange } }),
  
  getOrganizerRevenue: (organizerId, timeRange = '30d') =>
    analyticsApi.get(`/revenue/organizer/${organizerId}`, { params: { timeRange } }),
  
  getEventRevenue: (eventId, timeRange = '30d') =>
    analyticsApi.get(`/revenue/event/${eventId}`, { params: { timeRange } }),
  
  // Top Events APIs
  getTopEvents: (limit = 10) =>
    analyticsApi.get(`/events/top`, { params: { limit } }),
  
  getOrganizerTopEvents: (organizerId, limit = 10) =>
    analyticsApi.get(`/events/organizer/${organizerId}`, { params: { limit } }),
  
  // KPI APIs
  getSystemKPI: (timeRange = '30d') =>
    analyticsApi.get(`/kpi/system`, { params: { timeRange } }),
  
  getOrganizerKPI: (organizerId, timeRange = '30d') =>
    analyticsApi.get(`/kpi/organizer/${organizerId}`, { params: { timeRange } }),
  
  // Health Check
  healthCheck: () =>
    analyticsApi.get(`/health`)
};

export default analyticsApi;