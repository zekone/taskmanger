package com.theawesomeengineer.taskmanager.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.theawesomeengineer.taskmanager.TestUtil;
import com.theawesomeengineer.taskmanager.exception.TaskNotFoundException;
import com.theawesomeengineer.taskmanager.model.Task;
import com.theawesomeengineer.taskmanager.model.TaskRequest;
import com.theawesomeengineer.taskmanager.repository.TaskRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TasksApiControllerTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TasksApiController taskController;

    @Test
    void createTask_NullCompleted_Created() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Task Sample");
        request.setDescription("Task Description");
        request.setCompleted(null);
        Task task = TestUtil.getTaskInstance();
        task.completed(null);

        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ResponseEntity<Task> response = taskController.createTask(request);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Task result = response.getBody();
        assertNotNull(result);
        assertEquals(request.getTitle(), result.getTitle());
        assertEquals(request.getDescription(), result.getDescription());
        assertEquals(request.getCompleted(), result.getCompleted());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        verify(taskRepository, times(1)).save(any(Task.class));
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    void deleteTask_Normal_NoContent() {        
        when(taskRepository.existsById(any(Long.class))).thenReturn(true);
        ResponseEntity<Void> response = taskController.deleteTask(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(taskRepository, times(1)).deleteById(any(Long.class));
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    void deleteTask_NonexistentId_NotFound() {        
        when(taskRepository.existsById(any(Long.class))).thenReturn(false);
        
        TaskNotFoundException exception = assertThrows(
TaskNotFoundException.class, () -> taskController.deleteTask(1L)
        );
        assertEquals("Task not found", exception.getMessage());
        
        verify(taskRepository, never()).deleteById(anyLong());
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    void getAllTasks_Normal_Ok() {
        Task task = TestUtil.getTaskInstance();

        when(taskRepository.findAll()).thenReturn(List.of(task));
        ResponseEntity<List<Task>> response = taskController.getAllTasks();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(TestUtil.getTaskInstance(), response.getBody().get(0));

        verify(taskRepository, times(1)).findAll();
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    void getTaskById_Normal_Ok() {
        Task task = TestUtil.getTaskInstance();

        when(taskRepository.findById(any(Long.class))).thenReturn(Optional.of(task));
        ResponseEntity<Task> response = taskController.getTaskById(task.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(TestUtil.getTaskInstance(), response.getBody());

        verify(taskRepository, times(1)).findById(any(Long.class));
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    void getTaskById_NonexistentId_NotFound() {
        when(taskRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(
TaskNotFoundException.class, () -> taskController.getTaskById(1L)
        );
        assertEquals("Task not found", exception.getMessage());

        verify(taskRepository, times(1)).findById(any(Long.class));
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    void updateTask_CompletedTask_Updated() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Updated Sample");
        request.setDescription("Updated description");
        request.setCompleted(true);
        Task existingTask = TestUtil.getTaskInstance();

        when(taskRepository.findById(any(Long.class))).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ResponseEntity<Task> response = taskController.updateTask(existingTask.getId(), request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Task result = response.getBody();
        assertNotNull(result);
        assertEquals(existingTask.getId(), result.getId());
        assertEquals(request.getTitle(), result.getTitle());
        assertEquals(request.getDescription(), result.getDescription());
        assertEquals(request.getCompleted(), result.getCompleted());
        assertEquals(existingTask.getCreatedAt(), result.getCreatedAt());
        assertNotEquals(existingTask.getUpdatedAt(), result.getUpdatedAt());

        verify(taskRepository, times(1)).save(any(Task.class));
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    void updateTask_NonexistentId_NullCompleted_NotFound() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Updated Sample");
        request.setDescription("Updated description");
        request.setCompleted(null);

        when(taskRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(
TaskNotFoundException.class, () -> taskController.updateTask(1L, request)
        );
        assertEquals("Task not found", exception.getMessage());

        verify(taskRepository, times(1)).findById(any(Long.class));
        verifyNoMoreInteractions(taskRepository);
    }

}