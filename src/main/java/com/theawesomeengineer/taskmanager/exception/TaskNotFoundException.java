package com.theawesomeengineer.taskmanager.exception;

public class TaskNotFoundException extends RuntimeException {
    private long taskId;

    public TaskNotFoundException(long id) {
        super("Task not found");
        this.taskId = id;
    }

    public long getTaskId() {
        return this.taskId;
    }
   
}
