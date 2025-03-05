package tasks;

import managers.TypeOfTask;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtasks;

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        this.subtasks = new ArrayList<>();
        this.type = TypeOfTask.EPIC;
    }

    public List<Integer> getSubtasks() {
        return subtasks;
    }

    public void addSubtaskId(int id) {
        subtasks.add(id);
    }



    @Override
    public String toString() {
        return "tasks.Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", subTasks=" + subtasks +
                ", status=" + getStatus() +
                ", id=" + getId() +
                '}';
    }
}
