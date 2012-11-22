/*
 *  DetailsTable.java
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

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.InputMap;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.TableCellRenderer;

import net.sf.entDownloader.core.FS_Element;
import net.sf.entDownloader.gui.Misc;
import net.sf.entDownloader.gui.events.DoubleClickOnRowEvent;
import net.sf.entDownloader.gui.events.GuiBroadcaster;

public class DetailsTable extends JTable {

	private static final long serialVersionUID = 2480368648071210873L;

	private DetailsTableCellRenderer renderer = new DetailsTableCellRenderer();
	private int zoom = Misc.SMALL;

	public DetailsTable() {
		super();
		setOtherProperties();
	    removeDefaultKeys();
		addMouseActions();
	}

	private void setOtherProperties() {
		this.getTableHeader().setReorderingAllowed(false);
		this.setShowGrid(false);
		this.setSelectionBackground(new Color(208, 227, 252));

		//this.setColumnSelectionAllowed(false);
		//this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	}

	private void removeDefaultKeys() {
		InputMap mainMap = this.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		
		mainMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK) , "none");
		mainMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK) , "none");
		mainMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK) , "none");
	}

	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		return renderer;
	}

	private void addMouseActions() {
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = DetailsTable.this.rowAtPoint(e.getPoint());
				if (e.getClickCount() == 2
						&& (row >= 0)
						&& (row < ((DetailsTableModel) DetailsTable.this
								.getModel()).getRowCount())) {//double click on row
					GuiBroadcaster
							.fireDoubleClickOnRow(new DoubleClickOnRowEvent(
									(FS_Element) getModel().getValueAt(row, 0)));
					return;
				}
			}
		});
	}

	/**
	 * Scrolls the viewport to the row at the specified index.
	 * Stolen from javax.swing.JList.
	 * 
	 * @param index
	 *            the index of the row.
	 */
	public void ensureIndexIsVisible(int index) {
		Rectangle cellBounds = this.getCellRect(index, 0, true);
		if (cellBounds != null) {
			scrollRectToVisible(cellBounds);
		}
	}

	public void setZoomLevel(int zoom) {
		if(zoom==Misc.MEDIUM) {
			setRowHeight(35);
			this.zoom = zoom;
		} else {
			setRowHeight(19);
			this.zoom = Misc.SMALL;
		}
	}

	public int getZoomLevel() {
		return zoom;
	}
}
