package second;

public class MainThread {

	public String queryExecute;
	public String queryUpdate;
	public static boolean terminate;
	public String dbname;
	
	
	
	MainThread(String dbname, String qFile, String uFile)
	{
		this.dbname = dbname;
		readFiles rf=new readFiles();
		queryExecute=rf.read(qFile);//"src/query1"); //change the file path here
		queryUpdate=rf.read(uFile); //"src/query2"); //change the file path here
		terminate=false;
	}
	
	//starts threads according to the arguments of the function
	public void startProcess(int option,int limit1,int limit2)
	{
		
		if(option==0)//run the query as well as update
		{
			//boolean in the statement would determine if an updateQuery or ExecuteQuery
			
			QueryThread Thread1=new QueryThread(dbname, limit1,queryExecute, false); //for queries
			QueryThread Thread2=new QueryThread(dbname, limit2,queryUpdate,true); //for Updates
			Thread1.start();
			Thread2.start();

		}
		else if(option==1)//run just the query
		{
			QueryThread Thread1=new QueryThread(dbname, limit1,queryExecute, false); //for queries
			Thread1.start();			
		}
		else if(option==2)//run just the update
		{
			QueryThread Thread2=new QueryThread(dbname, limit2,queryUpdate,true); //for Updates
			Thread2.start();
			
		}
		else
		{
			System.out.println("Invalid option chosen");
		}
	}
	
	
	//this thread to execute the queries for certain iterations
	public static class QueryThread extends Thread{
		
		connector driver; 
		int limit;
		String query;
		boolean isUpdate;
		
		QueryThread(String dbname, int counter,String queryUpdate,boolean condition)
		{
		   limit=counter;	   //number of iterations
		   query=queryUpdate;  //query to be executed
		   isUpdate=condition; //checks whether an update or an execute statement
		   driver=new connector();
		   driver.connectToDatabase(dbname);
		}
		
		public void run()
		{
			
			int ctr=0;
			
			Long initialTime,finalTime;
			float averageTime;
			
			initialTime=System.currentTimeMillis(); //taking initial time
			
			if(isUpdate) //if an update query
			{
				while(ctr<limit && !terminate) //while depending on the number of iterations or  other threads status
				{
					driver.executeUpdate(query);
					ctr++;	
				}
			}
			else
			{
				while(ctr<limit && !terminate) //while depending on the number of iterations or  other threads status
				{
					driver.executeQuery(query);
					ctr++;	
				}
			}
			
			terminate=true; //if this thread finishes,stop the other one too
			
			finalTime=System.currentTimeMillis(); //taking final time
			averageTime=((float)(finalTime-initialTime)/1000)/ctr; //value in seconds
			
			driver.closeConnection();
			
			printStats(averageTime,ctr);
		}
		
		//prints the statistics
		void printStats(float averageTime,int iterations)
		{
			if(isUpdate)
			{
				System.out.println("\nStatistics for the Update ");
				System.out.println("Iterations of the Update: "+iterations);
				System.out.println("Average time taken by every update:"+averageTime);
			}
			else
			{
				System.out.println("\nStatistics for the Query:");
				System.out.println("Iterations of the Query: "+iterations);
				System.out.println("Average time taken by every query:"+averageTime);
				
			}
		}
	}
	
	
}