package tests;

import managers.HistoryManager;
import managers.InMemoryHistoryManager;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    @Test
    void historyMaintainsPreviousTaskState() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("Задача", "Описание", Status.NEW);
        task.setId(1);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать одну задачу.");
        assertEquals(task, history.get(0), "История должна содержать корректную задачу.");
    }
}
