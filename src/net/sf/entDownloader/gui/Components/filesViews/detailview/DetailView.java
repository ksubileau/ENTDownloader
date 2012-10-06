/*
 *  DetailView.java
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

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionListener;

import net.sf.entDownloader.core.CoreConfig;
import net.sf.entDownloader.core.FS_Element;
import net.sf.entDownloader.gui.GuiMain;
import net.sf.entDownloader.gui.MainFrame;
import net.sf.entDownloader.gui.Components.filesViews.ListView;

public class DetailView extends ListView {

	private static final long serialVersionUID = -6527502636190584594L;
	private DetailsTableModel model;
	private DetailsTable table = new DetailsTable();

	/**
	 * Constructor.
	 */
	public DetailView() {
		super();
		try {
			init();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	protected JComponent getViewComponent() {
		if (table == null) {
			table = new DetailsTable();
		}
		return table;
	}

	private void init() throws Exception {
		model = new DetailsTableModel();
		table.setModel(model);
		this.setLayout(new BorderLayout());
		scroll = new JScrollPane(table);
		scroll.getViewport().setBackground(CoreConfig.getBackgroundColor());
		table.setBackground(CoreConfig.getBackgroundColor());
		table.setForeground(CoreConfig.getForegroundColor());
		table.setFont(CoreConfig.getPanelsFont());

		add(scroll, BorderLayout.CENTER);
		add(topPanel, BorderLayout.NORTH);

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				if (SwingUtilities.isRightMouseButton(event)) {
					int selectedRow = table.rowAtPoint(event.getPoint());
					if (selectedRow < 0) {
						table.clearSelection();
					} else if (!table.isRowSelected(selectedRow)) {
						table.getSelectionModel().setSelectionInterval(
								selectedRow, selectedRow);
					}

					((MainFrame) GuiMain.getMainFrame()).getPopupMenu().show(
							table, event.getX(), event.getY());
				}
			}
		});
		scroll.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				table.clearSelection();
				if (SwingUtilities.isRightMouseButton(event)) {
					((MainFrame) GuiMain.getMainFrame()).getPopupMenu().show(
							scroll, event.getX(), event.getY());
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
		int row = table.getSelectedRow();
		if (row < 0)
			return null;
		return model.getFileAt(row);
	}

	@Override
	public FS_Element[] getSelectedFiles() {
		int rows[] = table.getSelectedRows();
		FS_Element[] files = new FS_Element[rows.length];
		for (int i = 0; i < rows.length; i++) {
			files[i] = model.getFileAt(rows[i]);
		}
		if (rows.length == 0) {
			files = new FS_Element[1];
			files[0] = getSelectedFile();
		}
		return files;
	}

	@Override
	public int getSelectedFilesCount() {
		return table.getSelectedRowCount();
	}

	@Override
	public void addListSelectionListener(ListSelectionListener listener) {
		table.getSelectionModel().addListSelectionListener(listener);
	}

	@Override
	public void removeListSelectionListener(ListSelectionListener listener) {
		table.getSelectionModel().removeListSelectionListener(listener);
	}

	@Override
	public ListSelectionModel getSelectionModel() {
		return table.getSelectionModel();
	}
}
