package main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Main {
	
	public static void main(String[] args) {
		Random testRand = new Random(1);

		try (FileReader courseReader = new FileReader(
				"src/resources/testing/required_classes.json");
				FileReader creditReader = new FileReader(
						"src/resources/testing/taken.json");
				Scanner scn = new Scanner(System.in)) {
			Gson gson = new Gson();
			List<Course> courses = gson.fromJson(courseReader, 
					new TypeToken<List<Course>>() {}.getType());
			
			List<Course> credit = gson.fromJson(creditReader, 
					new TypeToken<List<Course>>() {}.getType());

			courses.removeAll(credit);
			
			Plan p = new Plan(6, 1, testRand, credit);
			
			p.distributeCourses(courses);
			
			System.out.println(p);
			System.out.println(p.getScore());
			
			SimulatedAnnealing optimizer = new SimulatedAnnealing(p, testRand, courses.size());
			
			while (optimizer.getTemperature() > 1) {
				optimizer.annealCycle();
//				Plan current = optimizer.getCurrentPlan();
//				
//				System.out.println(current);
//				System.out.println(current.getScore());
//				System.out.println("Temperature: " + optimizer.getTemperature());
//				scn.nextLine();
			}
			System.out.println("DONE!");
			System.out.println(optimizer.getCurrentPlan());
			System.out.println(optimizer.getCurrentPlan().isValid());
			System.out.println(optimizer.getCurrentPlan().getScore());
			
//			List<Semester> semesters = p.getSemesters();
//			semesters.get(1).addCourse(semesters.get(0).removeCourse("math 221"));
//			semesters.get(0).addCourse(semesters.get(1).removeCourse("math 231"));
//			semesters.get(1).addCourse(semesters.get(0).removeCourse("chem 103"));
//			semesters.get(0).addCourse(semesters.get(1).removeCourse("cs 101"));
			
//			Plan pCopy = new Plan(p);
//			pCopy.randomMove();
//
//			System.out.println(pCopy);
//			System.out.println(pCopy.getScore());
//			
//			System.out.println(p);
//			System.out.println(p.getScore());
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
