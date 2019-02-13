package main;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;

public class CourseTyper {
	
	public static void main(String[] args) {
		List<Course> courses = new LinkedList<Course>();
		try (Scanner scan = new Scanner(System.in);
				Writer writer = new FileWriter("out.json");
				FileReader reader = new FileReader("tmp.json")) {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();

			try {
				List<Course> prevCourses = gson.fromJson(reader, 
						new TypeToken<List<Course>>() {}.getType());
				courses.addAll(prevCourses);
				print("Found old courses.");
				print("Added " + courses.size() + " existing courses:");
				for (int i = 0; i < Math.min(5, courses.size()); i++) {
					print("  > " + courses.get(i).longName);
				}
				if (courses.size() > 5) print("...");
			} catch (JsonIOException e) {
				print("Old courses not found.");
			}
			
			while (true) {
				print("Choose action: (list, detail, write, sort, exit)");
				String action = scan.nextLine().toLowerCase();
				if (action.equals("sort")) {
					print("Sorting and writing courses...");
					Collections.sort(courses, (a, b) -> a.id.compareTo(b.id));
					gson.toJson(courses, writer);
					print("Exiting course typer");
					break;
				}
				if (action.equals("list")) {
					listCourses(courses);
				}
				else if (action.equals("write")) {
					addCourses(scan, courses);
					
					print("Writing to out.json... ");
					gson.toJson(courses, writer);
					print("Exiting course typer");
					break;
				}
				else if (action.equals("detail")) {
					print("Course name or ID:");
					String input = scan.nextLine();
					Course foundCourse = null;
					for (Course course : courses) {
						if (course.longName.equalsIgnoreCase(input) || 
								course.id.equalsIgnoreCase(input)) {
							foundCourse = course;
							break;
						}
					}
					if (foundCourse == null) {
						print("No matching course found!");
						continue;
					}
					print("Course detail:");
					print();
					print(foundCourse.longName + "   |   " + foundCourse.id);
					print("Credit Hours: " + String.format("%.1f", foundCourse.creditHours));
					print("Weight: " + String.format("%.1f", foundCourse.weight));
					System.out.print("Attributes: ");
					if (foundCourse.getAttributes().size() == 0) print("None");
					else print();
					for (String s : foundCourse.getAttributes()) {
						print("  > " + s);
					}
					System.out.print("PreReqs: ");
					if (foundCourse.getPreReqs().size() == 0) print("None");
					else print();
					for (String s : foundCourse.getPreReqs()) {
						print("  > " + s);
					}
					System.out.print("CoReqs: ");
					if (foundCourse.getCoReqs().size() == 0) print("None");
					else print();
					for (String s : foundCourse.getCoReqs()) {
						print("  > " + s);
					}
					System.out.print("Excluded Semesters: ");
					if (foundCourse.getSemesterExclusions().length == 0) print("None");
					else print();
					for (int i : foundCourse.getSemesterExclusions()) {
						System.out.print(i + ", ");
					}
					print();
				}
				else if (action.equals("exit")) {
					print("Exiting course typer");
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void listCourses(List<Course> courses) {
		print("Found " + courses.size() + " existing courses:");
		int maxLengthName = 0;
		for (Course c : courses) {
			int length = c.longName.length();
			if (length > maxLengthName) {
				maxLengthName = length;
			}
		}
		for (Course c : courses) {
			System.out.printf("  > %-10s %-" + maxLengthName + "s %n", 
					c.id, c.longName);
		}
		print();
	}
	
	public static void addCourses(Scanner scan, List<Course> courses) {

		Course course = parseCourse(scan);
		while (course != null) {
			courses.add(course);
			course = parseCourse(scan);
		}
	}
	
	public static Course parseCourse(Scanner scan) {
		print("Scan Course? Y/N");
		if (scan.nextLine().toLowerCase().equals("n")) {
			return null;
		}
		
		Course course = new Course();
		print("Course descriptive name:");
		course.longName = scan.nextLine();
		print("Course id:");
		course.id = scan.nextLine().toLowerCase(); // to make sure no mistakes
		print("Course credit hours:");
		course.creditHours = scan.nextDouble();
		scan.nextLine();
		print("Course extra hours:");
		course.weight = scan.nextDouble();
		scan.nextLine();
		
		print("Course prereqs: (\'fin\' to exit)");
		String next = scan.nextLine().toLowerCase();
		Set<String> prereqs = course.getPreReqs();
		while (!next.equals("fin")) {
			prereqs.add(next);
			next = scan.nextLine().toLowerCase();
		}

		print("Course coreqs: (\'fin\' to exit)");
		next = scan.nextLine().toLowerCase();
		Set<String> coReq = course.getCoReqs();
		while (!next.equals("fin")) {
			coReq.add(next);
			next = scan.nextLine().toLowerCase();
		}
		
		print("Course attributes: (\'fin\' to exit)");
		next = scan.nextLine().toLowerCase();
		Set<String> attributes = course.getAttributes();
		while (!next.equals("fin")) {
			attributes.add(next);
			next = scan.nextLine().toLowerCase();
		}
		
		print("Course illegal semesters: (\'fin\' to exit)");
		next = scan.nextLine().toLowerCase();
		List<Integer> illegalSemesters = new ArrayList<Integer>();
		while (!next.equals("fin")) {
			illegalSemesters.add(Integer.parseInt(next));
			next = scan.nextLine().toLowerCase();
		}
		course.setSemesterExclusions(illegalSemesters.stream()
				.mapToInt(Integer::intValue).toArray());
		
		print("Scanned course " + course.longName);
		print();
		return course;
	}
	
	public static void print(Object o) {
		System.out.println(o);
	}
	public static void print() {
		System.out.println();
	}
}
