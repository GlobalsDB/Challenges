package com.xlab.apacheglobals.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

public class BlankIcon implements Icon {
	private Color fillColor;

	private int size;

	public BlankIcon() {
		this(null, 11);
	}

	public BlankIcon(Color color, int size) {
		// UIManager.getColor("control")
		// UIManager.getColor("controlShadow")
		fillColor = color;

		this.size = size;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		if (fillColor != null) {
			g.setColor(fillColor);
			g.drawRect(x, y, size - 1, size - 1);
		}
	}

	public int getIconWidth() {
		return size;
	}

	public int getIconHeight() {
		return size;
	}
}
