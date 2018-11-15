package org.vehicle.tracking.vehicleapi.api;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vehicle.tracking.vehicleapi.model.Vehicle;
import org.vehicle.tracking.vehicleapi.service.VehicleService;

import java.util.List;

import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/vehicle")
public class VehicleApi {

  @Autowired
  private VehicleService vehicleService;

  @GetMapping
  public ResponseEntity<List<Vehicle>> findAll() {
    List<Vehicle> vehicles = vehicleService.findAll();
    if (vehicles.isEmpty()) {
      return noContent().build();
    }
    return ok(vehicles);
  }

  @PostMapping("/{vehicleId}")
  public ResponseEntity<Vehicle> beat(@PathVariable("vehicleId") String vehicleId) {
    return vehicleService.beat(vehicleId)
        .map(ResponseEntity::ok)
        .orElse(notFound().build());
  }
}
