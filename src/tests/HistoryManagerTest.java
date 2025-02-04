package tests;

import managers.HistoryManager;
import managers.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager(); 
    }

    @Test
    void historyMaintainsPreviousTaskState() {
        Task task = new Task("Задача", "Описание", Status.NEW);
        task.setId(1);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать одну задачу.");
        assertEquals(task, history.get(0), "История должна содержать корректную задачу.");
    }

    @Test
    void testRemoveTask() {
        Task task1 = new Task("Задача1", "Описание1", Status.NEW);
        task1.setId(1);
        Task task2 = new Task("Задача2", "Описание2", Status.NEW);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size(), "После удаления одной задачи в истории должна остаться одна задача.");
        assertEquals(task1, history.get(0), "В истории должна остаться задача task1.");
    }

    @Test
    void testAddTask() {
        Task task = new Task("Задача", "Описание", Status.NEW);
        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size());
        assertEquals(task, historyManager.getHistory().get(0));
    }
}
