/*
 *  DoubleClickOnRowListener.java
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
package net.sf.entDownloader.gui.events;

import net.sf.entDownloader.core.events.BroadcastListener;

/**
 * Informe les observateurs que l'utilisateur a double-cliqué
 * sur une ligne de l'affichage Détail.
 */
public interface DoubleClickOnRowListener extends BroadcastListener {

	/**
	 * Appelée lorsque l'utilisateur a double-cliqué sur une ligne de
	 * l'affichage Détail. Voir la documentation de
	 * {@link DoubleClickOnRowEvent} pour plus d'informations à propos de cet
	 * événement.
	 */
	public void onDoubleClickOnRow(DoubleClickOnRowEvent event);
}