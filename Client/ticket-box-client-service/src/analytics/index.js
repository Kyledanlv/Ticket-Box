// src/analytics/index.js - CẬP NHẬT
// Contexts
export { AnalyticsProvider, useAnalytics } from './contexts/AnalyticsContext';

// Components
export { BaseChart } from './components/charts/BaseChart';
export { KPICard } from './components/charts/KPICard';
export { RevenueLineChart } from './components/charts/RevenueLineChart';
export { TopEventsLeaderboard } from './components/charts/TopEventsLeaderBoard';

// Boards
export { KPIOverviewBoard } from './components/boards/KPIOverviewBoard';
export { RevenueBoard } from './components/boards/RevenueBoard';

// Hooks
export { useRevenueData } from './hooks/useRevenueData';
export { useTopEventsData } from './hooks/useTopEventsData';
export { useKPIData } from './hooks/useKPIData';

// Utils
export { 
  formatCurrency, 
  formatPercentage, 
  formatNumber,
  formatDate 
} from './utils/formatters';

export { CHART_COLORS, COLOR_PALETTE } from './utils/colors';
export { TIME_RANGES, ROLES } from './utils/constants';