package parallel.async;




/**
 * The TokenCacheWorker is a worker thread that acquires a semaphore dictating
 * that there is work to be done.
 */
public abstract class TokenWorker extends Thread {
	private int _threadId;
	int workerID = -1;
	private long numRequestsProcessed;

	/**
	 * @param workerID
	 * @param threadId
	 *            the thread id will be added by the {@link ClientBaseAction#threadCount}
	 */
	public TokenWorker(int workerID, int threadId) {
		this.setName("WorkerID " + workerID);
		this.workerID = workerID;
		numRequestsProcessed = 0;
		this._threadId = threadId;
	}

	public void run() {

		while (AsyncSocketServer.ServerWorking) {
			
			try {
				AsyncSocketServer.requestsSemaphores.get(workerID).acquire();

			} catch (InterruptedException e) {
				System.out
						.println("Error: TokenCacheWorker - Could not acquire Semaphore");
				e.printStackTrace();
			}

			// We put this condition boolean here so TokenCacheWorkers
			// may close without performing these actions

			if (AsyncSocketServer.ServerWorking) {
				// Get the token object. Since objects are put in this data
				// structure
				// before the semaphore is released, there should never be a
				// null pointer exception.
				TokenObject tokenObject = null;
				
				//MR1 TODO: remove //
				tokenObject = AsyncSocketServer.requestsQueue.get(workerID).poll();
//				tokenObject = AsyncSocketServer.requestsQueue.get(workerID);
				numRequestsProcessed++;
				//MR1 TODO: remove //
//				MessageTokenObject mobj = (MessageTokenObject) tokenObject;
//				mobj.latch.countDown();
//				continue;

				if (tokenObject != null) {
					doWork(tokenObject);
				} else {
					System.out.println("no work to do? why wake me up? ");
				}
			}
		}
	}

	/**
	 * @param socket
	 * @param requestArray
	 */
	public abstract void doWork(TokenObject tokenObject);

	public long getNumRequestsProcessed() {
		return numRequestsProcessed;
	}
}