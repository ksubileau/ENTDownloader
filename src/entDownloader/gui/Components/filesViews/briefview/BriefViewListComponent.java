/*
 *  BriefViewListComponent.java
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
package entDownloader.gui.Components.filesViews.briefview;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import entDownloader.core.FS_Element;
import entDownloader.gui.events.DoubleClickOnRowEvent;
import entDownloader.gui.events.GuiBroadcaster;

public class BriefViewListComponent extends JList {

	private static final long serialVersionUID = 1303490565572301885L;
	private BriefViewListRenderer renderer = new BriefViewListRenderer();

	public BriefViewListComponent() {
		setFocusTraversalKeysEnabled(false);
		addMouseActions();
		setOtherProperties();
	}

	private void setOtherProperties() {
		setLayoutOrientation(JList.VERTICAL_WRAP);
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	}

	/**
	 * Returns the preferred number of visible rows.
	 * 
	 * @return an integer indicating the preferred number of rows to display
	 *         without using a scroll bar
	 */
	@Override
	public int getVisibleRowCount() {
		//to fill the whole space
		return -1;
	}

	@Override
	public ListCellRenderer getCellRenderer() {

		return renderer;
	}

	private void addMouseActions() {
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = BriefViewListComponent.this.locationToIndex(e
						.getPoint());
				if (e.getClickCount() == 2
						&& (row >= 0)
						&& (row < BriefViewListComponent.this.getModel()
								.getSize())) {//double click on row
					GuiBroadcaster
							.fireDoubleClickOnRow(new DoubleClickOnRowEvent(
									(FS_Element) getModel().getElementAt(row)));
					return;
				}
			}
		});
	}

}
