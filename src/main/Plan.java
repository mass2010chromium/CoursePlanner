package main;

import java.util.List;
import java.util.Random;

/**
 * A semester plan.
 * @author jcpen
 *
 */
public class Plan {
	
	private Semester lastSemester;
	private List<Semester> semestersFromLast;
	
	private Random rand;
	
	/**
	 * Copy constructor.
	 * @param toCopy
	 */
	public Plan(Plan toCopy) {
		this.rand = toCopy.rand;
		this.lastSemester = toCopy.lastSemester.copy();
		Semester s = lastSemester;
		while (s != null) {
			semestersFromLast.add(s);
			s = s.getPrevious();
		}
	}
	
	public Plan(int numSemesters, Random rand) {
		this.rand = rand;
		lastSemester = new Semester(numSemesters, rand);
		Semester s = lastSemester;
		while (s != null) {
			semestersFromLast.add(s);
			s = s.getPrevious();
		}
	}
	
	public boolean isValid() {
		return lastSemester.isValid();
	}
	
	public double score() {
		double totalScore = 0;
		lastSemester.validate(null);
		for (Semester s : semestersFromLast) {
			totalScore += s.getScore();
		}
		return totalScore;
	}
}
