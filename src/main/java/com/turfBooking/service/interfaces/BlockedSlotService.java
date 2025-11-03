// BlockedSlotService.java (Interface)
package com.turfBooking.service.interfaces;

import com.turfBooking.dto.BlockedSlotRequestDTO;
import com.turfBooking.dto.BlockedSlotResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface BlockedSlotService {

    BlockedSlotResponseDTO createBlockedSlot(BlockedSlotRequestDTO requestDTO);
    List<BlockedSlotResponseDTO> getAllBlockedSlots();
    BlockedSlotResponseDTO getBlockedSlotById(Long id);
    void deleteBlockedSlot(Long id);


    List<BlockedSlotResponseDTO> getBlockedSlotsByTurfId(Long turfId);
    List<BlockedSlotResponseDTO> getBlockedSlotsByTurfIdAndDate(Long turfId, LocalDate date);
    List<BlockedSlotResponseDTO> getBlockedSlotsByDate(LocalDate date);
    List<BlockedSlotResponseDTO> getBlockedSlotsByDateRange(LocalDate startDate, LocalDate endDate);
    List<BlockedSlotResponseDTO> getBlockedSlotsByTurfOwnerId(Long ownerId);
    List<BlockedSlotResponseDTO> getFutureBlockedSlotsByTurfId(Long turfId);

    void cleanupOldBlockedSlots(LocalDate beforeDate);
}
