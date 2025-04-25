package managers;

import org.junit.jupiter.api.BeforeEach;
import java.io.File;
import java.io.IOException;

public class FileBackedTaskManagerTest
        extends TaskManagerTest<FileBackedTaskManager> {

    private File file;

    @BeforeEach
    @Override
    public void setUp() {
        try {
            file = File.createTempFile("tempFile", ".csv");
            file.deleteOnExit();
            taskManager = new FileBackedTaskManager(file, new InMemoryHistoryManager());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
