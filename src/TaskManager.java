import java.util.List;

public interface TaskManager {
    int createId();

    void addTask(Task task);

    void updateTask(Task task);

    Task getTask(int id);

    List<Task> getAllTasks();

    void removeAllTasks();

    void removeAllSubtasks();

    void removeAllEpics();

    void removeTaskById(int id);

    void addEpic(Epic epic);

    void removeEpicById(int id);

    void removeSubtaskId(int id);

    void updateStatusForEpic(Epic epic);

    void addSubtask(Subtask subtask);

    List<Subtask> getAllSubtask();

    Subtask getSubtaskById(int id);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    List<Subtask> getEpicSubtasks(int epicId);

    List<Epic> getAllEpic();

    List<Task> getHistory();
}
