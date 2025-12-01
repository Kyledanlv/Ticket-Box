// src/analytics/contexts/AnalyticsContext.jsx
import React, { createContext, useContext, useReducer } from 'react';

const AnalyticsContext = createContext();

const initialState = {
  timeRange: '30d',
  filters: {
    eventType: 'all',
    ticketType: 'all',
    status: 'all'
  }
};

const analyticsReducer = (state, action) => {
  switch (action.type) {
    case 'SET_TIME_RANGE':
      return { ...state, timeRange: action.payload };
    case 'SET_FILTERS':
      return { ...state, filters: { ...state.filters, ...action.payload } };
    default:
      return state;
  }
};

export const AnalyticsProvider = ({ 
  children, 
  role = 'ORGANIZER', 
  organizerId = null,
  eventId = null,
  initialConfig = {} 
}) => {
  const [state, dispatch] = useReducer(analyticsReducer, {
    ...initialState,
    ...initialConfig
  });

  const value = {
    ...state,
    role,
    organizerId,
    eventId,
    dispatch
  };

  return (
    <AnalyticsContext.Provider value={value}>
      {children}
    </AnalyticsContext.Provider>
  );
};

export const useAnalytics = () => {
  const context = useContext(AnalyticsContext);
  if (!context) {
    throw new Error('useAnalytics must be used within AnalyticsProvider');
  }
  return context;
};