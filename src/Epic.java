import java.util.ArrayList;

public class Epic extends Task{
    private final ArrayList<Integer> subtasks;
    private TaskManager taskManager;

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        this.subtasks = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtasks() {
        return subtasks;
    }

    public void addSubtaskId(int id) {
        subtasks.add(id);
    }



    @Override
    public String toString() {
        String subtasksString = "";

        for (int subtaskId : subtasks) {
            Subtask subtask = TaskManager.getSubtaskById(subtaskId);
            if (subtask != null) {
                subtasksString = subtasksString + subtask.toString() + ", ";
            }
        }

        return "Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", subTasks=" + subtasksString +
                ", status=" + getStatus() +
                ", id=" + getId() +
                '}';
    }
}
