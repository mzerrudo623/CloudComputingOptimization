/* Mishael Zerrudo
 * A server class
 * Research Project
 * Dr. Chatterjee
 */

import java.io.*;
public class Server extends Thread{
	private int delay;		//delay experienced when sending request
	private int serverNum;	//the server number
	private int jobsDone;	//number of jobs completed by server
	private int memory;
	private int processorSpd;
	public static int totalDone;
	public static boolean[] lock;
	
	public Server(){		//default constructor
		this(0, 5000, 5000, 10000);
	}
	
	public Server(int n, int d, int m, int p){	//4-argument constructor
		delay = d;
		serverNum = n;
		jobsDone = 0;
		memory = m;
		processorSpd = p;
		totalDone = 0;
		lock = new boolean[10];
	}
	
	public void setDelay(int num){
		delay = num;
	}
	
	public void setServerNumber(int n){
		serverNum = n;
	}
	
	public int getDelay(){
		return delay;
	}
	
	public int getServerNumber(){
		return serverNum;
	}
	
	public int getJobsDone(){
		return jobsDone;
	}
	
	private void closeLock(int index){
		lock[index] = true;
	}
	
	private void openLock(int index){
		lock[index] = false;
	}
	
	//returns the index a job is found, -1 otherwise
	private Jobs searchForJobs(){
		Jobs newJob = null;
		for (int i = 0; i < Broker.job.length; i++){
			if (lock[i] == false){
				closeLock(i);
				if (Broker.job[i] != null && checkRequirements(Broker.job[i]) == true){
					newJob = Broker.job[i];
					Broker.job[i] = null;
					openLock(i);
					return newJob;
				}
				openLock(i);
			}
		}
		return newJob;
	}
	
	//server waits until a job is sent to the server
	public Jobs waitForRequest(){
		Jobs newJob = null;
		while (newJob == null){
			newJob = searchForJobs();
		}
		return newJob;
	}
	
	//checks if the server meets the requirements to do a job
	private boolean checkRequirements(Jobs j){
		if (memory >= j.getReqMemory() && processorSpd >= j.getReqProcessor())
			return true;
		return false;
	}
	
	public void run(){
		Jobs newJob = null;
		while (true){
			newJob = waitForRequest();
			try {
				sendRequest(newJob);		//simulates delay when sending request
			}
			catch (Exception e) {
			}
		}
	}
	
	/*private static synchronized Jobs getJob(int sNum){
		return Broker.job[sNum];
	}*/
	
	//simulates delay experienced when sending request to a server
	public void sendRequest(Jobs j) throws InterruptedException, IOException{
		
		//time the server received a job
		long jobReceivedTime;
		
		//gets the time for when server is receiving new job
		long requestTime = 0;
		long start = System.currentTimeMillis();
		//jobReceivedTime = (time server starts to receive job) - (time program starts running)
		jobReceivedTime = start - Broker.programStartTime;
		System.out.println("Server " + serverNum + " receiving request");
		Thread.sleep(delay);
		long end = System.currentTimeMillis();
		requestTime += end - start;
		System.out.println("Server " + serverNum + " received data after " + (end - start) + " milliseconds");
			
		long jobTime = startJob(j);
			
		//gets the time for when server returns the job
		start = System.currentTimeMillis();
		System.out.println("Server " + serverNum + " sending request back to broker");
		Thread.sleep(delay);
		end = System.currentTimeMillis();
		System.out.println("Server " + serverNum + " finished sending request");
		requestTime += end - start;
		
		totalDone++;
		//writes data into a file
		synchronized(this){
			System.out.println("Jobs Done: " + totalDone);
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(Broker.outputFileName, true)));
			pw.println(serverNum + " " + jobsDone + " " + (jobTime + requestTime) + " " + j.getJobNum() + 
					" " + j.getJobTime() + " " + jobReceivedTime + " " + j.getReqMemory() +
					" " + j.getReqProcessor());
			pw.close();
		}
	}
	
	//simulates delay experienced when server is working on a job
	private long startJob(Jobs j) throws InterruptedException, IOException{
		long start = System.currentTimeMillis();
		System.out.println("Server " + serverNum + " starting job" + j.getJobNum());
		Thread.sleep(j.getJobTime());
		long end = System.currentTimeMillis();
		System.out.println("Server " + serverNum + " has finished job" + j.getJobNum() + " after " + (end - start) + " milliseconds");
		jobsDone++;
		return (end - start);
	}
}