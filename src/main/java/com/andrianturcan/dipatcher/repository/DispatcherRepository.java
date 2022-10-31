package com.andrianturcan.dipatcher.repository;

import com.andrianturcan.dipatcher.entity.Dispatcher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface DispatcherRepository extends JpaRepository<Dispatcher, String> {

      Dispatcher findByShipmentName(String shipmentName);
      Dispatcher findByVehicleName(String vehicleName);

}
