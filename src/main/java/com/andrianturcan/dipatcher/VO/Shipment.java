package com.andrianturcan.dipatcher.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Shipment {
    private String name;
    private double weight;
}
