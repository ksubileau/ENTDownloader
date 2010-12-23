/*
 *  GuiEvent.java
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
package entDownloader.gui.events;

import entDownloader.core.events.Event;

public abstract class GuiEvent extends Event {

	/**
	 * Indique que l'utilisateur a double-cliqué sur une ligne de l'affichage Détail.
	 */
	public static final int DOUBLE_CLICK_ON_ROW_TYPE = 101;

	@Override
	public abstract int getType();

}
