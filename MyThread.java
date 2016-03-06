/*Mishael Zerrudo
 * A class that extends Thread
 * Research Project
 * Dr. Chatterjee
 */
public class MyThread extends Thread{
	
	private Server s;
	private Jobs j;
	
	public MyThread(Server newServer, Jobs newJob){
		s = newServer;
		j = newJob;
	}
	
	public void run(){
		try {
			s.sendRequest(j);
		} 
		catch (InterruptedException e) {
	}
	}
}
