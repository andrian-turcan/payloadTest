package com.andrianturcan.dipatcher.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Dispatcher {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String shipmentName;
    private String vehicleName;
    private double vehicleWeight;

}
