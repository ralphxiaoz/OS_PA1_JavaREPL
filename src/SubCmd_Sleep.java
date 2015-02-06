import java.util.concurrent.BlockingQueue;

/**
 * SubCmd_Sleep class
 * Arguments: Number of seconds
 * Piped input: No
 * Piping output/Output redirection: Yes
 */
public class SubCmd_Sleep extends Filter {
	long duration = 5;
	private SubCmd cmd;

	public SubCmd_Sleep(SubCmd inputCmd, BlockingQueue<Object> out) {
		super(null, out);
		cmd = inputCmd;
	}

	@Override
	public void run() {
		if (cmd.getArgus().size() == 0) {
			System.out.println("No argument, sleep 5 seconds by default.");
		}else if(Integer.parseInt(cmd.getArgus().get(0)) >=  0){
			duration = Integer.parseInt(cmd.getArgus().get(0));
		}else {
			System.out.print("Error: invalid argument. Sleep time must be positive integer.\n");
			this.done = true;
		}
		while (!this.done) {
			if (duration == 0) {
				this.done = true;
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.err.println(Thread.currentThread().getName() + " 's sleep has been interrupted.");
			}
			try {
				out.put("Sleep: " + --duration + " seconds left.");
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
