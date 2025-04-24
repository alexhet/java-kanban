package managers;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private int idCounter = 0;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager;

    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
    );

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public int createId() {
        return idCounter++;
    }

    private boolean isOverlappingInternal(Task newTask) {
        if (newTask.getStartTime() == null || newTask.getDuration() == null) {
            return false;
        }
        return prioritizedTasks.stream()
                .filter(t -> t.getStartTime() != null && t.getDuration() != null)
                .anyMatch(existing -> existing.overlapsWith(newTask));
    }

    @Override
    public void addTask(Task task) {
        if (isOverlappingInternal(task)) {
            throw new IllegalArgumentException("Задача пересекается по времени");
        }
        task.setId(createId());
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
    }

    @Override
    public void updateTask(Task task) {
        if (isOverlappingInternal(task)) {
            throw new IllegalArgumentException("Обновлённая задача пересекается по времени");
        }
        tasks.put(task.getId(), task);
        prioritizedTasks.removeIf(t -> t.getId() == task.getId());
        prioritizedTasks.add(task);
    }

    @Override
    public Task getTask(int id) {
        Task t = tasks.get(id);
        historyManager.add(t);
        return t;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void removeAllTasks() {
        tasks.values().forEach(prioritizedTasks::remove);
        tasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.values().forEach(prioritizedTasks::remove);
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.getSubtasks().clear();
            updateStatusForEpic(epic);
            epic.calculateTime(Collections.emptyList());
        });
    }

    @Override
    public void removeAllEpics() {
        epics.values().forEach(epic -> {
            epic.getSubtasks().forEach(id -> prioritizedTasks.remove(subtasks.get(id)));
            subtasks.keySet().removeAll(epic.getSubtasks());
        });
        epics.clear();
    }

    @Override
    public void removeTaskById(int id) {
        Task t = tasks.remove(id);
        historyManager.add(t);
        prioritizedTasks.remove(t);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(createId());
        epics.put(epic.getId(), epic);
        historyManager.add(epic);
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            epic.getSubtasks().forEach(subId -> {
                Subtask st = subtasks.remove(subId);
                prioritizedTasks.remove(st);
            });
        }
    }

    @Override
    public void removeSubtaskId(int id) {
        Subtask sub = subtasks.remove(id);
        if (sub != null) {
            historyManager.add(sub);
            prioritizedTasks.remove(sub);
            Epic epic = epics.get(sub.getEpicId());
            if (epic != null) {
                epic.getSubtasks().remove(Integer.valueOf(id));
                updateStatusForEpic(epic);
                epic.calculateTime(getEpicSubtasks(epic.getId()));
            }
        }
    }

    @Override
    public void updateStatusForEpic(Epic epic) {
        List<Status> statuses = epic.getSubtasks().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .map(Subtask::getStatus)
                .collect(Collectors.toList());
        if (statuses.isEmpty()) {
            epic.setStatus(Status.NEW);
        } else if (statuses.stream().allMatch(s -> s == Status.NEW)) {
            epic.setStatus(Status.NEW);
        } else if (statuses.stream().allMatch(s -> s == Status.DONE)) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (!epics.containsKey(subtask.getEpicId())) {
            throw new IllegalArgumentException("Добавление подзадачи с несуществующим ID эпика");
        }

        if (isOverlappingInternal(subtask)) {
            throw new IllegalArgumentException("Подзадача пересекается по времени");
        }
        subtask.setId(createId());
        subtasks.put(subtask.getId(), subtask);
        prioritizedTasks.add(subtask);
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
        Subtask s = subtasks.get(id);
        historyManager.add(s);
        return s;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (isOverlappingInternal(subtask)) {
            throw new IllegalArgumentException("Обновлённая подзадача пересекается по времени");
        }
        subtasks.put(subtask.getId(), subtask);
        prioritizedTasks.removeIf(t -> t.getId() == subtask.getId());
        prioritizedTasks.add(subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            updateStatusForEpic(epic);
            epic.calculateTime(getEpicSubtasks(epic.getId()));
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return Collections.emptyList();
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

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }
}
