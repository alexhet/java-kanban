package server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import tasks.Status;
import tasks.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler{
private final TaskManager taskManager;

public SubtasksHandler(TaskManager taskManager) {
    this.taskManager = taskManager;
}

@Override
public void handle(HttpExchange exchange) throws IOException {
    try {
        String method = exchange.getRequestMethod();
        String path   = exchange.getRequestURI().getPath();
        switch (method) {
            case "GET":    handleGet(exchange, path);    break;
            case "POST":   handlePost(exchange);        break;
            case "DELETE": handleDelete(exchange, path);break;
            default:       sendNotFound(exchange);
        }
    } catch (Exception e) {
        sendInternalError(exchange);
    }
}

private void handleGet(HttpExchange exchange, String path) throws IOException {
    if (path.matches("/subtasks/\\d+")) {
        try {
            int id = Integer.parseInt(path.split("/")[2]);
            Subtask sub = taskManager.getSubtaskById(id);
            if (sub != null) {
                sendText(exchange, gson.toJson(sub), 200);
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendNotFound(exchange);
        }
    } else if (path.equals("/subtasks")) {
        List<Subtask> all = taskManager.getAllSubtask();
        sendText(exchange, gson.toJson(all), 200);
    } else {
        sendNotFound(exchange);
    }
}

private void handlePost(HttpExchange exchange) throws IOException {
    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    JsonObject json = JsonParser.parseString(body).getAsJsonObject();

    String name        = json.get("name").getAsString();
    String description = json.get("description").getAsString();
    Status status      = Status.valueOf(json.get("status").getAsString());
    int epicId         = json.get("epicId").getAsInt();

    Duration duration       = json.has("duration")
            ? Duration.parse(json.get("duration").getAsString())
            : null;
    LocalDateTime startTime = json.has("startTime")
            ? LocalDateTime.parse(json.get("startTime").getAsString())
            : null;

    Subtask subtask = new Subtask(name, description, status, epicId, duration, startTime);

    if (json.has("id") && json.get("id").getAsInt() != 0) {
        subtask.setId(json.get("id").getAsInt());
        taskManager.updateSubtask(subtask);
        sendText(exchange, "Подзадача обновлена", 200);
    } else {
        taskManager.addSubtask(subtask);
        sendText(exchange, "Подзадача создана", 201);
    }
}

private void handleDelete(HttpExchange exchange, String path) throws IOException {
    if (path.matches("/subtasks/\\d+")) {
        try {
            int id = Integer.parseInt(path.split("/")[2]);
            taskManager.removeSubtaskId(id);
            sendText(exchange, "Подзадача удалена", 200);
        } catch (Exception e) {
            sendNotFound(exchange);
        }
    } else if (path.equals("/subtasks")) {
        taskManager.removeAllSubtasks();
        sendText(exchange, "Все подзадачи удалены", 200);
    } else {
        sendNotFound(exchange);
    }
}
}
