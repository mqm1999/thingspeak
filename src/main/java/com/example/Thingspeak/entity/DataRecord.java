package com.example.Thingspeak.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.SEQUENCE;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "data_record")
public class DataRecord implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "data_record_id_seq", sequenceName = "data_record_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = SEQUENCE, generator = "data_record_id_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "air_temperature")
    private Double airTemperature;

    @Column(name = "air_humidity")
    private Double airHumidity;

    @Column(name = "light_intensity")
    private Double lightIntensity;

    @Column(name = "soil_moisture")
    private Double soilMoisture;

    @Column(name = "lamp_status")
    private Boolean lampStatus;

    @Column(name = "pump_status")
    private Boolean pumpStatus;

    @Column(name = "created_time", unique = true)
    private LocalDateTime createdTime;

    @Column(name = "updated_by")
    private String updatedBy;
}
