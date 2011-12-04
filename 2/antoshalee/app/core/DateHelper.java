package core;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateHelper {
	private static String DateFormatString = "yyyy-MM-dd HH:mm:ss";

	public static String DateToString(Date dateValue) {
		if (dateValue == null)
			return "";
		DateFormat df = new SimpleDateFormat(DateFormatString);
		df.setTimeZone(TimeZone.getTimeZone("Asia/Irkutsk"));
		return df.format(dateValue);
	}

	public static Date StringToDate(String strValue) {
		DateFormat df = new SimpleDateFormat(DateFormatString);
		Date dateValue = null;
		try {
			dateValue = df.parse(strValue.toString());
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return dateValue;
	}

}
