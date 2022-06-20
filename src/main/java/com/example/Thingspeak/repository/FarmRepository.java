package com.example.Thingspeak.repository;

import com.example.Thingspeak.entity.DataRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FarmRepository extends JpaRepository<DataRecord, Long> {
    DataRecord findFirstByOrderByCreatedTimeDesc();
}
