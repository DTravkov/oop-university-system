package services;

import model.UIMessages;

import java.util.Scanner;

public class InputService {

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
                System.out.println(LanguageService.translate(UIMessages.INPUT_NUMBER_EXPECTED)
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
                LanguageService.translate(UIMessages.INPUT_RANGE_ERROR)
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
                LanguageService.translate(UIMessages.INPUT_YES_NO_EXPECTED)
                + " [" + context + "]"
            );
        }
    }
}