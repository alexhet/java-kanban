package tasks;

import managers.TypeOfTask;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private final int epicId;

    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status, Duration.ZERO, null);
        this.epicId = epicId;
        this.type = TypeOfTask.SUBTASK;
    }

    public Subtask(String name, String description, Status status, int epicId,
                   Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        this.epicId = epicId;
        this.type = TypeOfTask.SUBTASK;
    }

    @Override
    public void setId(int newId) {
        // Не позволяем устанавливать ID подзадачи равным её epicId
        if (newId == this.epicId) {
            return;
        }
        super.setId(newId);
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", id=" + getId() +
                ", epicId=" + epicId +
                ", startTime=" + getStartTime() +
                ", duration=" + getDuration() +
                '}';
    }
}