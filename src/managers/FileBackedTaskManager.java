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
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        super(new InMemoryHistoryManager());
        this.file = file;
    }


    protected void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            bufferedWriter.write("id,type,name,status,description,epic\n");

            for (Task task : getAllTasks()) {
                bufferedWriter.write(toString(task) + "\n");
            }

            for (Subtask subtask : getAllSubtask()) {
                bufferedWriter.write(toString(subtask) + "\n");
            }

            for (Epic epic : getAllEpic()) {
                bufferedWriter.write(toString(epic) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка в сохранении файла");
        }
    }

    private String toString(Task task) {
        String description = task.getDescription().replace(",", " "); //добавил в случае, если в описании будет запятая
        return String.format("%d,%s,%s,%s,%s,%s",
                task.getId(), task.getType(), task.getName(), task.getStatus(), description,
                (task instanceof Subtask) ? ((Subtask) task).getEpicId() : ""
        );
    }

    private Task fromString(String line) {
        String[] parts = line.split(",", 6);

        int id = Integer.parseInt(parts[0]);
        TypeOfTask type = TypeOfTask.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];

        switch (type) {
            case TASK:
                return new Task(name, description, status);
            case SUBTASK:
                int epicId = Integer.parseInt(parts[5]);
                return new Subtask(name, description, status, epicId);
            case EPIC:
                return new Epic(name, description, status);
            default:
                throw new IllegalArgumentException("Задача неизвестного типа: " + type);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
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
        } catch (IOException e) {
            throw new ManagerSaveException("При загрузке файла произошла ошибка: ", e);
        }
        return manager;
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

    public static void main(String[] args) {
        File file = new File("tasks.csv");

        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        Task task = new Task("Task1", "Description task", Status.NEW);
        Epic epic = new Epic("Epic1", "Description epic", Status.IN_PROGRESS);
        Subtask subtask = new Subtask("Subtask", "Description subtask", Status.NEW, epic.getId());

        manager.addTask(task);
        manager.addEpic(epic);
        manager.addSubtask(subtask);

        System.out.println("до сохранения:\n");
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpic());
        System.out.println(manager.getAllSubtask());

        FileBackedTaskManager loaderManager = FileBackedTaskManager.loadFromFile(file);

        System.out.println("после загрузки:\n");
        System.out.println(loaderManager.getAllTasks());
        System.out.println(loaderManager.getAllEpic());
        System.out.println(loaderManager.getAllSubtask());
    }
}
