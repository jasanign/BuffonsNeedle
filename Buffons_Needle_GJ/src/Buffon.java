import java.util.Scanner;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * Buffon Class - This class creates threads to breakdown all the experiment 
 * work and delegates it equally to each thread, receives result of experiment
 * from each thread and estimates the value of pi using those results 
 * @author Gajjan Jasani
 * @version 4/2/2017
 */
public class Buffon {
	
	/** Scanner to receive user input */
	private Scanner scan = new Scanner(System.in);
	/** field to hold total number of needles from user input */
	protected int numOfNeedles = 0;
	/** field to hold number of threads from user input */
	protected int numOfThreads = 0;
	/** field to hold length of needles from user input */
	protected double lengthOfNeedle = 0.0;
	/** field to hold distance between the lines from user input */
	protected double distanceBWLines = 0.0;
	/** field to keep track of incorrect attempts */
	private int numOfIvalidAttempts = 0;
	
	
	/**
	 * This method prompts user to enter number of needles, number of threads, 
	 * length of needles, and distance between the lines. It also makes sure 
	 * length of needles is not more than the distance between the lines.
	 */
	public void prompt(){
		
		int lengthVsDistanceCount = 0;
		System.out.println("\n--- To exit out of the program, enter \"exit\""
				+ " at any step in the program.\n"
				+ "--- 5 consiqutive invalid input attempts will terminate "
				+ "the program.\n");
		System.out.println("Enter the total number of needles to drop: ");
		numOfNeedles =enterInt("Please enter an integer for number of needles");
		
		System.out.println("Enter the number of threads: ");
		numOfThreads =enterInt("Please enter an integer for number of threads");
		
		System.out.println("Enter the length of needle: ");
		lengthOfNeedle =enterDouble("Please enter a double for needle length");
		
		System.out.println("Enter the distance between the lines: ");
		distanceBWLines = enterDouble("Please enter a double for "
												+ "distance between the lines");
		while(lengthOfNeedle >= distanceBWLines){
			lengthVsDistanceCount++;
			numOfIvalidAttempts = lengthVsDistanceCount;
			checkToTerm();
			System.out.println("Length of needle must be less than the distance"
					+ " between the lines. \nTry again: ");
			distanceBWLines = enterDouble("Please enter a double for "
					+ "distance between the lines");
		}
	}
	
	/**
	 * This is a helper method for prompt(). This method makes sure that user 
	 * enters an integer value that is not zero, and keeps track of number of 
	 * incorrect input attempts. If user enters "exit", it ends the program.
	 * @param s string to prompt
	 * @return integer that user enters
	 */
	private int enterInt(String s){
		int varToSave;
		while(!(scan.hasNextInt())){// check if user entered an int
			String input = scan.next();
			// If user enters "exit", exit out of the program
			if (input.equalsIgnoreCase("EXIT")) {
		         System.exit(0);
		    }
			numOfIvalidAttempts++;
			checkToTerm();
			System.out.println(s);
		}
		varToSave = scan.nextInt();
		if (varToSave == 0){ // checking if user entered a zero
			System.out.println("Zero is not a valid input");
			numOfIvalidAttempts++;
			checkToTerm();
			varToSave = enterInt(s);
		}
		numOfIvalidAttempts = 0; // setting incorrect attempts back to zero
		return varToSave;
	}
	
	/**
	 * This is a helper method for prompt(). This method makes sure that user 
	 * enters an double value that is not zero, and keeps track of number of 
	 * incorrect input attempts. If user enters "exit", it ends the program.
	 * @param s string to prompt
	 * @return integer that user enters
	 */
	private double enterDouble(String s){
		double varToSave;
		while(!(scan.hasNextDouble())){
			String input = scan.next();
			if (input.equalsIgnoreCase("EXIT")) {
		         System.exit(0);
		    }
			numOfIvalidAttempts++;
			checkToTerm();
			System.out.println(s);
		}
		varToSave = scan.nextDouble();
		if (varToSave == 0){
			System.out.println("Zero is not a valid input");
			numOfIvalidAttempts++;
			checkToTerm();
			varToSave = enterDouble(s);
		}
		numOfIvalidAttempts = 0;
		return varToSave;
	}
	
	/**
	 * This is a helper method for enterInt and enterDouble methods. It 
	 * terminates the program is user enters wrong input 5 times in a row.
	 */
	private void checkToTerm(){
		
		if(numOfIvalidAttempts >= 5){
			System.out.println("Too many invalid input attempts. Good Bye!");
			System.exit(0);
		}
	}
	
	/**
	 * This method is the entry point to the program. This method creates 
	 * threads according to the user input and distributes experiments evenly 
	 * across those threads, receives results from them and estimates the 
	 * value of pi
	 * @param args command line arguments array (not being used)
	 * @throws Throwable to get the cause of the execution exception
	 */
	public static void main(String[] args) throws Throwable {
		
		Buffon bf = new Buffon(); // instance of Buffon class
		bf.prompt(); // prompting user
		// breaking down needles per thread
		int needlesPerThread = bf.numOfNeedles/bf.numOfThreads;
		// creating the thread pool (create as needed, but re-use)
		ExecutorService exec = Executors.newCachedThreadPool();
		// handing the executor to completion service
		CompletionService<Double>taskCompletionService 
								= new ExecutorCompletionService<Double>(exec);
		double totalHits = 0.0;
		double estimateOfPi = 0.0;
		// submitting all the threads to the completion service
		for (int i = 1; i <= bf.numOfThreads; i++){
			taskCompletionService.submit(
					new Experiment(i, bf.distanceBWLines, 
										bf.lengthOfNeedle, needlesPerThread));
		}
		// Receiving results of the experiments or waiting for them to finish
		for (int i = 0; i < bf.numOfThreads; i++) {
			Future<Double> hits;
			try {
				//Retrieves and removes the Future representing the next 
				//completed task, waits if none are yet present(completed)
				hits = taskCompletionService.take();
				//retrieves its result, waits if computation isn't finished
				totalHits += hits.get();
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
				throw new RuntimeException(ie);	
			} catch (ExecutionException ee){
				throw ee.getCause();
			}
		}
		exec.shutdown();	
		try { // estimating the value of pi from the results
			estimateOfPi=(2*bf.lengthOfNeedle*bf.numOfNeedles)
												/(bf.distanceBWLines*totalHits);
		} catch (ArithmeticException ae) {
			System.out.println(ae.getMessage());
		}
		System.out.println("\nEstimated value of PI = "+estimateOfPi+"\n");	
	}
}
