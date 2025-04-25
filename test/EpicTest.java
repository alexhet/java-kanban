import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private Epic epic;
    private LocalDateTime base;

    @BeforeEach
    void setUp() {
        epic = new Epic("Эпик", "Описание", Status.NEW);
        epic.setId(1);
        base = LocalDateTime.of(2025,4,1,10,0);
    }

    @Test
    void epicCannotContainItself() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                epic.addSubtaskId(epic.getId())
        );
        assertEquals("Эпик не может содержать себя в качестве подзадачи.", ex.getMessage());
    }

    @Test
    void statusNewWhenNoSubtasks() {
        epic.updateStatus(new ArrayList<>());
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void statusNewWhenAllNew() {
        List<Subtask> subs = List.of(
                new Subtask("Сабтаска1","", Status.NEW, 1, Duration.ofMinutes(30), base),
                new Subtask("Сабтаска2","", Status.NEW, 1, Duration.ofMinutes(45), base.plusHours(1))
        );
        epic.updateStatus(subs);
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void statusDoneWhenAllDone() {
        List<Subtask> subs = List.of(
                new Subtask("Сабтаска1","", Status.DONE, 1, Duration.ofMinutes(30), base),
                new Subtask("Сабтаска2","", Status.DONE, 1, Duration.ofMinutes(45), base.plusHours(1))
        );
        epic.updateStatus(subs);
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    void statusInProgressWhenMixed() {
        List<Subtask> subs = List.of(
                new Subtask("Сабтаска1","", Status.NEW, 1, Duration.ofMinutes(30), base),
                new Subtask("Сабтаска2","", Status.DONE, 1, Duration.ofMinutes(45), base.plusHours(1))
        );
        epic.updateStatus(subs);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void statusInProgressWhenAnyInProgress() {
        List<Subtask> subs = List.of(
                new Subtask("Сабтаска1","", Status.IN_PROGRESS, 1, Duration.ofMinutes(30), base),
                new Subtask("Сабтаска2","", Status.NEW, 1, Duration.ofMinutes(45), base.plusHours(1))
        );
        epic.updateStatus(subs);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void timeCalculationCorrect() {
        Subtask a = new Subtask("Сабтаска1","", Status.NEW, 1, Duration.ofMinutes(30), base);
        Subtask b = new Subtask("Сабтаска2","", Status.DONE, 1, Duration.ofMinutes(45), base.plusHours(1));
        epic.calculateTime(List.of(a,b));

        assertEquals(Duration.ofMinutes(75), epic.getDuration());
        assertEquals(base, epic.getStartTime());
        assertEquals(base.plusHours(1).plusMinutes(45), epic.getEndTime());
    }

    @Test
    void timeEmptyWhenNoSubtasks() {
        epic.calculateTime(new ArrayList<>());
        assertNull(epic.getStartTime());
        assertNull(epic.getEndTime());
        assertEquals(Duration.ZERO, epic.getDuration());
    }

    @Test
    void timeIgnoresNulls() {
        Subtask a = new Subtask("Сабтаска1","", Status.NEW, 1);
        Subtask b = new Subtask("Сабтаска2","", Status.DONE, 1, Duration.ofMinutes(45), base);
        epic.calculateTime(List.of(a,b));

        assertEquals(Duration.ofMinutes(45), epic.getDuration());
        assertEquals(base, epic.getStartTime());
        assertEquals(base.plusMinutes(45), epic.getEndTime());
    }

    @Test
    void addSubtaskIdUpdatesList() {
        epic.addSubtaskId(5);
        epic.addSubtaskId(7);
        assertTrue(epic.getSubtasks().containsAll(List.of(5,7)));
    }

    @Test
    void toStringContainsAllFields() {
        List<Subtask> subs = List.of(
                new Subtask("Сабтаска1","", Status.NEW, 1, Duration.ofMinutes(30), base)
        );
        epic.calculateTime(subs);
        epic.updateStatus(subs);
        String s = epic.toString();
        assertTrue(s.contains("E"));
        assertTrue(s.contains("PT30M"));
        assertTrue(s.contains(base.toString()));
    }
}
