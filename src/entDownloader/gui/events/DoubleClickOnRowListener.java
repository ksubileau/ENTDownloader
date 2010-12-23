/*
 *  DoubleClickOnRowListener.java
 *      
 *  Copyright 2010 K�vin Subileau. 
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
package entDownloader.gui.events;

import entDownloader.core.events.BroadcastListener;

/**
 * Informe les observateurs que l'utilisateur a double-cliqu�
 * sur une ligne de l'affichage D�tail.
 */
public interface DoubleClickOnRowListener extends BroadcastListener {

	/**
	 * Appel�e lorsque l'utilisateur a double-cliqu� sur une ligne de
	 * l'affichage D�tail. Voir la documentation de
	 * {@link DoubleClickOnRowEvent} pour plus d'informations � propos de cet
	 * �v�nement.
	 */
	public void onDoubleClickOnRow(DoubleClickOnRowEvent event);
}