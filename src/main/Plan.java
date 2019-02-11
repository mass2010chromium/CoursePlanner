package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * A semester plan.
 * @author jcpen
 *
 */
public class Plan {
	
	private Semester lastSemester;
	private List<Semester> semestersFromLast;
	
	private List<Course> alreadyTaken;
	
	private Random rand;
	
	/**
	 * Copy constructor.
	 * @param toCopy
	 */
	public Plan(Plan toCopy) {
		this.rand = toCopy.rand;
		this.lastSemester = toCopy.lastSemester.copy();
		Semester s = lastSemester;
		semestersFromLast = new ArrayList<Semester>();
		while (s != null) {
			semestersFromLast.add(s);
			s = s.getPrevious();
		}
		alreadyTaken = toCopy.alreadyTaken;
	}
	
	public Plan(int numSemesters, int currentSemester, 
			Random rand, List<Course> credit) {
		alreadyTaken = credit;
		this.rand = rand;
		lastSemester = new Semester(numSemesters, currentSemester, rand);
		Semester s = lastSemester;
		semestersFromLast = new ArrayList<Semester>();
		while (s != null) {
			semestersFromLast.add(s);
			s = s.getPrevious();
		}
	}
	
	/**
	 * Distribute the courses blindly and evenly among all semesters.
	 * @param courses
	 */
	public void distributeCourses(List<Course> courses) {
		int index = 0;
		for (Course c : courses) {
			semestersFromLast.get(index).addCourse(c);
			index = (index + 1) % semestersFromLast.size();
		}
	}
	
	public boolean isValid() {
		return lastSemester.isValid();
	}
	
	public double getScore() {
		double totalScoreSq = 0;
		lastSemester.validate(null, 
				alreadyTaken.stream().map(c -> c.id).collect(Collectors.toList()));
		for (Semester s : semestersFromLast) {
			totalScoreSq += Math.pow(s.getScore(), 2);
		}
//		return Math.sqrt(totalScoreSq);
		return totalScoreSq;
	}
	
	public void randomMove() {
		int index1;
		int index2;
		Semester s1;
		Semester s2;
		do {
			index1 = rand.nextInt(semestersFromLast.size());
			index2 = rand.nextInt(semestersFromLast.size());
			s1 = semestersFromLast.get(index1);
			s2 = semestersFromLast.get(index2);
		} while (index2 == index1 || s1.getCourseList().isEmpty());
//		swapRandom(s1, s2);
		if (rand.nextBoolean() || s2.getCourseList().isEmpty()) {
			moveRandom(s1, s2);
		}
		else {
			swapRandom(s1, s2);
		}
	}
	
	public static void moveRandom(Semester from, Semester to) {
		to.addCourse(from.removeRandomCourse());
	}
	
	public static void swapRandom(Semester s1, Semester s2) {
		Course removed = s1.removeRandomCourse();
		s1.addCourse(s2.removeRandomCourse());
		s2.addCourse(removed);
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Plan:");
		for (Semester s : this.semestersFromLast) {
			buf.append("\n").append(s.toString());
		}
		return buf.toString();
	}
	
	public List<Semester> getSemesters() {
		return this.semestersFromLast;
	}
}
