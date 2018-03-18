import java.io.File;

public class Main {

	public static connector driverQ, driverU;
	public static String queryUpdate;

	public static final String[] argsNames = { "DB Name", "Iterations", "Query File", "Update File" };

	public static void main(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (i < argsNames.length) {
				System.out.println(argsNames[i] + ": " + args[i]);
			} else {
				System.out.println("Skipped argument " + i + ": " + args[i]);
			}
		}

		if (args.length < 4) {
			System.out.println("ERROR: Need DB name, number of iterations, query file, update file");
			System.exit(0);
		}

		int iterations = getIterations(args[1]);

		checkFile(args[2]);
		checkFile(args[3]);

		//		int count =10000; //defines the number of iteratios for the query execeution
		driverQ = new connector();
		driverQ.connectToDatabase(args[0]); //connects to the database 
		driverU = new connector();
		driverU.connectToDatabase(args[0]); //connects to the database 

		boolean same = false;
//		same = true;
		//Threads runs the queries and updates
		Threads thread = new Threads(driverQ, driverU, args[2], args[3], same);
		thread.startProcess(iterations);

		driverQ.closeConnection();
		driverU.closeConnection();

	}

	private static int getIterations(String string) {
		try {
			int iterations = Integer.parseInt(string);
			return iterations;
		} catch (Exception e) {
			System.out.println("ERROR: Second argument must be a number. (" + string + ") is not a number");
			System.exit(0);
		}
		return -1;
	}

	private static void checkFile(String file) {
		File x = new File(file);
		if (!x.isFile()) {
			System.out.println("ERROR: File \"" + file + "\" is not a file.");
			System.exit(0);
		}
	}

}