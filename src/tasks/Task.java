package tasks;

import managers.TypeOfTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private Status status;
    private int id;
    protected TypeOfTask type;
    private Duration duration;
    private LocalDateTime startTime;

    // Основной конструктор
    public Task(String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = TypeOfTask.TASK;
        this.duration = duration;
        this.startTime = startTime;
    }

    // Делегируем в основной, чтобы type всегда = TASK
    public Task(String name, String description, Status status) {
        this(name, description, status, null, null);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int newId) {
        id = newId;
    }

    public TypeOfTask getType() {
        return type;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public boolean overlapsWith(Task otherTask) {
        if (this.startTime == null || otherTask.startTime == null || this.duration == null || otherTask.duration == null) {
            return false;
        }
        LocalDateTime thisEnd = this.startTime.plus(this.duration);
        LocalDateTime otherEnd = otherTask.startTime.plus(otherTask.duration);
        return this.startTime.isBefore(otherEnd) && thisEnd.isAfter(otherTask.startTime);
    }


    @Override
    public String toString() {
        return "tasks.Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", id=" + id +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", endTime=" + getEndTime() +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return id == task.id && // Сравнение по id
                Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                Objects.equals(status, task.status) &&
                Objects.equals(duration, task.duration) &&
                Objects.equals(startTime, task.startTime);

    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status, id, duration, startTime);
    }
}
