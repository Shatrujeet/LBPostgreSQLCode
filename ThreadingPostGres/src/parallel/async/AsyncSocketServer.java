package parallel.async;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Copied from BG benchmark
 * 
 * @author Yazeed
 * 
 */
public class AsyncSocketServer extends Thread {
//	public static ConcurrentHashMap<Long, SockIOPool> SocketPool;
//	public static ConcurrentHashMap<Integer, RequestHandler> handlers = new ConcurrentHashMap<Integer, RequestHandler>();

	static AtomicInteger handlercount = new AtomicInteger(0);

	public static boolean ServerWorking = true;

	public static TokenWorker[] tokenWorkers;
	public static AtomicInteger NumConnections = new AtomicInteger(0);

	public static ConcurrentHashMap<Integer, Semaphore> requestsSemaphores = new ConcurrentHashMap<Integer, Semaphore>();

	public static int NumSemaphores = 101;
	public static int NumThreadsPerSemaphore = 20;
	public static int NumWorkerThreads = (NumSemaphores * NumThreadsPerSemaphore);
	public static int ID;
	//MR1 TODO: remove //
	public static ConcurrentHashMap<Integer, ConcurrentLinkedQueue<TokenObject>> requestsQueue;
//	public static HashMap<Integer, Queue<TokenObject>> requestsQueue;
//	public static ArrayList<Queue<TokenObject>> requestsQueue;
//	public static ArrayList<TokenObject> requestsQueue;


	public static boolean verbose = false;
	public static boolean logFile = false;

	public static int port;
	public static String dbname;
	private static int numSocketsPerClient = 10;
	
	private static ServerSocket server = null;

	public void setup(int numSemaphores, int numThreadsPerSemaphore,
			int numSoc, int portNumber, String dbname) {
		//MR1 TODO: remove //
//		numThreadsPerSemaphore = 10 / numSemaphores;
//		numSemaphores = 10;

		NumSemaphores = numSemaphores;
		NumThreadsPerSemaphore = numThreadsPerSemaphore;
		NumWorkerThreads = (NumSemaphores * NumThreadsPerSemaphore);
		numSocketsPerClient = numSoc;
		//MR1 TODO: remove //
		requestsQueue = new ConcurrentHashMap<Integer, ConcurrentLinkedQueue<TokenObject>>(
				NumSemaphores, .75f, 101);
//		requestsQueue = new ArrayList<Queue<TokenObject>>(NumSemaphores);
//		requestsQueue = new ArrayList<TokenObject>(NumSemaphores);

//		port = portNumber;
		this.dbname = dbname;
//		SocketPool = new ConcurrentHashMap<Long, SockIOPool>(0,
//				0.75f, 101);
		tokenWorkers = new TokenWorker[NumWorkerThreads];
	}

	public AsyncSocketServer(int numSemaphores, int numThreadsPerSemaphore,
			int numSoc, int portNumber, String dbname) {
		setup(numSemaphores, numThreadsPerSemaphore, numSoc, portNumber, dbname);
		startWorkers();
		//start();
	}

	private static void startWorkers() {
		int workerCount = 0;
		for (int i = 0; i < NumSemaphores; i++) {
			Semaphore semaphore = new Semaphore(0, true);
			requestsSemaphores.put(i, semaphore);
			//MR1 TODO: remove //
			requestsQueue.put(i, new ConcurrentLinkedQueue<TokenObject>());
//			requestsQueue.add(i, new LinkedList<TokenObject>());
//			requestsQueue.add(i, new TokenObject());

			for (int j = 0; j < NumThreadsPerSemaphore; j++) {
				tokenWorkers[workerCount] = new MessageTokenWorker(i, workerCount++, dbname);
			}
		}

		for (int i = 0; i < NumWorkerThreads; i++) {
			tokenWorkers[i].start();
		}
	}

	public static void generateSocketPool(long remoteHostId,
			String remoteHostAddr) {
//		SockIOPool pool = new SockIOPool(remoteHostAddr, numSocketsPerClient);
//		SocketPool.put(remoteHostId, pool);
	}

//	public void run() {
//		System.out.println("ERROR: shouldn't be here");
//		System.exit(0);
//		AtomicInteger handlerID = new AtomicInteger(0);
//		Socket socket = null;
//		try {
//			try {
//				server = new ServerSocket(port, 10000);
////				System.out.println("KOSAR is listening on addr: " + addr);
//			} catch (Exception e) {
//				System.out.println("Error in Server port " + e.getMessage());
//				System.exit(0);
//			}
//
//			System.out.println("the server is started");
//
//			while (AsyncSocketServer.ServerWorking) {
//				try {
//					// wait for a connection from a client
//					socket = server.accept();
//					RequestHandler handler = new RequestHandler(socket,
//							handlerID.getAndIncrement());
//					handler.setName("Request Handler " + handlerID);
//					handlers.put(NumConnections.getAndIncrement(), handler);
//					handler.start();
//				} catch (Exception e) {
//					System.out
//							.println("Async Server Component is not able to listen and accept requests "
//									+ e.getMessage());
//					System.exit(1);
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace(System.out);
//		} finally {
//			try {
//				if (null != server && !server.isClosed())
//					server.close();
//			} catch (IOException e) {
//				e.printStackTrace(System.out);
//			}
//		}
//		System.out.println("Finish stopping BG Server");
//	}

//	public static void shutdownSocketPool(int remoteHostId) {
//		SockIOPool pool = SocketPool.get(remoteHostId);
//		pool.shutdownPool();
//	}
	
	public static void reinitialize() {
		// stop workers, stop handlers, clear socket pool
		handlercount.set(0);
		NumConnections.set(0);
		
//		for (long hostId : SocketPool.keySet()) {
//			SocketPool.get(hostId).shutdownPool();
//		}
//		SocketPool.clear();
		
//		handlers.clear();
		//MR1 TODO: remove //
		for (int i : requestsQueue.keySet()) {
//		for (int i = 0 ; i < requestsQueue.size(); i++) {
			if (requestsQueue.get(i).size() > 0) {
				System.out.println("still processing requests ");
			}
			requestsQueue.get(i).clear();
		}
		
	}

	public static void shutdown() {
		ServerWorking = false;

		stopWorkers();

//		for (int rh : handlers.keySet()) {
//			handlers.get(rh).stopHandling();
//		}

//		try {
//			server.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace(System.out);
//		}
	}

	public static void stopWorkers() {

//		ServerWorking = false;
		for (int i = 0; i < NumSemaphores; i++)
			for (int j = 0; j < NumThreadsPerSemaphore; j++)
				// requestsSemaphores[i].release();
				requestsSemaphores.get(i).release();
		long totalNumberReqs = 0;
		for (int k = 0; k < NumWorkerThreads; k++) {
			try {

				totalNumberReqs = totalNumberReqs
						+ tokenWorkers[k].getNumRequestsProcessed();
			} catch (Exception e1) {
				e1.printStackTrace(System.out);
			}
			try {
				tokenWorkers[k].join();

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace(System.out);
			}

		}
		System.out.println("Number of requests processed by workers:"
				+ totalNumberReqs + System.getProperty("line.separator")
				+ "All done with workers");
	}

	public int getNumSocketsPerClient() {
		return numSocketsPerClient;
	}

}
