package com.example.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditResidentialAddressRequest {
  private UUID personId;
  private String country;
  private String city;
  private String street;
  private Integer postalCode;
  private Integer house;
  private Integer flat;
  private Integer block;
}
