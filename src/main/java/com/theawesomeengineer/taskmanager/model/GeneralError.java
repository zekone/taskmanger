package com.theawesomeengineer.taskmanager.model;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

public class GeneralError extends Error {
    public GeneralError(){
        super("Internal server error", OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        setDetails("An unexpected error has occurred.");
    } 
}
