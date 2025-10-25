package com.theawesomeengineer.taskmanager.model;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

public class TaskNotFoundError extends Error {
    public TaskNotFoundError(long id){
        super("Task not found", OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        setDetails(String.format("Task with ID %d does not exist", id));
    }   
}
