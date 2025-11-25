package com.chronos.chronos_engine.dto;

import com.chronos.chronos_engine.model.Priority;
import com.chronos.chronos_engine.model.Schedule;
import com.chronos.chronos_engine.model.Status;
import com.chronos.chronos_engine.model.Type;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.ZonedDateTime;

@Getter
@Setter
public class JobRequestDTO {
    private String name;
    private String description;
    private String ownerId;
    private Status status;
    private boolean enabled;
    private Type type;
    private JsonNode payload;
    private Schedule scheduleType;
    private ZonedDateTime runAt;
    private String cronExpression;
    private Priority priority;
}
