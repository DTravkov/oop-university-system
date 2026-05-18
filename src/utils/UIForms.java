package utils;

import model.domain.*;
import model.enumeration.AttestationType;
import model.enumeration.ComplaintUrgencyLevel;
import model.enumeration.CourseType;
import model.enumeration.LanguagePreference;
import model.enumeration.NewsUrgencyLevel;
import model.enumeration.TeacherType;
import model.enumeration.TechRequestStatus;
import settings.AppSettings;
import utils.UIText;

import java.util.List;
import java.util.Scanner;

import exceptions.DoesNotExist;
import exceptions.ListIsEmpty;

/**
 * UIForms is bunch of static methods that help with taking data from user in app layar.
 */
public class UIForms {

    // BASIC CONSOLE READS

    public static String readNonEmpty(Scanner scanner, UIText prompt) {
        return readNonEmpty(scanner, prompt.localized());
    }

    public static String readNonEmpty(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (!input.isEmpty()) {
                return input;
            }

            System.out.println(UIText.MSG_INPUT_EMPTY.localized());
        }
    }

    public static String readOptional(Scanner scanner, UIText prompt) {
        System.out.print(prompt.localized());
        return scanner.nextLine().trim();
    }

    public static int readInt(Scanner scanner, UIText prompt) {
        while (true) {
            System.out.print(prompt.localized());
            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(UIText.MSG_INPUT_NUMBER.localized());
            }
        }
    }

    public static double readDouble(Scanner scanner, UIText prompt) {
        while (true) {
            System.out.print(prompt.localized());
            String input = scanner.nextLine().trim();

            try {
                double value = Double.parseDouble(input);
                return Math.round(value * 100.0) / 100.0;
            } catch (NumberFormatException e) {
                System.out.println(UIText.MSG_INPUT_NUMBER.localized());
            }
        }
    }

    public static int readChoice(Scanner scanner, UIText prompt, int min, int max) {
        while (true) {
            System.out.print(prompt.localized());
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println(UIText.MSG_INPUT_RANGE.localized());
        }
    }

    public static boolean readYesNo(Scanner scanner, UIText prompt) {
        while (true) {
            System.out.print(prompt.localized());
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("y")) return true;
            if (input.equals("n")) return false;

            System.out.println(UIText.MSG_INPUT_YES_NO.localized());
        }
    }

    /**
     * one of the most useful helper in app.
     * It takes list of {@link SerializableModel}s and asks user to pick one, then returns the choice.
     * Makes application code short and readable.
     * @param <T>
     * @param scanner
     * @param prompt
     * @param entityList
     * @return
     */
    public static <T extends SerializableModel> T readIdFromList(Scanner scanner, UIText prompt, List<T> entityList) {
        if(entityList.isEmpty()){
            throw new ListIsEmpty();
        }
        while (true) {
            try {
                System.out.print(prompt.localized());
                String input = scanner.nextLine().trim();
                int id = Integer.parseInt(input);
                for(T entity : entityList){
                    if(entity.getId() == id){
                        return entity;
                    }
                }
                throw new DoesNotExist(UIText.ERR_LIST_ITEM_NOT_FOUND);
            } catch (NumberFormatException e) {
                System.out.println(UIText.MSG_INPUT_NUMBER.localized());
            }
            catch (DoesNotExist e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // ENUM AND DOMAIN MENUS

    public static TeacherType askTeacherType(Scanner scanner) {
        while (true) {
            System.out.print(UIText.INPUT_TEACHER_TYPE.localized());
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    return TeacherType.LECTURE;
                case "2":
                    return TeacherType.PRACTICE;
                case "3":
                    return TeacherType.BOTH;
                default:
                    System.out.println(UIText.MSG_INVALID_CHOICE.localized());
            }
        }
    }

    public static Class<? extends User> readUserClass(Scanner scanner) {

        List<Class<? extends User>> roles = AppSettings.REGISTRABLE_USER_CLASSES;

        System.out.println(UIText.INPUT_USER_ROLE.localized());
        for (int i = 0; i < roles.size(); i++) {
            System.out.println((i + 1) + "." + roles.get(i).getSimpleName());
        }

        int choice = readChoice(scanner, UIText.MENU_CHOOSE, 1, roles.size());
        return roles.get(choice - 1);
    }

    public static LanguagePreference readLanguagePreference(Scanner scanner) {
        while (true) {
            System.out.println(UIText.AUTH_CHANGE_LANG.localized());
            System.out.println("1. " + "English");
            System.out.println("2. " + "Kazakh");
            System.out.println("3. " + "Russian");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    return LanguagePreference.EN;
                case "2":
                    return LanguagePreference.KK;
                case "3":
                    return LanguagePreference.RU;
                default:
                    System.out.println(UIText.MSG_INVALID_CHOICE.localized());
            }
        }
    }

    public static TechRequestStatus readTechRequestStatus(Scanner scanner) {

        List<TechRequestStatus> roles = List.of(TechRequestStatus.values());

        System.out.println(UIText.INPUT_TECH_REQ_STATUS.localized());
        for (int i = 0; i < roles.size(); i++) {
            System.out.println((i + 1) + "." + roles.get(i).toString());
        }

        int choice = readChoice(scanner, UIText.MENU_CHOOSE, 1, roles.size());
        return roles.get(choice - 1);
    }

    public static CourseType readCourseType(Scanner scanner) {
        List<CourseType> types = List.of(CourseType.values());

        System.out.println(UIText.INPUT_COURSE_TYPE.localized());
        for (int i = 0; i < types.size(); i++) {
            System.out.println((i + 1) + ". " + types.get(i).name());
        }

        int choice = readChoice(scanner, UIText.MENU_CHOOSE, 1, types.size());
        return types.get(choice - 1);
    }

    public static TeacherType readLectureOrPractice(Scanner scanner) {
        System.out.println(UIText.INPUT_COURSE_TEACHER_TYPE.localized());
        System.out.println("1. " + TeacherType.LECTURE.name());
        System.out.println("2. " + TeacherType.PRACTICE.name());

        int choice = readChoice(scanner, UIText.MENU_CHOOSE, 1, 2);
        return choice == 1 ? TeacherType.LECTURE : TeacherType.PRACTICE;
    }

    public static ComplaintUrgencyLevel readComplaintUrgencyLevel(Scanner scanner) {
        List<ComplaintUrgencyLevel> levels = List.of(ComplaintUrgencyLevel.values());

        System.out.println(UIText.INPUT_COMPLAINT_LEVEL.localized());
        for (int i = 0; i < levels.size(); i++) {
            System.out.println((i + 1) + ". " + levels.get(i).name());
        }

        int choice = readChoice(scanner, UIText.MENU_CHOOSE, 1, levels.size());
        return levels.get(choice - 1);
    }

    public static AttestationType readAttestationType(Scanner scanner) {
        List<AttestationType> types = List.of(AttestationType.values());

        System.out.println("Choose point type:");
        for (int i = 0; i < types.size(); i++) {
            System.out.println((i + 1) + ". " + types.get(i).getLabel());
        }

        int choice = readChoice(scanner, UIText.MENU_CHOOSE, 1, types.size());
        return types.get(choice - 1);
    }

    public static NewsUrgencyLevel readNewsUrgencyLevel(Scanner scanner) {
        while (true) {
            System.out.println("Choose urgency level:");
            System.out.println("1. RESEARCH");
            System.out.println("2. HIGH");
            System.out.println("3. AVERAGE");
            System.out.println("4. LOW");
            int choice = readChoice(scanner, UIText.MENU_CHOOSE, 1, 4);
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
                    System.out.println(UIText.MSG_INVALID_CHOICE.localized());
            }
        }
    }
}
