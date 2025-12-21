// Client/ticket-box-client-service/src/pages/adminDashboardPage.jsx
import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuthStore } from '../store/useAuthStore';
import { AnalyticsProvider, KPIOverviewBoard, RevenueBoard } from '../analytics';
import { 
  Shield, 
  TrendingUp, 
  Users, 
  Calendar,
  DollarSign,
  Ticket,
  AlertCircle,
  CheckCircle,
  XCircle,
  Clock,
  BarChart3,
  Download,
  Filter,
  Search,
  Eye,
  UserCheck,
  UserX
} from 'lucide-react';

const AdminDashboardPage = () => {
  const { user } = useAuthStore();
  const [timeRange, setTimeRange] = useState('30d');
  const [activeTab, setActiveTab] = useState('overview');
  
  // Check if user is admin
  const isAdmin = user?.roles?.some(role => role.name === 'ROLE_ADMIN');
  
  if (!isAdmin) {
    return (
      <div className="container mx-auto max-w-7xl px-4 py-8">
        <div className="rounded-lg bg-red-900/20 p-8 text-center">
          <Shield className="mx-auto mb-4 h-16 w-16 text-red-500" />
          <h2 className="text-2xl font-bold text-white">Access Denied</h2>
          <p className="mt-2 text-gray-400">You don't have permission to view this page</p>
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto max-w-7xl px-4 py-8">
      {/* Header Section */}
      <div className="mb-8 flex flex-col gap-4 border-b border-gray-700 pb-6 md:flex-row md:items-center md:justify-between">
        <div className="flex items-center gap-3">
          <div className="rounded-lg bg-purple-600/20 p-3">
            <Shield className="h-8 w-8 text-purple-500" />
          </div>
          <div>
            <h1 className="text-3xl font-bold text-white">Admin Dashboard</h1>
            <p className="mt-1 text-gray-400">System-wide analytics and management</p>
          </div>
        </div>
        <div className="flex gap-3">
          <select 
            value={timeRange}
            onChange={(e) => setTimeRange(e.target.value)}
            className="rounded-lg bg-gray-800 px-4 py-2 text-sm text-white"
          >
            <option value="7d">Last 7 days</option>
            <option value="30d">Last 30 days</option>
            <option value="90d">Last 90 days</option>
            <option value="1y">Last year</option>
          </select>
          <button className="flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-700">
            <Download className="h-4 w-4" />
            Export
          </button>
        </div>
      </div>

      {/* Tab Navigation */}
      <div className="mb-8 flex gap-2 overflow-x-auto border-b border-gray-700">
        <button
          onClick={() => setActiveTab('overview')}
          className={`whitespace-nowrap px-6 py-3 font-semibold transition-colors ${
            activeTab === 'overview'
              ? 'border-b-2 border-blue-500 text-blue-400'
              : 'text-gray-400 hover:text-white'
          }`}
        >
          Overview
        </button>
        <button
          onClick={() => setActiveTab('events')}
          className={`whitespace-nowrap px-6 py-3 font-semibold transition-colors ${
            activeTab === 'events'
              ? 'border-b-2 border-blue-500 text-blue-400'
              : 'text-gray-400 hover:text-white'
          }`}
        >
          Events Management
        </button>
        <button
          onClick={() => setActiveTab('users')}
          className={`whitespace-nowrap px-6 py-3 font-semibold transition-colors ${
            activeTab === 'users'
              ? 'border-b-2 border-blue-500 text-blue-400'
              : 'text-gray-400 hover:text-white'
          }`}
        >
          Users Management
        </button>
      </div>

      {/* Tab Content */}
      {activeTab === 'overview' && <OverviewTab timeRange={timeRange} />}
      {activeTab === 'events' && <EventsManagementTab />}
      {activeTab === 'users' && <UsersManagementTab />}
    </div>
  );
};

// Overview Tab - Analytics
const OverviewTab = ({ timeRange }) => {
  return (
    <div className="space-y-6">
      {/* System-wide Quick Stats */}
      <div className="grid grid-cols-2 gap-4 md:grid-cols-3 lg:grid-cols-6">
      </div>

      {/* Analytics Section - System Level */}
      <AnalyticsProvider 
        role="ADMIN" 
        organizerId={null}
      >
        <div className="space-y-6">
          {/* System KPI Overview */}
          <div className="rounded-xl bg-gradient-to-br from-blue-900/30 to-purple-900/30 p-1">
            <div className="rounded-lg bg-gray-900 p-6">
              <div className="mb-4 flex items-center gap-2">
                <TrendingUp className="h-6 w-6 text-blue-400" />
                <h2 className="text-xl font-semibold text-white">System Performance Overview</h2>
              </div>
              <KPIOverviewBoard />
            </div>
          </div>
          
          {/* System Revenue Analytics & Top Events */}
          <div className="rounded-xl bg-gradient-to-br from-green-900/30 to-blue-900/30 p-1">
            <div className="rounded-lg bg-gray-900 p-6">
              <div className="mb-4 flex items-center gap-2">
                <BarChart3 className="h-6 w-6 text-green-400" />
                <h2 className="text-xl font-semibold text-white">Revenue & Top Events Analytics</h2>
              </div>
              <RevenueBoard />
            </div>
          </div>
        </div>
      </AnalyticsProvider>

      {/* Recent Activity */}
      <div className="rounded-lg bg-gray-800 p-6">
        <h3 className="mb-4 text-lg font-semibold text-white">Recent System Activity</h3>
        <div className="space-y-3">
        </div>
      </div>
    </div>
  );
};

// Events Management Tab
const EventsManagementTab = () => {
  const [statusFilter, setStatusFilter] = useState('pending');
  const [searchTerm, setSearchTerm] = useState('');

  return (
    <div className="space-y-6">
      {/* Filters */}
      <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
        <div className="flex items-center gap-3">
          <Filter className="h-5 w-5 text-gray-400" />
          <select 
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="rounded-lg bg-gray-700 px-4 py-2 text-sm text-white"
          >
            <option value="pending">Pending Approval</option>
            <option value="approved">Approved</option>
            <option value="declined">Declined</option>
            <option value="all">All Events</option>
          </select>
        </div>
        <div className="relative">
          <Search className="absolute left-3 top-1/2 h-5 w-5 -translate-y-1/2 text-gray-400" />
          <input
            type="text"
            placeholder="Search events..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="rounded-lg bg-gray-700 py-2 pl-10 pr-4 text-white placeholder-gray-400"
          />
        </div>
      </div>

      {/* Pending Events List */}
      <div className="rounded-lg bg-gray-800 p-6">
        <h3 className="mb-4 text-lg font-semibold text-white">
          Pending Approvals (0)
        </h3>
        <div className="space-y-4">
        </div>
      </div>
    </div>
  );
};

// Users Management Tab
const UsersManagementTab = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [roleFilter, setRoleFilter] = useState('all');

  return (
    <div className="space-y-6">
      {/* Filters */}
      <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
        <div className="flex items-center gap-3">
          <Filter className="h-5 w-5 text-gray-400" />
          <select 
            value={roleFilter}
            onChange={(e) => setRoleFilter(e.target.value)}
            className="rounded-lg bg-gray-700 px-4 py-2 text-sm text-white"
          >
            <option value="all">All Users</option>
            <option value="user">Regular Users</option>
            <option value="organizer">Organizers</option>
            <option value="approver">Approvers</option>
          </select>
        </div>
        <div className="relative">
          <Search className="absolute left-3 top-1/2 h-5 w-5 -translate-y-1/2 text-gray-400" />
          <input
            type="text"
            placeholder="Search users..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="rounded-lg bg-gray-700 py-2 pl-10 pr-4 text-white placeholder-gray-400"
          />
        </div>
      </div>

      {/* Users Table */}
      <div className="overflow-x-auto rounded-lg bg-gray-800">
        <table className="w-full">
          <thead className="border-b border-gray-700 bg-gray-900">
            <tr>
              <th className="px-6 py-4 text-left text-sm font-semibold text-gray-400">User</th>
              <th className="px-6 py-4 text-left text-sm font-semibold text-gray-400">Role</th>
              <th className="px-6 py-4 text-left text-sm font-semibold text-gray-400">Events</th>
              <th className="px-6 py-4 text-left text-sm font-semibold text-gray-400">Revenue</th>
              <th className="px-6 py-4 text-left text-sm font-semibold text-gray-400">Actions</th>
            </tr>
          </thead>
          <tbody>
          </tbody>
        </table>
      </div>
    </div>
  );
};

// Reusable Components
const StatCard = ({ icon: Icon, label, value, color, highlight }) => {
  const colorClasses = {
    blue: 'bg-blue-900/30 text-blue-400',
    green: 'bg-green-900/30 text-green-400',
    yellow: 'bg-yellow-900/30 text-yellow-400',
    purple: 'bg-purple-900/30 text-purple-400',
    indigo: 'bg-indigo-900/30 text-indigo-400',
    pink: 'bg-pink-900/30 text-pink-400',
  };

  return (
    <div className={`rounded-lg p-4 ${highlight ? 'ring-2 ring-yellow-500' : ''} ${colorClasses[color] || 'bg-gray-800'}`}>
      <div className="flex items-center justify-between">
        <div>
          <p className="text-xs text-gray-400">{label}</p>
          <p className="mt-1 text-xl font-bold text-white">{value}</p>
        </div>
        <Icon className="h-8 w-8 opacity-50" />
      </div>
    </div>
  );
};

const ActivityItem = ({ icon: Icon, text, time, color }) => {
  const colorClasses = {
    blue: 'text-blue-400',
    green: 'text-green-400',
    yellow: 'text-yellow-400',
    purple: 'text-purple-400',
  };

  return (
    <div className="flex items-start gap-3 rounded-lg bg-gray-700/50 p-3">
      <Icon className={`h-5 w-5 flex-shrink-0 ${colorClasses[color]}`} />
      <div className="flex-1">
        <p className="text-sm text-white">{text}</p>
        <p className="text-xs text-gray-400">{time}</p>
      </div>
    </div>
  );
};

export default AdminDashboardPage;