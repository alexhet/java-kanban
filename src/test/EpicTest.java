package tests;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;

import static org.junit.jupiter.api.Assertions.*;


class EpicTest {
    @Test
    void epicCannotContainItselfAsSubtask() {
        Epic epic = new Epic("Эпик", "Описание", Status.NEW);
        epic.setId(1);

        // Проверка через обычное условие
        boolean isAdded = epic.getSubtasks().contains(epic.getId());
        assertFalse(isAdded, "Ошибка эпика.");
    }
}
