import java.io.*;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Cat class
 * Arguments: multiple file relative paths
 * Piped input: No
 * Piping output/Output redirection: Yes
 */
public class SubCmd_Cat extends Filter{
	private SubCmd cmd;
	public SubCmd_Cat(SubCmd inputCmd, ArrayBlockingQueue<Object> out){
		super(null, out);
		this.cmd = inputCmd;
	}
	
	public void run(){
			// while there are arguments, keep reading and send result to out
			for (int i = 0; i < cmd.getArgus().size(); i++) {
				try {
					FileReader fr = new FileReader(System.getProperty("user.dir") + "/" + cmd.getArgus().get(i));
					BufferedReader br = new BufferedReader(fr);
					
					String catOutput;
					try {
						while ((catOutput = br.readLine()) != null) {
							try {
								out.put(catOutput);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						br.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
				} catch (FileNotFoundException e) {
					System.out.println("Error: file not found.");
				}
			}
		jobDone();
	}
	
	public Object transform(Object o){
		return o;
	}
}
