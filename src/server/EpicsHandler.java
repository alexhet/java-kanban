package server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class EpicsHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path   = exchange.getRequestURI().getPath();
            switch (method) {
                case "GET":    handleGet(exchange, path);    break;
                case "POST":   handlePost(exchange);         break;
                case "DELETE": handleDelete(exchange, path); break;
                default:       sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGet(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/epics/\\d+/subtasks")) {
            // GET /epics/{id}/subtasks
            int id = Integer.parseInt(path.split("/")[2]);
            List<Subtask> subs = taskManager.getEpicSubtasks(id);
            sendText(exchange, gson.toJson(subs), 200);

        } else if (path.equals("/epics")) {
            // GET /epics
            List<Epic> all = taskManager.getAllEpic();
            sendText(exchange, gson.toJson(all), 200);

        } else if (path.matches("/epics/\\d+")) {
            // GET /epics/{id}
            int id = Integer.parseInt(path.split("/")[2]);
            Optional<Epic> opt = taskManager.getAllEpic()
                    .stream()
                    .filter(e -> e.getId() == id)
                    .findFirst();
            if (opt.isPresent()) {
                sendText(exchange, gson.toJson(opt.get()), 200);
            } else {
                sendNotFound(exchange);
            }

        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        // Читаем строку и парсим JsonObject
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        JsonObject json = JsonParser.parseString(body).getAsJsonObject();

        // Обязательные поля
        String name        = json.get("name").getAsString();
        String description = json.get("description").getAsString();
        Status status      = Status.valueOf(json.get("status").getAsString());

        // Собираем новый Epic
        Epic epic = new Epic(name, description, status);

        // Если пришёл непустой id — это обновление
        if (json.has("id") && json.get("id").getAsInt() != 0) {
            epic.setId(json.get("id").getAsInt());
            taskManager.updateEpic(epic);
            sendText(exchange, "Эпик обновлён", 200);

        } else {
            // Новый эпик
            taskManager.addEpic(epic);
            sendText(exchange, "Эпик создан", 201);
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/epics/\\d+")) {
            // DELETE /epics/{id}
            int id = Integer.parseInt(path.split("/")[2]);
            taskManager.removeEpicById(id);
            sendText(exchange, "Эпик удалён", 200);

        } else if (path.equals("/epics")) {
            // DELETE /epics
            taskManager.removeAllEpics();
            sendText(exchange, "Все эпики удалены", 200);

        } else {
            sendNotFound(exchange);
        }
    }
}
