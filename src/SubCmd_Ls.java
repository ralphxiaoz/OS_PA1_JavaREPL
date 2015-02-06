import java.io.File;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Ls class
 * Arguments: No
 * Piped input: No
 * Piping output/Output redirection: Yes
 */
public class SubCmd_Ls extends Filter{
	
	private SubCmd cmd;
	
	public SubCmd_Ls(SubCmd inputCmd, ArrayBlockingQueue<Object> out){
		super(null, out);
		this.cmd = inputCmd;
	}
	
	public void run(){
		if (!cmd.getArgus().isEmpty()) {
			System.out.println("Error: ls takes no arguments.");
		}
		File f = new File(System.getProperty("user.dir"));
		File[] files = f.listFiles();
		Arrays.sort(files);
		for (File file : files) {
			try {
				out.put(file.getName());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		jobDone();
	}
	
	@Override
	public Object transform(Object o) {
		return null;
	}
}
