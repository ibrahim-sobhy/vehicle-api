package org.vehicle.tracking.vehicleapi.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vehicle.tracking.vehicleapi.model.Vehicle;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {
}
