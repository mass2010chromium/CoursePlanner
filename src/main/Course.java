package main;

import java.util.HashSet;
import java.util.Set;

/**
 * Class representing a single course taken at UIUC.
 * @author jcpen
 *
 */
public class Course {

	public String longName;
	public String id;
	public double creditHours;
	public double weight; // Added to creditHours to determine "effective" weight
	
	private Set<String> attributes;
	private Set<String> preReqs;
	private Set<String> coReqs;
	private int[] semesterExclusions;
	
	public Set<String> getAttributes() {
		if (attributes == null) {
			attributes = new HashSet<String>();
		}
		return attributes;
	}
	
	public Set<String> getPreReqs() {
		if (preReqs == null) {
			preReqs = new HashSet<String>();
		}
		return preReqs;
	}
	
	public Set<String> getCoReqs() {
		if (coReqs == null) {
			coReqs = new HashSet<String>();
		}
		return coReqs;
	}
	
	public int[] getSemesterExclusions() {
		if (semesterExclusions == null) {
			semesterExclusions = new int[]{};
		}
		return semesterExclusions;
	}
	
	public void setSemesterExclusions(int[] excluded) {
		this.semesterExclusions = excluded;
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof Course &&
				((Course) other).longName.equals(this.longName) && 
				((Course) other).creditHours == this.creditHours && 
				((Course) other).weight == this.weight && 
				((Course) other).id.equals(this.id);
//				((Course) other).attributes.equals(this.attributes) && 
//				((Course) other).coReqs.equals(this.coReqs) && 
//				((Course) other).preReqs.equals(this.preReqs) && 
//				((Course) other).semesterExclusions.equals(this.semesterExclusions);
				
	}
}
