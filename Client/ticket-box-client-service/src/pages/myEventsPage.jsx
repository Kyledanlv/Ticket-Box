// Client/ticket-box-client-service/src/pages/myEventsPage.jsx
import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuthStore } from '../store/useAuthStore';
import { useEventsByCreatorId } from '../hooks/useEventHook';
import { AnalyticsProvider, KPIOverviewBoard, RevenueBoard } from '../analytics';
import { 
  Calendar, 
  Plus, 
  TrendingUp, 
  DollarSign, 
  Eye,
  Edit,
  Trash2,
  CheckCircle,
  XCircle,
  Clock,
  Filter,
  Download,
  BarChart3,
  ChevronLeft,
  ChevronRight
} from 'lucide-react';
import EventCard from '../components/eventCard';

const MyEventsPage = () => {
  const { user } = useAuthStore();
  const [statusFilter, setStatusFilter] = useState('all');
  const [timeRange, setTimeRange] = useState('30d');
  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 10;
  
  // Fetch events created by this user (paginated for display)
  const { data: eventsData, isLoading, isError } = useEventsByCreatorId(user?.id, currentPage, pageSize);
  
  // Fetch ALL events for statistics (using a large page size)
  const { data: allEventsData } = useEventsByCreatorId(user?.id, 1, 1000);
  
  // Debug logging
  React.useEffect(() => {
    if (eventsData) {
      console.log('📊 My Events Data:', {
        pageNo: eventsData.pageNo,
        pageSize: eventsData.pageSize,
        totalPages: eventsData.totalPages,
        totalElements: eventsData.totalElements,
        eventsCount: eventsData.pageContent?.length
      });
    }
  }, [eventsData]);
  
  if (!user) {
    return (
      <div className="container mx-auto max-w-7xl px-4 py-8">
        <div className="rounded-lg bg-gray-800 p-8 text-center">
          <h2 className="text-2xl font-bold text-white">Please login to view your events</h2>
        </div>
      </div>
    );
  }

  const events = eventsData?.pageContent || [];
  const totalPages = eventsData?.totalPages || 1;
  const totalElements = eventsData?.totalElements || 0;
  
  // Get all events for statistics calculation
  const allEvents = allEventsData?.pageContent || [];

  // Filter events by status (for display)
  const filteredEvents = statusFilter === 'all' 
    ? events 
    : events.filter(e => {
        if (statusFilter === 'upcoming') return e.status === 2;
        if (statusFilter === 'running') return e.status === 3;
        if (statusFilter === 'ended') return e.status === 4;
        if (statusFilter === 'pending') return e.status === 1;
        return true;
      });

  // Stats calculation from ALL events (not just current page)
  const stats = {
    total: allEvents.length || totalElements,
    upcoming: allEvents.filter(e => e.status === 2).length,
    running: allEvents.filter(e => e.status === 3).length,
    ended: allEvents.filter(e => e.status === 4).length,
    pending: allEvents.filter(e => e.status === 1).length,
  };

  return (
    <div className="container mx-auto max-w-7xl px-4 py-8">
      {/* Header Section */}
      <div className="mb-8 flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
        <div>
          <h1 className="text-3xl font-bold text-white">My Events Dashboard</h1>
          <p className="mt-2 text-gray-400">Manage your events and view analytics</p>
        </div>
        <div className="flex gap-3">
          <button className="flex items-center gap-2 rounded-lg bg-gray-700 px-4 py-2 text-sm font-semibold text-white hover:bg-gray-600">
            <Download className="h-4 w-4" />
            Export Report
          </button>
          <Link
            to="/create-event"
            className="flex items-center gap-2 rounded-lg bg-blue-600 px-6 py-2 font-semibold text-white hover:bg-blue-700"
          >
            <Plus className="h-5 w-5" />
            Create Event
          </Link>
        </div>
      </div>

      {/* Quick Stats Cards */}
      <div className="mb-8 grid grid-cols-2 gap-4 md:grid-cols-5">
        <div className="rounded-lg bg-gray-800 p-4">
          <p className="text-sm text-gray-400">Total Events</p>
          <p className="mt-1 text-2xl font-bold text-white">{stats.total}</p>
        </div>
        <div className="rounded-lg bg-blue-900/30 p-4">
          <p className="text-sm text-blue-400">Pending</p>
          <p className="mt-1 text-2xl font-bold text-white">{stats.pending}</p>
        </div>
        <div className="rounded-lg bg-yellow-900/30 p-4">
          <p className="text-sm text-yellow-400">Upcoming</p>
          <p className="mt-1 text-2xl font-bold text-white">{stats.upcoming}</p>
        </div>
        <div className="rounded-lg bg-green-900/30 p-4">
          <p className="text-sm text-green-400">Running</p>
          <p className="mt-1 text-2xl font-bold text-white">{stats.running}</p>
        </div>
        <div className="rounded-lg bg-gray-700 p-4">
          <p className="text-sm text-gray-400">Ended</p>
          <p className="mt-1 text-2xl font-bold text-white">{stats.ended}</p>
        </div>
      </div>

      {/* Analytics Section - Revenue & Top Events */}
      <AnalyticsProvider 
        role="ORGANIZER" 
        organizerId={user.id?.toString()}
      >
        <div className="mb-8 space-y-6">
          {/* KPI Overview */}
          <div className="rounded-xl bg-gradient-to-br from-blue-900/20 to-purple-900/20 p-1">
            <div className="rounded-lg bg-gray-900 p-6">
              <div className="mb-4 flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <TrendingUp className="h-6 w-6 text-blue-400" />
                  <h2 className="text-xl font-semibold text-white">Performance Overview</h2>
                </div>
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
              </div>
              <KPIOverviewBoard />
            </div>
          </div>
          
          {/* Revenue Analytics & Top Events */}
          <div className="rounded-xl bg-gradient-to-br from-green-900/20 to-blue-900/20 p-1">
            <div className="rounded-lg bg-gray-900 p-6">
              <div className="mb-4 flex items-center gap-2">
                <BarChart3 className="h-6 w-6 text-green-400" />
                <h2 className="text-xl font-semibold text-white">Revenue & Performance Analytics</h2>
              </div>
              <RevenueBoard />
            </div>
          </div>
        </div>
      </AnalyticsProvider>

      {/* Event Management Section */}
      <div className="rounded-lg bg-gray-800 p-6">
        <div className="mb-6 flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <h2 className="flex items-center gap-2 text-2xl font-bold text-white">
            <Calendar className="h-6 w-6 text-blue-400" />
            My Events ({filteredEvents.length})
          </h2>
          
          {/* Status Filter */}
          <div className="flex items-center gap-2">
            <Filter className="h-5 w-5 text-gray-400" />
            <select 
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="rounded-lg bg-gray-700 px-4 py-2 text-sm text-white"
            >
              <option value="all">All Status</option>
              <option value="pending">Pending Approval</option>
              <option value="upcoming">Upcoming</option>
              <option value="running">Running</option>
              <option value="ended">Ended</option>
            </select>
          </div>
        </div>

        {isLoading ? (
          <div className="flex h-48 items-center justify-center">
            <div className="h-12 w-12 animate-spin rounded-full border-4 border-blue-500 border-t-transparent"></div>
          </div>
        ) : isError ? (
          <div className="rounded-lg bg-red-900/20 p-4 text-center text-red-400">
            Failed to load events
          </div>
        ) : filteredEvents.length > 0 ? (
          <div className="space-y-4">
            {filteredEvents.map((event) => (
              <EventRow key={event.id} event={event} />
            ))}
            
            {/* Pagination */}
            {totalPages > 1 && (
              <div className="mt-6 flex items-center justify-between border-t border-gray-700 pt-4">
                <div className="text-sm text-gray-400">
                  Showing {((currentPage - 1) * pageSize) + 1} - {Math.min(currentPage * pageSize, totalElements)} of {totalElements} events
                </div>
                
                <div className="flex items-center gap-2">
                  <button
                    onClick={() => setCurrentPage(prev => Math.max(1, prev - 1))}
                    disabled={currentPage === 1}
                    className="flex items-center gap-1 rounded-lg bg-gray-700 px-3 py-2 text-sm font-medium text-white hover:bg-gray-600 disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    <ChevronLeft className="h-4 w-4" />
                    Previous
                  </button>
                  
                  <div className="flex items-center gap-1">
                    {[...Array(totalPages)].map((_, idx) => {
                      const pageNum = idx + 1;
                      // Show first page, last page, current page, and pages around current
                      if (
                        pageNum === 1 ||
                        pageNum === totalPages ||
                        (pageNum >= currentPage - 1 && pageNum <= currentPage + 1)
                      ) {
                        return (
                          <button
                            key={pageNum}
                            onClick={() => setCurrentPage(pageNum)}
                            className={`min-w-[40px] rounded-lg px-3 py-2 text-sm font-medium ${
                              currentPage === pageNum
                                ? 'bg-blue-600 text-white'
                                : 'bg-gray-700 text-gray-300 hover:bg-gray-600'
                            }`}
                          >
                            {pageNum}
                          </button>
                        );
                      } else if (
                        pageNum === currentPage - 2 ||
                        pageNum === currentPage + 2
                      ) {
                        return <span key={pageNum} className="px-2 text-gray-500">...</span>;
                      }
                      return null;
                    })}
                  </div>
                  
                  <button
                    onClick={() => setCurrentPage(prev => Math.min(totalPages, prev + 1))}
                    disabled={currentPage === totalPages}
                    className="flex items-center gap-1 rounded-lg bg-gray-700 px-3 py-2 text-sm font-medium text-white hover:bg-gray-600 disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    Next
                    <ChevronRight className="h-4 w-4" />
                  </button>
                </div>
              </div>
            )}
          </div>
        ) : (
          <div className="rounded-lg bg-gray-700 p-8 text-center">
            <Calendar className="mx-auto mb-4 h-16 w-16 text-gray-500" />
            <h3 className="mb-2 text-xl font-semibold text-white">
              {statusFilter === 'all' ? 'No events yet' : `No ${statusFilter} events`}
            </h3>
            <p className="mb-4 text-gray-400">
              {statusFilter === 'all' 
                ? 'Create your first event to get started' 
                : 'Try changing the filter or create a new event'}
            </p>
            <Link
              to="/create-event"
              className="inline-flex items-center gap-2 rounded-full bg-blue-600 px-6 py-2 font-semibold text-white hover:bg-blue-700"
            >
              <Plus className="h-4 w-4" />
              Create Event
            </Link>
          </div>
        )}
      </div>
    </div>
  );
};

// Event Row Component with Actions
const EventRow = ({ event }) => {
  const getStatusBadge = (status) => {
    const badges = {
      1: { text: 'Pending', class: 'bg-blue-900/50 text-blue-400', icon: Clock },
      2: { text: 'Upcoming', class: 'bg-yellow-900/50 text-yellow-400', icon: Calendar },
      3: { text: 'Running', class: 'bg-green-900/50 text-green-400', icon: CheckCircle },
      4: { text: 'Ended', class: 'bg-gray-700 text-gray-400', icon: XCircle },
      '-1': { text: 'Declined', class: 'bg-red-900/50 text-red-400', icon: XCircle },
      0: { text: 'Canceled', class: 'bg-red-900/50 text-red-400', icon: XCircle },
    };
    const badge = badges[status] || badges[1];
    const Icon = badge.icon;
    return (
      <span className={`flex items-center gap-1 rounded-full px-3 py-1 text-xs font-semibold ${badge.class}`}>
        <Icon className="h-3 w-3" />
        {badge.text}
      </span>
    );
  };

  return (
    <div className="group rounded-lg border border-gray-700 bg-gray-800/50 p-4 transition-all hover:border-blue-500 hover:bg-gray-800">
      <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
        <div className="flex items-center gap-4">
          <img 
            src={event.img?.url || 'https://placehold.co/100x100/111/fff?text=Event'} 
            alt={event.name}
            className="h-16 w-16 rounded-lg object-cover"
          />
          <div>
            <h3 className="text-lg font-semibold text-white">{event.name}</h3>
            <p className="text-sm text-gray-400">
              {new Date(event.startDate).toLocaleDateString()} - {new Date(event.endDate).toLocaleDateString()}
            </p>
            <div className="mt-1">{getStatusBadge(event.status)}</div>
          </div>
        </div>
        
        <div className="flex items-center gap-2">
          <Link
            to={`/event/${event.id}`}
            className="flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-700"
          >
            <Eye className="h-4 w-4" />
            View
          </Link>
          {event.status === 1 && (
            <button className="flex items-center gap-2 rounded-lg bg-gray-700 px-4 py-2 text-sm font-semibold text-white hover:bg-gray-600">
              <Edit className="h-4 w-4" />
              Edit
            </button>
          )}
          <button className="flex items-center gap-2 rounded-lg bg-red-900/50 px-4 py-2 text-sm font-semibold text-red-400 hover:bg-red-900">
            <Trash2 className="h-4 w-4" />
            Delete
          </button>
        </div>
      </div>
    </div>
  );
};

export default MyEventsPage;