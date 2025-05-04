package server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import managers.NotFoundException;
import managers.TaskManager;
import tasks.Status;
import tasks.Task;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class TasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    handleGet(exchange);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange);
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        List<Task> tasks = taskManager.getAllTasks();
        sendText(exchange, gson.toJson(tasks), 200);
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            JsonObject json = JsonParser.parseString(body).getAsJsonObject();

            String name = json.get("name").getAsString();
            String description = json.get("description").getAsString();
            Status status = Status.valueOf(json.get("status").getAsString());

            Duration duration = json.has("duration") && !json.get("duration").isJsonNull()
                    ? Duration.parse(json.get("duration").getAsString())
                    : null;
            LocalDateTime startTime = json.has("startTime") && !json.get("startTime").isJsonNull()
                    ? LocalDateTime.parse(json.get("startTime").getAsString())
                    : null;

            Task task = new Task(name, description, status, duration, startTime);

            if (json.has("id") && json.get("id").getAsInt() != 0) {
                task.setId(json.get("id").getAsInt());
                taskManager.updateTask(task);
                sendText(exchange, "Задача обновлена", 200);
            } else {
                taskManager.addTask(task);
                sendText(exchange, "Задача создана", 201);
            }
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("пересекается")) {
                sendHasInteractions(exchange);
            } else {
                sendNotFound(exchange);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query != null && query.startsWith("id=")) {
            try {
                int id = Integer.parseInt(query.substring(3));
                taskManager.removeTaskById(id);
                sendText(exchange, "Задача удалена", 200);
            } catch (NumberFormatException | NotFoundException e) {
                sendNotFound(exchange);
            }
        } else {
            taskManager.removeAllTasks();
            sendText(exchange, "Все задачи удалены", 200);
        }
    }
}