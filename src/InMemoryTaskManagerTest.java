import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    @Test
    void addAndGetTaskById() {
        TaskManager taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        Task task = new Task("Задача", "Описание", Status.NEW);
        taskManager.addTask(task);
        Task retrievedTask = taskManager.getTask(task.getId());
        assertEquals(task, retrievedTask, "Задача должна корректно находиться по ID.");
    }

    @Test
    void tasksWithGeneratedAndSpecifiedIdsDoNotConflict() {
        TaskManager taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        Task task1 = new Task("Задача 1", "Описание 1", Status.NEW);
        task1.setId(1);
        taskManager.addTask(task1);

        Task task2 = new Task("Задача 2", "Описание 2", Status.NEW);
        taskManager.addTask(task2);

        assertNotEquals(task1.getId(), task2.getId(), "ID не должны конфликтовать.");
    }

    @Test
    void taskFieldsRemainUnchangedAfterAddition() {
        TaskManager taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        Task task = new Task("Задача", "Описание", Status.NEW);
        taskManager.addTask(task);
        Task retrievedTask = taskManager.getTask(task.getId());

        assertEquals("Задача", retrievedTask.getName(), "Имя задачи должно оставаться неизменным.");
        assertEquals("Описание", retrievedTask.getDescription(), "Описание задачи должно оставаться неизменным.");
        assertEquals(Status.NEW, retrievedTask.getStatus(), "Статус задачи должен оставаться неизменным.");
    }
}
