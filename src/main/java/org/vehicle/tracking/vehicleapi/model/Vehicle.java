package org.vehicle.tracking.vehicleapi.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@Setter
public class Vehicle {
  @Id
  private String id;
  @Column(name="REG_NUM")
  private String registrationNumber;
  private Status status;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime time;
}
