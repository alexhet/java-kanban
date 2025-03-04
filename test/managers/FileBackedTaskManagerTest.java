package managers;

import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    @Test
    public void shouldSaveAndLoadEmptyFile() throws IOException {
        File file = File.createTempFile("tempFile", ".csv");
        file.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        manager.save();

        FileBackedTaskManager loaderManager = FileBackedTaskManager.loadFromFile(file);

        assertTrue(loaderManager.getAllTasks().isEmpty());
        assertTrue(loaderManager.getAllSubtask().isEmpty());
        assertTrue(loaderManager.getAllEpic().isEmpty());
    }

    @Test
    public void saveAndLoadMultipleTasks() throws IOException {
        File file = File.createTempFile("tempFile", ".csv");
        file.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        Task task1 = new Task("Task1", " ", Status.NEW);
        Task task2 = new Task("Task2", " ", Status.NEW);

        manager.addTask(task1);
        manager.addTask(task2);

        List<String> lines = Files.readAllLines(file.toPath());

        assertEquals(3, lines.size());
        assertTrue(lines.get(1).contains("Task1"));
        assertTrue(lines.get(2).contains("Task2"));
    }

    @Test
    public void loadTasksSuccessfullyFromFile() throws IOException {
        File file = File.createTempFile("tempFile", ".csv");
        file.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        Task task1 = new Task("Task1", " ", Status.NEW);
        Task task2 = new Task("Task2", " ", Status.NEW);

        manager.addTask(task1);
        manager.addTask(task2);
        manager.save();

        FileBackedTaskManager loaderManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(2, loaderManager.getAllTasks().size());
        assertEquals("Task1", loaderManager.getAllTasks().get(0).getName());
        assertEquals("Task2", loaderManager.getAllTasks().get(1).getName());
    }

}