package com.example.userservice;

import com.example.userservice.config.HibernateUtil;
import com.example.userservice.dao.UserDao;
import com.example.userservice.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserDao userDao = new UserDao();

    public static void main(String[] args) {
        logger.info("User Service запущен");
        try {
            runMenu();
        } catch (Exception exception) {
            logger.error("Критическая ошибка приложения", exception);
            System.err.println("Ошибка приложения: " + exception.getMessage());
        } finally {
            HibernateUtil.shutdown();
            scanner.close();
            logger.info("User Service остановлен");
        }
    }

    private static void runMenu() {
        while (true) {
            System.out.println("\n========== USER SERVICE ==========");
            System.out.println("1. Создать пользователя");
            System.out.println("2. Найти пользователя по ID");
            System.out.println("3. Показать всех пользователей");
            System.out.println("4. Обновить пользователя");
            System.out.println("5. Удалить пользователя");
            System.out.println("0. Выход");
            int choice = readInt("Выберите действие: ");

            try {
                switch (choice) {
                    case 1 -> createUser();
                    case 2 -> findUser();
                    case 3 -> showAllUsers();
                    case 4 -> updateUser();
                    case 5 -> deleteUser();
                    case 0 -> {
                        return;
                    }
                    default -> System.out.println("Неизвестная команда.");
                }
            } catch (UserDao.DataAccessException exception) {
                System.out.println("Ошибка работы с базой данных: " + exception.getMessage());
                logger.warn("Операция с БД завершилась ошибкой", exception);
            } catch (IllegalArgumentException exception) {
                System.out.println("Некорректные данные: " + exception.getMessage());
            }
        }
    }

    private static void createUser() {
        String name = readText("Имя: ");
        String email = readText("Email: ");
        int age = readInt("Возраст: ");
        validate(name, email, age);

        User user = userDao.create(new User(name, email, age));
        System.out.println("Пользователь создан: " + user);
    }

    private static void findUser() {
        long id = readLong("ID пользователя: ");
        Optional<User> user = userDao.findById(id);
        System.out.println(user.map(User::toString).orElse("Пользователь не найден."));
    }

    private static void showAllUsers() {
        List<User> users = userDao.findAll();
        if (users.isEmpty()) {
            System.out.println("Список пользователей пуст.");
            return;
        }
        users.forEach(System.out::println);
    }

    private static void updateUser() {
        long id = readLong("ID пользователя: ");
        String name = readText("Новое имя: ");
        String email = readText("Новый email: ");
        int age = readInt("Новый возраст: ");
        validate(name, email, age);

        boolean updated = userDao.update(id, name, email, age);
        System.out.println(updated ? "Пользователь обновлён." : "Пользователь не найден.");
    }

    private static void deleteUser() {
        long id = readLong("ID пользователя: ");
        boolean deleted = userDao.delete(id);
        System.out.println(deleted ? "Пользователь удалён." : "Пользователь не найден.");
    }

    private static void validate(String name, String email, int age) {
        if (name.isBlank() || name.length() > 100) {
            throw new IllegalArgumentException("имя не должно быть пустым и должно содержать не более 100 символов");
        }
        if (email.isBlank() || email.length() > 150 || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("укажите корректный email (до 150 символов)");
        }
        if (age < 0 || age > 150) {
            throw new IllegalArgumentException("возраст должен быть в диапазоне от 0 до 150");
        }
    }

    private static String readText(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readText(prompt));
            } catch (NumberFormatException exception) {
                System.out.println("Введите целое число.");
            }
        }
    }

    private static long readLong(String prompt) {
        while (true) {
            try {
                return Long.parseLong(readText(prompt));
            } catch (NumberFormatException exception) {
                System.out.println("Введите целое число.");
            }
        }
    }
}
