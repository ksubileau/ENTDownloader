/*
 *  CellBorder.java
 *      
 *  Copyright 2010 Kévin Subileau. 
 *
 *	This file is part of ENTDownloader.
 *    
 *  ENTDownloader is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  ENTDownloader is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program; If not, see <http://www.gnu.org/licenses/>.
 */
package entDownloader.gui.Components.filesViews;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.border.LineBorder;

/**
 * This class creates a dotted border
 */
public class CellBorder extends LineBorder {
	private static final long serialVersionUID = -2938498018399922656L;
	final static float dash1[] = { 2.0f };
	final static BasicStroke dashed = new BasicStroke(0.2f,
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f);
	private Insets borderInsets;

	/**
	 * @param color
	 */
	public CellBorder(Color color) {
		super(color);
		borderInsets = new Insets(0, 0, 0, 0);
	}

	/**
	 * @param color
	 * @param thickness
	 */
	public CellBorder(Color color, int thickness) {
		super(color, thickness);
		borderInsets = new Insets(0, 0, 0, 0);
	}

	/**
	 * @param color
	 * @param thickness
	 * @param roundedCorners
	 */
	public CellBorder(Color color, int thickness, boolean roundedCorners) {
		super(color, thickness, roundedCorners);
		borderInsets = new Insets(0, 0, 0, 0);
	}

	public void setLineColor(Color c) {
		lineColor = c;
	}

	/**
	 * Paints the border for the specified component with the
	 * specified position and size.
	 * 
	 * @param c
	 *            the component for which this border is being painted
	 * @param g
	 *            the paint graphics
	 * @param x
	 *            the x position of the painted border
	 * @param y
	 *            the y position of the painted border
	 * @param width
	 *            the width of the painted border
	 * @param height
	 *            the height of the painted border
	 */
	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width,
			int height) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(dashed);
		Color oldColor = g2.getColor();
		int i;
		g2.setColor(lineColor);
		for (i = 0; i < thickness; i++) {
			if (!roundedCorners) {
				g2.drawRect(x + i, y + i, width - i - i - 1, height - i - i - 1);
			} else {
				g2.drawRoundRect(x + i, y + i, width - i - i - 1, height - i
						- i - 1, 3, 3);
			}
		}
		g2.setColor(oldColor);
	}

	@Override
	public Insets getBorderInsets(Component c, Insets insets) {
		return borderInsets;
	}

	@Override
	public Insets getBorderInsets(Component c) {
		return borderInsets;
	}

	public void setBorderInsets(Insets insets) {
		borderInsets = insets;
	}

}
