import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void tasksAreEqualIfIdsAreEqual() {
        Task task1 = new Task("tasks.Task 1", "", Status.NEW);
        Task task2 = new Task("tasks.Task 1", "", Status.NEW);
        task1.setId(1);
        task2.setId(1);
        assertEquals(task1, task2, "Tаски с одинаковым ID должны быть равны");
    }

    @Test
    void subtasksAreEqualIfIdsAreEqual() {
        Subtask subtask1 = new Subtask("tasks.Subtask 1", "", Status.NEW, 0);
        Subtask subtask2 = new Subtask("tasks.Subtask 1", "", Status.NEW, 0);
        subtask1.setId(1);
        subtask2.setId(1);
        assertEquals(subtask1, subtask2, "Субтаски с одинаковым ID должны быть равны");
    }
}
