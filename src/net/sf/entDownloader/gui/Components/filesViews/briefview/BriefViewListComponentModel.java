/*
 *  BriefViewListComponentModel.java
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
package net.sf.entDownloader.gui.Components.filesViews.briefview;

import javax.swing.DefaultListModel;
import javax.swing.SwingUtilities;

import net.sf.entDownloader.core.FS_Element;

public class BriefViewListComponentModel extends DefaultListModel {

	private static final long serialVersionUID = 9074875184106285114L;

	/**
	 * The Constructor
	 */
	public BriefViewListComponentModel() {
		super();
	}

	/**
	 * Met à jour le contenu de la vue Liste.
	 * Cette méthode est thread-safe, et sera toujours exécutée
	 * dans l'Event Dispatcher Thread.
	 * 
	 * @param dirContent Le contenu du dossier courant qui sera affiché
	 */
	public void browseDirectory(final java.util.List<FS_Element> dirContent) {
		Runnable code = new Runnable() {
			@Override
			public void run() {
				removeAllElements(); //removing the old rows
				for (FS_Element item : dirContent) {
					addElement(item);
				}
			}
		};

		if (SwingUtilities.isEventDispatchThread()) {
			code.run();
		} else {
			SwingUtilities.invokeLater(code);
		}
	}
}
