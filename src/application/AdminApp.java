package application;

import model.Admin;
import model.Course;
import model.CourseType;
import model.Student;
import model.User;
import services.AuthService;
import services.CourseService;
import services.UserService;
import java.util.Scanner;

import exceptions.DoesNotExist;

public class AdminApp {
	private static UserService userService = new UserService();
	private static AuthService authService = new AuthService();
	private static CourseService courseService = new CourseService();
    public static void startApp(Scanner scanner) {
        while (true) {
            System.out.println("\n--- Admin Panel ---");
            System.out.println("1. Update User");
            System.out.println("2. Delete User");
            System.out.println("3. Register a new admin");
            System.out.println("4. Register a new student");
            System.out.println("5. Register a new course");
            System.out.println("6. Exit");
            System.out.println("--------------------");
            System.out.print("Pick an option: ");

            String choice = scanner.nextLine();
            try {
	            switch (choice) {
	                case "1":
	                    System.out.print("Enter login of user to update: ");
	                    String targetLogin = scanner.nextLine();
	                    
	                    User user = userService.findOrThrow(targetLogin);
	                    
	                    if (user != null) {
	                    	
	                        User updated = new User(user);
	                        
	                        System.out.print("New name (leave blank to skip): ");
	                        String newName = scanner.nextLine();
	                        if (!newName.isEmpty()) updated.setName(newName);
	
	                        System.out.print("New surname (leave blank to skip): ");
	                        String newSurname = scanner.nextLine();
	                        if (!newSurname.isEmpty()) updated.setSurname(newName);
	
	                        System.out.print("New password (leave blank to skip): ");
	                        String newPass = scanner.nextLine();
	                        if (!newPass.isEmpty()) updated.setPassword(newPass);
	
	                        System.out.print("Ban user? (y/n/skip): ");
	                        String banChoice = scanner.nextLine();
	                        if (banChoice.equalsIgnoreCase("y")) updated.setBanned(true);
	                        else if (banChoice.equalsIgnoreCase("n")) updated.setBanned(false);
	
	                        userService.updateUser(user, updated);
	                    }
	                    break;
	
	                case "2":
	                    System.out.print("Enter login to delete: ");
	                    String delLogin = scanner.nextLine();
	                    userService.deleteUser(delLogin);
	                    break;
	
	                case "3":
                        System.out.print("Enter login: ");
                        String regLogin = scanner.nextLine();
                        System.out.print("Enter password: ");
                        String regPass = scanner.nextLine();
                        System.out.print("Enter name: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter surname: ");
                        String surname = scanner.nextLine();
                        authService.registerUser(new Admin(regLogin, regPass, name, surname));
                        break;
	                	
	                case "4":
                        System.out.print("Enter login: ");
                        regLogin = scanner.nextLine();
                        System.out.print("Enter password: ");
                        regPass = scanner.nextLine();
                        System.out.print("Enter name: ");
                        name = scanner.nextLine();
                        System.out.print("Enter surname: ");
                        surname = scanner.nextLine();
                        authService.registerUser(new Student(regLogin, regPass, name, surname));
                        System.out.println("Student registered successfully");
                        return;
	                case "5":
	                    System.out.print("Enter course name: ");
	                    String cName = scanner.nextLine();
	                    
	                    System.out.print("Enter description(blank to skip): ");
	                    String descr = scanner.nextLine();
	                    
	                    System.out.print("Enter credits: ");
	                    int credits = Integer.parseInt(scanner.nextLine());
	                    
	                    CourseType type = null;
	                    boolean typeSet = false;
	                    while (!typeSet) {
	                        System.out.print("Enter type: 1(maj), 2(min), 3(ele): ");
	                        String typeChoice = scanner.nextLine();
	                        switch (typeChoice) {
	                            case "1": type = CourseType.MAJOR; typeSet = true; break;
	                            case "2": type = CourseType.MINOR; typeSet = true; break;
	                            case "3": type = CourseType.ELECTIVE; typeSet = true; break;
	                            default: System.out.println("Invalid type choice.");
	                        }
	                    }
	                    
	                    Course newCourse = new Course(cName, descr, credits, type);
	                    
	                    courseService.createCourse(newCourse);
	                    System.out.println(newCourse);
	                    
	                    System.out.println("Course registered successfully!");
	                    break;
	                case "6":
	                    return;	
	                default:
	                    System.out.println("Invalid choice. Try again.");
	            }
            }catch (DoesNotExist e) {
            	System.out.println(e.getMessage());
            }
            catch (IllegalArgumentException e) {
                System.out.println("Registration Error: " + e.getMessage());
            }
            
        }
	
    }
}