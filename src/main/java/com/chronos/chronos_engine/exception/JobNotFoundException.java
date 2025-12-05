package com.chronos.chronos_engine.exception;

import java.util.UUID;

public class JobNotFoundException extends RuntimeException{
    public JobNotFoundException(UUID id){
        super(String.valueOf(id));
    }
}
