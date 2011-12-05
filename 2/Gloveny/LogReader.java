import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Scanner;

import com.intersys.globals.*;
/*
 * Run this with java -Xss1024k LogReader

 * @author Graham Vincent
 */
public class LogReader extends JFrame {
	
	Connection connection = ConnectionContext.getConnection();
	NodeReference webLogDB = null;
	public static void main(String args[]) {
       LogReader logreader = new LogReader();
	   logreader.setSize(800, 500);
		logreader.setVisible(true);
    }
	public LogReader() {
		initComponents();
	}
	
	private void initComponents() {
		
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		label1 = new JLabel();
		iptxt = new JTextField();
		Find = new JButton();
		label2 = new JLabel();
		datetxt = new JTextField();
		ReadLog = new JButton();
		label3 = new JLabel();
		porttxt = new JTextField();
		Next = new JButton();
		Prev = new JButton();
		scrollPane1 = new JScrollPane();
		txtOutput = new JTextArea();
		buttonBar = new JPanel();
		okButton = new JButton();
		dbAddCount = new Integer(0);
		currentPageIndicator = new String("");

		//======== this ========
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));

			// JFormDesigner evaluation mark
			dialogPane.setBorder(new javax.swing.border.CompoundBorder(
				new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
					"Globals Log file reader", javax.swing.border.TitledBorder.CENTER,
					javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
					java.awt.Color.red), dialogPane.getBorder())); dialogPane.addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{
				contentPanel.setLayout(new GridBagLayout());
				((GridBagLayout)contentPanel.getLayout()).columnWidths = new int[] {0, 95, 303, 326, 0};
				((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0, 189, 0};
				((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0E-4};
				((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

				//---- label1 ----
				label1.setText("IP Address");
				contentPanel.add(label1, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));
				contentPanel.add(iptxt, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));

				//---- Find ----
				Find.setText("Find");
				contentPanel.add(Find, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));
				Find.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e3)
					{
						// go find records with matching values to fields entered by user
						
						findRecordsUsingValues();
						
					}
				}); 

				//---- label2 ----
				label2.setText("Date");
				contentPanel.add(label2, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));
				contentPanel.add(datetxt, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));

				//---- ReadLog ----
				ReadLog.setText("Read log file");
				ReadLog.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						//Execute when button is pressed
						try {
			
							readTheLogFile();
						}
						catch(FileNotFoundException ie)
						{
						}
					}
				});  
					contentPanel.add(ReadLog, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

				//---- label3 ----
				label3.setText("Port number");
				contentPanel.add(label3, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));
				contentPanel.add(porttxt, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));

				//---- Next ----
				Next.setText("Next");
				contentPanel.add(Next, new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));
				Next.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e2)
					{
						loadPageOfRecords("Next");
					}
				}); 


				//---- Prev ----
				Prev.setText("Prev");
				contentPanel.add(Prev, new GridBagConstraints(3, 4, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));
				Prev.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e2)
					{
						loadPageOfRecords("Prev");
					}
				});
				//======== scrollPane1 ========
				{
					txtOutput.setEditable(false);
					scrollPane1.setViewportView(txtOutput);
				}
				contentPanel.add(scrollPane1, new GridBagConstraints(1, 6, 3, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
				buttonBar.setLayout(new GridBagLayout());
				((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 80};
				((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0};

				//---- okButton ----
				okButton.setText("Close");
				buttonBar.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));
				okButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						System.exit(0);
					}
				});
			}
			dialogPane.add(buttonBar, BorderLayout.SOUTH);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	

	private void findRecordsUsingValues()
	{
		// search through the db and find matches
		String IP=iptxt.getText();
		String date=datetxt.getText();
		String port=porttxt.getText();
		String msg="";
		Integer count=0;
		String t=IP+date+port+"";
		Integer tlen=t.length();
		if(tlen==0)
		{
			logToScreen("# # # # Please enter something to search for # # # #" , true);
			return;
		}
		try
		{	
			if (!connection.isConnected()) {
				connection.connect();
			}
			NodeReference webLogDB = connection.createNodeReference("webLogDB");

			// start a subscript key and loop
			String subscript = "";
			subscript = webLogDB.nextSubscript("webLogDB",subscript);
			String tIP="";
			String tDate="";
			String tPort="";
			Boolean foundIP=true;
			Boolean foundDate=true;
			Boolean foundPort=true;
			do {
				if (subscript.length() > 0)
				{
					foundIP=true;
					foundDate=true;
					foundPort=true;

					tIP=webLogDB.getString("webLogDB",subscript,"IP");
					tDate=webLogDB.getString("webLogDB",subscript,"Date");
					tPort=webLogDB.getString("webLogDB",subscript,"Port");
					
					
					if(IP.length()>0)
					{
						foundIP=false;
						if(tIP.indexOf(IP)>-1)
						{
							foundIP=true;
						}
					}
					if(date.length()>0)
					{
						foundDate=false;
						if(tDate.indexOf(date)>-1)
						{
							foundDate=true;
						}
					}
					if(port.length()>0)
					{
						foundPort=false;
						if(tPort.indexOf(port)>-1)
						{
							foundPort=true;
						}
					}
					if(foundIP==true && foundDate==true && foundPort==true)
					{
						msg+="IP: "+tIP+"	Date: "+tDate+"	Port: "+tPort+"\n";
						count++;
					}
				}
				subscript = webLogDB.nextSubscript("webLogDB",subscript);

			} while (subscript.length() > 0);

			if(msg!="")
			{
				logToScreen(msg+"\nFound:"+count,true);
			}else{
				logToScreen("No matches found",true);
			}
		
			//java -Xss1024k LogReader
		}
		catch (GlobalsException e) {
			logToConsole("Caught GlobalsException: " + e.getMessage());
		}
		finally {
			//logToConsole("Closing connection.");
			try {
				connection.close();
			} catch (GlobalsException e) {
				logToConsole("Caught GlobalsException: " + e.getMessage());
			}
		}
	}

	private void loadPageOfRecords(String direction){
		// this method pages thorugh the results in the database 10 records at a time.
		String msg=new String("");
		String tIP="";
		String tDate="";
		String tPort="";
		String FirstUrl="";
		String Mode="";
		String Err_code="";
		String SecondUrl="";
		String BrowserData="";
		Integer count=0;
		try
		{	
			if (!connection.isConnected()) {
				connection.connect();
			}
			NodeReference webLogDB = connection.createNodeReference("webLogDB");

			// start a subscript key and loop
			
			currentPageIndicator = webLogDB.nextSubscript("webLogDB",currentPageIndicator);
			logToScreen("",true); //clear screen
			do {
				if (currentPageIndicator.length() > 0)
				{
					count++;
					msg="";
					tIP=webLogDB.getString("webLogDB",currentPageIndicator,"IP");
					tDate=webLogDB.getString("webLogDB",currentPageIndicator,"Date");
					tPort=webLogDB.getString("webLogDB",currentPageIndicator,"Port");
					FirstUrl=webLogDB.getString("webLogDB",currentPageIndicator,"FirstUrl");
					Mode=webLogDB.getString("webLogDB",currentPageIndicator,"Mode");
					Err_code=webLogDB.getString("webLogDB",currentPageIndicator,"Err_code");
					SecondUrl=webLogDB.getString("webLogDB",currentPageIndicator,"SecondUrl");
					BrowserData=webLogDB.getString("webLogDB",currentPageIndicator,"BrowserData");

			
				
					msg+="ROWID: "+currentPageIndicator+"	count("+count+")\n";
					msg+="IP: "+tIP+"	Date: "+tDate+"	Port: "+tPort+"\n";
					msg+="FirstUrl: "+FirstUrl+"	Mode: "+Mode+"	Error code: "+Err_code+"\n";
					msg+="SecondUrl: "+SecondUrl+"\n";
					msg+="BrowserData: "+BrowserData+"\n";
					msg+="=============================================\n";
					logToScreen(msg,false);
				}
				if(direction=="Next")
				{
					currentPageIndicator = webLogDB.nextSubscript("webLogDB",currentPageIndicator);
				}
				if(direction=="Prev")
				{
					currentPageIndicator = webLogDB.previousSubscript("webLogDB",currentPageIndicator);
				}

			} while (currentPageIndicator.length() > 0  && count<10);
		}
		catch (GlobalsException e) {
			logToConsole("Caught GlobalsException: " + e.getMessage());
		}
		finally {
			//logToConsole("Closing connection.");
			try {
				connection.close();
			} catch (GlobalsException e) {
				logToConsole("Caught GlobalsException: " + e.getMessage());
			}
		}
	}

	private void logToScreen(String text,Boolean clear) {
		// print some text to the text area
		if(clear==false)
		{
			text=txtOutput.getText()+text;
		}
		txtOutput.setText(text+"\n");
	}
	private void logToConsole(String text)
	{
		System.out.println(text);
	}

	private void readTheLogFile() throws FileNotFoundException
	{
		// this method will read in the log file and add entries into the globals db
		logToScreen("Reading log file\n\n",true);
		Scanner scanner = new Scanner(new FileReader("C:\\JavaApps\\Globals\\log_src\\log.txt"));
		try {
			dbAddCount=0;
			try
			{
				// get a handle on the global 
				if (!connection.isConnected()) {
					connection.connect();
				}
				webLogDB = connection.createNodeReference("webLogDB");
				// first use a Scanner to get each line
				while ( scanner.hasNextLine() ){
					processLine( scanner.nextLine() );
				}
				logToScreen("Added "+dbAddCount+" to db",false);
			}
			catch (GlobalsException e) {
				connection.close();
				logToConsole("Caught GlobalsException: " + e.getMessage());
			}
			finally {
				//logToConsole("Closing connection.");
				try {
					connection.close();
				} catch (GlobalsException e) {
					logToConsole("Caught GlobalsException: " + e.getMessage());
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

	 protected void processLine(String aLine){

		//use a second Scanner to parse the content of each line 
		Scanner scanner = new Scanner(aLine);
		scanner.useDelimiter("	"); // tab delimited
		if ( scanner.hasNext() ){
			String[] anArray;              
			anArray = new String[8];
			anArray[0] = scanner.next(); // IP address
			anArray[1] = scanner.next(); // date
			anArray[2] = scanner.next(); // firstUrl
			anArray[3] = scanner.next(); // mode
			anArray[4] = scanner.next(); // err_code
			anArray[5] = scanner.next(); // port
			anArray[6] = scanner.next(); // secondUrl
			anArray[7] = scanner.next(); // browserData
		
			addToDatabase(anArray);
		}
		else {
			logToConsole("Empty or invalid line. Unable to process.");
		}
		//no need to call scanner.close(), since the source is a String
	}
	private void addToDatabase(String Data[])
	{
		try
		{	
			
			
			long ROWID = webLogDB.increment(1);
	
			webLogDB.set(Data[0], "webLogDB",ROWID,"IP");
			webLogDB.set(Data[1], "webLogDB",ROWID,"Date");
			webLogDB.set(Data[2], "webLogDB",ROWID,"FirstUrl");
			webLogDB.set(Data[3], "webLogDB",ROWID,"Mode");
			webLogDB.set(Data[4], "webLogDB",ROWID,"Err_code");
			webLogDB.set(Data[5], "webLogDB",ROWID,"Port");
			webLogDB.set(Data[6], "webLogDB",ROWID,"SecondUrl");
			webLogDB.set(Data[7], "webLogDB",ROWID,"BrowserData");

			//java -Xss1024k LogReader
			logToScreen("Adding record "+ROWID,false);
			dbAddCount++;
		}
		catch (GlobalsException e) {
			connection.close();
			logToConsole("Caught GlobalsException: " + e.getMessage());
		}
	}

	// Generated using JFormDesigner Evaluation license - Graham Vincent
	private JPanel dialogPane;
	private JPanel contentPanel;
	private JLabel label1;
	private JTextField iptxt;
	private JButton Find;
	private JLabel label2;
	private JTextField datetxt;
	private JButton ReadLog;
	private JLabel label3;
	private JTextField porttxt;
	private JButton Next;
	private JButton Prev;
	private JScrollPane scrollPane1;
	private JTextArea txtOutput;
	private JPanel buttonBar;
	private JButton okButton;
	private Integer dbAddCount;
	private String currentPageIndicator; // used as a global indecator for Prev and Next button.

	
}


/*public class ReadLogFile extends Thread {
	
	public static final int MAX_PRIMES = 1000000;
	public static final int TEN_SECONDS = 10;
	public static final int TEN_SECONDS = 10;
	public volatile boolean finished = false;
	public void run() {
		int[] primes = new int[MAX_PRIMES];
		int count = 0;
		for (int i=2; count<MAX_PRIMES; i++) {
		// Check to see if the timer has expired
		if (finished) {
			break;
		}
		boolean prime = true;
		for (int j=0; j<count; j++) {
		if (i % primes[j] == 0) {
			prime = false;
			break;
		}
	}
	if (prime) {
		primes[count++] = i;
		System.out.println("Found prime: " + i);
		}
	}
	}
	public static void main(String[] args) {
		CalculatePrimes calculator = new CalculatePrimes();
		calculator.start();
		try {
			Thread.sleep(TEN_SECONDS);

		}
		catch (InterruptedException e) {
		// fall through
		}
			calculator.finished = true;
		}
}




try
						{	
							if (!connection.isConnected()) {
								connection.connect();
							}
							// my debug, kill the database
							NodeReference webLogDB = connection.createNodeReference("webLogDB");
							webLogDB.kill();
							logToScreen("killed all nodes",false);
						}
						catch (GlobalsException e) {
							logToConsole("Caught GlobalsException: " + e.getMessage());
						}
						finally {
							//logToConsole("Closing connection.");
							try {
								connection.close();
							} catch (GlobalsException e) {
								logToConsole("Caught GlobalsException: " + e.getMessage());
							}
						}





*/