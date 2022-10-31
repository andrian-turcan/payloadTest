package com.andrianturcan.postcode.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ObjectListDto {

    private String string;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> list;

}
