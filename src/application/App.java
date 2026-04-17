package application;

import java.util.Scanner;

import model.SessionData;
import model.CourseRepository;
import model.EnrollmentRepository;
import model.UserRepository;



class App {
	public static void main(String args[]) {
//		Scanner scanner = new Scanner(System.in);
//		UserApp.startApp(scanner);
//		AuthApp.startApp(scanner);
//		EnrollmentApp.startApp(scanner);
////		AuthApp.startApp(scanner);
////		System.out.println(SessionData.getInstance().getUser());
//		AdminApp.startApp(scanner);
//		scanner.close();
		
		UserRepository r1 = new UserRepository();
		System.out.println(r1.getAll());
//		CourseRepository r2 = new CourseRepository();
//		System.out.println(r2.getAll());
		EnrollmentRepository r3 = new EnrollmentRepository();
		System.out.println(r3.getAll());
	}
}
