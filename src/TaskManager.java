import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private static final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public void addTask(Task task) {
        if (!tasks.equals(task)){
            tasks.put(task.getId(), task);
        }
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Error");
        }
    }

    public void getTask(int id) {
        if (tasks.containsKey(id)) {
            System.out.println(tasks.get(id));
        } else {
            System.out.println("Error");
        }
    }

    public void updateStatusByTask(int id, Status status) {
        if (!tasks.isEmpty()) {
            Task task = tasks.get(id);
            task.setStatus(status);
        }
    }

    public void getAllTasks() {
        System.out.println(tasks.entrySet());
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void addEpic(Epic epic) {
        if (!epic.equals(epics)) {
            epics.put(epic.getId(), epic);
        }
    }

    public void removeEpicById(int id) {
        epics.remove(id);
    }

    public void addSubtask(Subtask subtask) {
        if (!subtasks.equals(subtask)) {
            subtasks.put(subtask.getId(), subtask);
        }
    }

    public void getAllSubtask() {
        if (!subtasks.isEmpty()) {
            for (Subtask subtask : subtasks.values()) {
                System.out.println(subtask);
            }
        }
    }

    public static Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void getEpicSubtasks(int epicId, Subtask subtask) {
        Epic epic = epics.get(epicId);

        if (!(epic == null)) {
            subtasks.put(subtask.getId(), subtask);
            epic.addSubtaskId(subtask.getId());
        }
    }

    public void updateStatusForSubtask(int id, Status status) {
        Subtask subtask = TaskManager.getSubtaskById(id);

        if (subtask != null) {
            subtask.setStatus(status);
        }

        for (Epic epic : epics.values()) {
            ArrayList<Integer> subtaskId = epic.getSubtasks();

            if (subtaskId.contains(id)) {
                epic.updateStatusForEpic();
                break;
            }
        }
    }


    public HashMap<Integer, Subtask> printEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        HashMap<Integer, Subtask> epicSubtasks = new HashMap<>();
        if (epic != null) {
            for (int subtaskId : epic.getSubtasks()) {
                Subtask subtask = subtasks.get(subtaskId);
                if (subtask != null) {
                    epicSubtasks.put(subtaskId, subtask);
                }
            }
        }
        return epicSubtasks;
    }

    public void getAllEpic() {
        System.out.println(epics);
    }

}
