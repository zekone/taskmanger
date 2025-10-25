package com.theawesomeengineer.taskmanager.api;

import com.theawesomeengineer.taskmanager.exception.TaskNotFoundException;
import com.theawesomeengineer.taskmanager.model.Task;
import com.theawesomeengineer.taskmanager.model.TaskRequest;
import com.theawesomeengineer.taskmanager.repository.TaskRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

import jakarta.validation.Valid;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import jakarta.annotation.Generated;


@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-10-23T23:04:10.168047188+08:00[Asia/Singapore]", comments = "Generator version: 7.15.0")
@Controller
@RequestMapping("${openapi.taskManager.base-path:}")
public class TasksApiController implements TasksApi {

    private final NativeWebRequest request;
    private final TaskRepository taskRepository;

    @Autowired
    public TasksApiController(NativeWebRequest request, TaskRepository taskRepository) {
        this.request = request;
        this.taskRepository = taskRepository;
    }

    @Override
    public ResponseEntity<Task> createTask(@Valid @RequestBody TaskRequest taskRequest) {
        OffsetDateTime timeCreated = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        if (taskRequest.getCompleted() == null) {
            taskRequest.setCompleted(false);
        }

        Task newTask = new Task()
            .title(taskRequest.getTitle())
            .description(taskRequest.getDescription())
            .completed(taskRequest.getCompleted())
            .createdAt(timeCreated)
            .updatedAt(timeCreated);

        Task createdTask = taskRepository.save(newTask);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    } 

    @Override
    public ResponseEntity<Void> deleteTask(@PathVariable("id") Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(id);
        } 
        taskRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return ResponseEntity.ok(tasks);
    }

    @Override
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Optional<Task> taskOpt = taskRepository.findById(id);

        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            return ResponseEntity.ok(task);
        } else {
            throw new TaskNotFoundException(id);
        }
    }

    @Override
    public ResponseEntity<Task> updateTask(@PathVariable("id") Long id, @Valid @RequestBody TaskRequest taskRequest) {
        if (taskRequest.getCompleted() == null) {
            taskRequest.setCompleted(false);
        }
        
        Optional<Task> taskOpt = taskRepository.findById(id);
        if (taskOpt.isPresent()) {
            Task taskOld = taskOpt.get();
            Task taskNew = new Task()
                .id(taskOld.getId())
                .title(taskRequest.getTitle())
                .description(taskRequest.getDescription())
                .completed(taskRequest.getCompleted())
                .createdAt(taskOld.getCreatedAt())
                .updatedAt(OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS));
            taskRepository.save(taskNew);
            return ResponseEntity.ok(taskNew);
        } else {
            throw new TaskNotFoundException(id);
        }
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

}
