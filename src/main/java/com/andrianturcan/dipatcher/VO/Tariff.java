package com.andrianturcan.dipatcher.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Tariff {
    private String name;
    private double rate;
    private double discount;
    private double applicableFrom;
    private double applicableTo;
}
