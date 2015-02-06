import java.util.concurrent.*;

/*
 * This is the Filter class all you command implementation needs to extend.
 */
public abstract class Filter extends Thread {
	protected BlockingQueue<Object> in;
	protected BlockingQueue<Object> out;
	protected volatile boolean done;
	/** stopSign is a signal put in the queue to inform consumer that the producer's work is done*/
	protected static final Object stopSign = new Object();
	/** err will be set true if there's any error*/
	protected volatile boolean err = false;
	/** return proper message when err set true*/
	protected volatile String errMsg;
	/*
	 * The following flag is for Part 4.
	 */
	protected volatile boolean killed;

	public Filter (BlockingQueue<Object> in, BlockingQueue<Object> out) {
		this.in = in;
		this.out = out;
		this.done = false;
		this.killed = false;
	}


	/*
	 * This is for Part 4.
	 */
	public void cmdKill() {
		this.killed = true;
	}
	/*
	 * This method need to be overridden.
	 * @see java.lang.Thread#run()
	 */
	public void run() {
        Object o = null;
        while(! this.done) { //this.done Needs somewhere to be set true
			// read from input queue, may block
            try {
				o = in.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}    

			// allow filter to change message
            o = transform(o); 

			// forward to output queue
            try {
				out.put(o);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}       
        }
	}
	
	// put a stopSign to output queue to inform the next thread/output
	protected void jobDone(){
		try {
			out.put(stopSign);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/*
	 * This method might need to be overridden.
	 */
	public abstract Object transform(Object o);
}