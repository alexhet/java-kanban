package server;

import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import tasks.Task;
import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (!exchange.getRequestMethod().equals("GET")) {
                sendNotFound(exchange);
                return;
            }
            List<Task> history = taskManager.getHistory();
            sendText(exchange, gson.toJson(history), 200);
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }
}