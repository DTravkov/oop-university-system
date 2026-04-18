package application;

import model.domain.Course;
import model.domain.Mark;
import model.domain.Student;
import model.domain.Teacher;
import model.enumeration.CourseType;
import model.enumeration.TeacherTypeEnum;
import model.repository.CourseRepository;
import model.repository.EnrollmentRepository;
import model.repository.MarkRepository;
import model.repository.UserRepository;
import services.MarkService;
import services.UserService;

import java.util.Date;
import java.util.Scanner;


class TestApp {
	public static void main(String args[]) {
		Scanner scanner = new Scanner(System.in);

		UserApp.startApp(scanner);
		AuthApp.startApp(scanner);
		EnrollmentApp.startApp(scanner);
		AdminApp.startApp(scanner);
		scanner.close();
//
//		UserRepository r1 = new UserRepository();
//		System.out.println(r1.getAll());
//		CourseRepository r2 = new CourseRepository();
//		System.out.println(r2.getAll());
//		EnrollmentRepository r3 = new EnrollmentRepository();
//		System.out.println(r3.getAll());
// 		MarkRepository r4 = new MarkRepository();
//		System.out.println(r4.getAll());
	}
}
