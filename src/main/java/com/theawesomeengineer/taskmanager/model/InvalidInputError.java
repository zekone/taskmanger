package com.theawesomeengineer.taskmanager.model;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

public class InvalidInputError extends Error {
    public InvalidInputError(){
        super("Invalid input", OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        setDetails("Http message is unreadable.");
    }   
}
