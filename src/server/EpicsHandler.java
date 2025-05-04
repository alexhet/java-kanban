package server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import managers.NotFoundException;
import managers.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            switch (method) {
                case "GET":
                    handleGet(exchange, path);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange, path);
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGet(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/epics/\\d+/subtasks")) {
            try {
                int id = Integer.parseInt(path.split("/")[2]);
                List<Subtask> subtasks = taskManager.getEpicSubtasks(id);
                sendText(exchange, gson.toJson(subtasks), 200);
            } catch (NotFoundException e) {
                sendNotFound(exchange);
            }
        } else if (path.equals("/epics")) {
            List<Epic> epics = taskManager.getAllEpic();
            sendText(exchange, gson.toJson(epics), 200);
        } else if (path.matches("/epics/\\d+")) {
            try {
                int id = Integer.parseInt(path.split("/")[2]);
                Epic epic = taskManager.getAllEpic().stream()
                        .filter(e -> e.getId() == id)
                        .findFirst()
                        .orElseThrow(() -> new NotFoundException("Эпик с ID " + id + " не найден"));
                sendText(exchange, gson.toJson(epic), 200);
            } catch (NotFoundException e) {
                sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            JsonObject json = JsonParser.parseString(body).getAsJsonObject();

            String name = json.get("name").getAsString();
            String description = json.get("description").getAsString();
            Status status = Status.valueOf(json.get("status").getAsString());

            Epic epic = new Epic(name, description, status);

            if (json.has("id") && json.get("id").getAsInt() != 0) {
                epic.setId(json.get("id").getAsInt());
                taskManager.updateEpic(epic);
                sendText(exchange, "Эпик обновлён", 200);
            } else {
                taskManager.addEpic(epic);
                sendText(exchange, "Эпик создан", 201);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/epics/\\d+")) {
            try {
                int id = Integer.parseInt(path.split("/")[2]);
                taskManager.removeEpicById(id);
                sendText(exchange, "Эпик удалён", 200);
            } catch (NumberFormatException | NotFoundException e) {
                sendNotFound(exchange);
            }
        } else if (path.equals("/epics")) {
            taskManager.removeAllEpics();
            sendText(exchange, "Все эпики удалены", 200);
        } else {
            sendNotFound(exchange);
        }
    }
}