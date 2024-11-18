import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private int idCounter = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private int createId() {
        return idCounter++;
    }

    public void addTask(Task task) {
        task.setId(createId());
        tasks.put(task.getId(), task);
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
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

    public void removeAllSubtasks() {
        subtasks.clear();
    }

    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void addEpic(Epic epic) {
        epic.setId(createId());
        epics.put(epic.getId(), epic);
    }

    public void removeEpicById(int id) {
        Epic epic = epics.get(id);

        if (epic != null) {
            for (int subtaskId : epic.getSubtasks()) {
                subtasks.remove(subtaskId);
            }

            epics.remove(id);
        } else {
            System.out.println("Error");
        }
    }

    public void removeSubtaskId(int id) {
        Subtask subtask = subtasks.get(id);

        if (subtask != null) {

            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.getSubtasks().remove(Integer.valueOf(id));
                updateStatusForEpic(epic);
            }

            subtasks.remove(id);
        } else {
            System.out.println("Error");
        }
    }


    public void updateStatusForEpic(Epic epic) {
        if (epic.getSubtasks().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean isAllNew = true;
        boolean isAllDone = true;

        for (int id : epic.getSubtasks()) {
            Subtask subtask = subtasks.get(id);

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
            epic.setStatus(Status.NEW);
        } else if (isAllDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }

    }

    public void addSubtask(Subtask subtask) {
        subtask.setId(createId());
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.getSubtasks().add(subtask.getId());
            updateStatusForEpic(epic);
        } else {
            System.out.println("Error");
        }
    }

    public List<Subtask> getAllSubtask() {
        return new ArrayList<>(subtasks.values());
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);

            Epic epic = epics.get(subtask.getEpicId());

            if (epic != null) {
                updateStatusForEpic(epic);
            }
        } else {
            System.out.println("Error");
        }
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        } else {
            System.out.println("Error");
        }
    }


    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        List<Subtask> epicSubtasks = new ArrayList<>();
        if (epic != null) {
            for (int subtaskId : epic.getSubtasks()) {
                Subtask subtask = subtasks.get(subtaskId);
                if (subtask != null) {
                    epicSubtasks.add(subtaskId, subtask);
                }
            }
        }
        return epicSubtasks;
    }

    public List<Epic> getAllEpic() {
        return new ArrayList<>(epics.values());
    }

}
