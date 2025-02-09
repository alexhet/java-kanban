import managers.HistoryManager;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ManagerTest {
    @Test
    void defaultManagersAreInitialized() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "managers.TaskManager должен быть инициализирован.");

        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "managers.HistoryManager должен быть инициализирован.");
    }
}
