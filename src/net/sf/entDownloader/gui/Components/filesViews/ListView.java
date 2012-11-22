/*
 *  ListView.java
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
package net.sf.entDownloader.gui.Components.filesViews;

import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.KeyListener;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;

import net.sf.entDownloader.core.FS_Element;

public abstract class ListView extends JPanel {

	private static final long serialVersionUID = -4385424168501274263L;
	protected JScrollPane scroll = null;
	protected JPanel topPanel = null;

	public ListView() {
		try {
			initView();
			this.setOpaque(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void initView() throws Exception {
		topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
			}
		};
		topPanel.setOpaque(false);

	}

	@Override
	public void requestFocus() {
		getViewComponent().requestFocusInWindow();
	}

	/**
	 * This method returns the current selected file from the view. If no file
	 * is selected it just returns null.
	 * 
	 * @return the current selected file.
	 */
	public abstract FS_Element getSelectedFile();

	/**
	 * This method returns the current selected files from the view. If no files
	 * are selected it just returns null.
	 * 
	 * @return the current selected files.
	 */
	public abstract FS_Element[] getSelectedFiles();

	/**
	 * This method returns the number of selected files from the view.
	 * 
	 * @return the number of selected files.
	 * @since 1.0.2
	 */
	public int getSelectedFilesCount() {
		return getSelectedFiles().length;
	}

	/**
	 * Update the view from the content specified
	 * 
	 * @param dirContent
	 */
	public abstract void browseDirectory(java.util.List<FS_Element> dirContent);

	/**
	 * Returns the view component (JTable or a JList)
	 * 
	 * @return the view component
	 */
	protected abstract JComponent getViewComponent();

	/**
	 * Adds a listener to the list that's notified each time a change to the
	 * selection occurs.
	 * 
	 * @param listener
	 *            the <code>ListSelectionListener</code> to add
	 */
	public abstract void addListSelectionListener(ListSelectionListener listener);

	/**
	 * Removes a listener from the list that's notified each time a change to
	 * the selection occurs.
	 * 
	 * @param listener
	 *            the <code>ListSelectionListener</code> to remove
	 */
	public abstract void removeListSelectionListener(
			ListSelectionListener listener);

	/*
	 * (non-Javadoc)
	 * @see javax.swing.JList#getSelectionModel()
	 */
	public abstract ListSelectionModel getSelectionModel();

	/**
	 * Sets the zoom level of the view.
	 * @param zoom Zoom level
	 */
	public abstract void setZoomLevel(int zoom);

	/**
	 * Returns the current zoom level of the view.
	 * @return the current zoom level of the view.
	 */
	public abstract int getZoomLevel();

	/**
	 * Adds the specified key listener to receive key events from the view
	 * component (JTable or a JList). If keyListener is null, no exception
	 * is thrown and no action is performed.
	 * 
	 * @param keyListener the key listener to add
	 * @since 1.0.2
	 */
	public void addKeyListenerOnView(KeyListener keyListener) {
		getViewComponent().addKeyListener(keyListener);
	}

	/**
	 * Returns the InputMap that is used when the view component
	 * has focus. This is convenience method
	 * for {@link #getViewInputMap(int) getInputMap(WHEN_FOCUSED)}.
	 * 
	 * @return the InputMap used when the view component
	 *         (JTable or a JList) has focus.
	 * @since 1.0.2
	 */
	public InputMap getViewInputMap() {
		return getViewComponent().getInputMap();
	}

	/**
	 * Returns the InputMap that is used by the view component
	 * (JTable or a JList) during condition.
	 * 
	 * @param condition one of WHEN_IN_FOCUSED_WINDOW,
	 *            WHEN_FOCUSED, WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
	 * @return the InputMap for the specified condition
	 * @since 1.0.2
	 */
	public InputMap getViewInputMap(int condition) {
		return getViewComponent().getInputMap(condition);
	}

	/**
	 * Returns the ActionMap used to determine what Action to fire for
	 * particular KeyStroke binding. The returned ActionMap, unless
	 * otherwise set, will have the ActionMap from the UI set as the parent.
	 * 
	 * @return the ActionMap containing the key/action bindings
	 * @since 1.0.2
	 */
	public ActionMap getViewActionMap() {
		return getViewComponent().getActionMap();
	}
}
