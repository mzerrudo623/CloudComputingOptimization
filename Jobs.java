/* Mishael Zerrudo
 * A job class
 * Research Project
 * Dr. Chatterjee
 */
public class Jobs{
	private int jobTime;
	private int jobNum;
	
	public Jobs(){
		jobTime = 5000;
	}
	
	public Jobs(int num1, int num2){
		jobTime = num1;
		jobNum = num2;
	}
	
	public int getJobTime(){
		return jobTime;
	}
	
	public int getJobNum(){
		return jobNum;
	}
	
	/*public void startJob(String name) throws InterruptedException{
		System.out.println(name + "starting job");
		Thread.sleep(jobTime);
		System.out.println(name + " has finished job");
	}
	
	public String toString(){
		return "Job Time: " + jobTime + "\nJob Number: " + jobNum;
	}*/
}