# LBPostgreSQLCode
Introduction
	This java program analyses the average execution time for a PostgreSQL query according to user preferences.The user can choose to execute updates at the same time as the query by selecting option 0 or run the query by itself by selecting option 1.There is also an option to run the updates by themselves by passing in option 2 in the arguments. Furthermore, the user needs to provide the paths for the query sql file and the update sql file along with the number of iterations.

Structure of the program
	This program consists of two threads:  MainThread and QueryThread. MainThread inputs the user specified options and starts the QueryThread accordingly.
	With option 0, MainThread starts two QueryThread, one for executing SQL DML update and the other for executing the query. If one QueryThread completes its iterations, it terminates the other.
	With option 1, MainThread starts one QueryThread to execute the input query and waits for this thread to perform the specified number of iterations.
	With option 2, MainThread starts one QueryThread to execute the input update(s) and waits for this thread to perform the specified number of iterations.

Requirements
	PostgreSQL server must be running.
	User postgres must have the password ‘root’ - this is hardcoded in the program.  If the postgres password is different then change accordingly.
	The application must run on the same server as PostgreSQL.

Running the code
Inputs:
	Database name: String
	Number of Iterations: int
	Location of the query file: String
	Location of the update file: String
	Option number: [0, 2]
	Option 0: Run query and execute both
	Option 1: Run just query
	Option 2: Run just update

Outputs:
	Option 0: (runs until either the query or the update finishes its designated iterations)
	Statistics for Query
		Iterations of the Query
		Average time taken to execute a query
	Statistics for Update
		Iterations of the Update
		Average time taken to execute an update

	Option 1:(runs just the query for designated iterations)
	Statistics for Query
		Iterations of the Query
		Average time taken to execute a query
	
	Option 2: (runs just the Update for designated iterations)
	Statistics for Update
		Iterations of the Update
		Average time taken to execute an update
			

