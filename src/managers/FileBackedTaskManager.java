package managers;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file, InMemoryHistoryManager historyManager) {
        super(historyManager);
        this.file = file;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubtaskId(int id) {
        super.removeSubtaskId(id);
        save();
    }

    @Override
    public void updateStatusForEpic(Epic epic) {
        super.updateStatusForEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return super.getPrioritizedTasks();
    }

    /**
     * Загружает менеджер из CSV. Бросает IOException при проблемах с файлом.
     */
    public static FileBackedTaskManager loadFromFile(File file,
                                                     InMemoryHistoryManager historyManager) throws IOException {
        FileBackedTaskManager manager = new FileBackedTaskManager(file, historyManager);
        List<String> lines = Files.readAllLines(file.toPath());
        if (lines.size() < 2) {
            return manager;
        }
        for (int i = 1; i < lines.size(); i++) {
            Task task = manager.fromString(lines.get(i));
            switch (task.getType()) {
                case TASK:
                    manager.addTask(task);
                    break;
                case SUBTASK:
                    manager.addSubtask((Subtask) task);
                    break;
                case EPIC:
                    manager.addEpic((Epic) task);
                    break;
            }
        }
        return manager;
    }

    protected void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,StartTime,duration,epic\n");
            Stream.concat(
                    Stream.concat(
                            getAllTasks().stream(),
                            getAllSubtask().stream()
                    ),
                    getAllEpic().stream()
            ).forEach(task -> {
                try {
                    writer.write(toString(task));
                    writer.newLine();
                } catch (IOException e) {
                    throw new ManagerSaveException("Ошибка при записи задачи в файл", e);
                }
            });
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка в сохранении файла", e);
        }
    }

    private String toString(Task task) {
        String description = task.getDescription().replace(",", " ");
        String startTime = task.getStartTime() == null ? "null" : task.getStartTime().toString();
        String duration = task.getDuration() == null ? "0" : String.valueOf(task.getDuration().toMinutes());
        String epicId = task instanceof Subtask ? String.valueOf(((Subtask) task).getEpicId()) : "";
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s",
                task.getId(), task.getType(), task.getName(), task.getStatus(),
                description, startTime, duration, epicId
        );
    }

    private Task fromString(String line) {
        String[] parts = line.split(",", 8);
        if (parts.length < 8) {
            throw new IllegalArgumentException("Недостаточно данных для разбора строки: " + line);
        }
        int id = Integer.parseInt(parts[0]);
        TypeOfTask type = TypeOfTask.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        String startTimeStr = parts[5];
        long minutes = Long.parseLong(parts[6]);
        Duration duration = Duration.ofMinutes(minutes);
        String epicIdStr = parts[7];

        LocalDateTime startTime = startTimeStr.equals("null") ? null : LocalDateTime.parse(startTimeStr);

        switch (type) {
            case TASK:
                Task task = new Task(name, description, status, duration, startTime);
                task.setId(id);
                return task;
            case SUBTASK:
                if (epicIdStr.isEmpty()) {
                    throw new IllegalArgumentException("ИД Эпика не может быть пустым");
                }
                int epicId = Integer.parseInt(epicIdStr);
                Subtask subtask = new Subtask(name, description, status, epicId, duration, startTime);
                subtask.setId(id);
                return subtask;
            case EPIC:
                Epic epic = new Epic(name, description, status);
                epic.setId(id);
                return epic;
            default:
                throw new IllegalArgumentException("Задача неизвестного типа: " + type);
        }
    }
}
