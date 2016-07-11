/* Mishael Zerrudo
 * A job class
 * Research Project
 * Dr. Chatterjee
 */
public class Jobs implements Comparable<Jobs>{
	private int jobNum;
	private int jobTime;
	private int reqMem;		//required memory to do job
	private int reqProc;	//required processor speed to do job
	public static boolean batch;
	public static boolean service;
	
	public Jobs(){
		jobTime = 5000;
	}
	
	//Jobs(job number, job delay, required memory, required processor speed)
	public Jobs(int num1, int num2, int num3, int num4){
		jobNum = num1;
		jobTime = num2;
		reqMem = num3;
		reqProc = num4;
	}
	
	public int getJobTime(){
		return jobTime;
	}
	
	public int getJobNum(){
		return jobNum;
	}
	
	public int getReqMemory(){
		return reqMem;
	}
	
	public int getReqProcessor(){
		return reqProc;
	}
	
	//returns 0 if batch, 1 if service
	public int checkJobType(){
		if (batch == true)
			return 0;
		else
			return 1;
	}
	
	//compares based on job number
	public int compareTo(Jobs j){
		if (jobNum < j.getJobNum())
			return -1;
		else if (jobNum > j.getJobNum())
			return 1;
		else
			return 0;
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