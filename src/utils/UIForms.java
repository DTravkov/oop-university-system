package utils;

import model.domain.*;
import model.enumeration.AttestationType;
import model.enumeration.CourseType;
import model.enumeration.NewsUrgencyLevel;
import model.enumeration.TeacherType;
import model.enumeration.TechRequestStatus;
import model.enumeration.UIMessage;
import settings.AppSettings;

import java.util.List;
import java.util.Scanner;

public class UIForms {

    public static String readNonEmpty(Scanner scanner, UIMessage prompt) {
        return readNonEmpty(scanner, Translator.translate(prompt));
    }

    public static String readNonEmpty(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (!input.isEmpty()) {
                return input;
            }

            System.out.println(Translator.translate(UIMessage.MSG_INPUT_EMPTY));
        }
    }

    public static String readOptional(Scanner scanner, UIMessage prompt) {
        System.out.print(Translator.translate(prompt));
        return scanner.nextLine().trim();
    }

    public static int readInt(Scanner scanner, UIMessage prompt) {
        while (true) {
            System.out.print(Translator.translate(prompt));
            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(Translator.translate(UIMessage.MSG_INPUT_NUMBER));
            }
        }
    }

    public static double readDouble(Scanner scanner, UIMessage prompt) {
        while (true) {
            System.out.print(Translator.translate(prompt));
            String input = scanner.nextLine().trim();

            try {
                double value = Double.parseDouble(input);
                return Math.round(value * 100.0) / 100.0;
            } catch (NumberFormatException e) {
                System.out.println(Translator.translate(UIMessage.MSG_INPUT_NUMBER));
            }
        }
    }

    public static int readChoice(Scanner scanner, UIMessage prompt, int min, int max) {
        while (true) {
            System.out.print(Translator.translate(prompt));
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println(Translator.translate(UIMessage.MSG_INPUT_RANGE));
        }
    }

    public static boolean readYesNo(Scanner scanner, UIMessage prompt) {
        while (true) {
            System.out.print(Translator.translate(prompt));
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("y")) return true;
            if (input.equals("n")) return false;

            System.out.println(Translator.translate(UIMessage.MSG_INPUT_YES_NO));
        }
    }
    
    public static TeacherType askTeacherType(Scanner scanner) {
        while (true) {
            System.out.print(Translator.translate(UIMessage.INPUT_TEACHER_TYPE));
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    return TeacherType.LECTURE;
                case "2":
                    return TeacherType.PRACTICE;
                case "3":
                    return TeacherType.BOTH;
                default:
                    System.out.println(Translator.translate(UIMessage.MSG_INVALID_CHOICE));
            }
        }
    }

    public static Class<? extends User> readUserClass(Scanner scanner) {

        List<Class<? extends User>> roles = AppSettings.REGISTRABLE_USER_CLASSES;

        System.out.println(Translator.translate(UIMessage.INPUT_USER_ROLE));
        for (int i = 0; i < roles.size(); i++) {
            System.out.println((i + 1) + "." + roles.get(i).getSimpleName());
        }

        int choice = readChoice(scanner, UIMessage.MENU_CHOOSE, 1, roles.size());
        return roles.get(choice - 1);
    }

    public static TechRequestStatus readTechRequestStatus(Scanner scanner) {

        List<TechRequestStatus> roles = List.of(TechRequestStatus.values());

        System.out.println(Translator.translate(UIMessage.INPUT_USER_ROLE));
        for (int i = 0; i < roles.size(); i++) {
            System.out.println((i + 1) + "." + roles.get(i).toString());
        }

        int choice = readChoice(scanner, UIMessage.MENU_CHOOSE, 1, roles.size());
        return roles.get(choice - 1);
    }

    public static CourseType readCourseType(Scanner scanner) {
        List<CourseType> types = List.of(CourseType.values());

        System.out.println(Translator.translate(UIMessage.INPUT_COURSE_TYPE));
        for (int i = 0; i < types.size(); i++) {
            System.out.println((i + 1) + ". " + types.get(i).name());
        }

        int choice = readChoice(scanner, UIMessage.MENU_CHOOSE, 1, types.size());
        return types.get(choice - 1);
    }

    public static TeacherType readLectureOrPractice(Scanner scanner) {
        System.out.println(Translator.translate(UIMessage.INPUT_COURSE_TEACHER_TYPE));
        System.out.println("1. " + TeacherType.LECTURE.name());
        System.out.println("2. " + TeacherType.PRACTICE.name());

        int choice = readChoice(scanner, UIMessage.MENU_CHOOSE, 1, 2);
        return choice == 1 ? TeacherType.LECTURE : TeacherType.PRACTICE;
    }

    public static AttestationType readAttestationType(Scanner scanner) {
        List<AttestationType> types = List.of(AttestationType.values());

        System.out.println("Choose point type:");
        for (int i = 0; i < types.size(); i++) {
            System.out.println((i + 1) + ". " + types.get(i).getLabel());
        }

        int choice = readChoice(scanner, UIMessage.MENU_CHOOSE, 1, types.size());
        return types.get(choice - 1);
    }

    public static NewsUrgencyLevel readNewsUrgencyLevel(Scanner scanner) {
        while (true) {
            System.out.println("Choose urgency level:");
            System.out.println("1. RESEARCH");
            System.out.println("2. HIGH");
            System.out.println("3. AVERAGE");
            System.out.println("4. LOW");
            int choice = readChoice(scanner, UIMessage.MENU_CHOOSE, 1, 4);
            switch (choice) {
                case 1:
                    return NewsUrgencyLevel.RESEARCH;
                case 2:
                    return NewsUrgencyLevel.HIGH;
                case 3:
                    return NewsUrgencyLevel.AVERAGE;
                case 4:
                    return NewsUrgencyLevel.LOW;
                default:
                    System.out.println(Translator.translate(UIMessage.MSG_INVALID_CHOICE));
            }
        }
    }
}
