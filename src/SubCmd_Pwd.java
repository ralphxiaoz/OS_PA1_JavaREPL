import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * SubCmd_Pwd class
 * Arguments: No
 * Piped input: No
 * Piping output/Output redirection: Yes
 */
public class SubCmd_Pwd extends Filter{
	
	private SubCmd cmd;
	
	public SubCmd_Pwd(SubCmd inputCmd, ArrayBlockingQueue<Object> out){
		super(null, out);
		this.cmd = inputCmd;
	}
	public void run(){
		try {
			out.put(System.getProperty("user.dir"));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		jobDone();
	}
	
	public Object transform(Object o){
		return o;
	}
}