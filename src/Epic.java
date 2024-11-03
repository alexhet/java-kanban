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

    public void updateStatusForEpic() {
        if (subtasks.isEmpty()) {
            setStatus(Status.NEW);
            return;
        }

        boolean isAllNew = true;
        boolean isAllDone = true;

        for (int id : subtasks) {
            Subtask subtask = TaskManager.getSubtaskById(id);

            if (subtask != null) {
                if (subtask.getStatus() != Status.NEW) {
                    isAllNew = false;
                }
                if (subtask.getStatus() != Status.DONE) {
                    isAllDone = false;
                }
            }
        }

        if(isAllNew) {
            setStatus(Status.NEW);
        } else if (isAllDone) {
            setStatus(Status.DONE);
        } else {
            setStatus(Status.IN_PROGRESS);
        }

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
