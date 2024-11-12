import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private static int idCounter = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private static final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private static int createId() {
        return idCounter++;
    }

    public void addTask(Task task) {
        if (!tasks.equals(task)){
            task.setId(createId());
            tasks.put(task.getId(), task);
        }
    }

    public void updateTask(Task task, Status status) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            task.setStatus(status);
        } else {
            System.out.println("Error");
        }
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void addEpic(Epic epic) {
        if (!epic.equals(epics)) {
            epic.setId(createId());
            epics.put(epic.getId(), epic);
        }
    }

    public void removeEpicById(int id) {
        Epic epic = epics.get(id);

        for (int subtaskId : epic.getSubtasks()) {
            subtasks.remove(subtaskId);
        }

        epics.remove(id);
    }

    public void addSubtask(Subtask subtask) {
        if (!subtasks.equals(subtask)) {
            subtask.setId(createId());
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

    public void addEpicSubtasks(int epicId, Subtask subtask) {
        Epic epic = epics.get(epicId);

        if (!(epic == null)) {
            subtasks.put(subtask.getId(), subtask);
            epic.addSubtaskId(subtask.getId());
        }
    }

    public void updateSubtask(Subtask subtask, Status status) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            subtask.setStatus(status);
        } else {
            System.out.println("Error");
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

    public List<Epic> getAllEpic() {
        return new ArrayList<>(epics.values());
    }

}
