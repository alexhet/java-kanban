package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private File file;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        file = File.createTempFile("tempFile", ".csv");
        file.deleteOnExit();
        manager = new FileBackedTaskManager(file, new InMemoryHistoryManager());
    }

    @Test
    void shouldSaveAndLoadEmptyFile() throws IOException {
        manager.save();
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file, new InMemoryHistoryManager());

        assertTrue(loaded.getAllTasks().isEmpty(),    "tasks должен быть пустым");
        assertTrue(loaded.getAllSubtask().isEmpty(),  "subtasks должен быть пустым");
        assertTrue(loaded.getAllEpic().isEmpty(),     "epics должен быть пустым");
    }

    @Test
    void saveAndLoadMultipleTasks() throws IOException {
        Task t1 = new Task("Task1", " ", Status.NEW);
        Task t2 = new Task("Task2", " ", Status.NEW);
        manager.addTask(t1);
        manager.addTask(t2);

        List<String> lines = Files.readAllLines(file.toPath());
        // шапка + 2 задачи
        assertEquals(3, lines.size(), "В файле должно быть 3 строки");
        assertTrue(lines.get(1).contains("Task1"));
        assertTrue(lines.get(2).contains("Task2"));
    }

    @Test
    void loadTasksSuccessfullyFromFile() throws IOException {
        Task t1 = new Task("Таск1", " ", Status.NEW);
        Task t2 = new Task("Таск2", " ", Status.NEW);
        manager.addTask(t1);
        manager.addTask(t2);
        manager.save();

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file, new InMemoryHistoryManager());
        List<Task> loadedTasks = loaded.getAllTasks();

        assertEquals(2, loadedTasks.size(), "Должны загрузиться 2 задачи");
        assertEquals("Таск1", loadedTasks.get(0).getName());
        assertEquals("Таск2", loadedTasks.get(1).getName());
    }

    @Test
    void testGetPrioritizedTasks() {
        Task t1 = new Task("Таск1", "Описание", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025,4,1,10,0));
        Task t2 = new Task("Таск2", "Описание", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025,4,1,9,0));
        Task t3 = new Task("Таск3", "Описание", Status.NEW,
                Duration.ofMinutes(45), LocalDateTime.of(2025,4,1,11,0));

        manager.addTask(t1);
        manager.addTask(t2);
        manager.addTask(t3);

        List<Task> ordered = manager.getPrioritizedTasks();
        assertEquals(List.of(t2, t1, t3), ordered,
                "Приоритизация по startTime должна работать");
    }

    @Test
    void loadFromNonexistentFileShouldThrow() {
        File bad = new File("i_dont_exist.csv");
        assertThrows(IOException.class, () -> {
            FileBackedTaskManager.loadFromFile(bad, new InMemoryHistoryManager());
        }, "Загрузка из несуществующего файла должна выкидывать IOException");
    }
}
