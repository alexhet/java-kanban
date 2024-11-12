public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Переезд в новую квартиру", "Упаковать вещи", Status.NEW);
        Task task2 = new Task("Запись к врачу", "Записаться на прием.", Status.NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Epic epic1 = new Epic("Подготовка к отпуску", "", Status.NEW);
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Забронировать билеты", "Выбрать даты и купить авиабилеты.", Status.NEW);
        Subtask subtask2 = new Subtask("Найти жилье", "Поиск и бронирование отеля или квартиры.", Status.NEW);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        epic1.addSubtaskId(subtask1.getId());
        epic1.addSubtaskId(subtask2.getId());

        Epic epic2 = new Epic("Организация семейного праздника", "", Status.NEW);
        taskManager.addEpic(epic2);

        Subtask subtask3 = new Subtask("Позвать гостей", "Подумать, кто будет приглашен", Status.NEW);
        taskManager.addSubtask(subtask3);

        epic2.addSubtaskId(subtask3.getId());

        System.out.println("Список всех задач:\n" + taskManager.getAllTasks());

        System.out.println("\nСписок всех эпиков:\n" + taskManager.getAllEpic());

        System.out.println("\nСписок всех подзадач:");
        taskManager.getAllSubtask();

        //Изменение статуса задач и подзадач
        taskManager.updateTask(task1, Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1, Status.DONE);
        taskManager.updateSubtask(subtask2, Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask3, Status.DONE);

        System.out.println("\nСтатусы после обновлений:");
        System.out.println("Task_1: " + task1);
        System.out.println("Task_2: " + task2);
        System.out.println("Epic_1: " + epic1);
        System.out.println("Epic_2: " + epic2);

        System.out.println("\nУдаление задачи и эпика:");
        taskManager.removeTaskById(task1.getId());
        taskManager.removeEpicById(epic1.getId());

        System.out.println("\nСписок всех задач после удаления:");
        taskManager.getAllTasks();

        System.out.println("\nСписок всех эпиков после удаления:");
        taskManager.getAllEpic();
    }
}
