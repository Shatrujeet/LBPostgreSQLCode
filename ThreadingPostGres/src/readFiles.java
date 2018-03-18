import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class readFiles {
 
	//reads the file and returns the string
	String read(String path)
	{
		File file = new File(path);
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		 
		String query = "",queryPart="";
		
		try {
			while (( queryPart= br.readLine()) != null)
			{
			   query+=queryPart;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return query;
	
	}
	
	
	
}