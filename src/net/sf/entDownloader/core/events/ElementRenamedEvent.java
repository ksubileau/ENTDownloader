/*
 *  ElementRenamedEvent.java
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
 * Un événement qui indique qu'un élément du dossier courant
 * a été renommé.
 * 
 * @since 2.0.0
 */
public class ElementRenamedEvent extends Event {

	private String oldname;
	private String newname;

	/**
	 * Construit un nouvel évènement ElementRenamedEvent.
	 */
	public ElementRenamedEvent() {
		this(null, null);
	}

	/**
	 * Construit un nouvel évènement ElementRenamedEvent.
	 * 
	 * @param oldname L'ancien nom de l'élément renommé.
	 * @param newname Le nouveau nom de l'élément renommé.
	 */
	public ElementRenamedEvent(String oldname, String newname) {
		setOldName(oldname);
		setNewName(newname);
	}

	/**
	 * Définit l'ancien nom du fichier renommé.
	 * 
	 * @param oldname
	 */
	public void setOldName(String oldname) {
		this.oldname = oldname;
	}

	/**
	 * Définit le nouveau nom du fichier renommé.
	 * 
	 * @param newname
	 */
	public void setNewName(String newname) {
		this.newname = newname;
	}

	/**
	 * Retourne l'ancien nom du fichier renommé.
	 */
	public String getOldName() {
		return oldname;
	}

	/**
	 * Retourne le nouveau nom du fichier renommé.
	 */
	public String getNewName() {
		return newname;
	}

	/**
	 * Retourne le type d'événement porté par cette instance. Ici, retourne
	 * {@link Event#ELEMENT_RENAMED_TYPE}
	 */
	@Override
	public int getType() {
		return ELEMENT_RENAMED_TYPE;
	}

}
