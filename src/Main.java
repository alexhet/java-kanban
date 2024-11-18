public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Закупка продуктов", "Купить все для праздничного ужина.", Status.NEW);
        Task task2 = new Task("Планирование тренировки", "Составить план тренировок на месяц.", Status.NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Epic epic1 = new Epic("Подготовка к экзаменам", "Изучить все важные темы и материалы.", Status.NEW);
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Изучить лекции", "Посмотреть все лекции по теме.", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Прочитать материалы", "", Status.NEW, epic1.getId());
        taskManager.addSubtask(subtask1, epic1.getId());
        taskManager.addSubtask(subtask2, epic1.getId());

        Epic epic2 = new Epic("Организация конференции", "Подготовка к научной конференции.", Status.NEW);
        taskManager.addEpic(epic2);

        Subtask subtask3 = new Subtask("Забронировать зал", "Найти и забронировать зал для конференции.", Status.NEW, epic2.getId());
        taskManager.addSubtask(subtask3, epic2.getId());

        System.out.println("Список всех задач:\n" + taskManager.getAllTasks());
        System.out.println("\nСписок всех эпиков:\n" + taskManager.getAllEpic());
        System.out.println("\nСписок всех подзадач:\n" + taskManager.getAllSubtask());

        task1.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task1);

        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);

        subtask2.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask2);

        subtask3.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask3);

        System.out.println("\nСтатусы после обновлений:");
        System.out.println("Task_1: " + task1);
        System.out.println("Task_2: " + task2);
        System.out.println("Epic_1: " + epic1);
        System.out.println("Epic_2: " + epic2);

        System.out.println("\nУдаление задачи и эпика:");
        taskManager.removeTaskById(task1.getId());
        taskManager.removeEpicById(epic1.getId());

        System.out.println("\nСписок всех задач после удаления:");
        System.out.println(taskManager.getAllTasks());

        System.out.println("\nСписок всех эпиков после удаления:");
        System.out.println(taskManager.getAllEpic());
    }
}
