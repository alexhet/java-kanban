package server;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.*;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private static final String BASE = "http://localhost:8080";

    private TaskManager manager;
    private HttpTaskServer server;
    private HttpClient client;
    private Gson gson;

    @BeforeEach
    void setUp() throws IOException {
        manager = Managers.getDefault();
        manager.removeAllTasks();
        manager.removeAllSubtasks();
        manager.removeAllEpics();

        server = new HttpTaskServer(manager);
        server.start();

        client = HttpClient.newHttpClient();
        gson = new GsonBuilder()
                // 1. адаптеры для Duration и LocalDateTime
                .registerTypeAdapter(Duration.class, new TypeAdapter<Duration>() {
                    @Override
                    public void write(JsonWriter out, Duration value) throws IOException {
                        out.value(value == null ? null : value.toString());
                    }
                    @Override
                    public Duration read(JsonReader in) throws IOException {
                        String s = in.nextString();
                        return s == null ? null : Duration.parse(s);
                    }
                })
                .registerTypeAdapter(LocalDateTime.class, new TypeAdapter<LocalDateTime>() {
                    @Override
                    public void write(JsonWriter out, LocalDateTime value) throws IOException {
                        out.value(value == null ? null : value.toString());
                    }
                    @Override
                    public LocalDateTime read(JsonReader in) throws IOException {
                        String s = in.nextString();
                        return s == null ? null : LocalDateTime.parse(s);
                    }
                })
                // 2. пропускаем поля duration и startTime из суперкласса Task, чтобы не было дублирования
                .addSerializationExclusionStrategy(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(tasks.Task.class)
                                && (f.getName().equals("duration") || f.getName().equals("startTime"));
                    }
                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .addDeserializationExclusionStrategy(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(tasks.Task.class)
                                && (f.getName().equals("duration") || f.getName().equals("startTime"));
                    }
                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    //  /tasks

    @Test
    void createTaskSuccess() throws IOException, InterruptedException {
        Task t = new Task("T1","D1", Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        String body = gson.toJson(t);

        HttpResponse<String> resp = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(BASE + "/tasks"))
                        .header("Content-Type","application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(201, resp.statusCode());
        List<Task> all = manager.getAllTasks();
        assertEquals(1, all.size());
        assertEquals("T1", all.get(0).getName());
    }

    @Test
    void invalidMethodTasks() throws IOException, InterruptedException {
        HttpResponse<String> resp = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(BASE + "/tasks"))
                        .method("PUT", HttpRequest.BodyPublishers.noBody())
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(404, resp.statusCode());
    }

    //  /subtasks

    @Test
    void createSubtaskSuccess() throws IOException, InterruptedException {
        Epic epic = new Epic("E1","ED", Status.NEW);
        manager.addEpic(epic);

        Subtask s = new Subtask("S1","SD", Status.NEW,
                epic.getId(), Duration.ofMinutes(3), LocalDateTime.now());
        String body = gson.toJson(s);

        HttpResponse<String> resp = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(BASE + "/subtasks"))
                        .header("Content-Type","application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(201, resp.statusCode());
        assertEquals(1, manager.getAllSubtask().size());
    }

    @Test
    void invalidMethodSubtasks() throws IOException, InterruptedException {
        HttpResponse<String> resp = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(BASE + "/subtasks"))
                        .method("PATCH", HttpRequest.BodyPublishers.noBody())
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(404, resp.statusCode());
    }

    // /epics

    @Test
    void getNonExistingEpic() throws IOException, InterruptedException {
        HttpResponse<String> resp = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(BASE + "/epics/999"))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(404, resp.statusCode());
    }

    //  /history

    @Test
    void getEmptyHistory() throws IOException, InterruptedException {
        HttpResponse<String> resp = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(BASE + "/history"))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, resp.statusCode());
        assertEquals("[]", resp.body());
    }

    @Test
    void invalidMethodHistory() throws IOException, InterruptedException {
        HttpResponse<String> resp = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(BASE + "/history"))
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(404, resp.statusCode());
    }

    //  /prioritized 

    @Test
    void getEmptyPrioritized() throws IOException, InterruptedException {
        HttpResponse<String> resp = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(BASE + "/prioritized"))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, resp.statusCode());
        assertEquals("[]", resp.body());
    }

    @Test
    void invalidMethodPrioritized() throws IOException, InterruptedException {
        HttpResponse<String> resp = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(BASE + "/prioritized"))
                        .method("DELETE", HttpRequest.BodyPublishers.noBody())
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(404, resp.statusCode());
    }
}
