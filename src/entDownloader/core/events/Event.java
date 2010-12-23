/*
 *  Event.java
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

/** Classe de base pour tous les �v�nements. */
public abstract class Event {

	protected Object source;
	protected Object userObj;

	/**
	 * Type d'�v�nement inconnu.
	 */
	public static final int UNKNOWN_TYPE = 0;
	/**
	 * Indique un changement de r�pertoire en cours.
	 */
	public static final int CHANGING_DIR_TYPE = 1;
	/**
	 * Indique un changement de r�pertoire termin�.
	 */
	public static final int CHANGED_DIR_TYPE = 2;
	/**
	 * Indique que des octets ont �t� re�us durant un t�l�chargement.
	 */
	public static final int DOWNLOADED_BYTES_TYPE = 3;
	/**
	 * Indique le d�but d'un t�l�chargement.
	 */
	public static final int START_DOWNLOAD_TYPE = 4;
	/**
	 * Indique la fin d'un t�l�chargement.
	 */
	public static final int END_DOWNLOAD_TYPE = 5;

	protected Event() {
	}

	/** Construit un �v�nement avec la source sp�cifi�. */
	public Event(Object source) {
		this.source = source;
	}

	/** Retourne la source de l'�v�nement. */
	public Object getSource() {
		return source;
	}

	/** D�finit la source de l'�v�nement. */
	public void setSource(Object newSource) {
		source = newSource;
	}

	/** Sets the user object. */
	public void setUserObject(Object obj) {
		userObj = obj;
	}

	/** Returns the user object. */
	public Object getUserObject() {
		return userObj;
	}

	/**
	 * @return Retourne le type de l'�v�nement.
	 */
	public abstract int getType();

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Event))
			return false;

		return getType() == ((Event) obj).getType();
	}
}
