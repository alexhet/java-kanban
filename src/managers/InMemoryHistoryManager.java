package managers;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    static class Node<T> {

        public T data;
        public Node<T> next;
        public Node<T> prev;
        public Node(T data) {
            this.data = data;
            this.next = null;
            this.prev = null;
        }

    }
    private Node<Task> head;

    private Node<Task> tail;
    private int size = 0;
    private final Map<Integer, Node<Task>> historyMap = new HashMap<>();

    public void addLast(Task task) {
        Node<Task> newNode = new Node<>(task);
        if (tail == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        size++;

        historyMap.put(task.getId(), newNode);
    }

    @Override
    public void add(Task task) {
        if (historyMap.containsKey(task.getId())) {
            remove(task.getId());
        } else {
            addLast(task);
        }
    }

    @Override
    public void remove(int id) {
        Node<Task> removeElement = historyMap.remove(id);
        removeNode(removeElement);
    }

    private void removeNode(Node<Task> node) {
        if (node == null) return;

        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }

        size--;
    }

    @Override
    public List<Task> getHistory() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> current = head;
        while (current != null) {
            tasks.add(current.data);
            current = current.next;
        }
        return tasks;
    }
}
