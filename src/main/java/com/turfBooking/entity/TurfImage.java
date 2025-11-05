package com.turfBooking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "turf_images")
public class TurfImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String imageUrl;

    private String imageName;

    private boolean isPrimary = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turf_id", nullable = false)
    private Turf turf;

    // Constructors
    public TurfImage() {}

    public TurfImage(String imageUrl, String imageName, boolean isPrimary, Turf turf) {
        this.imageUrl = imageUrl;
        this.imageName = imageName;
        this.isPrimary = isPrimary;
        this.turf = turf;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }

    public boolean isPrimary() { return isPrimary; }
    public void setPrimary(boolean primary) { isPrimary = primary; }

    public Turf getTurf() { return turf; }
    public void setTurf(Turf turf) { this.turf = turf; }
}