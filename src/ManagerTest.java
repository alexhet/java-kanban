import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ManagerTest {
    @Test
    void defaultManagersAreInitialized() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "TaskManager должен быть инициализирован.");

        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "HistoryManager должен быть инициализирован.");
    }
}
