package com.andrianturcan.dipatcher.service;

import com.andrianturcan.dipatcher.VO.Shipment;
import com.andrianturcan.dipatcher.VO.Tariff;
import com.andrianturcan.dipatcher.VO.Vehicle;
import com.andrianturcan.dipatcher.controller.dto.DispatcherDto;
import com.andrianturcan.dipatcher.entity.Dispatcher;
import com.andrianturcan.dipatcher.exceptions.*;
import com.andrianturcan.dipatcher.repository.DispatcherRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class DispatcherService {

    @NonNull
    private DispatcherRepository dispatcherRepository;

    @NonNull
    private RestTemplate vehicleRestTemplate;

    @NonNull
    private RestTemplate tariffRestTemplate;

    public DispatcherDto assign(Shipment shipment, Vehicle vehicle, Tariff tariff) {
        initialCheck(shipment, vehicle, tariff);
        DispatcherDto dispatcherDto = DispatcherDto.builder()
                .shipmentName(shipment.getName()).build();
        if (Objects.isNull(tariff.getName())) {
            calculateCostWithoutTariff(shipment, vehicle, dispatcherDto);
        } else {
            dispatcherDto.setTariffName(tariff.getName());
            calculateCostWithSpecificTariff(shipment, vehicle, tariff, dispatcherDto);
        }

        if (Objects.isNull(dispatcherDto.getTotalCost())) {
            throw new CostCannotBeCalculatedException();
        }
        return dispatcherDto;
    }

    private void calculateCostWithSpecificTariff(Shipment shipment, Vehicle vehicle, Tariff tariff, DispatcherDto dispatcherDto) {
        if (Objects.nonNull(vehicle.getName()))
            calculateCostWithVehicle(shipment, vehicle, tariff, dispatcherDto);
        else
            calculateCost(shipment, tariff, dispatcherDto);
    }

    private void calculateCostWithoutTariff(Shipment shipment, Vehicle vehicle, DispatcherDto dispatcherDto) {
        if (Objects.nonNull(vehicle.getName())) {
            assignShipmentToVehicle(shipment, vehicle, dispatcherDto);
        } else {
            optimize(shipment, dispatcherDto);
        }
    }

    private static void initialCheck(Shipment shipment, Vehicle vehicle, Tariff tariff) {
        if (Objects.isNull(shipment))
            throw new ShipmentNotFoundException();
        if (Objects.isNull(vehicle))
            throw new VehicleNotFoundException();
        if (Objects.isNull(tariff))
            throw new TariffNotFoundException();
    }

    private void assignShipmentToVehicle(Shipment shipment, Vehicle vehicle, DispatcherDto dispatcherDto) {
        if (vehicle.getWeightCapacity() > shipment.getWeight()) {
            savePair(shipment, vehicle, dispatcherDto);
        } else {
            throw new NotEnaughtCapacityException();
        }
    }

    private void calculateCostWithVehicle(Shipment shipment, Vehicle vehicle, Tariff tariff, DispatcherDto dispatcherDto) {
        double weightCapacity = vehicle.getWeightCapacity();
        if (isTrafficAllowed(weightCapacity, shipment.getWeight(), tariff.getApplicableFrom(), tariff.getApplicableTo())) {
            savePair(shipment, vehicle, dispatcherDto);
            dispatcherDto.setTotalCost(calculateCostFormula(tariff, weightCapacity));
        }
    }

    private static double calculateCostFormula(Tariff tariff, double weightCapacity) {
        return weightCapacity * tariff.getRate() - (weightCapacity * tariff.getRate() * tariff.getDiscount());
    }

    private void calculateCost(Shipment shipment, Tariff tariff, DispatcherDto dispatcherDto) {
        Dispatcher dispatcher = dispatcherRepository.findByShipmentName(shipment.getName());
        if (Objects.isNull(dispatcher))
            throw new ShipmentWasNotAssignedException();
        double weightCapacity = dispatcher.getVehicleWeight();
        if (isTrafficAllowed(weightCapacity, shipment.getWeight(), tariff.getApplicableFrom(), tariff.getApplicableTo())) {
            dispatcherDto.setVehicleName(dispatcher.getVehicleName());
            dispatcherDto.setTotalCost(calculateCostFormula(tariff, weightCapacity));
        }
    }


    private boolean isTrafficAllowed(double weightCapacity, double weight, double applicableFrom, double applicableTo) {
        boolean isCapacity = weightCapacity > weight;
        boolean isTariff = isTariffApplicable(weightCapacity, applicableFrom, applicableTo);
        return isCapacity && isTariff;
    }

    private static boolean isTariffApplicable(double weightCapacity, double applicableFrom, double applicableTo) {
        boolean isTariff = weightCapacity > applicableFrom &&
                (weightCapacity < applicableTo || applicableTo == 0.0);
        return isTariff;
    }

    private void savePair(Shipment shipment, Vehicle vehicle, DispatcherDto dispatcherDto) {
        if (Objects.nonNull(dispatcherRepository.findByShipmentName(shipment.getName()))) {
            throw new ShipmentAlreadyAssignedException();
        }
        if (Objects.nonNull(dispatcherRepository.findByVehicleName(vehicle.getName()))) {
            throw new VehicleAlreadyAssignedException();
        }
        dispatcherDto.setVehicleName(vehicle.getName());
        dispatcherDto.setTotalCost(0.0);
        dispatcherRepository.save(
                Dispatcher.builder()
                        .shipmentName(shipment.getName())
                        .vehicleName(vehicle.getName())
                        .vehicleWeight(vehicle.getWeightCapacity())
                        .build());
    }

    private void optimize(Shipment shipment, DispatcherDto dispatcherDto) {
        Double cost = null;
        Vehicle vehicle = null;
        String tariffName = null;
        if (Objects.nonNull(dispatcherRepository.findByShipmentName(shipment.getName()))) {
            throw new ShipmentAlreadyAssignedException();
        }
        List<Vehicle> availableVehicles = getVailableVehicles().stream().filter(v -> v.getWeightCapacity() > shipment.getWeight()).toList();

        for (Vehicle v : availableVehicles) {
            Double vehicleCost = null;
            for (Tariff t : getTariffs()) {
                if (isTariffApplicable(v.getWeightCapacity(), t.getApplicableFrom(), t.getApplicableTo())) {
                    double costWithCurrentTariff = calculateCostFormula(t, v.getWeightCapacity());
                    if (Objects.isNull(vehicleCost) || vehicleCost > costWithCurrentTariff) {
                        vehicleCost = costWithCurrentTariff;
                        tariffName = t.getName();
                    }
                }
            }
            if (Objects.isNull(cost) || cost > vehicleCost) {
                cost = vehicleCost;
                vehicle = v;
            }
        }
        savePair(shipment, vehicle, dispatcherDto);
        dispatcherDto.setTariffName(tariffName);
        dispatcherDto.setTotalCost(cost);
    }

    private List<Vehicle> getVailableVehicles() {
        List<Dispatcher> assignedList = dispatcherRepository.findAll();
        List<String> assignedVehicleList = assignedList.stream().map(Dispatcher::getVehicleName).toList();
        return getVehicles()
                .stream()
                .filter(vehicle -> !assignedVehicleList.contains(vehicle.getName())).toList();
    }


    private List<Tariff> getTariffs() {
        return List.of(Objects.requireNonNull(tariffRestTemplate.getForObject("/tariffs/", Tariff[].class)));
    }

    private List<Vehicle> getVehicles() {
        return List.of(Objects.requireNonNull(vehicleRestTemplate.getForObject("/vehicles/", Vehicle[].class)));

    }


    public DispatcherDto getMostExpensiveShipment() {
        DispatcherDto dispatcherDto = new DispatcherDto();
        List<Dispatcher> assignedList = dispatcherRepository.findAll();
        Double maxCost = 0.0;
        for (Dispatcher dispatcher : assignedList) {
            for (Tariff t : getTariffs()) {
                if (isTariffApplicable(dispatcher.getVehicleWeight(), t.getApplicableFrom(), t.getApplicableTo())) {
                    double costWithCurrentTariff = calculateCostFormula(t, dispatcher.getVehicleWeight());
                    if (maxCost < costWithCurrentTariff) {
                        maxCost = costWithCurrentTariff;
                        dispatcherDto.setShipmentName(dispatcher.getShipmentName());
                        dispatcherDto.setVehicleName(dispatcher.getVehicleName());
                        dispatcherDto.setTariffName(t.getName());
                        dispatcherDto.setTotalCost(maxCost);
                    }
                }
            }
        }
        return dispatcherDto;
    }
}
