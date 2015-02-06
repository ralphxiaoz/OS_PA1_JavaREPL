import java.util.concurrent.ArrayBlockingQueue;


/**
 * SubCmd_Grep class
 * Arguments: A pattern to match string
 * Piped input: Yes
 * Piping output/Output redirection: Yes
 */
public class SubCmd_Grep extends Filter{
	
	private SubCmd cmd;
	
	public SubCmd_Grep(SubCmd inputCmd, ArrayBlockingQueue<Object> in, ArrayBlockingQueue<Object> out){
		super(in, out);
		this.cmd = inputCmd;
	}
	
	public void run(){
			Object queueEle = new Object();
			while (queueEle != stopSign) {
				try {
					queueEle = in.take();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (queueEle.toString().contains(cmd.getArgus().get(0))) {
					try {
						out.put(queueEle);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		jobDone();
	}
	
	public Object transform(Object o){
		return o;
	}
}