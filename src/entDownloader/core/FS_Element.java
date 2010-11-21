/*
 *  FS_Element.java
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
package entDownloader.core;

import java.util.GregorianCalendar;

public abstract class FS_Element {

	private GregorianCalendar dateModif;
	private String name;
	//Element type constants
	public static final short DIRECTORY = 0;
	public static final short FILE = 1;

	/**
	 * Construit un nouvel élément de système de fichiers (dossier ou fichier)
	 * 
	 * @param name
	 *            Nom de l'élément
	 * @param dateModif
	 *            Date de modification de l'élément
	 */
	FS_Element(String name, GregorianCalendar dateModif) {
		this.name = name;
		setDateModif(dateModif);
	}

	/**
	 * @return La date de modification de l'élément
	 */
	public GregorianCalendar getDateModif() {
		return dateModif;
	}

	/**
	 * @param dateModif
	 *            Définit la date de modification de l'élément.
	 */
	public void setDateModif(GregorianCalendar dateModif) {
		this.dateModif = dateModif;
	}

	/**
	 * @return Le nom de l'élément
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            Le nouveau nom de l'élément
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		//return "\n" + getName() + "\t" + dateModif.get(Calendar.DATE) + "-" + ((dateModif.get(Calendar.MONTH)==0)?12:dateModif.get(Calendar.MONTH)) + "-" + dateModif.get(Calendar.YEAR) + " " + dateModif.get(Calendar.HOUR_OF_DAY) + ":" + dateModif.get(Calendar.MINUTE);
		return getName();
	}

	abstract short getType();

	public boolean isFile() {
		return getType() == FILE;
	}

	public boolean isDirectory() {
		return getType() == DIRECTORY;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dateModif == null) ? 0 : dateModif.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj instanceof String && obj.equals(name))
			return true;
		else if (getClass() != obj.getClass())
			return false;
		FS_Element other = (FS_Element) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (dateModif == null) {
			if (other.dateModif != null)
				return false;
		} else if (!dateModif.equals(other.dateModif))
			return false;
		return true;
	}
}
