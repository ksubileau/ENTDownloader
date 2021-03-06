/*
 *  ElementsDeletedEvent.java
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
package net.sf.entDownloader.core.events;

/**
 * Un événement qui indique qu'un ou plusieurs éléments
 * du dossier courant ont été supprimés.
 * 
 * @since 2.0.0
 */
public class ElementsDeletedEvent extends Event {

	private String[] targets;

	/**
	 * Construit un nouvel évènement ElementsDeletedEvent.
	 */
	public ElementsDeletedEvent() {
		this(null);
	}

	/**
	 * Construit un nouvel évènement ElementsDeletedEvent.
	 * 
	 * @param targets Liste des éléments supprimés.
	 */
	public ElementsDeletedEvent(String[] targets) {
		setTargets(targets);
	}

	/**
	 * Définit la liste des éléments supprimés.
	 * 
	 * @param targets
	 */
	public void setTargets(String[] targets) {
		this.targets = targets;
	}

	/**
	 * Retourne la liste des éléments supprimés.
	 */
	public String[] getTargets() {
		return targets;
	}

	/**
	 * Retourne le type d'événement porté par cette instance. Ici, retourne
	 * {@link Event#ELEMENTS_DELETED_TYPE}
	 */
	@Override
	public int getType() {
		return ELEMENTS_DELETED_TYPE;
	}

}
