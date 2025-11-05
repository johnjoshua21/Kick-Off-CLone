package com.turfBooking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import com.turfBooking.enums.SportType;

@Entity
@Table(name = "turfs")
public class Turf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Turf name is required")
    private String name;

    @NotBlank(message = "Phone number is required")
    @Size(min = 10, max = 14)
    private String phone;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Sport type is required")
    @Enumerated(EnumType.STRING)
    private SportType type;

    @NotNull(message = "Price per slot is required")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal pricePerSlot;

    private String description;

    @NotNull(message = "Operating start time is required")
    private LocalTime operatingStartTime;

    @NotNull(message = "Operating end time is required")
    private LocalTime operatingEndTime;

    // Owner relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // Bookings
    @OneToMany(mappedBy = "turf", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;

    // Blocked Slots
    @OneToMany(mappedBy = "turf", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BlockedSlot> blockedSlots;

    // NEW: Images relationship
    @OneToMany(mappedBy = "turf", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<TurfImage> images = new ArrayList<>();

    // Constructors
    public Turf() {}

    public Turf(String name, String phone, String location, SportType type,
                BigDecimal pricePerSlot, String description,
                LocalTime operatingStartTime, LocalTime operatingEndTime, User owner) {
        this.name = name;
        this.phone = phone;
        this.location = location;
        this.type = type;
        this.pricePerSlot = pricePerSlot;
        this.description = description;
        this.operatingStartTime = operatingStartTime;
        this.operatingEndTime = operatingEndTime;
        this.owner = owner;
    }

    // Full constructor with images
    public Turf(String name, String phone, String location, SportType type,
                BigDecimal pricePerSlot, String description,
                LocalTime operatingStartTime, LocalTime operatingEndTime,
                User owner, List<TurfImage> images) {
        this.name = name;
        this.phone = phone;
        this.location = location;
        this.type = type;
        this.pricePerSlot = pricePerSlot;
        this.description = description;
        this.operatingStartTime = operatingStartTime;
        this.operatingEndTime = operatingEndTime;
        this.owner = owner;
        this.images = images != null ? images : new ArrayList<>();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public SportType getType() {
        return type;
    }

    public void setType(SportType type) {
        this.type = type;
    }

    public BigDecimal getPricePerSlot() {
        return pricePerSlot;
    }

    public void setPricePerSlot(BigDecimal pricePerSlot) {
        this.pricePerSlot = pricePerSlot;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalTime getOperatingStartTime() {
        return operatingStartTime;
    }

    public void setOperatingStartTime(LocalTime operatingStartTime) {
        this.operatingStartTime = operatingStartTime;
    }

    public LocalTime getOperatingEndTime() {
        return operatingEndTime;
    }

    public void setOperatingEndTime(LocalTime operatingEndTime) {
        this.operatingEndTime = operatingEndTime;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public List<BlockedSlot> getBlockedSlots() {
        return blockedSlots;
    }

    public void setBlockedSlots(List<BlockedSlot> blockedSlots) {
        this.blockedSlots = blockedSlots;
    }

    // NEW: Images getter and setter
    public List<TurfImage> getImages() {
        return images;
    }

    public void setImages(List<TurfImage> images) {
        this.images = images;
    }

    // Helper method to add image
    public void addImage(TurfImage image) {
        images.add(image);
        image.setTurf(this);
    }

    // Helper method to remove image
    public void removeImage(TurfImage image) {
        images.remove(image);
        image.setTurf(null);
    }
}