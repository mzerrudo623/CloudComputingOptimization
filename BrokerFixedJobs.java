/* Mishael Zerrudo
 * Broker sends each server 3 jobs
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
	public static boolean flag;
	
	public Server(){		//default constructor
		this(0, 5000, 5000, 10000);
		start();
	}
	
	public Server(int n, int d, int m, int p){	//4-argument constructor
		delay = d;
		serverNum = n;
		jobsDone = 0;
		memory = m;
		processorSpd = p;
		flag = false;
		start();
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
	public void waitForRequest(){
		while (BrokerFixedJobs.job[jobsDone][serverNum - 1] == null){
			//server will continue to wait until it has a job
		}
	}
	
	//checks if the server meets the requirements to do a job
	private boolean checkRequirements(Jobs j){
		if (memory >= j.getReqMemory() && processorSpd >= j.getReqProcessor())
			return true;
		return false;
	}
		
	public void run(){
		while (jobsDone != 3){
			waitForRequest();
			try {
				if(checkRequirements(getJob(jobsDone, serverNum - 1))  == true)
					sendRequest(getJob(jobsDone, serverNum - 1));	//simulates delay when sending request
				else{	//discard job if requirements not met
					System.out.println("Server " + serverNum + " discarded a job");
					BrokerFixedJobs.job[jobsDone][serverNum - 1] = null;
					jobsDone++;
				}
			}
			catch (Exception e) {
			}
		}
	}
	
	private static synchronized Jobs getJob(int jobsDone, int sNum){
		return BrokerFixedJobs.job[jobsDone][sNum];
	}
	
	//simulates delay experienced when sending request to a server
	public void sendRequest(Jobs j) throws InterruptedException, IOException{
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(BrokerFixedJobs.outputFileName, true)));
		
		//time the server received a job
		long jobReceivedTime;
				
		//gets the time for when server is receiving new job
		long requestTime = 0;
		long start = System.currentTimeMillis();
		jobReceivedTime = start - BrokerFixedJobs.programStartTime;
		System.out.println("Server " + serverNum + " receiving request");
		Thread.sleep(delay);
		long end = System.currentTimeMillis();
		requestTime += end - start;
		System.out.println("Server " + serverNum + " received data after " + (end - start) + " milliseconds");
		BrokerFixedJobs.job[jobsDone][serverNum - 1] = null;
		
		long jobTime = startJob(j);
		
		//gets the time for when server returns the job
		start = System.currentTimeMillis();
		System.out.println("Server " + serverNum + " sending request back to broker");
		Thread.sleep(delay);
		end = System.currentTimeMillis();
		System.out.println("Server " + serverNum + " finished sending request");
		requestTime += end - start;
		
		//writes data into a file
		pw.println(serverNum + " " + jobsDone + " " + (jobTime + requestTime) + " " + j.getJobNum() + 
				" " + j.getJobTime() + " " + jobReceivedTime + " " + j.getReqMemory() +
				" " + j.getReqProcessor());
		pw.close();
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

public class BrokerFixedJobs {
	private Server[] server;
	private Stack<Jobs> jobStack;
	public static Jobs[][] job;
	private Random rand;	//used to create random delay for server and job
	public static String outputFileName;
	public static long programStartTime;	//stores the time the program starts running
	
	public BrokerFixedJobs(){		//default constructor
		this(10);
	}
	
	public BrokerFixedJobs(int size){	//one-argument constructor
		server = new Server[size];
		jobStack = new Stack<Jobs>();
		job = new Jobs[3][size];
		rand = new Random();
	}
	
	public BrokerFixedJobs(int size, String fname){
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
		for (int i = 0; i < server.length; i++)
			server[i] = new Server(input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt());
	}
	
	//get job from a file and store in stack
	public void getJobs() throws FileNotFoundException{
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
	public void sendJob() throws InterruptedException{
		for (int i = 0; i < job.length; i++){
			for (int j = 0; j < job[i].length; j++){
				job[i][j] = jobStack.pop();
			}
		}
	}
		
	public static void main(String[] args) throws InterruptedException, FileNotFoundException{
		int numOfServer = 10;
		String filename = "";
		Scanner keyboard = new Scanner(System.in);
		System.out.println("Enter the output file name: ");
		filename = keyboard.next();
		BrokerFixedJobs test = new BrokerFixedJobs(numOfServer, filename);
		//test.generateServers();
		programStartTime = System.currentTimeMillis();
		test.getServers();
		test.getJobs();
		test.sendJob();
	}
	
	//create servers and stores into an array
	/*private void generateServers(){
		int serverNumber;
		int delay;
		for (int i = 0; i < server.length; i++)
		{
			delay = rand.nextInt(9000);		//sets a random delay for server from 0 - 9000
			serverNumber = i + 1;			//creates a name for server (Server1, Server2, etc.)
			server[i] = new Server(serverNumber, delay);	//create new server object and store in array
		}
	}*/
	
	//create jobs and stores in stack
	/*private void generateJobs(){
		int jobTime;
		for (int i = 24; i >= 0; i--){
			jobTime = rand.nextInt(5000);
			jobStack.push(new Jobs(jobTime, i + 1));
		}
	}*/
}