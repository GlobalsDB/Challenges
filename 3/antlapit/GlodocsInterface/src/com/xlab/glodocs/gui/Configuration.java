package com.xlab.glodocs.gui;

import java.util.Locale;
import java.util.ResourceBundle;

public class Configuration {
	public static Locale[] locales = new Locale[] { Locale.ENGLISH,
			new Locale("ru") };
	public static Locale currentLocale = locales[0];

	public static ResourceBundle localization = ResourceBundle.getBundle(
			"com.xlab.glodocs.gui.localization.Localization",
			Configuration.currentLocale);
}
