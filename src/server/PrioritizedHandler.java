package server;

import com.sun.net.httpserver.HttpExchange;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import tasks.Task;
import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
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