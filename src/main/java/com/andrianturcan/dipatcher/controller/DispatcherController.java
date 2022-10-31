package com.andrianturcan.dipatcher.controller;

import com.andrianturcan.dipatcher.VO.Shipment;
import com.andrianturcan.dipatcher.VO.Tariff;
import com.andrianturcan.dipatcher.VO.Vehicle;
import com.andrianturcan.dipatcher.controller.dto.DispatcherDto;
import com.andrianturcan.dipatcher.exceptions.*;
import com.andrianturcan.dipatcher.service.DispatcherService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dispatcher")
@Slf4j
public class DispatcherController {

    @NonNull
    private DispatcherService dispatcherService;

    @NonNull
    private RestTemplate shipmentRestTemplate;

    @NonNull
    private RestTemplate vehicleRestTemplate;

    @NonNull
    private RestTemplate tariffRestTemplate;

    @PostMapping("/assign")
    public DispatcherDto assign(@RequestParam String shipmentName, @RequestParam(required = false) String vehicleName, @RequestParam(required = false) String tariffName) {
        log.info("Inside assign method of DispatcherController");
        return dispatcherService.assign(getShipment(shipmentName), getVehicle(vehicleName), getTariff(tariffName));
    }

    @GetMapping("/mostExpensiveShipment")
    public DispatcherDto findMostExpensiveShipment() {
        log.info("Inside findMostExpensiveShipment method of DispatcherController");
        return dispatcherService.getMostExpensiveShipment();
    }

    @ExceptionHandler({CostCannotBeCalculatedException.class})
    public String costCannotBeCalculatedError() {
        return "Cost could not be calculated tariff is not applicable for this vehicle";
    }

    @ExceptionHandler({NotEnaughtCapacityException.class})
    public String notEnaughtCapacityErrow() {
        return "There is no capacity in this vehicle for this shipment";
    }

    @ExceptionHandler({ShipmentAlreadyAssignedException.class})
    public String shipmentAlreadyAssignedError() {
        return "This shipment is already assigned to a vehicle";
    }

    @ExceptionHandler({ShipmentNotFoundException.class})
    public String shipmentNotFoundError() {
        return "Shipment not found";
    }

    @ExceptionHandler({TariffNotFoundException.class})
    public String tariffNotFoundError() {
        return "Tariff not found";
    }

    @ExceptionHandler({VehicleNotFoundException.class})
    public String vehicleNotFoundError() {
        return "Vehicle not found";
    }

    @ExceptionHandler({VehicleAlreadyAssignedException.class})
    public String vehicleAlreadyAssignedError() {
        return "Vehicle already have a shipment";
    }

    @ExceptionHandler({ShipmentWasNotAssignedException.class})
    public String shipmentWasNotAssignedError() {
        return "Shipment was not assigned to a vehicle";
    }

    private Tariff getTariff(String tariffName) {
        if (Objects.isNull(tariffName))
            return new Tariff();
        try {
            return tariffRestTemplate.getForObject("/tariffs/" + tariffName, Tariff.class);
        } catch (HttpStatusCodeException e) {
            return null;
        }
    }

    private Vehicle getVehicle(String vehicleName) {
        if (Objects.isNull(vehicleName))
            return new Vehicle();
        try {
            return vehicleRestTemplate.getForObject("/vehicles/" + vehicleName, Vehicle.class);
        } catch (HttpStatusCodeException e) {
            return null;
        }
    }

    private Shipment getShipment(String shipmentName) {
        try {
            return shipmentRestTemplate.getForObject("/shipments/" + shipmentName, Shipment.class);
        } catch (HttpStatusCodeException e) {
            return null;
        }
    }


}
