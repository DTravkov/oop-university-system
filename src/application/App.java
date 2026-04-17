package application;

import java.util.Scanner;

import model.SessionData;
import model.CourseRepository;
import model.UserRepository;



class App {
	public static void main(String args[]) {
		Scanner scanner = new Scanner(System.in);
		AuthApp.startApp(scanner);
		System.out.println(SessionData.getUser());
		AdminApp.startApp(scanner);
		scanner.close();
		
		UserRepository r1 = new UserRepository();
		System.out.println(r1.getAll());
//		CourseRepository r2 = new CourseRepository();
//		System.out.println(r2.getAll());
		
	}
}
