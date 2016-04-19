/* Mishael Zerrudo
 * Server[i] gets job in array[i]
 * Research Project
 * Dr. Chatterjee
 */

import java.util.*;
import java.io.*;
public class Server extends Thread{
	private int delay;		//delay experienced when sending request
	private int serverNum;	//the server number
	private int jobsDone;	//number of jobs completed by server
	private int memory;
	private int processorSpd;
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
	
	//server waits until a job is sent to the server
	public Jobs waitForRequest(){
		Jobs newJob = null;
		while (newJob == null){
			newJob = searchForJobs();
		}
		return newJob;
	}
	
	//returns the job found, null otherwise
	private Jobs searchForJobs(){
		Jobs newJob = null;
		if (BrokerGivenJobs.job[serverNum - 1] != null){
			newJob = BrokerGivenJobs.job[serverNum - 1];
			return newJob;
		}
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
			newJob = waitForRequest();
			try {
				sendRequest(newJob);		//simulates delay when sending request
			}
			catch (Exception e) {
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
		BrokerGivenJobs.job[serverNum - 1] = null;
		
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

public class Jobs{
	private int jobNum;
	private int jobTime;
	private int reqMem;		//required memory to do job
	private int reqProc;	//required processor speed to do job
	
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

	/*public String toString(){
		return "Job Time: " + jobTime + "\nJob Number: " + jobNum;
	}*/
}

public class BrokerGivenJobs {
	private Server[] server;
	private Stack<Jobs> jobStack;
	public static Jobs[] job;
	private Random rand;	//used to create random delay for server and job
	public static String outputFileName;
	public static long programStartTime;	//stores the time the program starts running
	
	public BrokerGivenJobs(){		//default constructor
		this(10);
	}
	
	public BrokerGivenJobs(int size){	//one-argument constructor
		server = new Server[size];
		jobStack = new Stack<Jobs>();
		job = new Jobs[size];
		rand = new Random();
	}
	
	public BrokerGivenJobs(int size, String fname){
		this(size);
		outputFileName = fname;
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
			jobStack.push(new Jobs(input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt()));
		}
	}
	
	//stores jobs in the array
	public void sendJob(){
		int jobsSent = 0;
		while (!jobStack.empty()){
			for (int i = 0; i < server.length; i++){
				if (job[i] == null && server[i].checkRequirements(jobStack.peek()) == true){	//if job[i] currently has no job
					job[i] = jobStack.pop();
					jobsSent++;
					System.out.println("Broker has sent " + jobsSent + " jobs");
				}
				if (jobStack.empty()){
					break;
				}
			}
		}
	}
		
	public static void main(String[] args) throws InterruptedException, FileNotFoundException{
		int numOfServer = 10;
		String filename = "";
		Scanner keyboard = new Scanner(System.in);
		System.out.println("Enter the output file name: ");
		filename = keyboard.next();
		BrokerGivenJobs test = new BrokerGivenJobs(numOfServer, filename);
		//test.generateServers();
		programStartTime = System.currentTimeMillis();
		test.getServers();
		test.getJobs();
		test.sendJob();
		System.out.println("Broker done working");
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