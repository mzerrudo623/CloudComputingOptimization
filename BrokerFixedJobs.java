/* Mishael Zerrudo
 * Broker sends server a fixed number of jobs
 * Research Project
 * Dr. Chatterjee
 */

import java.util.*;
import java.io.*;
public class BrokerFixedJobs {
	private Server[] server;
	private PriorityQueue<Jobs> jobQueue;
	public static Jobs[][] job;
	private Random rand;	//used to create random delay for server and job
	public static String outputFileName;
	public static long programStartTime;	//stores the time the program starts running
	public static boolean[] arrayLock;
	
	public String schedulingMethod;
	
	public BrokerFixedJobs(){		//default constructor
		this(10);
	}
	
	public BrokerFixedJobs(int size){	//one-argument constructor
		server = new Server[size];
		jobQueue = new PriorityQueue<Jobs>();
		job = new Jobs[3][size];
		rand = new Random();
		arrayLock = new boolean[size];
	}
	
	public BrokerFixedJobs(int size, String fname, String s){
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
			jobQueue.add(new Jobs(input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt()));
		}
	}
	
	//stores jobs in the array
	public void sendJob() throws InterruptedException{
		int index = -1;
		while (!jobQueue.isEmpty()){
			//if scheduling method chosen is best-fit or worse-fit
			if (schedulingMethod.equalsIgnoreCase("b") ||
					schedulingMethod.equalsIgnoreCase("w")){
				index = findBestServer();	//find the best server to send job to
				if (index != -1){
					arrayLock[index] = true;
					for (int j = 0; j < 3; j++){
						job[j][index] = jobQueue.remove();
					}
				}
			}
			//if scheduling method chosen is first-fit
			else{
				for (int i = 0; i < job.length; i++){
					for (int j = 0; j < job[i].length; j++){
						job[i][j] = jobQueue.remove();
					}
				}
			}
		}
	}
	
	//find the best server to do a job, returns index of server
	public int findBestServer(){
		int index = -1;
		//if scheduling method chosen is best-fit
		//send 3 jobs to server with low memory first
		for (int i = 0; i < 10; i++){
			if (schedulingMethod.equalsIgnoreCase("b")){
				//if job array is null, and server meets requirement and,
				//index is -1 or server memory is less than current lowest server memory
				if ((index == -1 || server[i].getMemory() < server[index].getMemory()) &&
						arrayLock[i] == false){
					index = i;
				}
			}
			//else if scheduling method chosen is worse-fit
			//send 3 jobs to server with highest memory first
			else{
				//if job array is null, and server meets requirement and,
				//index is -1 or server memory is less than current lowest server memory
				if ((index == -1 || server[i].getMemory() > server[index].getMemory()) &&
						arrayLock[i] == false){
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
		BrokerFixedJobs test = new BrokerFixedJobs(numOfServer, filename, schedMethod);
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

class Server extends Thread{
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
	
	public int getMemory(){
		return memory;
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
		
	public void run() {
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

	//compares based on required memory for job
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