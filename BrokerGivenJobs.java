/* Mishael Zerrudo
 * Server gets job in array in same element as it
 * Research Project
 * Dr. Chatterjee
 */

import java.util.*;
import java.io.*;
public class BrokerGivenJobs {
	private Server[] server;
	private PriorityQueue<Jobs> jobQueue;
	public static Jobs[] job;
	private Random rand;	//used to create random delay for server and job
	public static String outputFileName;
	public static long programStartTime;	//stores the time the program starts running
	
	public String schedulingMethod;
	
	public BrokerGivenJobs(){		//default constructor
		this(10);
	}
	
	public BrokerGivenJobs(int size){	//one-argument constructor
		server = new Server[size];
		jobQueue = new PriorityQueue<Jobs>();
		job = new Jobs[size];
		rand = new Random();
	}
	
	public BrokerGivenJobs(int size, String fname, String s){
		this(size);
		outputFileName = fname;
		schedulingMethod = s;
	}
	
	//get server from a file and store into the array
	public void getServers() throws FileNotFoundException{
		String filename;
		Scanner keyboard = new Scanner(System.in);
			
		System.out.println("Enter the input file name: ");
		filename = keyboard.next();
		File inputFile = new File(filename);
		if (!inputFile.exists()){
			System.out.println(filename + " not found");
			System.exit(0);
		}
		Scanner input = new Scanner(inputFile);
		for (int i = 0; i < server.length; i++){
			server[i] = new Server(input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt());
			server[i].start();
		}
		
		keyboard.close();
		input.close();
	}
	
	//get job from a file and store in stack
	private void getJobs() throws FileNotFoundException{
		//String fname;
		//Scanner keyboard = new Scanner(System.in);
		File inputFile = new File("Jobs.txt");
		if (!inputFile.exists()){
			System.out.println("Jobs.txt not found");
			System.exit(0);
		}
		Scanner input = new Scanner(inputFile);
		for(int i = 0; i < 30; i++){
			jobQueue.add(new Jobs(input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt()));
		}
		
		input.close();
	}
	
	//stores jobs in the array
	public void sendJob(){
		int jobsSent = 0;
		int index;
		while (!jobQueue.isEmpty()){
			//if scheduling method chosen is best-fit or worse-fit
			if (schedulingMethod.equalsIgnoreCase("b") || schedulingMethod.equalsIgnoreCase("w")){
				index = findBestServer();	//find the best or worse server
				if(index != -1){			//if a server has been found, then send the job
					job[index] = jobQueue.remove();
					jobsSent++;
					System.out.println("Broker has sent " + jobsSent + " jobs");
				}
			}
			//if scheduling method chosen is first-fit
			else{
				//send job to the first server the broker finds that is available
				//and meets the requirements
				for (int i = 0; i < server.length; i++){
					if (job[i] == null && server[i].checkRequirements(jobQueue.peek()) == true){	//if job[i] currently has no job
						job[i] = jobQueue.remove();
						jobsSent++;
						System.out.println("Broker has sent " + jobsSent + " jobs");
					}
					if (jobQueue.isEmpty()){
						break;
					}
				}
			}
		}
	}
	
	//find the best server (or worse server) to do a job, returns index of server
	public int findBestServer(){
		int index = -1;
		for (int i = 0; i < job.length; i++){
			//if scheduling method chosen is best-fit
			if (schedulingMethod.equalsIgnoreCase("b")){
				//if job array is null, and server meets requirement and,
				//index is -1 or server memory is LESS than current LOWEST server memory
				if (job[i] == null && server[i].checkRequirements(jobQueue.peek()) == true && 
						(index == -1 || server[i].getMemory() < server[index].getMemory())){
					index = i;
				}
			}
			//else if scheduling method chosen is worse-fit
			else{
				//if job array is null, and server meets requirement and,
				//index is -1 or server memory is GREATER than current HIGHEST server memory
				if (job[i] == null && server[i].checkRequirements(jobQueue.peek()) == true && 
						(index == -1 || server[i].getMemory() > server[index].getMemory())){
					index = i;
				}
			}
		}
		return index;
	}
		
	public static void main(String[] args) throws InterruptedException, FileNotFoundException{
		int numOfServer = 10;
		String filename = "";
		String schedMethod = "";
		Scanner keyboard = new Scanner(System.in);
		System.out.println("Enter the output file name: ");
		filename = keyboard.next();
		do{
			System.out.println("Enter scheduling method: ");
			schedMethod = keyboard.next();
		} while(!schedMethod.equalsIgnoreCase("b") && 
				!schedMethod.equalsIgnoreCase("f") &&
				!schedMethod.equalsIgnoreCase("w"));
		BrokerGivenJobs test = new BrokerGivenJobs(numOfServer, filename, schedMethod);
		//test.generateServers();
		programStartTime = System.currentTimeMillis();
		test.getServers();
		test.getJobs();
		test.sendJob();
		System.out.println("Broker done working");
		keyboard.close();
	}
	/*
	//create servers and stores into an array
	private void generateServers(){
		int serverNumber;
		int delay;
		for (int i = 0; i < server.length; i++)
		{
			delay = rand.nextInt(9000);		//sets a random delay for server from 0 - 9000
			serverNumber = i + 1;			//creates a name for server (Server1, Server2, etc.)
			server[i] = new Server(serverNumber, delay);	//create new server object and store in array
		}
	}
	//create jobs and stores in stack
	private void generateJobs(){
		int jobTime;
		for (int i = 24; i >= 0; i--){
			jobTime = rand.nextInt(5000);
			jobStack.push(new Jobs(jobTime, i + 1));
		}
	}
	*/
}

class Server extends Thread{
	private int delay;		//delay experienced when sending request
	private int serverNum;	//the server number
	private int jobsDone;	//number of jobs completed by server
	private int memory;
	private int processorSpd;
	//public static boolean flag;
	public static int totalDone;
	
	public Server(){		//default constructor
		this(0, 5000, 5000, 10000);
	}
	
	public Server(int n, int d, int m, int p){	//4-argument constructor
		delay = d;
		serverNum = n;
		jobsDone = 0;
		memory = m;
		processorSpd = p;
		//flag = false;
		totalDone = 0;
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
	
	public int getMemory(){
		return memory;
	}
	
	//server waits until a job is sent to the server
	/*public Jobs waitForRequest(){
		Jobs newJob = null;
		while (newJob == null){
			while (flag == true){
				//loops while a different server is currently
				//searching for a job
			}
			newJob = searchForJobs();
		}
		return newJob;
	}*/
	
	//returns the job found, null otherwise
	private Jobs searchForJobs(){
		//flag = true;
		Jobs newJob = null;
		if (BrokerGivenJobs.job[serverNum - 1] != null){
			newJob = BrokerGivenJobs.job[serverNum - 1];
			BrokerGivenJobs.job[serverNum - 1] = null;
			//flag = false;
			return newJob;
		}
		//flag = false;
		return newJob;
	}
	
	//checks if the server meets the requirements to do a job
	public boolean checkRequirements(Jobs j){
		if (memory >= j.getReqMemory() && processorSpd >= j.getReqProcessor())
			return true;
		return false;
	}
		
	public void run(){
		Jobs newJob = null;
		while (true){
			newJob = searchForJobs();
			if (newJob != null){
				try {
					sendRequest(newJob);		//simulates delay when sending request
				}
				catch (Exception e) {
				}
			}
		}
	}
	
	//simulates delay experienced when sending request to a server
	public void sendRequest(Jobs j) throws InterruptedException, IOException{
		
		//time the server received a job
		long jobReceivedTime;
				
		//gets the time for when server is receiving new job
		long requestTime = 0;
		long start = System.currentTimeMillis();
		jobReceivedTime = start - BrokerGivenJobs.programStartTime;
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
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(BrokerGivenJobs.outputFileName, true)));
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

class Jobs implements Comparable<Jobs>{
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

	public int compareTo(Jobs j){
		if (reqMem < j.getReqMemory())
			return -1;
		else if (reqMem > j.getReqMemory())
			return 1;
		else
			return 0;
	}
	
	/*public String toString(){
		return "Job Time: " + jobTime + "\nJob Number: " + jobNum;
	}*/
}