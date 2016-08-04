/* Mishael Zerrudo
 * A broker class
 * Research Project
 * Dr. Chatterjee
 */

import java.util.*;
public class Broker {
	private Server[] server;
	private Stack<Jobs> job;
	private boolean[] lock;
	private Random rand;	//used to create random delay for server and job
	
	public Broker(){		//default constructor
		this(10);
	}
	
	public Broker(int size){	//one-argument constructor
		server = new Server[size];
		job = new Stack<Jobs>();
		lock = new boolean[size];
		rand = new Random();
	}
	
	//fill up array of servers with Server objects
	private void getServers(){
		String name = "Server";
		int delay;
		for (int i = 0; i < server.length; i++)
		{
			delay = rand.nextInt(9000);		//sets a random delay for server from 0 - 9000
			name = name + (i + 1);			//creates a name for server (Server1, Server2, etc.)
			server[i] = new Server(delay, name);	//create new server object and store in array
			name = "Server";				//reset name for the next server to be created
		}
	}
	
	//fill up stack with Jobs objects
	private void getJobs(){
		int jobTime;
		for (int i = 24; i >= 0; i--){
			jobTime = rand.nextInt(5000);
			job.push(new Jobs(jobTime, i + 1));
		}
	}
	
	//updates boolean array lock by checking server's lock status
	private void checkLocks(){
		for (int i = 0; i < lock.length; i++){
			lock[i] = server[i].getLock();
		}
	}
	
	//runs the Broker class
	public void run() throws InterruptedException{
		int i;			//used to create a delay if no open server is found
		MyThread thread;
		
		getServers();
		getJobs();
		while (!job.empty()){
			checkLocks();		//update lock status of the servers
			for (i = 0; i < server.length; i++){
				if (lock[i] == true){
					thread = new MyThread(server[i], job.pop());
					thread.start();
					Thread.sleep(1000);		//delay to prevent server from working on two jobs
					break;
				}
			}
			if (i == server.length)		//broker failed to find an open server
				Thread.sleep(5000);	//wait 5 seconds before trying again
		}
	}
	
	public static void main(String[] args) throws InterruptedException{
		Broker test = new Broker();
		test.run();
	}
}