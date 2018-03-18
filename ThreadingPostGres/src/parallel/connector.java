package parallel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;


public class connector {
	Connection connection;

	public connector()
	{

	}

	public boolean connectToDatabase(String dbname) //connects to the database
	{
//		System.out.println("-------- PostgreSQL "
//				+ "JDBC Connection Testing ------------");

		try {

			Class.forName("org.postgresql.Driver");

		} catch (ClassNotFoundException e) {

			System.out.println("Where is your PostgreSQL JDBC Driver? "
					+ "Include in your library path!");
			e.printStackTrace();
			return false;

		}

//		System.out.println("PostgreSQL JDBC Driver Registered!");

		connection = null;

		try {

			connection = DriverManager.getConnection(
					"jdbc:postgresql://localhost:5432/"+ dbname, "mr1",
					"root");

		} catch (SQLException e) {

			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return false;

		}

		if (connection != null) {
//			System.out.println("You made it, take control your database now!");
			return true;
		} else {
			System.out.println("Failed to make connection!");
			return false;
		}
	}

	void closeConnection() 
	{
		try {
			connection.close();
		} catch (SQLException e) {

			e.printStackTrace();
			System.out.println("The connection did not close successfuly");
		}
	}


	///just executes the query and returns the count of rows
	public long executeQuery(String query) 
	{

		long result=-1;
		try 
		{
			ResultSet rs;
			PreparedStatement pStatement;
			pStatement = connection.prepareStatement(query);
			rs=pStatement.executeQuery();
			result=0;
			while(rs.next())
			{
				result++;
			}
			pStatement.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return result;  //if something fails,-1 gets returned
		}

	}
	
	//especially to execute an update query
	public boolean executeUpdate(String query) //just executes the query and returns if exceuted
	{

		try 
		{
			ResultSet rs;
			PreparedStatement pStatement;
			pStatement = connection.prepareStatement(query);
			return pStatement.execute();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;  //if something fails,-1 gets returned
		}

	}

	/*
	//runs a query for limit iterations
	float getAverTimeQuery(int limit,String query) 
	{
		float averageTime; //for calculating overall average query execution time
		Long initialTime,finalTime;
		initialTime=System.currentTimeMillis();
		
		for(int i=0;i<limit;i++)
			{

				long count=executeQuery(query);

				if(count==-1)
				{
					System.out.println("Query "+limit+" failed to execute.");
					return 0;
				}
				
			}
		finalTime=System.currentTimeMillis();

		averageTime=((float)(finalTime-initialTime)/1000)/limit; //value in seconds
		return averageTime;
	}
	*/

}