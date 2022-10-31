package com.andrianturcan.postcode.service;


import com.andrianturcan.postcode.controller.dto.ResponseDto;
import com.andrianturcan.postcode.exceptions.UnprocessableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PostCodeServiceTest {

    private static final String STRING_FIELD_IS_MISSING_IN_MAIN_BODY = "string field is missing in main body";
    private static final String STRING_FIELD = "hello";
    private static final String STRINGINTEGER_FIELD = "123";
    private static final String ONE = "1";
    private static final String TWO = "2";
    private static final String STRINGINTEGER_FIELD_CANT_BE_CONVERTED_TO_INT_IN_MAIN_BODY = "stringinteger field cant be converted to int in main body";
    private PostCodeService postCodeService;

    @BeforeEach
    void setUp() {
        postCodeService = new PostCodeService();
    }

    @Test
    void shouldProcessString() throws UnprocessableException {
        ResponseDto actualResult = ResponseDto.builder()
                .string("  " + STRING_FIELD + "  ")
                .build();
        postCodeService.processPayload(actualResult);
        ResponseDto expectedResult = ResponseDto.builder()
                .string(STRING_FIELD)
                .build();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void shouldProcessStringInteger() throws UnprocessableException {
        ResponseDto actualResult = ResponseDto.builder()
                .string("  " + STRING_FIELD + "  ")
                .stringinteger(STRINGINTEGER_FIELD).build();
        postCodeService.processPayload(actualResult);
        ResponseDto expectedResult = ResponseDto.builder()
                .string(STRING_FIELD)
                .stringinteger(STRINGINTEGER_FIELD).build();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void shouldReverseAList() throws UnprocessableException {
        ResponseDto actualResult = ResponseDto.builder()
                .string("  " + STRING_FIELD + "  ")
                .list(Arrays.asList(ONE, TWO)).build();
        postCodeService.processPayload(actualResult);
        ResponseDto expectedResult = ResponseDto.builder()
                .string(STRING_FIELD)
                .list(Arrays.asList(TWO, ONE)).build();
        assertEquals(expectedResult, actualResult);
    }


    @Test
    public void shouldThrowStringMissingException() {
        UnprocessableException exception = assertThrows(UnprocessableException.class, () -> {
            postCodeService.processPayload(ResponseDto.builder().build());
        });
        assertEquals(STRING_FIELD_IS_MISSING_IN_MAIN_BODY, exception.getMessage());
    }

    @Test
    public void shouldThrowIntConvertException() {
        UnprocessableException exception = assertThrows(UnprocessableException.class, () -> {
            postCodeService.processPayload(ResponseDto.builder().string(STRING_FIELD).stringinteger(STRING_FIELD).build());
        });
        assertEquals(STRINGINTEGER_FIELD_CANT_BE_CONVERTED_TO_INT_IN_MAIN_BODY, exception.getMessage());
    }
}