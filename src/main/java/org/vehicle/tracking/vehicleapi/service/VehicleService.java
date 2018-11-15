package org.vehicle.tracking.vehicleapi.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vehicle.tracking.vehicleapi.model.Status;
import org.vehicle.tracking.vehicleapi.model.Vehicle;
import org.vehicle.tracking.vehicleapi.repo.VehicleRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@Service
public class VehicleService {

  @Autowired
  private VehicleRepository vehicleRepository;

  public List<Vehicle> findAll() {
    return vehicleRepository.findAll().stream()
        .map(vehicle -> {  // Disconnected if time is not exist
              if (Objects.isNull(vehicle.getTime())) {
                vehicle.setStatus(Status.Disconnected);
              }
              return vehicle;
            }
        )
        .map(vehicle ->  // Disconnected if time expired
            ofNullable(vehicle.getTime())
                .filter(t -> vehicle.getTime().isBefore(now().minusMinutes(1)))
                .map(t1 -> {
                  vehicle.setStatus(Status.Disconnected);
                  return vehicle;
                }).orElse(vehicle)
        )
        .collect(toList());
  }

  public Optional<Vehicle> beat(String vehicleId) {
    if (StringUtils.isBlank(vehicleId)) {
      throw new IllegalArgumentException("missing vehicleId");
    }

    return ofNullable(vehicleRepository.findById(vehicleId)
        .map(vehicle -> {
          vehicle.setStatus(Status.Connected);
          vehicle.setTime(now());
          vehicleRepository.save(vehicle);
          return vehicle;
        })
        .orElse(null));
  }


}
