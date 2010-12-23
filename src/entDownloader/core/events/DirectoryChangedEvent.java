/*
 *  DirectoryChangedEvent.java
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
package entDownloader.core.events;

/**
 * Un �v�nement qui indique que le r�pertoire courant a chang�.
 */
public class DirectoryChangedEvent extends Event {

	/**
	 * Retourne le type d'�v�nement port� par cette instance. Ici, retourne
	 * {@link Event#CHANGED_DIR_TYPE}
	 */
	@Override
	public int getType() {
		return Event.CHANGED_DIR_TYPE;
	}

	/**
	 * Construit un nouvel �v�nement DirectoryChangedEvent.
	 */
	public DirectoryChangedEvent() {
		this(null);
	}

	/**
	 * Construit un nouvel �v�nement DirectoryChangedEvent.
	 * 
	 * @param targetPath Le chemin vers lequel on a migr�.
	 */
	public DirectoryChangedEvent(String targetPath) {
		setDirectory(targetPath);
	}

	private String targetPath;

	/**
	 * Retourne le chemin vers lequel on a migr�.
	 */
	public String getDirectory() {
		return targetPath;
	}

	/**
	 * D�finit le chemin vers lequel on a migr�.
	 */
	public void setDirectory(String targetPath) {
		this.targetPath = targetPath;
	}
}
