/*
 *  BriefViewListRenderer.java
 *      
 *  Copyright 2010-2011 Kévin Subileau. 
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
package net.sf.entDownloader.gui.Components.filesViews.briefview;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import net.sf.entDownloader.core.FS_Element;
import net.sf.entDownloader.gui.Misc;
import net.sf.entDownloader.gui.Components.filesViews.CellBorder;

public class BriefViewListRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = 7583591992506052375L;

	private CellBorder border = new CellBorder(
			net.sf.entDownloader.core.CoreConfig.getForegroundColor(), 1, true);

	public BriefViewListRenderer() {
		super();
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		JLabel c = (JLabel) super.getListCellRendererComponent(list, value,
				index, isSelected, cellHasFocus);
		if (isSelected) {
			border.setLineColor(new Color(125, 162, 206));
			border.setBorderInsets(getBorder().getBorderInsets(this));
			setBorder(border);
			setBackground(new Color(208, 227, 252));
		} else {
			setBackground(list.getBackground());
		}

		FS_Element f = (FS_Element) value;
		setForeground(list.getForeground());
		c.setIcon(Misc.getIcon(f));
		return c;
	}
}
