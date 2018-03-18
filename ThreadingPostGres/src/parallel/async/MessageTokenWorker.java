package parallel.async;

import java.util.concurrent.CountDownLatch;

import parallel.connector;
import parallel.MainThread2;

public class MessageTokenWorker extends TokenWorker {
	connector driver;

	public MessageTokenWorker(int workerID, int threadId, String dbname) {
		super(workerID, threadId);

		driver = new connector();
		driver.connectToDatabase(dbname);
	}

	@Override
	public void doWork(TokenObject tokenObject) {
		MessageTokenObject mobj = (MessageTokenObject) tokenObject;
		long initialTime = System.currentTimeMillis(); //taking initial time

		int numthreads = 0;
		if (mobj.node.left != null) {
			numthreads++;
		}
		if (mobj.node.right != null) {
			numthreads++;
		}
		if (numthreads != 0) {
			CountDownLatch latch = new CountDownLatch(numthreads);
			if (mobj.node.left != null) {
				MessageTokenObject mobj2 = new MessageTokenObject(latch, mobj.node.left, false);
				submitMessage(MainThread2.counter.incrementAndGet() % MainThread2.threads, mobj2);
			}
			if (mobj.node.right != null) {
				MessageTokenObject mobj2 = new MessageTokenObject(latch, mobj.node.right, false);
				submitMessage(MainThread2.counter.incrementAndGet() % MainThread2.threads, mobj2);
			}
			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace(System.out);
				System.exit(0);
			}
		}
		long myTime = System.currentTimeMillis(); //taking final time
		if (mobj.isRoot) {
			mobj.rows = driver.executeQuery(mobj.node.data);
			driver.executeUpdate(mobj.node.extra);

		} else {
			driver.executeUpdate(mobj.node.data);
		}
		long finalTime = System.currentTimeMillis(); //taking final time
		mobj.node.duration += finalTime - initialTime;
		mobj.node.durationSelf += finalTime - myTime;
//		System.out.println(mobj.node.name + "(" + mobj.duration + ")("+(finalTime - myTime)+")");
		mobj.latch.countDown();
	}

	private static void submitMessage(int serverId, MessageTokenObject mobj) {
		AsyncSocketServer.requestsQueue.get(serverId).add(mobj);
		//		AsyncSocketServer.requestsQueue.set(serverId, mobj);
		AsyncSocketServer.requestsSemaphores.get(serverId).release();
	}
}
