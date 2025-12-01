// src/analytics/services/analyticsApi.js
import axios from 'axios';

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
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
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
    if (error.response?.status === 401) {
      // Handle unauthorized - redirect to login
      localStorage.removeItem('accessToken');
      window.location.href = '/login';
    }
    
    console.error('Analytics API Error:', error);
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
    analyticsApi.get(`/top-events`, { params: { limit } }),
  
  getOrganizerTopEvents: (organizerId, limit = 10) =>
    analyticsApi.get(`/top-events/organizer/${organizerId}`, { params: { limit } }),
  
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