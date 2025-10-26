package com.theawesomeengineer.taskmanager.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.jayway.jsonpath.JsonPath;
import com.theawesomeengineer.taskmanager.IntegrationTestUtil;
import com.theawesomeengineer.taskmanager.model.Task;
import com.theawesomeengineer.taskmanager.repository.TaskRepository;

import java.time.OffsetDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=true"
})
class TasksApiControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setup() {
        taskRepository.deleteAll();
    }

    @Test
    void createTask_NullCompleted_Created() throws Exception {
        String requestJson = """
            {
                "title": "Task Sample",
                "description": "Task Description",
                "completed": null
            }
        """;

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Task Sample"))
                .andExpect(jsonPath("$.description").value("Task Description"))
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty());

        List<Task> tasks = taskRepository.findAll();
        assertEquals(1, tasks.size());
        Task saved = tasks.get(0);
        assertEquals("Task Sample", saved.getTitle());
        assertEquals("Task Description", saved.getDescription());
        assertFalse(saved.getCompleted());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
    }

    @Test
    void deleteTask_Normal_NoContent() throws Exception {
        Task task = taskRepository.save(IntegrationTestUtil.getTaskInstance());
        Long taskId = task.getId();
        assertTrue(taskRepository.findById(taskId).isPresent());

        mockMvc.perform(delete("/tasks/{id}", taskId))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        assertTrue(taskRepository.findById(taskId).isEmpty());
    }

    @Test
    void deleteTask_NonexistentId_NotFound() throws Exception {
        Task task = taskRepository.save(IntegrationTestUtil.getTaskInstance());
        Long presentId = task.getId();
        Long absentId = 999L;
        assertTrue(taskRepository.findById(presentId).isPresent());
        assertTrue(taskRepository.findById(absentId).isEmpty());

        mockMvc.perform(delete("/tasks/{id}", absentId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Task not found"));

        assertTrue(taskRepository.findById(presentId).isPresent());
        assertTrue(taskRepository.findById(absentId).isEmpty());
    }

    @Test
    void getAllTasks_Normal_Ok() throws Exception{
        Task task1 = IntegrationTestUtil.getTaskInstance();
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setTitle("Fix bug");
        task2.setDescription("Resolve issue #42");
        task2.setCompleted(true);
        taskRepository.save(task2);

        MvcResult result = mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].title").value(task1.getTitle()))
                .andExpect(jsonPath("$[0].description").value(task1.getDescription()))
                .andExpect(jsonPath("$[0].completed").value(task1.getCompleted()))
                .andExpect(jsonPath("$[0].createdAt").isNotEmpty())
                .andExpect(jsonPath("$[0].updatedAt").isNotEmpty())
                .andExpect(jsonPath("$[1].id").isNumber())
                .andExpect(jsonPath("$[1].title").value("Fix bug"))
                .andExpect(jsonPath("$[1].description").value("Resolve issue #42"))
                .andExpect(jsonPath("$[1].completed").value(true))
                .andExpect(jsonPath("$[1].createdAt").isEmpty())
                .andExpect(jsonPath("$[1].updatedAt").isEmpty())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        String createdAtStr = JsonPath.read(json, "$[0].createdAt");
        String updatedAtStr = JsonPath.read(json, "$[0].updatedAt");
        assertEquals(OffsetDateTime.parse(createdAtStr), task1.getCreatedAt());
        assertEquals(OffsetDateTime.parse(updatedAtStr), task1.getUpdatedAt());
    }

    @Test
    void getTaskById_Normal_Ok() throws Exception {
        Task task = IntegrationTestUtil.getTaskInstance();
        taskRepository.save(task);


        MvcResult result = mockMvc.perform(get("/tasks/{id}", task.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(task.getId()))
                .andExpect(jsonPath("$.title").value(task.getTitle()))
                .andExpect(jsonPath("$.description").value(task.getDescription()))
                .andExpect(jsonPath("$.completed").value(task.getCompleted()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty())
                .andReturn();
        
        String json = result.getResponse().getContentAsString();
        String createdAtStr = JsonPath.read(json, "$.createdAt");
        String updatedAtStr = JsonPath.read(json, "$.updatedAt");
        assertEquals(OffsetDateTime.parse(createdAtStr), task.getCreatedAt());
        assertEquals(OffsetDateTime.parse(updatedAtStr), task.getUpdatedAt());
    }

    @Test
    void getTaskById_NonexistentId_NotFound() throws Exception {
        Long absentId = 999L;
        assertTrue(taskRepository.findById(absentId).isEmpty());

        mockMvc.perform(get("/tasks/{id}", absentId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Task not found"));

        assertTrue(taskRepository.findById(absentId).isEmpty());
    }

    @Test
    void updateTask_Normal_Updated() throws Exception {
        Task task = IntegrationTestUtil.getTaskInstance();
        taskRepository.save(task);

        String requestBody = """
            {
                "title": "Updated title",
                "description": "Updated description",
                "completed": true
            }
        """;

        MvcResult result = mockMvc.perform(put("/tasks/{id}", task.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(task.getId()))
                .andExpect(jsonPath("$.title").value("Updated title"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.completed").value(true))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists())
                .andReturn();
        
        String json = result.getResponse().getContentAsString();
        String createdAtStr = JsonPath.read(json, "$.createdAt");
        String updatedAtStr = JsonPath.read(json, "$.updatedAt");
        assertEquals(OffsetDateTime.parse(createdAtStr), task.getCreatedAt());
        assertNotEquals(OffsetDateTime.parse(updatedAtStr), task.getUpdatedAt());

        Task updated = taskRepository.findById(task.getId()).orElseThrow();
        assertNotEquals(task, updated);
        assertEquals("Updated title", updated.getTitle());
        assertEquals("Updated description", updated.getDescription());
        assertTrue(updated.getCompleted());
        assertEquals(task.getCreatedAt(), updated.getCreatedAt());
    }

    @Test
    void updateTask_NonexistentId_NullCompleted_NotFound() throws Exception {
        String requestBody = """
            {
                "title": "Updated title",
                "description": "Updated description",
                "completed": null
            }
        """;
        Long absentId = 999L;
        assertTrue(taskRepository.findById(absentId).isEmpty());

        mockMvc.perform(put("/tasks/{id}", absentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Task not found"));

        assertTrue(taskRepository.findById(absentId).isEmpty());
    }
}