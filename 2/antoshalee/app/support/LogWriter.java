package support;
import java.io.*;
import java.io.PrintWriter;
public class LogWriter {

	public String FileName;
	public String WriteToFile(String str, Boolean writeToScreen)
	{
	    PrintWriter writer = null;
	    try 
	    {
	    	if (writeToScreen)
	    	{
	    		System.out.println(str);
	    	}
	       	writer = new PrintWriter(
	        				new OutputStreamWriter(
	        				new FileOutputStream(this.FileName), "windows-1251"));
	        				writer.write(str);
	        				writer.close();
	    }
	    catch (Exception ex) 
	    {
	    	
	    } 
	    return str;
	}
}
