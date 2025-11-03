package com.turfBooking.controller;

import com.turfBooking.dto.BookingResponseDTO;
import com.turfBooking.dto.TurfResponseDTO;
import com.turfBooking.dto.UserResponseDTO;
import com.turfBooking.security.CustomUserDetails;
import com.turfBooking.service.interfaces.BookingService;
import com.turfBooking.service.interfaces.TurfService;
import com.turfBooking.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private TurfService turfService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    // Universal dashboard endpoint - returns data based on user role
    @GetMapping
    public ResponseEntity<?> getDashboardData() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            String role = userDetails.getRole();
            Long userId = userDetails.getUserId();

            Map<String, Object> dashboardData = new HashMap<>();
            dashboardData.put("userInfo", userService.getUserById(userId));
            dashboardData.put("role", role);

            switch (role) {
                case "USER":
                    return ResponseEntity.ok(getUserDashboardData(userId, dashboardData));

                case "TURF_OWNER":
                    return ResponseEntity.ok(getTurfOwnerDashboardData(userId, dashboardData));

                case "ADMIN":
                    return ResponseEntity.ok(getAdminDashboardData(dashboardData));

                default:
                    return ResponseEntity.badRequest().body("Invalid user role");
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to load dashboard: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // User Dashboard Data
    private Map<String, Object> getUserDashboardData(Long userId, Map<String, Object> dashboardData) {
        // All available turfs for booking
        List<TurfResponseDTO> allTurfs = turfService.getAllTurfs();
        dashboardData.put("availableTurfs", allTurfs);

        // User's upcoming bookings
        List<BookingResponseDTO> upcomingBookings = bookingService.getUpcomingBookingsForUser(userId);
        dashboardData.put("upcomingBookings", upcomingBookings);

        // User's all bookings (for history)
        List<BookingResponseDTO> allBookings = bookingService.getBookingsByUserId(userId);
        dashboardData.put("bookingHistory", allBookings);

        // Quick stats
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBookings", allBookings.size());
        stats.put("upcomingBookingsCount", upcomingBookings.size());
        stats.put("availableTurfsCount", allTurfs.size());
        dashboardData.put("stats", stats);

        dashboardData.put("dashboardType", "USER_DASHBOARD");
        return dashboardData;
    }

    // Turf Owner Dashboard Data
    private Map<String, Object> getTurfOwnerDashboardData(Long ownerId, Map<String, Object> dashboardData) {
        // Owner's turfs
        List<TurfResponseDTO> ownedTurfs = turfService.getTurfsByOwnerId(ownerId);
        dashboardData.put("ownedTurfs", ownedTurfs);

        // Bookings for owner's turfs
        List<BookingResponseDTO> allBookings = bookingService.getBookingsForTurfOwner(ownerId);
        dashboardData.put("allBookings", allBookings);

        // Upcoming bookings for owner's turfs
        List<BookingResponseDTO> upcomingBookings = bookingService.getUpcomingBookingsForTurfOwner(ownerId);
        dashboardData.put("upcomingBookings", upcomingBookings);

        // Quick stats
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTurfs", ownedTurfs.size());
        stats.put("totalBookings", allBookings.size());
        stats.put("upcomingBookingsCount", upcomingBookings.size());

        // Revenue calculation (if you have pricing logic)
        // BigDecimal totalRevenue = calculateTotalRevenue(allBookings);
        // stats.put("totalRevenue", totalRevenue);

        dashboardData.put("stats", stats);
        dashboardData.put("dashboardType", "TURF_OWNER_DASHBOARD");
        return dashboardData;
    }

    // Admin Dashboard Data
    private Map<String, Object> getAdminDashboardData(Map<String, Object> dashboardData) {
        // All system data
        List<TurfResponseDTO> allTurfs = turfService.getAllTurfs();
        List<BookingResponseDTO> allBookings = bookingService.getAllBookings();
        List<UserResponseDTO> allUsers = userService.getAllUsers();

        dashboardData.put("allTurfs", allTurfs);
        dashboardData.put("allBookings", allBookings);
        dashboardData.put("allUsers", allUsers);

        // System-wide stats
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", allUsers.size());
        stats.put("totalTurfs", allTurfs.size());
        stats.put("totalBookings", allBookings.size());

        // Count by roles
        long regularUsers = allUsers.stream().filter(u -> "USER".equals(u.getRole())).count();
        long turfOwners = allUsers.stream().filter(u -> "TURF_OWNER".equals(u.getRole())).count();
        long admins = allUsers.stream().filter(u -> "ADMIN".equals(u.getRole())).count();

        stats.put("regularUsers", regularUsers);
        stats.put("turfOwners", turfOwners);
        stats.put("admins", admins);

        dashboardData.put("stats", stats);
        dashboardData.put("dashboardType", "ADMIN_DASHBOARD");
        return dashboardData;
    }

    // Specific endpoint for users to get all turfs (alternative approach)
    @GetMapping("/turfs")
    public ResponseEntity<List<TurfResponseDTO>> getAllTurfsForUser() {
        List<TurfResponseDTO> turfs = turfService.getAllTurfs();
        return ResponseEntity.ok(turfs);
    }

    // Specific endpoint for turf owners to get their turfs (alternative approach)
    @GetMapping("/my-turfs")
    public ResponseEntity<?> getMyTurfs() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long ownerId = userDetails.getUserId();

            List<TurfResponseDTO> ownedTurfs = turfService.getTurfsByOwnerId(ownerId);
            return ResponseEntity.ok(ownedTurfs);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to load turfs: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Get dashboard stats only
    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            String role = userDetails.getRole();
            Long userId = userDetails.getUserId();

            Map<String, Object> stats = new HashMap<>();

            switch (role) {
                case "USER":
                    long userBookings = bookingService.getBookingsCountByUser(userId);
                    long upcomingBookings = bookingService.getUpcomingBookingsForUser(userId).size();
                    stats.put("totalBookings", userBookings);
                    stats.put("upcomingBookings", upcomingBookings);
                    break;

                case "TURF_OWNER":
                    long ownedTurfs = turfService.getTurfsCountByOwner(userId);
                    long ownerBookings = bookingService.getBookingsForTurfOwner(userId).size();
                    stats.put("totalTurfs", ownedTurfs);
                    stats.put("totalBookings", ownerBookings);
                    break;

                case "ADMIN":
                    stats.put("totalUsers", userService.getTotalUsersCount());
                    stats.put("totalTurfs", turfService.getTotalTurfsCount());
                    stats.put("totalBookings", bookingService.getTotalBookingsCount());
                    break;
            }

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to load stats: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}