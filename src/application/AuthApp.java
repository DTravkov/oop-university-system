package application;

import model.SessionData;
import model.User;
import services.AuthService;
import java.util.Scanner;

import exceptions.DoesNotExist;
import exceptions.InvalidCredentials;

class AuthApp {
	
	private static AuthService authService = new AuthService();
	
	public static void startApp(Scanner scanner) {
            while (true) {
                System.out.println("\n---Authentication---");
                System.out.println("1. Sign Up (Register)");
                System.out.println("2. Log In");
                System.out.println("3. Exit");
                System.out.println("--------------------");
                System.out.print("Pick an option: ");

                String choice = scanner.nextLine();
                try {
	                switch (choice) {
	                    case "1":
	                        System.out.print("Enter login: ");
	                        String regLogin = scanner.nextLine();
	                        System.out.print("Enter password: ");
	                        String regPass = scanner.nextLine();
	                        System.out.print("Enter name: ");
	                        String name = scanner.nextLine();
	                        System.out.print("Enter surname: ");
	                        String surname = scanner.nextLine();
	                        
	                        authService.registerUser(new User(regLogin, regPass, name, surname));
	                        break;
	
	                    case "2":
	                        System.out.print("Login: ");
	                        String logLogin = scanner.nextLine();
	                        System.out.print("Password: ");
	                        String logPass = scanner.nextLine();
	                        
	                        User user = authService.authenticate(logLogin, logPass);
	                        if (user != null) {
	                            System.out.println("Access granted to " + user.getName());
	                            
	                            SessionData.setUser(user);
	                            
	                            return;
	                        }else {
	                        	System.out.println("Such user does not exist");
	                        }
	                        break;
	
	                    case "3":
	                        System.out.println("Goodbye!");
	                        return;
	
	                    default:
	                        System.out.println("Invalid choice. Try again.");
	                }
                }catch (DoesNotExist e) {
                	 System.out.println(e.getMessage());
                }catch (InvalidCredentials e) {
                	 System.out.println(e.getMessage());
                }
            }
        	
        }

}