package parallel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import parallel.async.AsyncSocketServer;

public class Main {

	public static final String[] argsNames = { "DB Name", "Iterations", "Dependency File", "Number of threads" };

	public static void main(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (i < argsNames.length) {
				System.out.println(argsNames[i] + ": " + args[i]);
			} else {
				System.out.println("Skipped argument " + i + ": " + args[i]);
			}
		}

		if (args.length < 4) {
			System.out.println("ERROR: Need DB name, number of iterations, dependency file, and number of threads");
			System.exit(0);
		}

		int iterations = getIterations(args[1]);
		int threads = getIterations(args[3]);
		String dbname = args[0];

		checkFile(args[2]);
		String dir = getDirectory(args[2]);
		BinaryTree<String> bt = readDependencyFile(args[2], dir);

		//starting the major thread
		AsyncSocketServer server = new AsyncSocketServer(threads, 2, 0, 0, dbname);
		MainThread2 thread = new MainThread2(dbname, threads);
		thread.startProcess(bt, iterations);
		AsyncSocketServer.shutdown();

	}

	private static String getDirectory(String string) {
		return string.substring(0, string.lastIndexOf('/') + 1);
	}

	private static BinaryTree<String> readDependencyFile(String fileName, String dir) {
		File dependencyFile = new File(fileName);
		if (!dependencyFile.exists()) {
			System.out.println("ERROR: The dependency file doesn't exist \"" + fileName + "\"");
			System.exit(0);
		}
		if (!dependencyFile.isFile()) {
			System.out.println("ERROR: This is not a dependency file \"" + fileName + "\"");
			System.exit(0);
		}

		BinaryTree<String> bt = new BinaryTree<String>();
		HashMap<Integer, BTNode<String>> hm = new HashMap<>();
		hm.put(1, bt.root);
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(dependencyFile));
			String line = null;
			while ((line = in.readLine()) != null) {
				processLine(line, hm, dir);
			}
		} catch (IOException e) {
			e.printStackTrace(System.out);
			System.exit(0);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace(System.out);
					System.exit(0);
				}
			}
		}
		return bt;
	}

	private static void processLine(String line, HashMap<Integer, BTNode<String>> hm, String dir) {
		String[] tokens = line.split(",");
		if (tokens.length == 2) {
			if (tokens[0].equals("drop")) {
				BTNode<String> btn = hm.get(1);
				btn.extra = getQuery(dir + tokens[1]);
				return;
			}
		}
		if (tokens.length != 4) {
			System.out.println("ERROR: Following line in Dependency file have " + tokens.length + " tokens.");
			System.out.println(line);
			System.exit(0);
		}
		int key = toInt(tokens[0], true);
		BTNode<String> btn = hm.get(key);
		if (btn == null) {
			System.out.println("ERROR: Unknown parent for node id = " + key + ".");
			System.exit(0);
		}
		btn.data = getQuery(dir + tokens[3]);
		btn.name = tokens[3];
		int left = toInt(tokens[1], false);
		int right = toInt(tokens[2], false);
		if (left != -1) {
			BTNode<String> child = new BTNode<String>();
			btn.left = child;
			hm.put(left, child);
		}
		if (right != -1) {
			BTNode<String> child = new BTNode<String>();
			btn.right = child;
			hm.put(right, child);
		}
	}

	private static int toInt(String string, boolean must) {
		try {
			int num = Integer.parseInt(string);
			return num;
		} catch (Exception e) {
			if (must) {
				System.out.println("ERROR: not a number. (" + string + ")");
				System.exit(0);
			}
		}
		return -1;
	}

	private static String getQuery(String fileName) {

		File queryFile = new File(fileName);
		if (!queryFile.exists()) {
			System.out.println("ERROR: The query file doesn't exist \"" + fileName + "\"");
			System.exit(0);
		}
		if (!queryFile.isFile()) {
			System.out.println("ERROR: This is not a query file \"" + fileName + "\"");
			System.exit(0);
		}

		BufferedReader in = null;
		String query = "";
		try {
			in = new BufferedReader(new FileReader(queryFile));
			String line = null;
			while ((line = in.readLine()) != null) {
				query += line;
			}
		} catch (IOException e) {
			e.printStackTrace(System.out);
			System.exit(0);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace(System.out);
					System.exit(0);
				}
			}
		}
		return query;
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
		if (!x.exists()) {
			System.out.println("ERROR: File \"" + file + "\" does not exist.");
			System.exit(0);
		}
		if (!x.isFile()) {
			System.out.println("ERROR: File \"" + file + "\" is not a file.");
			System.exit(0);
		}
	}

}