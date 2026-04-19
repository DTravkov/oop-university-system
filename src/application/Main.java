package application;

import java.util.Scanner;
import model.repository.CourseRepository;
import model.repository.EnrollmentRepository;
import model.repository.MarkRepository;
import model.repository.UserRepository;

class Main {

    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);

        UserApp.startApp(scanner);
        AuthApp.startApp(scanner);
        AdminApp.startApp(scanner);
        EnrollmentApp.startApp(scanner);
        scanner.close();

        UserRepository r1 = new UserRepository();
        System.out.println(r1.getAll());
        CourseRepository r2 = new CourseRepository();
        System.out.println(r2.getAll());
        EnrollmentRepository r3 = new EnrollmentRepository();
        System.out.println(r3.getAll());
        MarkRepository r4 = new MarkRepository();
        System.out.println(r4.getAll());
    }
}
