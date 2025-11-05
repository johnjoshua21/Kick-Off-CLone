package com.turfBooking.dto;

import com.turfBooking.enums.SportType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

public class TurfUpdateDTO {

    @Size(max = 100, message = "Turf name cannot exceed 100 characters")
    private String name;

    @Size(min = 10, max = 14, message = "Phone number must be between 10 and 14 characters")
    private String phone;

    @Size(max = 200, message = "Location cannot exceed 200 characters")
    private String location;

    private SportType type;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal pricePerSlot;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    private LocalTime operatingStartTime;

    private LocalTime operatingEndTime;

    // NEW: Image URLs
    private List<String> imageUrls;

    // Constructors
    public TurfUpdateDTO() {}

    // Getters and Setters
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

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
}