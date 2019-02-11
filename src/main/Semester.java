package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Class representing courses taken in a single semester.
 * @author jcpen
 *
 */
public class Semester {
	
	private static final int REQUISITE_ERROR_PENALTY = 100;
	private static final int OVER_MAX_HOUR_PENALTY = 20; // Per credit hour
	private static final double MAX_CREDIT_HOURS = 18;
	
	private Semester prevSemester;
	
	private Map<String, Integer> totalAttributes;
	private Map<String, Double> attributeHours;
	
	private Set<String> unmetReqs;
	
	private List<Course> courseList;
	private int semesterNum;
	
	/**
	 * Whether or not this course's data is up to date.
	 */
	private boolean isValidated;
	
	/**
	 * double because stupid ass MechE courses exist.
	 */
	private double creditHours;
	private double weight;
	private boolean isValid;
	
	private Random rand;
	
	/**
	 * Learning score of this semester.
	 */
	private double score;
	
	/**
	 * Copy previous 
	 * @param prev : Previous semester
	 */
	public Semester(Semester prev, Random rand) {
		this.prevSemester = prev;
		this.rand = rand;
		isValidated = false;
	}
	
	public Semester(int numSemesters, int currentSemester, Random rand) {
		this.rand = rand;
		if (numSemesters > 1) {
			prevSemester = new Semester(numSemesters - 1, currentSemester, rand);
		}
		courseList = new ArrayList<Course>();
		isValidated = false;
		semesterNum = numSemesters + currentSemester;
	}
	
	/**
	 * Get a copy of this Semester.
	 * @return Shallow copy (doesn't copy semesters, but thats fine cause
	 * semesters are never modified.)
	 */
	public Semester copy() {
		Semester previous = null;
		if (prevSemester != null) {
			previous = prevSemester.copy();
		}
		Semester copy = new Semester(previous, rand);
		copy.courseList = new ArrayList<Course>(courseList);
		copy.semesterNum = semesterNum;
		return copy;
	}
	
	/**
	 * Check if this Semester has been validated. Throws an exception if
	 * {@link #isValidated} is false.
	 * 
	 * @throws IllegalStateException
	 */
	private void checkValidated() throws IllegalStateException{
		if (!this.isValidated) 
			throw new IllegalStateException("Semester has not been validated yet!");
	}
	
	/**
	 * Update this semester's information (credit hours, errors, etc)
	 * @param reqs : Requirements from later semesters.
	 * @return Set of unfulfilled course requirements.
	 */
	public Set<String> validate(Set<String> reqs) {
		
		/*
		 * Logic:
		 * Reset params
		 * 
		 * For each course:
		 *     - Check exclusion
		 * 	   - Add all coreqs to the coreq list
		 *     - Add all prereqs to the prereq list
		 *     - Add weights and credit hours to total
		 *     - Increment attributes map
		 *     
		 * Add input reqs to coreq list
		 *     
		 * For each course:
		 *     - Remove it from the coreq list
		 * 
		 * //Now, the coreq list will have all unmet coreqs
		 * 
		 * add all unmet coreqs to the preqs list
		 * 
		 * RECURSIVE: -> set prereq list to previous_semester.validate();
		 *     if previous semester is not null
		 * 
		 * Update credit hours, weight, requisite errors
		 * 
		 * valid = false
		 * if (over hours): // Implied valid = false
		 *     Add over hour penalty
		 * else if (no prerequisite errors):
		 *     valid = true
		 *     
		 * set score, set validated
		 * return prereq list
		 */
		
		// Shouldn't be needed but just in case; reset validated
		// this.isValidated = false; 
		if (this.isValidated) {
			if (this.prevSemester == null) return unmetReqs;
			unmetReqs = this.prevSemester.validate(unmetReqs);
			return unmetReqs;
		}
		
		this.totalAttributes = new HashMap<String, Integer>();
		this.attributeHours = new HashMap<String, Double>();
		Set<String> coReqs = new HashSet<String>();
		Set<String> preReqs = new HashSet<String>();
		
		double creditHours = 0;
		double weight = 0;
		int requisiteErrors = 0;
		for (Course course : courseList) {
			for (int i : course.getSemesterExclusions()) {
				if (i == semesterNum) {
					requisiteErrors++;
				}
			}
			for (String attr : course.getAttributes()) {
				totalAttributes.merge(attr, 1, Integer::sum);
				attributeHours.merge(attr, course.creditHours, Double::sum);
			}
			coReqs.addAll(course.getCoReqs());
			preReqs.addAll(course.getPreReqs());
			creditHours += course.creditHours;
			weight += course.weight;
		}
		
		if (reqs != null) {
			coReqs.addAll(reqs);
		}
		
		for (Course course : courseList) {
			coReqs.remove(course.id);
//			if (coReqs.remove(course.id)) {
//				System.out.println("Semester " + this.semesterNum + 
//						" passed " + course.id);
//			}
		}
		
		preReqs.addAll(coReqs);
		
		if (this.prevSemester != null) {
			preReqs = this.prevSemester.validate(preReqs);
		}
		
		weight += creditHours;
		this.creditHours = creditHours;
		this.weight = weight;
		
		requisiteErrors += preReqs.size();  // For every unsatisfied prereq,
											// Add one to the req error count
		
		double totalScore = creditHours + weight + requisiteErrors * REQUISITE_ERROR_PENALTY;
		
		this.isValid = false;
		if (creditHours > MAX_CREDIT_HOURS) {
			totalScore += (creditHours - MAX_CREDIT_HOURS) * OVER_MAX_HOUR_PENALTY;
		}
		// If I get here, then I don't have requisite errors or over credit errors!
		else if (requisiteErrors == 0){
			this.isValid = true;
		}
		
//		System.out.println("Semester " + semesterNum + " failed:");
//		for (String s : preReqs) {
//			System.out.println(s);
//		}
		
		this.score = totalScore;
		this.unmetReqs = preReqs;
		this.isValidated = true;
		return preReqs;
	}
	
	/**
	 * Check if this semester has been validated. (Updated data)
	 * @return True if validated and getters can be called, false otherwise.
	 */
	public boolean isValidated() {
		return this.isValidated;
	}
	
	/*
	 * Computed values.
	 * Trying to get a computed value without calling validate() will throw
	 * an IllegalStateException.
	 */
	
	/**
	 * Check if this semester is valid.
	 * @return True if valid, False if not.
	 * @throws IllegalStateException if this Semester has not been validated.
	 */
	public boolean isValid() throws IllegalStateException {
		this.checkValidated();
		return this.isValid && prevSemester.isValid();
	}
	
	/**
	 * Get the learning score of this semester. Even invalid semesters have
	 * score, just really large score.
	 * @return The score. Higher is worse.
	 * @throws IllegalStateException if this Semester has not been validated.
	 */
	public double getScore() throws IllegalStateException {
		this.checkValidated();
		return this.score;
	}

	
	/**
	 * Get the total credit hours in this semester.
	 * @return
	 * @throws IllegalStateException if this Semester has not been validated.
	 */
	public double getCreditHours() throws IllegalStateException {
		this.checkValidated();
		return this.creditHours;
	}

	/**
	 * Get the total weight (credit hours + manual estimated weight) in this semester.
	 * @return
	 * @throws IllegalStateException if this Semester has not been validated.
	 */
	public double getWeight() throws IllegalStateException {
		this.checkValidated();
		return this.weight;
	}
	
	/**
	 * Get the course attibutes satisfied by this semester.
	 * @return A map of attributes and counts.
	 * @throws IllegalStateException if this Semester has not been validated.
	 */
	public Map<String, Integer> getAttributes() throws IllegalStateException {
		this.checkValidated();
		return this.totalAttributes;
	}
	
	/**
	 * Get the course attibute-hours satisfied by this semester.
	 * @return A map of attributes and total hours of each attribute.
	 * @throws IllegalStateException if this Semester has not been validated.
	 */
	public Map<String, Double> getAttributeHours() throws IllegalStateException {
		this.checkValidated();
		return this.attributeHours;
	}
	
	
	
	/*
	 * Non computed values. Safe to get anytime.
	 */
	
	/**
	 * Get the semester number of this semester. 
	 * (1 is one semester in the future.)
	 * @return
	 */
	public int getSemesterNum() {
		return semesterNum;
	}
	
	public List<Course> getCourseList() {
		return courseList;
	}
	
	public Semester getPrevious() {
		return prevSemester;
	}
	
	
	/*
	 * Setting modifiers. Invalidates the semester.
	 */
	
	/**
	 * Removes a random course from this semester, and returns it.
	 * Sets validated to false.
	 * @return A random course, removed from this semester.
	 */
	public Course removeRandomCourse() {
		isValidated = false;
		return courseList.remove(rand.nextInt(courseList.size()));
	}
	
	public Course removeCourse(String id) {
		Course ret = null;
		for (Course c : this.courseList) {
			if (c.id.equals(id)) {
				ret = c;
				break;
			}
		}
		if (ret != null) {
			isValidated = false;
			courseList.remove(ret);
		}
		return ret;
	}
	
	/**
	 * Adds a course to this semester. Sets validated to false.
	 * @param c : Course to add.
	 */
	public void addCourse(Course c) {
		isValidated = false;
		courseList.add(c);
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Semester " + this.semesterNum);
		for (Course c : this.courseList) {
			buf.append("\n  > " + c.longName);
		}
		return buf.toString();
	}
}
