package application;

import java.util.Scanner;

import model.SessionData;

class App {
	public static void main(String args[]) {
		Scanner scanner = new Scanner(System.in);
		AuthApp.startApp(scanner);
		System.out.println(SessionData.getUser());
		
		AdminApp.startApp(scanner);
		
	}
}
