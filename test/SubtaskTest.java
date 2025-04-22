import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Subtask;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    private LocalDateTime base;
    private int epicId = 1;

    @BeforeEach
    void setUp() {
        base = LocalDateTime.of(2023,1,1,10,0);
    }

    @Test
    void cannotHaveItselfAsEpic() {
        Subtask s = new Subtask("Сабтаска","", Status.NEW, epicId);
        s.setId(epicId);
        assertFalse(s.getEpicId() == s.getId(),
                "Подзадача не может быть своим же эпиком.");
    }

    @Test
    void partialOverlap() {
        Subtask a = new Subtask("Сабтаска1","", Status.NEW, 1,
                Duration.ofMinutes(60), base);
        Subtask b = new Subtask("Сабтаска2","", Status.NEW, 1,
                Duration.ofMinutes(60), base.plusMinutes(30));
        assertTrue(a.overlapsWith(b));
        assertTrue(b.overlapsWith(a));
    }

    @Test
    void fullOverlap() {
        Subtask a = new Subtask("Сабтаска1","", Status.NEW, 1,
                Duration.ofMinutes(60), base);
        Subtask b = new Subtask("Сабтаска2","", Status.NEW, 1,
                Duration.ofMinutes(60), base);
        assertTrue(a.overlapsWith(b));
    }

    @Test
    void containedOverlap() {
        Subtask a = new Subtask("Сабтаска1","", Status.NEW, 1,
                Duration.ofMinutes(120), base);
        Subtask b = new Subtask("Сабтаска2","", Status.NEW, 1,
                Duration.ofMinutes(30), base.plusMinutes(30));
        assertTrue(a.overlapsWith(b));
    }

    @Test
    void noOverlap() {
        Subtask a = new Subtask("Сабтаска1","", Status.NEW, 1,
                Duration.ofMinutes(60), base);
        Subtask b = new Subtask("Сабтаска2","", Status.NEW, 1,
                Duration.ofMinutes(60), base.plusHours(2));
        assertFalse(a.overlapsWith(b));
    }

    @Test
    void touchingNoOverlap() {
        Subtask a = new Subtask("Сабтаска1","", Status.NEW, 1,
                Duration.ofMinutes(60), base);
        Subtask b = new Subtask("Сабтаска2","", Status.NEW, 1,
                Duration.ofMinutes(60), base.plusMinutes(60));
        assertFalse(a.overlapsWith(b));
    }

    @Test
    void nullStartOrNullDurationNoOverlap() {
        Subtask a = new Subtask("Сабтаска1","", Status.NEW, 1, null, base);
        Subtask b = new Subtask("Сабтаска2","", Status.NEW, 1,
                Duration.ofMinutes(60), null);
        assertFalse(a.overlapsWith(b));
        assertFalse(b.overlapsWith(a));
    }

    @Test
    void zeroDurationNoOverlap() {
        Subtask a = new Subtask("Сабтаска1","", Status.NEW, 1,
                Duration.ZERO, base);
        Subtask b = new Subtask("Сабтаска2","", Status.NEW, 1,
                Duration.ofMinutes(60), base);
        assertFalse(a.overlapsWith(b));
    }
}
