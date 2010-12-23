/*
 *  DoubleClickOnRowEvent.java
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

import entDownloader.core.FS_Element;

/**
 * Un �v�nement qui indique que l'utilisateur a double-cliqu�
 * sur une ligne de l'affichage D�tail.
 */
public class DoubleClickOnRowEvent extends GuiEvent {

	/**
	 * Retourne le type d'�v�nement port� par cette instance. Ici, retourne
	 * {@link GuiEvent#DOUBLE_CLICK_ON_ROW_TYPE}
	 */
	@Override
	public int getType() {
		return GuiEvent.DOUBLE_CLICK_ON_ROW_TYPE;
	}

	/**
	 * Construit un nouvel �v�nement DoubleClickOnRowEvent.
	 */
	public DoubleClickOnRowEvent() {
		this(null);
	}

	/**
	 * Construit un nouvel �v�nement DoubleClickOnRowEvent.
	 * 
	 * @param target Le {@link FS_Element} sur lequel on a double-cliqu�.
	 */
	public DoubleClickOnRowEvent(FS_Element target) {
		setTarget(target);
	}

	private FS_Element target;

	/**
	 * Retourne le {@link FS_Element} sur lequel on a double-cliqu�.
	 */
	public FS_Element getTarget() {
		return target;
	}

	/**
	 * D�finit le {@link FS_Element} sur lequel on a double-cliqu�.
	 */
	public void setTarget(FS_Element target) {
		this.target = target;
	}
}
