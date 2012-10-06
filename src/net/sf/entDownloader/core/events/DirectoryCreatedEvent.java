/*
 *  DirectoryCreatedEvent.java
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
package net.sf.entDownloader.core.events;

/**
 * Un événement qui indique qu'un répertoire a été créé.
 */
public class DirectoryCreatedEvent extends Event {

	/**
	 * Retourne le type d'événement porté par cette instance. Ici, retourne
	 * {@link Event#CREATED_DIR_TYPE}
	 */
	@Override
	public int getType() {
		return Event.CREATED_DIR_TYPE;
	}

	/**
	 * Construit un nouvel évènement DirectoryCreatedEvent.
	 */
	public DirectoryCreatedEvent() {
		this(null);
	}

	/**
	 * Construit un nouvel évènement DirectoryCreatedEvent.
	 * 
	 * @param name Le nom du répertoire créé.
	 */
	public DirectoryCreatedEvent(String name) {
		setName(name);
	}

	private String name;

	/**
	 * Retourne le nom du répertoire créé.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Définit le nom du répertoire créé.
	 */
	public void setName(String name) {
		this.name = name;
	}
}
