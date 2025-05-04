package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = new Gson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("GET")) {
            sendNotFound(exchange);
            return;
        }

        if (taskManager instanceof InMemoryTaskManager) {
            List<Task> prioritizedTasks = ((InMemoryTaskManager) taskManager).getPrioritizedTasks();
            sendText(exchange, gson.toJson(prioritizedTasks), 200);
        } else {
            sendInternalError(exchange);
        }
    }
}