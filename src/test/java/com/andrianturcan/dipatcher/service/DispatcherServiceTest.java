package com.andrianturcan.dipatcher.service;

import com.andrianturcan.dipatcher.VO.Shipment;
import com.andrianturcan.dipatcher.VO.Tariff;
import com.andrianturcan.dipatcher.VO.Vehicle;
import com.andrianturcan.dipatcher.controller.dto.DispatcherDto;
import com.andrianturcan.dipatcher.entity.Dispatcher;
import com.andrianturcan.dipatcher.exceptions.NotEnaughtCapacityException;
import com.andrianturcan.dipatcher.exceptions.ShipmentAlreadyAssignedException;
import com.andrianturcan.dipatcher.repository.DispatcherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DispatcherServiceTest {

    private DispatcherService dispatcherService;

    @Mock
    private DispatcherRepository dispatcherRepository;

    @Mock
    private RestTemplate vehicleRestTemplate;

    @Mock
    private RestTemplate tariffRestTemplate;

    private Shipment testShipment;
    private Vehicle testVehicle;
    private Tariff testTariff;


    @BeforeEach
    void setUp() {
        dispatcherService = new DispatcherService(dispatcherRepository, vehicleRestTemplate, tariffRestTemplate);
        testShipment = new Shipment("SH_1", 1000);
        testVehicle = new Vehicle("TRUCK_1", 2000);
        testTariff = Tariff.builder()
                .name("TARIFF_1")
                .rate(3)
                .discount(0)
                .applicableFrom(0)
                .applicableTo(0).build();
    }

    @Test
    void shouldCalculateCostAndSaveIntoDB() {
        DispatcherDto expectedResult = DispatcherDto.builder()
                .shipmentName(testShipment.getName())
                .tariffName(testTariff.getName())
                .vehicleName(testVehicle.getName())
                .totalCost(6000.0).build();
        Dispatcher expectedDbSavedObject = Dispatcher.builder()
                .id(0)
                .vehicleWeight(testVehicle.getWeightCapacity())
                .shipmentName(testShipment.getName())
                .vehicleName(testVehicle.getName()).build();
        Mockito.when(dispatcherRepository.findByShipmentName(anyString())).thenReturn(null);
        Mockito.when(dispatcherRepository.findByVehicleName(anyString())).thenReturn(null);

        DispatcherDto actualResult = dispatcherService.assign(testShipment, testVehicle, testTariff);
        //Check the total cost
        assertEquals(expectedResult, actualResult);
        //Check that it saved in DB
        verify(dispatcherRepository).save(expectedDbSavedObject);
        //Check if we looked in DB if vehicle is already assigned
        verify(dispatcherRepository).findByVehicleName(testVehicle.getName());
        //Check if we looked in DB if shipment is already assigned
        verify(dispatcherRepository).findByShipmentName(testShipment.getName());
    }

    @Test
    public void shouldThrowNotEnaughtCapacityException() {
        Vehicle testVehicleWithLowCapacity = new Vehicle("TRUCK_2", 1);
        assertThrows(NotEnaughtCapacityException.class, () -> {
            dispatcherService.assign(testShipment, testVehicleWithLowCapacity, new Tariff());
        });
    }

    @Test
    public void shouldThrowShipmentAlreadyAssignedException() {
        Mockito.when(dispatcherRepository.findByShipmentName(anyString())).thenReturn(new Dispatcher());
        assertThrows(ShipmentAlreadyAssignedException.class, () -> {
            dispatcherService.assign(testShipment, testVehicle, new Tariff());
        });
    }

}