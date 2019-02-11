package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Optimize something with simulated annealing.
 * @author jcpen
 *
 */
public class SimulatedAnnealing {
	
	private double temperature;
	private List<Plan> bestPlans; // Size limited to 4;
	private Random rand;
	private Plan currentPlan;
	
	public SimulatedAnnealing(Plan p, Random rand, int numClasses) {
		this.bestPlans = new ArrayList<Plan>();
		this.rand = rand;
		this.temperature = 200 * numClasses;
		currentPlan = p;
		bestPlans.add(p);
	}
	
	public double getTemperature() {
		return this.temperature;
	}
	
	private void checkAddBestPlan(Plan p) {
		if (bestPlans.size() < 4) {
			bestPlans.add(p);
			return;
		}
		for (int i = 0; i < bestPlans.size(); i++) {
			if (p.getScore() < bestPlans.get(i).getScore()) {
				p = bestPlans.set(i, p);
			}
		}
	}
	
	public void annealCycle() {
		int movesToMake = rand.nextInt((int) Math.ceil(temperature / 10));
		Plan newPlan = new Plan(currentPlan);
		for (int i = 0; i < movesToMake; i++) {
			newPlan.randomMove();
		}
		double diff = currentPlan.getScore() - newPlan.getScore();
//		System.out.println(diff);
		if (diff > 0) {
			temperature *= 0.99;
			currentPlan = newPlan;
		}
		temperature *= 0.9999;
//		else {
//			temperature *= 0.9999;
//			double cutoff = 1 / (1 + Math.exp(diff / temperature));
//			if (rand.nextDouble() < cutoff) {
//				currentPlan = newPlan;
//			}
//		}
		checkAddBestPlan(newPlan);
	}
	
	/**
	 * Just for debugging.
	 * @return
	 */
	public Plan getCurrentPlan() {
		return this.currentPlan;
	}
}
