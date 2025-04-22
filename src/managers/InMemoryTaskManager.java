package managers;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemoryTaskManager implements TaskManager {
    private int idCounter = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public int createId() {
        return idCounter++;
    }

    @Override
    public void addTask(Task task) {
        if (isOverlapping(task)) {
            throw new IllegalArgumentException("Задача пересекается по времени с существующей");
        }
        task.setId(createId());
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateTask(Task task) {
        if (isOverlapping(task)) {
            throw new IllegalArgumentException("Обновлённая задача пересекается по времени");
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public Task getTask(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.clear();
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeTaskById(int id) {
        historyManager.add(tasks.get(id));
        tasks.remove(id);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(createId());
        epics.put(epic.getId(), epic);
        historyManager.add(epic);
    }

    @Override
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

    @Override
    public void removeSubtaskId(int id) {
        Subtask subtask = subtasks.get(id);

        if (subtask != null) {

            Epic epic = epics.get(subtask.getEpicId());

            if (epic != null) {
                epic.getSubtasks().remove(Integer.valueOf(id));
                updateStatusForEpic(epic);
            }

            historyManager.add(subtask);
        } else {
            System.out.println("Error");
        }
    }


    @Override
    public void updateStatusForEpic(Epic epic) {
        if (epic.getSubtasks().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        List<Status> subtaskStatuses = epic.getSubtasks().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .map(Subtask::getStatus)
                .collect(Collectors.toList());

        boolean isAllNew = subtaskStatuses.stream().allMatch(status -> status == Status.NEW);
        boolean isAllDone = subtaskStatuses.stream().allMatch(status -> status == Status.DONE);

        if (isAllNew) {
            epic.setStatus(Status.NEW);
        } else if (isAllDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (subtask.getEpicId() == subtask.getId()) {
            throw new IllegalArgumentException("Эпик не может быть подзадачей самого себя");
        }
        if (isOverlapping(subtask)) {
            throw new IllegalArgumentException("Подзадача пересекается по времени");
        }
        subtask.setId(createId());
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.getSubtasks().add(subtask.getId());
            updateStatusForEpic(epic);
            epic.calculateTime(getEpicSubtasks(epic.getId()));
        }
    }


    @Override
    public List<Subtask> getAllSubtask() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (isOverlapping(subtask)) {
            throw new IllegalArgumentException("Обновлённая подзадача пересекается по времени");
        }
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            updateStatusForEpic(epic);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        } else {
            System.out.println("Error");
        }
    }


    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return new ArrayList<>();
        }

        historyManager.add(epic);

        return epic.getSubtasks().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    @Override
    public List<Epic> getAllEpic() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public boolean isOverlapping(Task newTask) {
        return Stream.concat(tasks.values().stream(), subtasks.values().stream())
                .filter(task -> task.getStartTime() != null && task.getDuration() != null)
                .anyMatch(existingTask -> existingTask.overlapsWith(newTask));
    }
}
