/* Mishael Zerrudo
 * A server class
 * Research Project
 * Dr. Chatterjee
 */

public class Server{
	private int delay;		//delay experienced when sending request
	private String name;	//name of server
	private boolean lock;	//true = server is open, false = close
	
	public Server(){		//default constructor
		delay = 5000;
		name = "Server";
		lock = true;
	}
	
	public Server(int num, String s){	//two-argument constructor
		delay = num;
		name = s;
		lock = true;
	}
	
	public void setDelay(int num){
		delay = num;
	}
	
	public void setServerName(String s){
		name = s;
	}
	
	public int getDelay(){
		return delay;
	}
	
	public String getServerName(){
		return name;
	}
	
	public boolean getLock(){
		return lock;
	}
	
	/*public void run(){
		//Random rand = new Random();
		//int num;						//random delay value for jobs
		//for (int i = 0; i < 5; i++){	//server runs 5 jobs
		try {
			sendRequest();		//simulates delay when sending request
				//num = rand.nextInt(5000);	//sets a random delay for jobs from 0 - 5000
				//startJob(new Jobs(num, i + 1));	//simulates server working on job
		} 
		catch (InterruptedException e) {
		}
		
		//System.out.println("**************" + name + " finished all jobs**************");
	}*/
	
	//simulates delay experienced when sending request to a server
	public void sendRequest(Jobs j) throws InterruptedException{
		//long start = System.currentTimeMillis();
		lock = false;
		System.out.println("Sending request to " + name);
		Thread.sleep(delay);
		//long end = System.currentTimeMillis();
		//System.out.println(name + " data received with " + ((end - start) / 1000) + " second delay.\n");
		//System.out.println(name + " data received");
		startJob(j);
		lock = true;
	}
	
	//simulates delay experienced when server is working on a job
	private void startJob(Jobs j) throws InterruptedException{
		System.out.println(name + " starting job" + j.getJobNum());
		Thread.sleep(j.getJobTime());
		System.out.println(name + " has finished job" + j.getJobNum());
	}
}