package com.theawesomeengineer.taskmanager;

import java.time.OffsetDateTime;

import com.theawesomeengineer.taskmanager.model.Task;

public class IntegrationTestUtil {
    public static OffsetDateTime TEST_TIME = OffsetDateTime.parse("2025-10-25T18:00:00Z");

    public static Task getTaskInstance(){
        return new Task()
                .title("Task Sample")
                .description("Task Description")
                .completed(false)
                .createdAt(TEST_TIME)
                .updatedAt(TEST_TIME);
    }
}
