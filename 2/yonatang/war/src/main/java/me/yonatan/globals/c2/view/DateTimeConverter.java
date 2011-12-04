package me.yonatan.globals.c2.view;

import java.util.Locale;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

@FacesConverter(forClass = DateTime.class)
public class DateTimeConverter implements Converter {
	private DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withLocale(Locale.US);

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		try {
			return DateTime.parse(value, dtf);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value instanceof DateTime) {
			return ((DateTime) value).toString(dtf);
		}
		return null;
	}

}
