import java.io.*;
import java.util.Scanner;

public class LogFeeder {

  public static void main(String... aArgs) throws FileNotFoundException {
    LogFeeder parser = new LogFeeder("C:\\JavaApps\\Globals\\log_src\\log_src.txt");
    parser.processLineByLine();
    log("Done.");
  }
  
  /**
   Constructor.
   @param aFileName full name of an existing, readable file.
  */
  public LogFeeder(String aFileName){
    fFile = new File(aFileName);  
  }
  
  /** Template method that calls {@link #processLine(String)}.  */
  public final void processLineByLine() throws FileNotFoundException {
    //Note that FileReader is used, not File, since File is not Closeable
    Scanner scanner = new Scanner(new FileReader(fFile));
    try {
      //first use a Scanner to get each line
      while ( scanner.hasNextLine() ){
        processLine( scanner.nextLine() );
		try{
			sleep(); // give the impresseion that this is a live feed.
		}
		catch(InterruptedException ie)
		{
		}
      }
    }
    finally {
      //ensure the underlying stream is always closed
      //this only has any effect if the item passed to the Scanner
      //constructor implements Closeable (which it does in this case).
      scanner.close();
    }
  }

	private static void sleep() throws InterruptedException
	{
		try{
			Thread.currentThread().sleep(1);
		}
		catch(InterruptedException ie)
		{
		}
	}
  /** 

  */
  protected void processLine(String aLine){
    //use a second Scanner to parse the content of each line 
    Scanner scanner = new Scanner(aLine);
    scanner.useDelimiter(" ");
    if ( scanner.hasNext() ){
      String[] anArray;              
	  anArray = new String[8];
	  String IP = scanner.next();
	  if (IP.length()==3)
	  {
		  return;
	  }
	  anArray[0]=IP;
	  scanner.next();// throw away
	  scanner.next();// throw away
      String date = scanner.next()+scanner.next();	anArray[1]=date;
	  scanner.next(); // throw away
	  String firstUrl=scanner.next();anArray[2]=firstUrl;
	  String mode=scanner.next();					anArray[3]=mode;
	  String err_code=scanner.next();				anArray[4]=err_code;
	  String port=scanner.next();					anArray[5]=port;
	  String secondUrl=scanner.next();				anArray[6]=secondUrl;
	  String browserData="";
	  while (scanner.hasNext()) {browserData+=scanner.next() + " ";}
													anArray[7]=browserData;

      log("Writting data..."+IP);
		 
		 try {
			print(anArray);
		 }catch(IOException err)
		{

		}
    }
    else {
      log("Empty or invalid line. Unable to process.");
    }
    //no need to call scanner.close(), since the source is a String
  }
  
  // PRIVATE 
  private final File fFile;
  
  private static void log(Object aObject){
    System.out.println(String.valueOf(aObject));
  }
  private static void print(String Data[]) throws IOException{
	String IP=Data[0];
	String date=Data[1];
	String firstUrl=Data[2];
	String mode=Data[3];
	String err_code=Data[4];
	String port=Data[5];
	String secondUrl=Data[6];
	String browserData=Data[7];
	String Delim="	";
	String lineToFile=IP + Delim + date + Delim + firstUrl + Delim + mode + Delim + err_code + Delim + port + Delim + secondUrl + Delim + browserData;
	//use buffering
	Writer output = new BufferedWriter(new FileWriter("C:\\JavaApps\\Globals\\log_src\\log.txt",true));
    try {
      //FileWriter always assumes default encoding is OK!
      output.write( lineToFile+"\n");
    }
    finally {
      output.close();
    }
  }

  
  private String quote(String aText){
    String QUOTE = "'";
    return QUOTE + aText + QUOTE;
  }
} 