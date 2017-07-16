import java.util.concurrent.Callable;

/**
 * Experiment class - This class actually performs the experiment and provides 
 * the results to the Buffon class
 * @author Gajjan Jasani
 * @version 4/4/2017
 *
 */
public class Experiment implements Callable<Double> {
	
	/** Unique id for each instance of this class */
	//private int id = 0;
	/** distance between the lines for the experiment */
	private double distanceBWLines = 0.0;
	/** length of needles for the experiment */
	private double lengthOfNeedle = 0.0;
	/** number of needles for the experiment */
	private int numOfNeedles = 0;
	
	/**
	 * Modified constructor for setting all the fields
	 * @param id Unique id
	 * @param distanceBWLines distance between the lines
	 * @param lenghtOfNeedle length of needles
	 * @param numOfNeedles number of needles
	 */
	public Experiment(int id, double distanceBWLines,
			double lenghtOfNeedle, int numOfNeedles){
		
		//this.id = id;
		this.distanceBWLines = distanceBWLines;
		this.lengthOfNeedle = lenghtOfNeedle;
		this.numOfNeedles = numOfNeedles;
	}

	/**
	 * Each thread performs an experiment by dropping a needle numOfNeedles
	 * times and counts the number of time the needle hit a line.
	 * @return hits the number of time a needle hit a line
	 */
	@Override
	public Double call() throws Exception {
		double angle = 0.0;	// angle of the needle
		double position = 0.0; // position of the needle
		double hits = 0.0; // number of needles touching a line
		// dropping a needle numOfNeedles times
		for(int i=0;i<numOfNeedles;i++) {
			angle = Math.random()*Math.toRadians(180); //random angle in radians
			position = distanceBWLines * Math.random(); // random position
			// checking for the hit (if not, it's a miss)
			if(((position + lengthOfNeedle*Math.sin(angle)/2 >= distanceBWLines)
				&& (position-lengthOfNeedle*Math.sin(angle)/2<=distanceBWLines))
				|| ((position + lengthOfNeedle*Math.sin(angle)/2 >= 0)
				&& (position - lengthOfNeedle*Math.sin(angle)/2 <= 0))) {
				hits++;
			}
		}
		//System.out.println("Number of hits from thread "+id+": "+hits);
		return hits;
	}
	
	/*
	//concurrency check
	@Override
	public Double call() throws Exception {

		System.out.println("I am thread "+id+".\nI am going to sleep.");
		try { 
            Thread.sleep(5000);

        }catch (InterruptedException ie) {
			System.out.println("It's "+id+". I'm interrupted!");
        }
		System.out.println("Good Bye! - from "+id);
		return (double) id;
	}
	*/
}
