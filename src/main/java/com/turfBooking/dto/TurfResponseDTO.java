package com.turfBooking.dto;

import com.turfBooking.enums.SportType;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

public class TurfResponseDTO {

    private Long id;
    private String name;
    private String phone;
    private String location;
    private SportType type;
    private BigDecimal pricePerSlot;
    private String description;
    private LocalTime operatingStartTime;
    private LocalTime operatingEndTime;
    private Long ownerId;
    private String ownerName;
    private String ownerPhone;
    private int totalBookings;
    private int totalBlockedSlots;

    // NEW: Image fields
    private List<String> imageUrls;
    private String primaryImageUrl;

    // Constructors
    public TurfResponseDTO() {}

    public TurfResponseDTO(Long id, String name, String phone, String location, SportType type,
                           BigDecimal pricePerSlot, String description,
                           LocalTime operatingStartTime, LocalTime operatingEndTime,
                           Long ownerId, String ownerName, String ownerPhone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.location = location;
        this.type = type;
        this.pricePerSlot = pricePerSlot;
        this.description = description;
        this.operatingStartTime = operatingStartTime;
        this.operatingEndTime = operatingEndTime;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.ownerPhone = ownerPhone;
    }

    // Full constructor with images
    public TurfResponseDTO(Long id, String name, String phone, String location, SportType type,
                           BigDecimal pricePerSlot, String description,
                           LocalTime operatingStartTime, LocalTime operatingEndTime,
                           Long ownerId, String ownerName, String ownerPhone,
                           List<String> imageUrls, String primaryImageUrl) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.location = location;
        this.type = type;
        this.pricePerSlot = pricePerSlot;
        this.description = description;
        this.operatingStartTime = operatingStartTime;
        this.operatingEndTime = operatingEndTime;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.ownerPhone = ownerPhone;
        this.imageUrls = imageUrls;
        this.primaryImageUrl = primaryImageUrl;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public SportType getType() { return type; }
    public void setType(SportType type) { this.type = type; }

    public BigDecimal getPricePerSlot() { return pricePerSlot; }
    public void setPricePerSlot(BigDecimal pricePerSlot) { this.pricePerSlot = pricePerSlot; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalTime getOperatingStartTime() { return operatingStartTime; }
    public void setOperatingStartTime(LocalTime operatingStartTime) { this.operatingStartTime = operatingStartTime; }

    public LocalTime getOperatingEndTime() { return operatingEndTime; }
    public void setOperatingEndTime(LocalTime operatingEndTime) { this.operatingEndTime = operatingEndTime; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getOwnerPhone() { return ownerPhone; }
    public void setOwnerPhone(String ownerPhone) { this.ownerPhone = ownerPhone; }

    public int getTotalBookings() { return totalBookings; }
    public void setTotalBookings(int totalBookings) { this.totalBookings = totalBookings; }

    public int getTotalBlockedSlots() { return totalBlockedSlots; }
    public void setTotalBlockedSlots(int totalBlockedSlots) { this.totalBlockedSlots = totalBlockedSlots; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }

    public String getPrimaryImageUrl() { return primaryImageUrl; }
    public void setPrimaryImageUrl(String primaryImageUrl) { this.primaryImageUrl = primaryImageUrl; }
}