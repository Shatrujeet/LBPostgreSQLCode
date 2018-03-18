
public class Threads {

	public String queryExecute;
	public static String queryUpdate;
	public static float averageTime1;
	public static int ctr;
	public static connector driverQ, driverU;

	Threads(connector connectQuery, connector connectUpdate, String qFile, String uFile, boolean same) {
		readFiles rf = new readFiles();
		queryExecute = rf.read(qFile); //change the file path here
		queryUpdate = rf.read(uFile); //change the file path here
		checkQuery(queryExecute, "SELECT");
		checkQuery(queryUpdate, "UPDATE");
		averageTime1 = 0;
		ctr = 0;
		driverQ = connectQuery;
		if (same) {
			driverU = connectQuery;
			System.out.println("Using Same connections");
		} else {
			driverU = connectUpdate;
			System.out.println("Using Different connections");
		}
	}

	private void checkQuery(String query, String string) {
		if (!query.toUpperCase().startsWith(string.toUpperCase())) {
			System.out.println("ERROR:the following query should start with \"" + string + "\"");
			System.out.println(query);
			System.exit(0);
		}
	}

	public void startProcess(int count) {

		MyThread shootUpdate = new MyThread();
		//this would start shooting Updates
		shootUpdate.start();

		//calculating average Times 
		averageTime1 = driverQ.getAverTimeQuery(count, queryExecute);

		float averageTime2;
		averageTime2 = driverQ.getAverTimeQuery(count, queryExecute);

		printStats(count, averageTime2);
	}

	//this thread to execute updates
	public static class MyThread extends Thread {

		public void run() {
			//run till averageTime 1 changes from 0
			while (averageTime1 == 0) {
				driverU.executeUpdate(queryUpdate);
				ctr++;
			}
		}
	}

	//printing results
	public void printStats(int count, float averageTime2) {

		System.out.println("\n\nAverage Execution time for query with updates ");
		System.out.println("Row count returned by Query:" + driverQ.executeQuery(queryExecute));
		System.out.println("Iterations " + count);
		System.out.println("Average Time:" + averageTime1);
		System.out.println("Updates ran simultaneusly for " + ctr + " times");

		System.out.println("\n\nAverage Execution time for query without updates");
		System.out.println("Row count returned by Query:" + driverQ.executeQuery(queryExecute));
		System.out.println("Iterations " + count);
		System.out.println("Average Time:" + averageTime2);
		System.out.println("Updates ran simultaneusly for 0 times");

	}

}