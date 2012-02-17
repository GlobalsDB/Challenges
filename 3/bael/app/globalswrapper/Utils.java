package globalswrapper;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class Utils {
	
	
	public static String TableNameToGlobalsName(String tableName)
    {
        return tableName + "D";
    }
	
	public static String TableNameToGlobalsIndexName(String tableName)
    {
        return tableName + "I";
    }
    
    public static String GlobalsNameToTableName(String globalsName)
    {
        return globalsName.substring(0, globalsName.length()-1);
    }
    
    public static String writeToFile(String filename, String str)
    {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(
             new OutputStreamWriter(
             new FileOutputStream("c:\\temp\\javaLog.txt"), "windows-1251"));
            writer.write(str);
            writer.close();
            }
        catch (Exception ex) {} 
        return str;
    }

}
