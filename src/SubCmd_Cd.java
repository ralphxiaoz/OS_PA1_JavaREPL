import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;

import org.omg.PortableInterceptor.USER_EXCEPTION;


/**
 * Cd class
 * Arguments: A valid directory path. Make sure to support ". " & ".."
 * Piped input: No
 * Piping output/Output redirection: No
 */
public class SubCmd_Cd extends Filter{
	
	private SubCmd cmd;
	private String workspace;
	
	public SubCmd_Cd(SubCmd inputCmd, ArrayBlockingQueue<Object> out){
		super(null, out);
		this.cmd = inputCmd;
		this.workspace =  System.getProperty("user.dir");
	}

	public void run(){
		if (cmd.getArgus().size() == 0) {
			System.setProperty("user.dir", CommandManager.oriWorkspace);
		}
		else if (cmd.getArgus().size() == 1) {
			if (cmd.getArgus().get(0).equals("..")) {
				if (new File(new File(System.getProperty("user.dir")).getParent()).exists()) {
					System.setProperty("user.dir", new File(System.getProperty("user.dir")).getParent());
				}else {
					System.out.println("Error: directory not found.");
				}
			}else if (cmd.getArgus().get(0).equals(".")) {
				System.out.println("Current workspace switched to: " + System.getProperty("user.dir"));
			}
			else {
				if (new File(System.getProperty("user.dir")+System.getProperty("file.separator")+cmd.getArgus().get(0)).exists()) {
					System.setProperty("user.dir", System.getProperty("user.dir")+System.getProperty("file.separator")+cmd.getArgus().get(0));
				}else {
					System.out.println("Error: directory not found.");
				}
			}
			File f = new File(System.getProperty("user.dir"));
			if (!f.exists()) {
				System.out.println("Error: directory not found.");
			}
		}
		else {
			System.out.println("Error: cd takes maximum one path argument.");
		}
		jobDone();
	}
	
	public Object transform(Object o) {
		return o;
	};
}
