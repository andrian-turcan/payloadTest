package com.andrianturcan.dipatcher.controller.dto;

import com.andrianturcan.dipatcher.VO.Shipment;
import com.andrianturcan.dipatcher.VO.Tariff;
import com.andrianturcan.dipatcher.VO.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DispatcherDto {
   private String shipmentName;
   private String vehicleName;
   private String tariffName;
   private Double totalCost;
}
