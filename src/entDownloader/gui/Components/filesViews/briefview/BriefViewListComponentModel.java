/*
 *  BriefViewListComponentModel.java
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

import javax.swing.DefaultListModel;

import entDownloader.core.FS_Element;

public class BriefViewListComponentModel extends DefaultListModel {

	private static final long serialVersionUID = 9074875184106285114L;

	/**
	 * The Constructor
	 */
	public BriefViewListComponentModel() {
		super();
	}

	public void browseDirectory(java.util.List<FS_Element> dirContent) {
		removeAllElements(); //removing the old rows
		//FIXME Vue liste : Exception in thread "AWT-EventQueue-0" java.lang.NullPointerException dans certains dossier, cause d'un affichage incomplet du dossier
		for (FS_Element item : dirContent) {
			addElement(item);
		}
	}
}
