package managers;

import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected LocalDateTime baseTime;

    @BeforeEach
    public abstract void setUp();

    @Test
    public void testAddAndGetTask() {
        Task task = new Task("Таск", "Описание", Status.NEW);
        taskManager.addTask(task);
        Task retrieved = taskManager.getTask(task.getId());
        assertEquals(task, retrieved, "Задача должна быть доступна по ID");
    }

    @Test
    public void testUpdateTask() {
        Task task = new Task("Таск", "Описание", Status.NEW);
        taskManager.addTask(task);
        task.setName("Обновленное название");
        taskManager.updateTask(task);
        Task retrieved = taskManager.getTask(task.getId());
        assertEquals("Обновленное название", retrieved.getName(), "Имя задачи должно быть обновлено");
    }

    @Test
    public void testDeleteTask() {
        Task task = new Task("Таск", "Описание", Status.NEW);
        taskManager.addTask(task);
        taskManager.removeTaskById(task.getId());
        assertNull(taskManager.getTask(task.getId()), "Задача должна быть удалена");
    }

    @Test
    public void testAddAndGetEpic() {
        Epic epic = new Epic("Эпик", "Описание", Status.NEW);
        taskManager.addEpic(epic);
        Epic retrieved = getEpicById(epic.getId());
        assertEquals(epic, retrieved, "Эпик должен быть доступен по ID");
    }

    @Test
    public void testAddSubtaskToEpic() {
        Epic epic = new Epic("Эпик", "Описание", Status.NEW);
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Сабтаска", "Описание", Status.NEW, epic.getId());
        taskManager.addSubtask(subtask);
        List<Subtask> subtasks = taskManager.getEpicSubtasks(epic.getId());
        assertFalse(subtasks.isEmpty(), "Сабтаска должна быть добавлена к эпику");
        assertEquals(subtask, subtasks.get(0), "Сабтаска должна быть корректно связана с эпиком");
    }

    @Test
    public void testSubtaskRequiresValidEpic() {
        Subtask subtask = new Subtask("Сабтаска", "Описание", Status.NEW, 999); // Несуществующий ID у Эпика
        assertThrows(IllegalArgumentException.class, () -> {
            taskManager.addSubtask(subtask);
        }, "Добавление подзадачи с несуществующим ID эпика");
    }

    @Test
    public void testEpicStatusCalculation() {
        Epic epic = new Epic("Эпик", "Описание", Status.NEW);
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Сабтаска1", "Описание", Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Сабтаска2", "Описание", Status.NEW, epic.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        Epic updatedEpic = getEpicById(epic.getId());
        assertEquals(Status.NEW, updatedEpic.getStatus(), "Статус должен быть NEW, если все подзадачи NEW");

        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);
        updatedEpic = getEpicById(epic.getId());
        assertEquals(Status.DONE, updatedEpic.getStatus(), "Статус должен быть DONE, если все подзадачи DONE");

        subtask1.setStatus(Status.NEW);
        taskManager.updateSubtask(subtask1);
        updatedEpic = getEpicById(epic.getId());
        assertEquals(Status.IN_PROGRESS, updatedEpic.getStatus(), "Статус должен быть IN_PROGRESS, если статусы NEW и DONE");

        subtask2.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask2);
        updatedEpic = getEpicById(epic.getId());
        assertEquals(Status.IN_PROGRESS, updatedEpic.getStatus(), "Статус олжен быть IN_PROGRESS, если есть подзадача IN_PROGRESS");
    }

    @Test
    public void testTaskIntervalOverlap() {
        baseTime = LocalDateTime.of(2025, 4, 1, 10, 0);
        Task task1 = new Task("Таск1", "Описание", Status.NEW, Duration.ofMinutes(60), baseTime);
        Task task2 = new Task("Таск2", "Описание", Status.NEW, Duration.ofMinutes(60), baseTime.plusMinutes(30));
        taskManager.addTask(task1);
        assertThrows(IllegalArgumentException.class, () -> {
            taskManager.addTask(task2);
        }, "Добавление задачи с пересекающимся временным интервалом должно вызывать исключение");
    }

    private Epic getEpicById(int id) {
        return taskManager.getAllEpic().stream()
                .filter(epic -> epic.getId() == id)
                .findFirst()
                .orElse(null);
    }
}