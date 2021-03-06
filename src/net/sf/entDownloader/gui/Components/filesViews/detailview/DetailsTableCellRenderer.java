/*
 *  DetailsTableCellRenderer.java
 *      
 *  Copyright 2010-2012 Kévin Subileau. 
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
package net.sf.entDownloader.gui.Components.filesViews.detailview;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import net.sf.entDownloader.core.FS_Element;
import net.sf.entDownloader.gui.Misc;
import net.sf.entDownloader.gui.Components.filesViews.CellBorder;

public class DetailsTableCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 7406804911789608757L;
	private CellBorder border = new CellBorder(
			net.sf.entDownloader.core.CoreConfig.getForegroundColor(), 1, true);

	public DetailsTableCellRenderer() {
		super();
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		setFont(table.getFont());
		setValue(value);
		setForeground(table.getForeground());

		if (isSelected) {
			border.setLineColor(table.getSelectionBackground());
			border.setBorderInsets(getBorder().getBorderInsets(this));
			setBorder(border);
			setBackground(table.getSelectionBackground());
		} else {
			setBorder(noFocusBorder);
			setBackground(table.getBackground());
		}

		if (column == table.getTableHeader().getColumnModel()
				.getColumnIndex("Taille")) {
			setHorizontalAlignment(SwingConstants.RIGHT);
		} else {
			setHorizontalAlignment(SwingConstants.LEFT);
		}

		if (!(value instanceof FS_Element)) {
			setIcon(null);
		} else {
			FS_Element f = (FS_Element) value;
			setIcon(Misc.getFileTypeIcon(f, ((DetailsTable) table).getZoomLevel()));
		}
		return this;
	}
}
