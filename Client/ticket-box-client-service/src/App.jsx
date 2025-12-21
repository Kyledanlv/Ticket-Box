// Client/ticket-box-client-service/src/App.jsx - UPDATED VERSION
import React, { Suspense } from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { Toaster } from "sonner";
import Layout from "./components/layout";
import AuthModal from "./components/authModal";

// --- Auth Stores ---
import { useAuthStore } from "./store/useAuthStore";

// --- Page Components ---
const HomePage = React.lazy(() => import("./pages/homePage"));
const EventDetailPage = React.lazy(() => import("./pages/eventDetailPage"));
const EventCreationPage = React.lazy(() => import("./pages/eventCreationPage"));
const MyEventsPage = React.lazy(() => import("./pages/myEventsPage"));
const AdminDashboardPage = React.lazy(() => import("./pages/adminDashboardPage"));

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 1000 * 60 * 5, // 5 minutes
      refetchOnWindowFocus: false,
    },
  },
});

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        {/* AuthModal is placed outside routes so it can overlay any page */}
        <AuthModal />

        {/* Toaster provides notifications for login success/failure */}
        <Toaster richColors position="top-right" />

        <Suspense fallback={<LoadingSpinner />}>
          <Routes>
            {/* All routes are nested under the main Layout (Header, etc.) */}
            <Route path="/" element={<Layout />}>
              {/* --- Public Routes --- */}
              <Route index element={<HomePage />} />
              <Route path="event/:eventId" element={<EventDetailPage />} />
              
              {/* --- Protected Routes --- */}
              <Route
                path="/create-event"
                element={
                  <RoleBasedRoute
                    roles={["ROLE_USER", "ROLE_ADMIN", "ROLE_APPROVER"]}
                  >
                    <EventCreationPage />
                  </RoleBasedRoute>
                }
              />

              {/* My Events Page - For Organizers */}
              <Route
                path="/my-events"
                element={
                  <RoleBasedRoute
                    roles={["ROLE_USER", "ROLE_ADMIN", "ROLE_APPROVER"]}
                  >
                    <MyEventsPage />
                  </RoleBasedRoute>
                }
              />

              {/* Admin Dashboard - Only for Admins */}
              <Route
                path="/admin-dashboard"
                element={
                  <RoleBasedRoute roles={["ROLE_ADMIN"]}>
                    <AdminDashboardPage />
                  </RoleBasedRoute>
                }
              />

              {/* --- Catch-all (404) Route --- */}
              <Route path="*" element={<Navigate to="/" replace />} />
            </Route>
          </Routes>
        </Suspense>
      </BrowserRouter>
    </QueryClientProvider>
  );
}

// --- Helper Components ---

/**
 * A simple loading spinner component for React.Suspense
 */
const LoadingSpinner = () => (
  <div className="flex h-screen w-full items-center justify-center bg-gray-900">
    <div className="h-16 w-16 animate-spin rounded-full border-4 border-dashed border-blue-500"></div>
  </div>
);

/**
 * A component to protect routes based on user roles.
 * It checks the auth state from Zustand.
 * @param {{ children: React.ReactNode, roles: string[] }} props
 */
const RoleBasedRoute = ({ children, roles }) => {
  const { user } = useAuthStore();

  // Check if user is logged in
  const isAuth = !!user;

  // Check if user has one of the required roles
  const hasRole =
    isAuth && user.roles.some((role) => roles.includes(role.name));

  if (!isAuth || !hasRole) {
    console.warn("Unauthorized access attempt to a protected route.");
    return <Navigate to="/" replace />;
  }

  return children;
};