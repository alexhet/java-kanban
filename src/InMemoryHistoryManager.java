import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private final static int MAX_SIZE = 10;
    private final ArrayList<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        history.add(task);

        if (history.size() > MAX_SIZE) {
            history.remove(0);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
