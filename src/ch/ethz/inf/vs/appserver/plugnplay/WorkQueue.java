package ch.ethz.inf.vs.appserver.plugnplay;

import java.util.LinkedList;

import coap.Request;
import endpoint.Resource;

/**
 * Inspired by
 * http://www.ibm.com/developerworks/library/j-jtp0730/index.html
 * 
 * @author Martin Lanter
 */
public class WorkQueue {
	
	private final PoolWorker thread;
	private final LinkedList<RequestDelivery> queue;

	public WorkQueue() {
		this(null);
	}
	
	public WorkQueue(String name) {
		queue = new LinkedList<WorkQueue.RequestDelivery>();
		if (name==null)
			thread = new PoolWorker();
		else
			thread = new PoolWorker(name);
		//thread.start();
	}

	public void deliver(Request request, Resource resource) {
		synchronized (queue) {
			queue.addLast(new RequestDelivery(request, resource));
			queue.notify(); // notifyAll not required
		}
	}
	
	/**
	 * Starts the queue concurrently
	 */
	public void start() {
		thread.start();
	}
	
	/**
	 * Executes the queue with the thread, that calls this method
	 */
	public void execute() {
		thread.run();
	}
	
	public void stop() {
		thread.interrupt();
	}

	/*
	 * The handler for the requests of the queue
	 */
	private class PoolWorker extends Thread {
		
		private PoolWorker() {
			super();
		}
		
		private PoolWorker(String name) {
			super(name);
		}
		
		public void run() {
			RequestDelivery rd;
			
			while (true) {
				// wait for another request to deliver
				synchronized (queue) {
					while (queue.isEmpty()) {
						try {
							queue.wait();
						} catch (InterruptedException ignored) { }
					}

					rd = queue.removeFirst();
				}

				/*
				 * Calls performXXX Method. If an exception occurs it must be
				 * caught, to ensure, the thread doesn't stop.
				 */
				try {
					rd.request.dispatch(rd.resource);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private class RequestDelivery {
		private Request request;
		private Resource resource;
		private RequestDelivery(Request request, Resource resource) {
			this.request = request;
			this.resource = resource;
		}
	}
}
