package tasks;

import managers.TypeOfTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Integer> subtasks;
    private Duration duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Epic(String name, String description, Status status) {
        super(name, description, status, Duration.ZERO, null);
        this.subtasks = new ArrayList<>();
        this.type = TypeOfTask.EPIC;
        this.startTime = null;
        this.endTime = null;
    }

    public List<Integer> getSubtasks() {
        return subtasks;
    }

    public void addSubtaskId(int id) {
        System.out.println("Adding subtask ID: " + id + ", Epic ID: " + getId());
        if (id == getId()) {
            throw new IllegalArgumentException("Эпик не может содержать себя в качестве подзадачи.");
        }
        subtasks.add(id);
    }

    public void calculateTime(List<Subtask> subtasks) {
        if (subtasks == null || subtasks.isEmpty()) {
            this.duration = Duration.ZERO;
            this.startTime = null;
            this.endTime = null;
            return;
        }

        Duration totalSubtaskDuration = subtasks.stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);

        LocalDateTime minSubtaskStartTime = subtasks.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime maxSubtaskEndTime = subtasks.stream()
                .filter(sub -> sub.getStartTime() != null && sub.getDuration() != null)
                .map(sub -> sub.getStartTime().plus(sub.getDuration()))
                .max(LocalDateTime::compareTo)
                .orElse(null);

        setDuration(totalSubtaskDuration);
        setStartTime(minSubtaskStartTime);
        setEndTime(maxSubtaskEndTime);
    }

    public void updateStatus(List<Subtask> subtasks) {
        if (subtasks == null || subtasks.isEmpty()) {
            setStatus(Status.NEW);
            return;
        }

        boolean allNew = subtasks.stream().allMatch(sub -> sub.getStatus() == Status.NEW);
        boolean allDone = subtasks.stream().allMatch(sub -> sub.getStatus() == Status.DONE);

        if (allNew) {
            setStatus(Status.NEW);
        } else if (allDone) {
            setStatus(Status.DONE);
        } else {
            setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "tasks.Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", subTasks=" + subtasks +
                ", status=" + getStatus() +
                ", id=" + getId() +
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                ", endTime=" + getEndTime() +
                '}';
    }
}