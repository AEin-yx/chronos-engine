package com.chronos.chronos_engine.dto;

import com.chronos.chronos_engine.model.Priority;
import com.chronos.chronos_engine.model.Schedule;
import com.chronos.chronos_engine.model.Status;
import com.chronos.chronos_engine.model.Type;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.ZonedDateTime;

@Getter
@Setter
public class JobRequestDTO {
    @NotBlank(message = "Name is required")
    @NotNull(message = "name cannot be null")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @Size(max = 1000,message = "Description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "owner id cannot be null")
    @NotBlank(message = "owner id cannot be blank")
    private String ownerId;

    @NotNull(message = "status cannot be null")
    private Status status;

    @NotNull(message = "enabled cannot be null")
    private boolean enabled;

    @NotNull(message = "type cannot be null")
    private Type type;
    private JsonNode payload;

    @NotNull(message = "schedule cannot be null")
    private Schedule scheduleType;
    private ZonedDateTime runAt;
    private String cronExpression;

    @NotNull(message = "priority cannot be null")
    private Priority priority;
}
