package server;

import com.google.gson.Gson;
import managers.InMemoryHistoryManager;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import managers.HistoryManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.Status;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TasksHandlerTest {
    private HttpTaskServer taskServer;
    private TaskManager taskManager;
    private Gson gson;
    private HttpClient client;

    public TasksHandlerTest() throws IOException {
        HistoryManager historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
        taskServer = new HttpTaskServer(taskManager);
        gson = new Gson();
        client = HttpClient.newHttpClient();
    }

    @BeforeEach
    public void setUp() {
        taskManager.removeAllTasks();
        taskManager.removeAllSubtasks();
        taskManager.removeAllEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTaskSuccess() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Description", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals("Задача создана", response.body());

        List<Task> tasks = taskManager.getAllTasks();
        assertEquals(1, tasks.size());
        assertEquals("Test Task", tasks.get(0).getName());
    }

    @Test
    public void testUpdateTaskSuccess() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Description", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addTask(task);
        task.setName("Updated Task");
        String taskJson = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("Задача обновлена", response.body());

        List<Task> tasks = taskManager.getAllTasks();
        assertEquals(1, tasks.size());
        assertEquals("Updated Task", tasks.get(0).getName());
    }

    @Test
    public void testGetAllTasksSuccess() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Description", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task[] tasks = gson.fromJson(response.body(), Task[].class);
        assertEquals(1, tasks.length);
        assertEquals("Test Task", tasks[0].getName());
    }

    @Test
    public void testDeleteTaskByIdSuccess() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Description", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks?id=1"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("Задача удалена", response.body());

        assertEquals(0, taskManager.getAllTasks().size());
    }

    @Test
    public void testDeleteAllTasksSuccess() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Description", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("Все задачи удалены", response.body());

        assertEquals(0, taskManager.getAllTasks().size());
    }

    @Test
    public void testInvalidMethodReturnsNotFound() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("Ресурс не найден", response.body());
    }
}