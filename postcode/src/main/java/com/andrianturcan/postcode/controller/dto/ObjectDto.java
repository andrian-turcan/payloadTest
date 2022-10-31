package com.andrianturcan.postcode.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ObjectDto {

    private String string;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String stringinteger;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer integer;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String optional;

}
