package org.vehicle.tracking.vehicleapi.api;

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
import org.vehicle.tracking.vehicleapi.service.VehicleService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
public class VehicleApiTest {

  @InjectMocks
  private VehicleApi vehicleApi;

  private MockMvc mvc;

  @Mock
  private VehicleService vehicleService;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);

    mvc = MockMvcBuilders.standaloneSetup(vehicleApi).build();
  }


  @Test
  public void shouldReturn204IfNoVehicleReturned() throws Exception {

    when(vehicleService.findAll()).thenReturn(emptyList());

    mvc.perform(get("/api/vehicle")
        .contentType(APPLICATION_JSON_VALUE))
        .andExpect(status().isNoContent());

    verify(vehicleService, times(1)).findAll();
  }

  @Test
  public void shouldReturn200IfVehiclesFound() throws Exception {
    when(vehicleService.findAll()).thenReturn(sampleOfVehicles());

    mvc.perform(get("/api/vehicle")
        .contentType(APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[*]", hasSize(5)))
        .andExpect(jsonPath("$.[0].status", is("Connected")));

    verify(vehicleService, times(1)).findAll();
  }

  @Test
  public void shouldReturnDisconnectedVehiclesIfMoreThanMinuteFromLastUpdate() throws Exception {
    LocalDateTime t1 = LocalDateTime.now().minusMinutes(2);
    String timeFormatted = t1.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    List<Vehicle> vehicles = asList(
        Vehicle.builder()
            .id("1234")
            .registrationNumber("1232eEE")
            .status(Status.Disconnected)
            .time(t1)
            .build(),
        Vehicle.builder()
            .id("135")
            .registrationNumber("11EERRW")
            .status(Status.Disconnected)
            .time(t1)
            .build());

    when(vehicleService.findAll()).thenReturn(vehicles);

    mvc.perform(get("/api/vehicle")
        .contentType(APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[0].status", is("Disconnected")))
        .andExpect(jsonPath("$.[0].time", is(timeFormatted)));

    verify(vehicleService, times(1)).findAll();
  }

  @Test
  public void shouldReturn404IfVehicleIdIsMissing() throws Exception {

    mvc.perform(post("/api/vehicle")
        .contentType(APPLICATION_JSON_VALUE))
        .andExpect(status().isMethodNotAllowed());
    verify(vehicleService, never()).beat(null);
  }

  @Test
  public void shouldReturn404IfVehicleNotFound() throws Exception {
    String vehicleId = "123EE";
    when(vehicleService.beat(vehicleId)).thenReturn(empty());

    mvc.perform(post("/api/vehicle/" + vehicleId)
        .contentType(APPLICATION_JSON_VALUE))
        .andExpect(status().isNotFound());

    verify(vehicleService, times(1)).beat(vehicleId);
  }

  @Test
  public void shouldReturn200IfVehicleUpdated() throws Exception {
    String vehicleId = "123EE";
    Vehicle vehicle = Vehicle.builder()
        .id(vehicleId)
        .status(Status.Connected)
        .registrationNumber("EERR").build();

    when(vehicleService.beat(vehicleId)).thenReturn(Optional.of(vehicle));

    mvc.perform(post("/api/vehicle/" + vehicleId)
        .contentType(APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is("Connected")));

    verify(vehicleService, times(1)).beat(vehicleId);
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
