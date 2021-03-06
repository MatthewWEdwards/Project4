/* CRITTERS Critter.java
 * EE422C Project 4 submission by
 * Regan Stehle
 * rms3762
 * 16465
 * Matthew Edwards
 * mwe295
 * 16475
 * Slip days used: <0>
 * Fall 2016
 */
package assignment4;

import java.util.*;
import java.lang.*;

/* see the PDF for descriptions of the methods and fields in this class
 * you may add fields, methods or inner classes to Critter ONLY if you make your additions private
 * no new public, protected or default-package code or data can be added to Critter
 */


public abstract class Critter {
	private static String myPackage;
	private	static List<Critter> population = new java.util.ArrayList<Critter>();
	private static List<Critter> babies = new java.util.ArrayList<Critter>();
	private static List<String> typesOfCritters = new java.util.ArrayList<String>();
	private static int timeStep = 0;
	private static int[][] worldArray = new int[Params.world_width][Params.world_height];

	// Gets the package name.  This assumes that Critter and its subclasses are all in the same package.
	static {
		myPackage = Critter.class.getPackage().toString().split(" ")[1];
	}
	
	private static java.util.Random rand = new java.util.Random();
	public static int getRandomInt(int max) {
		return rand.nextInt(max);
	} 
	
	public static void setSeed(long new_seed) {
		rand = new java.util.Random(new_seed);
	}
	
	
	/* a one-character long string that visually depicts your critter in the ASCII interface */
	public String toString() { return ""; }
	
	private int energy = 0;
	
	protected int getEnergy() { return energy; }
	
	private int x_coord;
	private int y_coord;
	private boolean movedThisStep; 
	private boolean inFight;
	
	
	/**
	 * This function allows the critter to move one space in a given direction if it meets certain criteria
	 * @param direction indicates which direction the Critter wishes to walk
	 */
	protected final void walk(int direction) {
		energy -= Params.walk_energy_cost;
		if(movedThisStep == true)
			return;
		int startx = x_coord;
		int starty = y_coord;
		worldArray[x_coord][y_coord] -= 1;
		
		switch (direction){
			case 0: x_coord++;
					if (x_coord == Params.world_width)
						x_coord = 0;
				break;
			case 1: y_coord--;
					x_coord++;
					if (x_coord == Params.world_width)
						x_coord = 0;
					if (y_coord < 0)
						y_coord = Params.world_height - 1;
				break;
			case 2: y_coord--;
					if (y_coord < 0)
						y_coord = Params.world_height - 1;
				break;
			case 3: y_coord--;
					x_coord--;
					if (y_coord < 0)
						y_coord = Params.world_height - 1;
					if (x_coord < 0)
						x_coord = Params.world_width - 1;
				break;
			case 4: x_coord--;
					if (x_coord < 0)
						x_coord = Params.world_width - 1;
				break;
			case 5: x_coord--;
					y_coord++;
					if (x_coord < 0)
						x_coord = Params.world_width - 1;
					if (y_coord == Params.world_height)
						y_coord = 0;
				break;
			case 6: y_coord++;
					if (y_coord == Params.world_height)
						y_coord = 0;
				break;
			case 7: y_coord++;
					x_coord--;
					if (y_coord == Params.world_height)
						y_coord = 0;
					if(x_coord < 0)
						x_coord = Params.world_width -1;
				break;
		}
		if(this.inFight && (worldArray[x_coord][y_coord] >= 1)){
			x_coord = startx;
			y_coord = starty;
		}
		else {
			movedThisStep = true;
		}
		worldArray[x_coord][y_coord] += 1;
		
	}
	/**
	 * This function allows a Critter to move two spaces in a given direction if it meets certain criteria
	 * @param direction is the direction it wishes to move
	 */
	protected final void run(int direction) {
		energy -= Params.run_energy_cost;
		if(movedThisStep == true)
			return;
		int startx = x_coord;
		int starty = y_coord;
		worldArray[x_coord][y_coord] -= 1; 
		
		switch (direction){
			case 0: x_coord+= 2;
					if (x_coord >= Params.world_width)
						x_coord = x_coord - Params.world_width;
					
				break;
			case 1: y_coord-= 2;
					x_coord+= 2;
					if (x_coord >= Params.world_width)
						x_coord = x_coord - Params.world_width;
					if (y_coord < 0)
						y_coord = Params.world_height + y_coord;
				break;
			case 2: y_coord-= 2;
					if (y_coord < 0)
						y_coord = Params.world_height + y_coord;
				break;
			case 3: y_coord-= 2;
					x_coord-= 2;
					if (y_coord < 0)
						y_coord = Params.world_height + y_coord;
					if (x_coord < 0)
						x_coord = Params.world_width + x_coord;
				break;
			case 4: x_coord-= 2;
					if (x_coord < 0)
						x_coord = Params.world_width + x_coord;
				break;
			case 5: x_coord-= 2;
					y_coord+= 2;
					if (x_coord < 0)
						x_coord = Params.world_width + x_coord;
					if (y_coord >= Params.world_height)
						y_coord = y_coord - Params.world_height;
				break;
			case 6: y_coord+= 2;
					if (y_coord >= Params.world_height)
						y_coord = y_coord - Params.world_height;
				break;
			case 7: y_coord+= 2;
					x_coord-= 2;
					if (y_coord >= Params.world_height)
						y_coord = y_coord - Params.world_height;
					if(x_coord < 0)
						x_coord = Params.world_width + x_coord;
				break;
		}
		if(this.inFight && (worldArray[x_coord][y_coord] >= 1)){
			x_coord = startx;
			y_coord = starty;
		}
		else {
			movedThisStep = true;
		}
		worldArray[x_coord][y_coord] += 1;
	}
	/**
	 * This function allows a critter to reproduce and create a new critter of the same type
	 * @param offspring the new critter created
	 * @param direction the original critter used to create the offspring
	 */
	protected final void reproduce(Critter offspring, int direction) {
		
		if(this.getEnergy() < Params.min_reproduce_energy){
			return;
		}
		offspring.energy = this.getEnergy()/2;
		if(this.getEnergy() % 2 == 1)
			this.energy = this.getEnergy()/2 + 1;
		else
			this.energy = this.getEnergy()/2;
		offspring.x_coord = this.x_coord;
		offspring.y_coord = this.y_coord;
		offspring.movedThisStep = false;
		offspring.walk(direction);
		babies.add(offspring);
		
	}

	public abstract void doTimeStep();
	public abstract boolean fight(String oponent); //in fight, if choose to move, cannot move to unoccupied space
	
	/**
	 * create and initialize a Critter subclass.
	 * critter_class_name must be the unqualified name of a concrete subclass of Critter, if not,
	 * an InvalidCritterException must be thrown.
	 * upper. For example, if craig is supplied instead of Craig, an error is thrown instead of
	 * an Exception.)
	 * @param critter_class_name
	 * @throws InvalidCritterException
	 * @throws ClassNotFoundException 
	 */
	
	public static void makeCritter(String critter_class_name) throws InvalidCritterException{
		
		try {
			Class<?> critter = Class.forName("assignment4." + critter_class_name);
			Critter newCrit = (Critter) critter.newInstance();
			population.add(newCrit);
			newCrit.x_coord = Critter.getRandomInt(Params.world_width);
			newCrit.y_coord = Critter.getRandomInt(Params.world_height);
			worldArray[newCrit.x_coord][newCrit.y_coord] += 1;
			newCrit.energy = Params.start_energy;
			newCrit.movedThisStep = false;
			newCrit.inFight = false;
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoClassDefFoundError e) {
			throw new InvalidCritterException(critter_class_name);
		}
		
		
		
	
	}
	
	/**
	 * Gets a list of critters of a specific type.
	 * @param critter_class_name What kind of Critter is to be listed.  Unqualified class name.
	 * @return List of Critters.
	 * @throws InvalidCritterException
	 */
	public static List<Critter> getInstances(String critter_class_name) throws InvalidCritterException {
		List<Critter> result = new java.util.ArrayList<Critter>();
		try{
			Class<?> typeToAddClass = Class.forName("assignment4." + critter_class_name);
			Critter typeToAdd = (Critter) typeToAddClass.newInstance();
			for(Critter toAdd: population){
				if(toAdd.getClass() == typeToAdd.getClass()){
					result.add(toAdd);
				}
	
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoClassDefFoundError e) {
			throw new InvalidCritterException(critter_class_name);
		}
		return result;
	}
	
	/**
	 * Prints out how many Critters of each type there are on the board.
	 * @param critters List of Critters.
	 */
	public static void runStats(List<Critter> critters) {
		System.out.print("" + critters.size() + " critters as follows -- ");
		java.util.Map<String, Integer> critter_count = new java.util.HashMap<String, Integer>();
		for (Critter crit : critters) {
			String crit_string = crit.toString();
			Integer old_count = critter_count.get(crit_string);
			if (old_count == null) {
				critter_count.put(crit_string,  1);
			} else {
				critter_count.put(crit_string, old_count.intValue() + 1);
			}
		}
		String prefix = "";
		for (String s : critter_count.keySet()) {
			System.out.print(prefix + s + ":" + critter_count.get(s));
			prefix = ", ";
		}
		System.out.println();		
	}
	
	/* the TestCritter class allows some critters to "cheat". If you want to 
	 * create tests of your Critter model, you can create subclasses of this class
	 * and then use the setter functions contained here. 
	 * 
	 * NOTE: you must make sure that the setter functions work with your implementation
	 * of Critter. That means, if you're recording the positions of your critters
	 * using some sort of external grid or some other data structure in addition
	 * to the x_coord and y_coord functions, then you MUST update these setter functions
	 * so that they correctly update your grid/data structure.
	 */
	static abstract class TestCritter extends Critter {
		protected void setEnergy(int new_energy_value) {
			super.energy = new_energy_value;
		}
		
		protected void setX_coord(int new_x_coord) {
			worldArray[super.x_coord][super.y_coord] -= 1;
			super.x_coord = new_x_coord;
			worldArray[super.x_coord][super.y_coord] += 1;
		}
		
		protected void setY_coord(int new_y_coord) {
			worldArray[super.x_coord][super.y_coord] -= 1;
			super.y_coord = new_y_coord;
			worldArray[super.x_coord][super.y_coord] += 1;
		}
		
		protected int getX_coord() {
			return super.x_coord;
		}
		
		protected int getY_coord() {
			return super.y_coord;
		}
		

		/*
		 * This method getPopulation has to be modified by you if you are not using the population
		 * ArrayList that has been provided in the starter code.  In any case, it has to be
		 * implemented for grading tests to work.
		 */
		protected static List<Critter> getPopulation() {
			return population;
		}
		
		/*
		 * This method getBabies has to be modified by you if you are not using the babies
		 * ArrayList that has been provided in the starter code.  In any case, it has to be
		 * implemented for grading tests to work.  Babies should be added to the general population 
		 * at either the beginning OR the end of every timestep.
		 */
		protected static List<Critter> getBabies() {
			return babies;
		}
	}

	/**
	 * Clear the world of all critters, dead and alive
	 */
	public static void clearWorld() {
		worldArray = new int[Params.world_width][Params.world_height];
		population.clear();
	}
	/** This function calls doTimeStep for all Critters in population, then resolves encounters of all critters located
	 * in the same spot,subtracts rest energy from all critters, generates Algae, moves babies created during this time
	 * step to the population array
	 */ 
	public static void worldTimeStep() {
		timeStep++;
		for(int i = 0; i < population.size(); i++){ //Time Steps
			Critter current = population.get(i);
			current.doTimeStep();
			current.energy -= Params.rest_energy_cost;
			if(current instanceof Algae){
				current.energy += Params.photosynthesis_energy_amount;
			}
		}
		
		for(int k = 0; k < population.size(); k++){
			Queue<Critter> Q = new LinkedList<Critter>();
			Critter current = population.get(k);
			for(int i = 0; i < population.size(); i++){
				if(current.x_coord == population.get(i).x_coord && current.y_coord == population.get(i).y_coord && i != k){
					//add to conflict queue
					Q.add(population.get(i));
					//conflict(current, population.get(i)); 
				}
			}
			//go through queue and call conflict on all critters in queue
			while(Q.peek() != null){
			//while queue has next
				Critter winner = conflict(current, Q.poll());
			//have current critter as A, poll to get critter B
				current = winner;
			//call conflict(current, poll);
			}
		}
		
		boolean [] toRemove = new boolean[population.size()];
		for(int i = 0; i < population.size(); i++){
			if(population.get(i).energy < 1){
				toRemove[i] = true;
			}
		}
		for(int i = population.size()-1; i >= 0; i--){
			if(toRemove[i] == true){
				population.remove(i);
			}
		}
		
		for(Critter addBabies: babies){
			population.add(addBabies);
		}
		
		for(int i = 0; i < Params.refresh_algae_count; i++){
			try {
				makeCritter("Algae");
			} catch (InvalidCritterException e) {
				break;
			}
		}
		
		babies.clear();
		
		for(int k = 0; k < population.size(); k++){
			population.get(k).movedThisStep = false;
		}
		
	}

	/**
	 * Prints a 2D grid simulation of the world
	 */
	public static void displayWorld() {
		System.out.print("+");
		for(int printDash = 0; printDash<Params.world_width; printDash++){
			System.out.print("-");
		}
		System.out.println("+");
		
		for(int printCols = 0; printCols < Params.world_height; printCols++){
			System.out.print("|");
			rowLoop:
			for(int printRows = 0; printRows < Params.world_width; printRows++){
				for(int checkCrittersIndex = 0; checkCrittersIndex < population.size(); checkCrittersIndex++){
					if(population.get(checkCrittersIndex).x_coord == printCols && population.get(checkCrittersIndex).y_coord == printRows){
						System.out.print(population.get(checkCrittersIndex).toString());
						continue rowLoop;
					}
				}
				System.out.print(" ");
			}
			System.out.println("|");
		}
		
		System.out.print("+");
		for(int printDash = 0; printDash<Params.world_width; printDash++){
			System.out.print("-");
		}
		System.out.println("+");
		
		
		
	}
	/**
	 *  Resolves conflicts between critters located in the same space in the world, only at most one critter can remain, 
	 *  whether because the other one flees or because it fights and gets killed
	 * @param a first critter
	 * @param b second critter
	 * @return the winning critter
	 */
	private static Critter conflict(Critter a, Critter b){
		int aRoll = 0;
		int bRoll = 0;
		int startx = a.x_coord;
		int starty = a.y_coord;
		a.inFight = true;
		b.inFight = true;
		boolean aChoice = a.fight(b.toString());
		boolean bChoice = b.fight(a.toString());
		
		if(aChoice){
			if(a.energy < 0)
				aRoll = 0;
			else 
				aRoll = Critter.getRandomInt(a.energy+1);
		}
		if(bChoice){
			if(b.energy < 0)
				bRoll = 0;
			else
				bRoll = Critter.getRandomInt(b.energy+1);
		}

		a.inFight = false;
		b.inFight = false;
		
		
		
		if(a.x_coord == b.x_coord && a.y_coord == b.y_coord){
			if(aRoll > bRoll){
				aRoll += b.energy/2;
				b.energy = 0;
				return a;
			}
			else{
				bRoll += a.energy/2;
				a.energy = 0;
				return b;
			}
		}	
		
		//one of the critters moved if we reach this code, need to return one still in that spot
		if(a.x_coord == startx && a.y_coord == starty){
			return a;
		}
		return b;
	}
}
