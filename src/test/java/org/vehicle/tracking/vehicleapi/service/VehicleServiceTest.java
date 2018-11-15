package org.vehicle.tracking.vehicleapi.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.vehicle.tracking.vehicleapi.model.Status;
import org.vehicle.tracking.vehicleapi.model.Vehicle;
import org.vehicle.tracking.vehicleapi.repo.VehicleRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class VehicleServiceTest {

  @InjectMocks
  VehicleService vehicleService;
  @Mock
  private VehicleRepository vehicleRepository;

  private MockMvc mvc;


  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    mvc = MockMvcBuilders.standaloneSetup(vehicleService).build();
  }


  @Test
  public void shouldReturnFullListOfVehicle() throws Exception {
    List<Vehicle> vehicles = sampleOfVehicles();

    when(vehicleRepository.findAll()).thenReturn(vehicles);

    assertThat(vehicleService.findAll())
        .isEqualTo(vehicles);
  }

  @Test
  public void shouldReturnVehiclesAsDisconnectedIfExpired() throws Exception {
    LocalDateTime t1 = LocalDateTime.now().minusMinutes(2);
    String timeFormatted = t1.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    List<Vehicle> vehicles = asList(
        Vehicle.builder()
            .id("1234")
            .registrationNumber("1232eEE")
            .status(Status.Connected)
            .time(t1)
            .build(),
        Vehicle.builder()
            .id("135")
            .registrationNumber("11EERRW")
            .build());
    when(vehicleRepository.findAll()).thenReturn(vehicles);

    assertThat(vehicleService.findAll().get(0).getStatus())
        .isEqualByComparingTo(Status.Disconnected);
    assertThat(vehicleService.findAll().get(1).getStatus())
        .isEqualByComparingTo(Status.Disconnected);

  }

  @Test
  public void shouldThrowIllegalArgumentExceptionIfNoVehicleIdProvided() {
    assertThatThrownBy(() -> vehicleService.beat(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("missing vehicleId");
  }

  @Test
  public void shouldReturnConnectedVehicleWhenBeat() {
    String vehicleId = "123EER";
    Vehicle vehicle = Vehicle.builder()
        .id(vehicleId)
        .registrationNumber("1232eEE")
        .status(Status.Connected)
        .build();

    when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));

    assertThat(vehicleService.beat(vehicleId))
        .isPresent()
        .contains(vehicle);

    verify(vehicleRepository, times(1)).findById(vehicleId);
    verify(vehicleRepository, times(1)).save(vehicle);
  }

  private List<Vehicle> sampleOfVehicles() {
    return asList(
        Vehicle.builder()
            .id("1234")
            .registrationNumber("1232eEE")
            .status(Status.Connected)
            .build(),
        Vehicle.builder()
            .id("135")
            .registrationNumber("11EERRW")
            .status(Status.Disconnected)
            .build(),
        Vehicle.builder()
            .id("222")
            .registrationNumber("1232MM")
            .status(Status.Disconnected)
            .build(),
        Vehicle.builder()
            .id("333")
            .registrationNumber("45456NNN")
            .status(Status.Disconnected)
            .build(),
        Vehicle.builder()
            .id("444")
            .registrationNumber("999ERTW")
            .status(Status.Disconnected)
            .build()
    );
  }
}
