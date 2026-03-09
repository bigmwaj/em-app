package ca.bigmwaj.emapp.dm.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StatusChangeRequest<T>{

    private T status;

    private LocalDateTime statusDate;

    private String statusReason;
}
