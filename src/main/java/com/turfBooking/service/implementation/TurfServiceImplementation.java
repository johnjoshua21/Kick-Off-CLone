package com.turfBooking.service.implementation;

import com.turfBooking.dto.TurfRequestDTO;
import com.turfBooking.dto.TurfResponseDTO;
import com.turfBooking.dto.TurfUpdateDTO;
import com.turfBooking.dto.TurfSearchDTO;
import com.turfBooking.entity.Turf;
import com.turfBooking.entity.User;
import com.turfBooking.entity.Booking;
import com.turfBooking.entity.BlockedSlot;
import com.turfBooking.entity.TurfImage;
import com.turfBooking.enums.SportType;
import com.turfBooking.repository.TurfRepository;
import com.turfBooking.repository.UserRepository;
import com.turfBooking.repository.TurfImageRepository;
import com.turfBooking.service.interfaces.TurfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TurfServiceImplementation implements TurfService {

    @Autowired
    private TurfRepository turfRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TurfImageRepository turfImageRepository;

    @Override
    public TurfResponseDTO createTurf(TurfRequestDTO turfRequestDTO) {
        // Validate owner exists
        User owner = userRepository.findById(turfRequestDTO.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Owner not found with id: " + turfRequestDTO.getOwnerId()));

        // Validate operating hours
        if (!validateOperatingHours(turfRequestDTO.getOperatingStartTime(), turfRequestDTO.getOperatingEndTime())) {
            throw new RuntimeException("Invalid operating hours: start time must be before end time");
        }

        // Check if turf name already exists for this owner
        if (turfNameExistsForOwner(turfRequestDTO.getName(), turfRequestDTO.getOwnerId())) {
            throw new RuntimeException("Turf name already exists for this owner");
        }

        // Create new turf entity
        Turf turf = new Turf();
        turf.setName(turfRequestDTO.getName());
        turf.setPhone(turfRequestDTO.getPhone());
        turf.setLocation(turfRequestDTO.getLocation());
        turf.setType(turfRequestDTO.getType());
        turf.setPricePerSlot(turfRequestDTO.getPricePerSlot());
        turf.setDescription(turfRequestDTO.getDescription());
        turf.setOperatingStartTime(turfRequestDTO.getOperatingStartTime());
        turf.setOperatingEndTime(turfRequestDTO.getOperatingEndTime());
        turf.setOwner(owner);

        // Save turf first
        Turf savedTurf = turfRepository.save(turf);

        // Handle images - ADD THIS BLOCK
        if (turfRequestDTO.getImageUrls() != null && !turfRequestDTO.getImageUrls().isEmpty()) {
            for (int i = 0; i < turfRequestDTO.getImageUrls().size(); i++) {
                TurfImage image = new TurfImage();
                image.setImageUrl(turfRequestDTO.getImageUrls().get(i));
                image.setImageName("Image " + (i + 1));
                image.setPrimary(i == 0); // First image is primary
                image.setTurf(savedTurf);
                turfImageRepository.save(image);
            }

            // Refresh the turf to get the images
            savedTurf = turfRepository.findById(savedTurf.getId()).orElse(savedTurf);
        }

        return convertToDetailedResponseDTO(savedTurf);
    }

    @Override
    @Transactional(readOnly = true)
    public TurfResponseDTO getTurfById(Long id) {
        Turf turf = turfRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turf not found with id: " + id));

        return convertToDetailedResponseDTO(turf);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TurfResponseDTO> getAllTurfs() {
        return turfRepository.findAll()
                .stream()
                .map(this::convertToDetailedResponseDTO) // Changed from convertToResponseDTO
                .collect(Collectors.toList());
    }

    @Override
    public TurfResponseDTO updateTurf(Long id, TurfUpdateDTO turfUpdateDTO) {
        Turf turf = turfRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turf not found with id: " + id));

        // Update only non-null fields
        if (turfUpdateDTO.getName() != null && !turfUpdateDTO.getName().trim().isEmpty()) {
            // Check if new name already exists for this owner (excluding current turf)
            if (!turf.getName().equals(turfUpdateDTO.getName()) &&
                    turfNameExistsForOwner(turfUpdateDTO.getName(), turf.getOwner().getId())) {
                throw new RuntimeException("Turf name already exists for this owner");
            }
            turf.setName(turfUpdateDTO.getName());
        }

        if (turfUpdateDTO.getPhone() != null && !turfUpdateDTO.getPhone().trim().isEmpty()) {
            turf.setPhone(turfUpdateDTO.getPhone());
        }

        if (turfUpdateDTO.getLocation() != null && !turfUpdateDTO.getLocation().trim().isEmpty()) {
            turf.setLocation(turfUpdateDTO.getLocation());
        }

        if (turfUpdateDTO.getType() != null) {
            turf.setType(turfUpdateDTO.getType());
        }

        if (turfUpdateDTO.getPricePerSlot() != null) {
            turf.setPricePerSlot(turfUpdateDTO.getPricePerSlot());
        }

        if (turfUpdateDTO.getDescription() != null) {
            turf.setDescription(turfUpdateDTO.getDescription());
        }

        if (turfUpdateDTO.getOperatingStartTime() != null && turfUpdateDTO.getOperatingEndTime() != null) {
            if (!validateOperatingHours(turfUpdateDTO.getOperatingStartTime(), turfUpdateDTO.getOperatingEndTime())) {
                throw new RuntimeException("Invalid operating hours: start time must be before end time");
            }
            turf.setOperatingStartTime(turfUpdateDTO.getOperatingStartTime());
            turf.setOperatingEndTime(turfUpdateDTO.getOperatingEndTime());
        } else if (turfUpdateDTO.getOperatingStartTime() != null) {
            if (!validateOperatingHours(turfUpdateDTO.getOperatingStartTime(), turf.getOperatingEndTime())) {
                throw new RuntimeException("Invalid operating hours: start time must be before end time");
            }
            turf.setOperatingStartTime(turfUpdateDTO.getOperatingStartTime());
        } else if (turfUpdateDTO.getOperatingEndTime() != null) {
            if (!validateOperatingHours(turf.getOperatingStartTime(), turfUpdateDTO.getOperatingEndTime())) {
                throw new RuntimeException("Invalid operating hours: start time must be before end time");
            }
            turf.setOperatingEndTime(turfUpdateDTO.getOperatingEndTime());
        }

        // Handle image updates - ADD THIS BLOCK
        if (turfUpdateDTO.getImageUrls() != null) {
            // Delete existing images
            turfImageRepository.deleteByTurfId(id);

            // Add new images
            for (int i = 0; i < turfUpdateDTO.getImageUrls().size(); i++) {
                TurfImage image = new TurfImage();
                image.setImageUrl(turfUpdateDTO.getImageUrls().get(i));
                image.setImageName("Image " + (i + 1));
                image.setPrimary(i == 0);
                image.setTurf(turf);
                turfImageRepository.save(image);
            }
        }

        Turf updatedTurf = turfRepository.save(turf);
        // Refresh to get updated images
        updatedTurf = turfRepository.findById(updatedTurf.getId()).orElse(updatedTurf);

        return convertToDetailedResponseDTO(updatedTurf);
    }

    @Override
    public void deleteTurf(Long id) {
        if (!turfRepository.existsById(id)) {
            throw new RuntimeException("Turf not found with id: " + id);
        }
        // Images will be deleted automatically due to cascade
        turfRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TurfResponseDTO> getTurfsByOwnerId(Long ownerId) {
        return turfRepository.findByOwnerId(ownerId)
                .stream()
                .map(this::convertToDetailedResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TurfResponseDTO> getTurfsBySportType(SportType type) {
        return turfRepository.findByType(type)
                .stream()
                .map(this::convertToDetailedResponseDTO) // Changed
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TurfResponseDTO> searchTurfsByLocation(String location) {
        return turfRepository.findByLocationContainingIgnoreCase(location)
                .stream()
                .map(this::convertToDetailedResponseDTO) // Changed
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TurfResponseDTO> searchTurfsByName(String name) {
        return turfRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::convertToDetailedResponseDTO) // Changed
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TurfResponseDTO> getTurfsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return turfRepository.findByPricePerSlotBetween(minPrice, maxPrice)
                .stream()
                .map(this::convertToDetailedResponseDTO) // Changed
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TurfResponseDTO> searchTurfs(TurfSearchDTO searchDTO) {
        return turfRepository.searchTurfs(
                        searchDTO.getName(),
                        searchDTO.getLocation(),
                        searchDTO.getType(),
                        searchDTO.getMinPrice(),
                        searchDTO.getMaxPrice()
                ).stream()
                .map(this::convertToDetailedResponseDTO) // Changed
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocalTime> getAvailableTimeSlots(Long turfId, LocalDate date) {
        Turf turf = turfRepository.findById(turfId)
                .orElseThrow(() -> new RuntimeException("Turf not found with id: " + turfId));

        List<LocalTime> availableSlots = new ArrayList<>();
        LocalTime currentTime = turf.getOperatingStartTime();
        LocalTime endTime = turf.getOperatingEndTime();

        while (currentTime.isBefore(endTime)) {
            LocalTime slotEndTime = currentTime.plusHours(1);
            if (slotEndTime.isAfter(endTime)) {
                break;
            }

            if (isTimeSlotAvailable(turfId, date, currentTime, slotEndTime)) {
                availableSlots.add(currentTime);
            }

            currentTime = currentTime.plusHours(1);
        }

        return availableSlots;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTimeSlotAvailable(Long turfId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        Turf turf = turfRepository.findById(turfId)
                .orElseThrow(() -> new RuntimeException("Turf not found with id: " + turfId));

        if (startTime.isBefore(turf.getOperatingStartTime()) || endTime.isAfter(turf.getOperatingEndTime())) {
            return false;
        }

        if (turf.getBookings() != null) {
            for (Booking booking : turf.getBookings()) {
                if (booking.getBookingDate().equals(date)) {
                    if (!(endTime.isBefore(booking.getSlotStartTime()) || startTime.isAfter(booking.getSlotEndTime()))) {
                        return false;
                    }
                }
            }
        }

        if (turf.getBlockedSlots() != null) {
            for (BlockedSlot blockedSlot : turf.getBlockedSlots()) {
                if (blockedSlot.getBlockedDate().equals(date)) {
                    if (!(endTime.isBefore(blockedSlot.getStartTime()) || startTime.isAfter(blockedSlot.getEndTime()))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TurfResponseDTO> getTurfsOrderedByPopularity() {
        return turfRepository.findTurfsOrderedByBookingCount()
                .stream()
                .map(this::convertToDetailedResponseDTO) // Changed
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TurfResponseDTO> getAvailableTurfsOnDate(LocalDate date) {
        return turfRepository.findAvailableTurfsOnDate(date)
                .stream()
                .map(this::convertToDetailedResponseDTO) // Changed
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalTurfsCount() {
        return turfRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getTurfsCountBySportType(SportType type) {
        return turfRepository.countByType(type);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTurfsCountByOwner(Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found with id: " + ownerId));
        return turfRepository.countByOwner(owner);
    }

    @Override
    public boolean validateOperatingHours(LocalTime startTime, LocalTime endTime) {
        return startTime != null && endTime != null && startTime.isBefore(endTime);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean turfNameExistsForOwner(String name, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found with id: " + ownerId));
        return turfRepository.existsByNameAndOwner(name, owner);
    }

    // CORRECTED: This method now properly fetches and includes images
    private TurfResponseDTO convertToDetailedResponseDTO(Turf turf) {
        TurfResponseDTO responseDTO = new TurfResponseDTO(
                turf.getId(),
                turf.getName(),
                turf.getPhone(),
                turf.getLocation(),
                turf.getType(),
                turf.getPricePerSlot(),
                turf.getDescription(),
                turf.getOperatingStartTime(),
                turf.getOperatingEndTime(),
                turf.getOwner().getId(),
                turf.getOwner().getName(),
                turf.getOwner().getPhone()
        );

        // Set counts
        responseDTO.setTotalBookings(turf.getBookings() != null ? turf.getBookings().size() : 0);
        responseDTO.setTotalBlockedSlots(turf.getBlockedSlots() != null ? turf.getBlockedSlots().size() : 0);

        // CORRECTED: Fetch images from database and add to response
        List<TurfImage> images = turfImageRepository.findByTurfId(turf.getId());
        if (images != null && !images.isEmpty()) {
            List<String> imageUrls = images.stream()
                    .map(TurfImage::getImageUrl)
                    .collect(Collectors.toList());
            responseDTO.setImageUrls(imageUrls);

            // Set primary image
            images.stream()
                    .filter(TurfImage::isPrimary)
                    .findFirst()
                    .ifPresent(img -> responseDTO.setPrimaryImageUrl(img.getImageUrl()));
        }

        return responseDTO;
    }
}