/*
 *  BriefView.java
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
package entDownloader.gui.Components.filesViews.briefview;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionListener;

import entDownloader.core.CoreConfig;
import entDownloader.core.FS_Element;
import entDownloader.gui.GuiMain;
import entDownloader.gui.MainFrame;
import entDownloader.gui.Components.filesViews.ListView;

/**
 * Vue liste
 */
public class BriefView extends ListView {

	private static final long serialVersionUID = 3597009385704780850L;
	private BriefViewListComponentModel model;
	private BriefViewListComponent list = new BriefViewListComponent();

	public BriefView() {
		try {
			init();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	protected JComponent getViewComponent() {
		return list;
	}

	private void init() throws Exception {
		model = new BriefViewListComponentModel();
		list.setModel(model);

		this.setLayout(new BorderLayout());
		scroll = new JScrollPane(list);
		scroll.getViewport().setBackground(CoreConfig.getBackgroundColor());
		list.setBackground(CoreConfig.getBackgroundColor());
		list.setForeground(CoreConfig.getForegroundColor());
		list.setFont(CoreConfig.getPanelsFont());
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		add(scroll, BorderLayout.CENTER);
		add(topPanel, BorderLayout.NORTH);

		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				if (SwingUtilities.isRightMouseButton(event)) {
					int select = list.locationToIndex(event.getPoint());
					if (select < 0)
						return;
					if (list.getCellBounds(select, select).contains(
							event.getPoint())) {
						list.setSelectedIndex(select);
					} else {
						list.clearSelection();
					}
					((MainFrame) GuiMain.getMainFrame()).getPopupMenu().show(
							list, event.getX(), event.getY());
				}
			}
		});
	}

	@Override
	public void browseDirectory(List<FS_Element> dirContent) {
		model.browseDirectory(dirContent);
	}

	@Override
	public FS_Element getSelectedFile() {
		int row = list.getSelectedIndex();
		if (row < 0)
			return null;

		return (FS_Element) model.getElementAt(row);
	}

	@Override
	public FS_Element[] getSelectedFiles() {
		int rows[] = list.getSelectedIndices();
		FS_Element[] files = new FS_Element[rows.length];
		for (int i = 0; i < rows.length; i++) {
			files[i] = (FS_Element) model.getElementAt(rows[i]);
		}
		if (rows.length == 0) {
			files = new FS_Element[1];
			files[0] = getSelectedFile();
		}
		return files;
	}

	@Override
	public void addListSelectionListener(ListSelectionListener listener) {
		list.getSelectionModel().addListSelectionListener(listener);
	}

	@Override
	public void removeListSelectionListener(ListSelectionListener listener) {
		list.getSelectionModel().removeListSelectionListener(listener);
	}

	@Override
	public ListSelectionModel getSelectionModel() {
		return list.getSelectionModel();
	}
}
