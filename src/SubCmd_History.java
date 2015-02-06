import java.util.concurrent.ArrayBlockingQueue;


/**
 * History class
 * Arguments: No
 * Piped input: No
 * Piping output/Output redirection: Yes
 */
public class SubCmd_History extends Filter{

	private SubCmd cmd;
	
	public SubCmd_History(SubCmd inputCmd, ArrayBlockingQueue<Object> out){
		super(null, out);
		this.cmd = inputCmd;
	}
	
	public void run(){
		if (CommandManager.history.size() != 0) {
			for (int i = 0; i < CommandManager.history.size(); i++) {
				try {
					out.put(CommandManager.history.get(i));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}else {
			System.out.println("Error: No input history");
		}
		jobDone();
		
	}
	
	public Object transform(Object o){
		return o;
	}
}
