import java.util.ArrayList;

public class Epic extends Task{
    private final ArrayList<Integer> subtasks;

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
        return "Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", subTasks=" + subtasks +
                ", status=" + getStatus() +
                ", id=" + getId() +
                '}';
    }
}
