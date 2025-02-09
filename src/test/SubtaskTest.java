package tests;

import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Subtask;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    @Test
    void subtaskCannotHaveItselfAsEpic() {
        Subtask subtask = new Subtask("Подзадача", "Описание", Status.NEW, 1);
        subtask.setId(2);

        // Проверка на то, что эпик не равен id подзадачи
        boolean isInvalidEpic = subtask.getEpicId() == subtask.getId();
        assertFalse(isInvalidEpic, "Подзадача не может быть своим же эпиком.");
    }
}
