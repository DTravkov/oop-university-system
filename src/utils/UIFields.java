package utils;

import model.TeacherTypeEnum;
import model.UIMessages;
import services.LanguageService;

import java.util.Scanner;

public class UIFields {

    public static String readNonEmpty(Scanner scanner, String context, UIMessages prompt) {
        while (true) {
            System.out.print(LanguageService.translate(prompt));
            String input = scanner.nextLine().trim();

            if (!input.isEmpty()) {
                return input;
            }

            System.out.println(LanguageService.translate(UIMessages.INPUT_EMPTY)
                + " [" + context + "]");
        }
    }

    public static String readOptional(Scanner scanner, UIMessages prompt) {
        System.out.print(LanguageService.translate(prompt));
        return scanner.nextLine().trim();
    }

    public static int readInt(Scanner scanner, String context, UIMessages prompt) {
        while (true) {
            System.out.print(LanguageService.translate(prompt));
            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(LanguageService.translate(UIMessages.INPUT_NUMBER)
                    + " [" + context + "]");
            }
        }
    }

    public static String readChoice(Scanner scanner, String context, int min, int max) {
        while (true) {
            String input = scanner.nextLine().trim();

            if (input.matches("\\d+")) {
                int value = Integer.parseInt(input);

                if (value >= min && value <= max) {
                    return input;
                }
            }

            System.out.println(
                LanguageService.translate(UIMessages.INPUT_RANGE)
                + " [" + context + "]"
            );
        }
    }

    public static boolean readYesNo(Scanner scanner, String context, UIMessages prompt) {
        while (true) {
            System.out.print(LanguageService.translate(prompt));
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("y")) return true;
            if (input.equals("n")) return false;

            System.out.println(
                LanguageService.translate(UIMessages.INPUT_YES_NO)
                + " [" + context + "]"
            );
        }
    }
    
    public static TeacherTypeEnum askTeacherType(Scanner scanner) {
        while (true) {
            System.out.print(LanguageService.translate(UIMessages.TEACHER_TYPE));
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    return TeacherTypeEnum.LECTURE;
                case "2":
                    return TeacherTypeEnum.PRACTICE;
                case "3":
                    return TeacherTypeEnum.BOTH;
                default:
                    System.out.println(LanguageService.translate(UIMessages.INVALID_CHOICE));
            }
        }
    }
}
