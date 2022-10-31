package com.andrianturcan.postcode.controller;


import com.andrianturcan.postcode.controller.dto.ResponseDto;
import com.andrianturcan.postcode.exceptions.UnprocessableException;
import com.andrianturcan.postcode.service.PostCodeService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/postcode")
@Slf4j
public class PostCodeController {

    @NonNull
    private PostCodeService postCodeService;

    @PostMapping()
    public ResponseDto postCodeApi(@RequestBody ResponseDto responseDto) throws UnprocessableException {
        log.info("Inside assign postCodeApi of PostCodeController");
        return postCodeService.processPayload(responseDto);
    }

    @ExceptionHandler(UnprocessableException.class)
    public ResponseEntity<String> handleNoSuchElementFoundException(UnprocessableException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Unprocessable Entity: " + exception.getMessage());
    }

}
