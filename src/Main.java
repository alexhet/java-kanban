import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        // Создаем managers.TaskManager
        TaskManager taskManager = Managers.getDefault();

        // Создаем задачи и добавляем их в managers.TaskManager
        Task task1 = new Task("Закупка продуктов", "Купить все для праздничного ужина.", Status.NEW, Duration.ofMinutes(60), LocalDateTime.now());
        Task task2 = new Task("Планирование тренировки", "Составить план тренировок на месяц.", Status.NEW, Duration.ofMinutes(60), LocalDateTime.now());
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        // Создаем эпики
        Epic epic1 = new Epic("Подготовка к экзаменам", "Изучить все важные темы и материалы.", Status.NEW);
        taskManager.addEpic(epic1);

        // Создаем подзадачи
        Subtask subtask1 = new Subtask("Изучить лекции", "Посмотреть все лекции по теме.", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Прочитать материалы", "", Status.NEW, epic1.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        // Создаем второй эпик
        Epic epic2 = new Epic("Организация конференции", "Подготовка к научной конференции.", Status.NEW);
        taskManager.addEpic(epic2);

        // Создаем подзадачу для второго эпика
        Subtask subtask3 = new Subtask("Забронировать зал", "Найти и забронировать зал для конференции.", Status.NEW, epic2.getId());
        taskManager.addSubtask(subtask3);

        // Используем метод для вывода всех задач, эпиков и истории
        printAllTasks(taskManager);

        // Дополнительные действия: обновление статусов и удаления
        task1.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task1);

        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);

        subtask2.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask2);

        subtask3.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask3);

        // Пример обновленных данных
        System.out.println("\nСтатусы после обновлений:");
        System.out.println("Task_1: " + task1);
        System.out.println("Task_2: " + task2);
        System.out.println("Epic_1: " + epic1);
        System.out.println("Epic_2: " + epic2);

        // Пример удаления задачи и эпика
        System.out.println("\nУдаление задачи и эпика:");
        taskManager.removeTaskById(task1.getId());
        taskManager.removeEpicById(epic1.getId());

        System.out.println("\nСписок всех задач после удаления:");
        printAllTasks(taskManager);  // Выводим обновленные данные
    }

    private static void printAllTasks(TaskManager manager) {
        // Вывод всех задач
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        // Вывод всех эпиков и их подзадач
        System.out.println("Эпики:");
        for (Epic epic : manager.getAllEpic()) {
            System.out.println(epic);
            for (Subtask subtask : manager.getEpicSubtasks(epic.getId())) {
                System.out.println("--> " + subtask);
            }
        }

        // Вывод всех подзадач
        System.out.println("Подзадачи:");
        for (Subtask subtask : manager.getAllSubtask()) {
            System.out.println(subtask);
        }

        // Вывод истории
        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
