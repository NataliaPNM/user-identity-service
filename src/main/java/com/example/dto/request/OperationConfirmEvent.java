package com.example.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OperationConfirmEvent {

  private UUID personId;
  private UUID operationId;
  private String status;
  private String lockTime;
}
