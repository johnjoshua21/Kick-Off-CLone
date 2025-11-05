package com.turfBooking.repository;

import com.turfBooking.entity.TurfImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TurfImageRepository extends JpaRepository<TurfImage, Long> {
    List<TurfImage> findByTurfId(Long turfId);
    void deleteByTurfId(Long turfId);
}