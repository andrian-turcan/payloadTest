package com.andrianturcan.postcode.service;


import com.andrianturcan.postcode.controller.dto.ObjectDto;
import com.andrianturcan.postcode.controller.dto.ObjectListDto;
import com.andrianturcan.postcode.controller.dto.ResponseDto;
import com.andrianturcan.postcode.exceptions.UnprocessableException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class PostCodeService {

    private static final String MAIN_BODY = "main body";
    public static final String OBJECT_BODY = "object body";
    public static final String OBJECTLIST_BODY = "objectlist body";

    public ResponseDto processPayload(ResponseDto responseDto) throws UnprocessableException {

        responseDto.setString(processString(responseDto.getString(), MAIN_BODY));
        processStringInteger(responseDto.getStringinteger(), MAIN_BODY);
        processList(responseDto.getList());
        processObject(responseDto);
        processObjectList(responseDto);

        return responseDto;
    }

    private String processString(String string, String path) throws UnprocessableException {
        if (Objects.isNull(string))
            throw new UnprocessableException("string field is missing in " + path);
        return string.trim();
    }

    private void processStringInteger(String stringToInt, String path) throws UnprocessableException {
        if (Objects.isNull(stringToInt))
            return;
        try {
            Integer.parseInt(stringToInt);
        } catch (NumberFormatException ex) {
            throw new UnprocessableException("stringinteger field cant be converted to int in " + path);
        }

    }

    private void processList(List<String> list) {
        if (Objects.isNull(list))
            return;
        Collections.reverse(list);
    }

    private void processObject(ResponseDto responseDto) throws UnprocessableException {
        ObjectDto objectDto = responseDto.getObjectDto();
        if (Objects.isNull(objectDto))
            return;
        objectDto.setString(processString(objectDto.getString(), OBJECT_BODY));
        processStringInteger(objectDto.getStringinteger(), OBJECT_BODY);
    }

    private void processObjectList(ResponseDto responseDto) throws UnprocessableException {
        List<ObjectListDto> objectListDto = responseDto.getObjectListDto();
        if (Objects.isNull(objectListDto))
            return;

        for (ObjectListDto dto : objectListDto) {
            dto.setString(processString(dto.getString(), OBJECTLIST_BODY));
            processList(dto.getList());
        }
    }
}
