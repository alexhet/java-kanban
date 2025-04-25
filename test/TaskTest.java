import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private LocalDateTime base;

    @BeforeEach
    void setUp() {
        base = LocalDateTime.of(2025,4,1,10,0);
    }

    @Test
    void equalById() {
        Task a = new Task("Таск","", Status.NEW);
        Task b = new Task("Таск","", Status.NEW);
        a.setId(5);
        b.setId(5);
        assertEquals(a, b);
    }

    @Test
    void partialOverlap() {
        Task a = new Task("Таск1","", Status.NEW,
                Duration.ofMinutes(60), base);
        Task b = new Task("Таск2","", Status.NEW,
                Duration.ofMinutes(60), base.plusMinutes(30));
        assertTrue(a.overlapsWith(b));
    }

    @Test
    void fullOverlap() {
        Task a = new Task("Таск1","", Status.NEW,
                Duration.ofMinutes(60), base);
        Task b = new Task("Таск2","", Status.NEW,
                Duration.ofMinutes(60), base);
        assertTrue(a.overlapsWith(b));
    }

    @Test
    void containedOverlap() {
        Task a = new Task("Таск1","", Status.NEW,
                Duration.ofMinutes(120), base);
        Task b = new Task("Таск2","", Status.NEW,
                Duration.ofMinutes(30), base.plusMinutes(30));
        assertTrue(a.overlapsWith(b));
    }

    @Test
    void noOverlap() {
        Task a = new Task("Таск1","", Status.NEW,
                Duration.ofMinutes(60), base);
        Task b = new Task("Таск2","", Status.NEW,
                Duration.ofMinutes(60), base.plusHours(2));
        assertFalse(a.overlapsWith(b));
    }

    @Test
    void touchingNoOverlap() {
        Task a = new Task("Таск1","", Status.NEW,
                Duration.ofMinutes(60), base);
        Task b = new Task("Таск2","", Status.NEW,
                Duration.ofMinutes(60), base.plusMinutes(60));
        assertFalse(a.overlapsWith(b));
    }

    @Test
    void nullStartOrNullDurationNoOverlap() {
        Task a = new Task("Таск1","", Status.NEW, null, base);
        Task b = new Task("Таск2","", Status.NEW,
                Duration.ofMinutes(60), null);
        assertFalse(a.overlapsWith(b));
    }

    @Test
    void zeroDurationNoOverlap() {
        Task a = new Task("Таск1","", Status.NEW,
                Duration.ZERO, base);
        Task b = new Task("Таск2","", Status.NEW,
                Duration.ofMinutes(60), base);
        assertFalse(a.overlapsWith(b));
    }
}
