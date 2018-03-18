package parallel;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainThread {

	public String queryExecute;
	public String queryUpdate;
	public static boolean terminate;
	public String dbname;

	MainThread(String dbname) {
		this.dbname = dbname;
		terminate = false;
	}

	//starts threads according to the arguments of the function
	public void startProcess(BinaryTree bt, int limit) {
		QueryThread Thread1 = new QueryThread(dbname, limit, bt.root);
		Thread1.start();
	}

	//this thread to execute the queries for certain iterations
	public static class SubQueryThread implements Callable<Long> {

		connector driver;
		BTNode<String> node;
		ExecutorService exe;
		String dbname;

		public SubQueryThread(String dbname, BTNode<String> node) {
			this.node = node; //query to be executed
			this.dbname = dbname;
		}

		@Override
		public Long call() throws Exception {
			long initialTime = System.currentTimeMillis(); //taking initial time

			int numthreads = 0;
			if (node.left != null) {
				numthreads++;
			}
			if (node.right != null) {
				numthreads++;
			}
			if (numthreads != 0) {
				exe = Executors.newFixedThreadPool(numthreads);
				ArrayList<Future<Long>> f = new ArrayList<>();
				if (node.left != null) {
					SubQueryThread l = new SubQueryThread(dbname, node.left);
					f.add(exe.submit(l));
				}
				if (node.right != null) {
					SubQueryThread r = new SubQueryThread(dbname, node.right);
					f.add(exe.submit(r));
				}
				for (Future<Long> future : f) {
					future.get();
				}
			}
			driver = new connector();
			driver.connectToDatabase(dbname);
			driver.executeUpdate(node.data);
			long finalTime = System.currentTimeMillis(); //taking final time
			driver.closeConnection();
			if (exe != null) {
				exe.shutdown();
			}
			return finalTime - initialTime;
		}
	}

	//this thread to execute the queries for certain iterations
	public static class QueryThread extends Thread {

		connector driver;
		int limit;
		BTNode<String> node;
		ExecutorService exe;
		String dbname;

		QueryThread(String dbname, int counter, BTNode<String> node) {
			limit = counter; //number of iterations
			this.node = node; //query to be executed
			this.dbname = dbname;
			driver = new connector();
			driver.connectToDatabase(dbname);
		}

		public void run() {

			float averageTime;long rows = -1;

			long initialTime = System.currentTimeMillis(); //taking initial time
			for (int i = 0; i < limit; i++) {
				int numthreads = 0;
				if (node.left != null) {
					numthreads++;
				}
				if (node.right != null) {
					numthreads++;
				}
				ExecutorService exe = Executors.newFixedThreadPool(numthreads);
				ArrayList<Future<Long>> f = new ArrayList<>();
				if (node.left != null) {
					SubQueryThread l = new SubQueryThread(dbname, node.left);
					f.add(exe.submit(l));
				}
				if (node.right != null) {
					SubQueryThread r = new SubQueryThread(dbname, node.right);
					f.add(exe.submit(r));
				}
				for (Future<Long> future : f) {
					try {
						future.get();
					} catch (InterruptedException e) {
						e.printStackTrace(System.out);
						System.exit(0);
					} catch (ExecutionException e) {
						e.printStackTrace(System.out);
						System.exit(0);
					}
				}
				rows = driver.executeQuery(node.data);
				if (exe != null) {
					exe.shutdown();
				}
				driver.executeUpdate(node.extra);
			}
			long finalTime = System.currentTimeMillis(); //taking final time
			driver.closeConnection();
			averageTime = ((float) (finalTime - initialTime) / 1000) / limit; //value in seconds

			printStats(averageTime, limit, rows);
		}

		//prints the statistics
		void printStats(float averageTime, int iterations, long rows) {

			System.out.println("\nStatistics for the Query:");
			System.out.println("Iterations of the Query: " + iterations);
			System.out.println("Average time taken by every query:" + averageTime);
			System.out.println("Number of rows:" + rows);

		}
	}

}