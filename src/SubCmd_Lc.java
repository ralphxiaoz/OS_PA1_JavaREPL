import java.util.concurrent.ArrayBlockingQueue;

/**
 * Lc class
 * Arguments: No
 * Piped input: Yes
 * Piping output/Output redirection: Yes
 */
public class SubCmd_Lc extends Filter{
	
	private int lineCount = -1; //set to -1 because the last object in ABQ is the stopSign
	
	public SubCmd_Lc(SubCmd inputCmd, ArrayBlockingQueue<Object> in, ArrayBlockingQueue<Object> out){
		super(in, out);
	}
	
	public void run(){
			Object queueEle = new Object();
			while (queueEle != stopSign) {
				try {
					queueEle = in.take();
					lineCount++;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			try {
				out.put(Integer.toString(lineCount));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			jobDone();
	}
	
	public Object transform(Object o){
		return o;
	}
}
