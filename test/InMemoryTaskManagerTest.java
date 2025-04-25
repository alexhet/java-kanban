import managers.InMemoryHistoryManager;
import managers.InMemoryTaskManager;
import managers.TaskManagerTest;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    @Override
    public void setUp() {
        // История нужна только для TaskManager; она не влияет на логику add/update/remove/...
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
    }
}