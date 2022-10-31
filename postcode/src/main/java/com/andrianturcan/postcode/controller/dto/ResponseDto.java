package com.andrianturcan.postcode.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseDto {


    private String string;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String stringinteger;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer integer;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String optional;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> list;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("object")
    private ObjectDto objectDto;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("objectlist")
    private List<ObjectListDto> objectListDto;

}
