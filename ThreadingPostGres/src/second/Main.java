package second;

import java.io.File;

public class Main {

	public static final String[] argsNames = { "DB Name", "Iterations", "Query File", "Update File", "Option" };

	public static void main(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (i < argsNames.length) {
				System.out.println(argsNames[i] + ": " + args[i]);
			} else {
				System.out.println("Skipped argument " + i + ": " + args[i]);
			}
		}

		if (args.length < 5) {
			System.out.println("ERROR: Need DB name, number of iterations, query file, update file, option");
			System.exit(0);
		}

		int iterations = getIterations(args[1]);

		checkFile(args[2]);
		checkFile(args[3]);

		int option, limit1, limit2;

		//options:
		//0 Run query and execute both
		//1 run just query
		//2 run just update
		//Variables: limit1(iterations for query) limit2(iterations for update)

		switch (args[4]) {
		case "0":
			System.out.println("Run query and execute both");
			option = 0;
			break;
		case "1":
			System.out.println("Run just query");
			option = 1;
			break;
		case "2":
			System.out.println("Run just update");
			option = 2;
			break;
		default:
			option = -1;
			System.out.println("ERROR: Incorrect option (" + args[4] + ").");
			System.exit(0);

		}

		limit1 = iterations; //100;
		limit2 = iterations; //10000;

		//starting the major thread
		MainThread thread = new MainThread(args[0], args[2], args[3]);
		thread.startProcess(option, limit1, limit2);

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