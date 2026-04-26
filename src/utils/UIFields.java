package utils;

import model.domain.*;
import model.enumeration.TeacherType;
import model.enumeration.UIMessages;
import services.LanguageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UIFields {

    public static String readNonEmpty(Scanner scanner, UIMessages prompt) {
        while (true) {
            System.out.print(LanguageService.translate(prompt));
            String input = scanner.nextLine().trim();

            if (!input.isEmpty()) {
                return input;
            }

            System.out.println(LanguageService.translate(UIMessages.MSG_INPUT_EMPTY));
        }
    }

    public static String readOptional(Scanner scanner, UIMessages prompt) {
        System.out.print(LanguageService.translate(prompt));
        return scanner.nextLine().trim();
    }

    public static int readInt(Scanner scanner, UIMessages prompt) {
        while (true) {
            System.out.print(LanguageService.translate(prompt));
            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(LanguageService.translate(UIMessages.MSG_INPUT_NUMBER));
            }
        }
    }

    public static double readDouble(Scanner scanner, UIMessages prompt) {
        while (true) {
            System.out.print(LanguageService.translate(prompt));
            String input = scanner.nextLine().trim();

            try {
                double value = Double.parseDouble(input);
                return Math.round(value * 100.0) / 100.0;
            } catch (NumberFormatException e) {
                System.out.println(LanguageService.translate(UIMessages.MSG_INPUT_NUMBER));
            }
        }
    }

    public static String readChoice(Scanner scanner, UIMessages prompt, int min, int max) {
        while (true) {
            System.out.print(LanguageService.translate(prompt));
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return input;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println(LanguageService.translate(UIMessages.MSG_INPUT_RANGE));
        }
    }

    public static boolean readYesNo(Scanner scanner, UIMessages prompt) {
        while (true) {
            System.out.print(LanguageService.translate(prompt));
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("y")) return true;
            if (input.equals("n")) return false;

            System.out.println(LanguageService.translate(UIMessages.MSG_INPUT_YES_NO));
        }
    }
    
    public static TeacherType askTeacherType(Scanner scanner) {
        while (true) {
            System.out.print(LanguageService.translate(UIMessages.INPUT_TEACHER_TYPE));
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    return TeacherType.LECTURE;
                case "2":
                    return TeacherType.PRACTICE;
                case "3":
                    return TeacherType.BOTH;
                default:
                    System.out.println(LanguageService.translate(UIMessages.MSG_INVALID_CHOICE));
            }
        }
    }

    public static Class<? extends User> readUserClass(Scanner scanner) {

        List<Class<? extends User>> roles = new ArrayList<>();
        
        roles.add(User.class);
        roles.add(Student.class);
        roles.add(GraduateStudent.class);
        roles.add(Employee.class);
        roles.add(Teacher.class);
        roles.add(Manager.class);
        roles.add(Dean.class);
        roles.add(Admin.class);
        roles.add(TechSupportSpecialist.class);
        roles.add(DeletedUser.class);

        System.out.println(LanguageService.translate(UIMessages.INPUT_USER_ROLE));
        for (int i = 0; i < roles.size(); i++) {
            System.out.println((i + 1) + "." + roles.get(i).getSimpleName());
        }

        String choice = readChoice(scanner, UIMessages.MENU_CHOOSE, 1, roles.size());
        return roles.get(Integer.parseInt(choice) - 1);
    }
}
