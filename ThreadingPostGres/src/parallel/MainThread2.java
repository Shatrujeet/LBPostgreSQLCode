package parallel;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import parallel.async.AsyncSocketServer;
import parallel.async.MessageTokenObject;

public class MainThread2 {

	public String dbname;
	public static int threads;
	public static AtomicInteger counter = new AtomicInteger(0);

	MainThread2(String dbname, int threads) {
		this.dbname = dbname;
		this.threads = threads;
	}

	//starts threads according to the arguments of the function
	public void startProcess(BinaryTree<String> bt, int limit) {
		MessageTokenObject mobj = null;
		long initialTime = System.currentTimeMillis(); //taking final time
		for (int i = 0; i < limit; i++) {
			CountDownLatch latch = new CountDownLatch(1);
			mobj = new MessageTokenObject(latch, bt.root, true);
			submitMessage(counter.incrementAndGet() % threads, mobj);
			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace(System.out);
				System.exit(0);
			}
		}

		long finalTime = System.currentTimeMillis(); //taking final time
		float averageTime = ((float) (finalTime - initialTime) / 1000) / limit; //value in seconds

		printStats(averageTime, limit, mobj.rows);
		printTree(bt.root, limit);
	}
	private static void submitMessage(int serverId, MessageTokenObject mobj) {
		AsyncSocketServer.requestsQueue.get(serverId).add(mobj);
		//		AsyncSocketServer.requestsQueue.set(serverId, mobj);
		AsyncSocketServer.requestsSemaphores.get(serverId).release();
	}

	//prints the statistics
	void printStats(float averageTime, int iterations, long rows) {

		System.out.println("\nStatistics for the Query:");
		System.out.println("Iterations of the Query: " + iterations);
		System.out.println("Average time taken by every query:" + averageTime);
		System.out.println("Number of rows:" + rows);

	}

	private void printTree(BTNode<String> node, int iterations ) {
		System.out.println(node.name+"," + ((node.duration/1000.0)/iterations) + "," + ((node.durationSelf/1000.0)/iterations));
		if(node.left != null)
			printTree(node.left, iterations);
		if(node.right != null)
				printTree(node.right, iterations);
		
	}


}