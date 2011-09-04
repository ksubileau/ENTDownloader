/*
 *  DirectoryChangedEvent.java
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
 * Un événement qui indique que le répertoire courant a changé.
 */
public class DirectoryChangedEvent extends Event {

	/**
	 * Retourne le type d'événement porté par cette instance. Ici, retourne
	 * {@link Event#CHANGED_DIR_TYPE}
	 */
	@Override
	public int getType() {
		return Event.CHANGED_DIR_TYPE;
	}

	/**
	 * Construit un nouvel évènement DirectoryChangedEvent.
	 */
	public DirectoryChangedEvent() {
		this(null);
	}

	/**
	 * Construit un nouvel évènement DirectoryChangedEvent.
	 * 
	 * @param targetPath Le chemin vers lequel on a migré.
	 */
	public DirectoryChangedEvent(String targetPath) {
		setDirectory(targetPath);
	}

	private String targetPath;

	/**
	 * Retourne le chemin vers lequel on a migré.
	 */
	public String getDirectory() {
		return targetPath;
	}

	/**
	 * Définit le chemin vers lequel on a migré.
	 */
	public void setDirectory(String targetPath) {
		this.targetPath = targetPath;
	}
}
