package main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Main {
	
	public static void main(String[] args) {
		Random testRand = new Random(0);
		Plan p = new Plan(2, -1, testRand);

		try (FileReader reader = new FileReader(
				"src/resources/testing/required_classes.json")) {
			Gson gson = new Gson();
			List<Course> courses = gson.fromJson(reader, 
					new TypeToken<List<Course>>() {}.getType());
			
			p.distributeCourses(courses);
			
			System.out.println(p);
			System.out.println(p.getScore());
			
//			List<Semester> semesters = p.getSemesters();
//			semesters.get(1).addCourse(semesters.get(0).removeCourse("math 221"));
//			semesters.get(0).addCourse(semesters.get(1).removeCourse("math 231"));
//			semesters.get(1).addCourse(semesters.get(0).removeCourse("chem 103"));
//			semesters.get(0).addCourse(semesters.get(1).removeCourse("cs 101"));
			
			Plan pCopy = new Plan(p);
			pCopy.randomMove();

			System.out.println(pCopy);
			System.out.println(pCopy.getScore());
			
			System.out.println(p);
			System.out.println(p.getScore());
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
