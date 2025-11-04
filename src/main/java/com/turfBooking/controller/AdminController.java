package com.turfBooking.controller;

// Im the admin
import com.turfBooking.dto.BookingResponseDTO;
import com.turfBooking.dto.TurfResponseDTO;
import com.turfBooking.dto.UserResponseDTO;
import com.turfBooking.service.interfaces.BookingService;
import com.turfBooking.service.interfaces.TurfService;
import com.turfBooking.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//
@RestController
@RequestMapping("/api/admin")
@CrossOrigin
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private TurfService turfService;

    @Autowired
    private BookingService bookingService;

    // User management
    @GetMapping("/users")
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    // Turf management
    @GetMapping("/turfs")
    public List<TurfResponseDTO> getAllTurfs() {
        return turfService.getAllTurfs();
    }

    @DeleteMapping("/turfs/{id}")
    public void deleteTurf(@PathVariable Long id) {
        turfService.deleteTurf(id);
    }

    // Booking management
    @GetMapping("/bookings")
    public List<BookingResponseDTO> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @DeleteMapping("/bookings/{id}")
    public void deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
    }
}