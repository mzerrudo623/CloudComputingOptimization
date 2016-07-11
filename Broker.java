/* Mishael Zerrudo
 * Server takes any job from the array
 * First-Fit Algorithm
 * Research Project
 * Dr. Chatterjee
 */

import java.util.*;
import java.io.*;
public class Broker {
	private Server[] server;
	private PriorityQueue<Jobs> jobQueue;
	public static Jobs[] job;
	private Random rand;	//used to create random delay for server and job
	public static String outputFileName;
	public static long programStartTime;	//stores the time the program starts running
	
	public Broker(){		//default constructor
		this(10);
	}
	
	public Broker(int size){	//one-argument constructor
		server = new Server[size];
		jobQueue = new PriorityQueue<Jobs>();
		job = new Jobs[size];
		rand = new Random();
	}
	
	public Broker(int size, String fname){
		this(size);
		outputFileName = fname;
	}
	
	//get server from a file and store into the array
	public void getServers() throws FileNotFoundException{
		String filename;
		Scanner keyboard = new Scanner(System.in);
				
		System.out.println("Enter the file name that holds server data: ");
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
	
	//get job from a file and store in priority queue
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
	}
	
	//stores jobs in the array
	public void sendJob() throws InterruptedException{
		while (!jobQueue.isEmpty()){
			for (int i = 0; i < server.length; i++){
				if (job[i] == null){	//if job[i] currently has no job
					job[i] = jobQueue.remove();
				}
			}
		}
	}
		
	public static void main(String[] args) throws InterruptedException, FileNotFoundException{
		int numOfServer = 10;
		String filename = "";
		Scanner keyboard = new Scanner(System.in);
		System.out.println("Enter the output file name: ");	//output file will hold data for timing
		filename = keyboard.next();
		Broker test = new Broker(numOfServer, filename);
		//test.generateServers();
		programStartTime = System.currentTimeMillis();
		test.getServers();
		test.getJobs();
		test.sendJob();
	}
	
	//fill up array of servers with Server objects
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
	
	//fill up stack with Jobs objects
	/*private void generateJobs(){
		int jobTime;
		for (int i = 24; i >= 0; i--){
			jobTime = rand.nextInt(5000);
			jobStack.push(new Jobs(jobTime, i + 1));
		}
	}*/
}