/*
 *  FS_Element.java
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
package entDownloader.core;

import java.util.GregorianCalendar;

/**
 * Repr�sente un �l�ment abstrait (dossier ou fichier) stock� sur l'ENT.
 * 
 */
public abstract class FS_Element {

	private GregorianCalendar dateModif;
	private String name;
	//Element type constants
	public static final short DIRECTORY = 0;
	public static final short FILE = 1;

	/**
	 * Construit un nouvel �l�ment de syst�me de fichiers (dossier ou fichier).
	 * 
	 * @param name Nom de l'�l�ment.
	 * @param dateModif Date de modification de l'�l�ment.
	 */
	FS_Element(String name, GregorianCalendar dateModif) {
		this.name = name;
		setDateModif(dateModif);
	}

	/**
	 * Retourne la date de modification de l'�l�ment.
	 */
	public GregorianCalendar getDateModif() {
		return dateModif;
	}

	/**
	 * D�finit la date de modification de l'�l�ment.
	 * 
	 * @param dateModif La date de modification de l'�l�ment.
	 */
	public void setDateModif(GregorianCalendar dateModif) {
		this.dateModif = dateModif;
	}

	/**
	 * Retourne le nom de l'�l�ment.
	 */
	public String getName() {
		return name;
	}

	/**
	 * D�finit le nom de l'�l�ment.
	 * 
	 * @param name Le nouveau nom de l'�l�ment.
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

	/**
	 * D�termine si l'�l�ment repr�sent� par cette instance est un fichier.
	 * 
	 * @return True si l'�l�ment repr�sent� par cette instance est un fichier,
	 *         false sinon.
	 */
	public boolean isFile() {
		return getType() == FILE;
	}

	/**
	 * D�termine si l'�l�ment repr�sent� par cette instance est un dossier.
	 * 
	 * @return True si l'�l�ment repr�sent� par cette instance est un dossier,
	 *         false sinon.
	 */
	public boolean isDirectory() {
		return getType() == DIRECTORY;
	}

	/**
	 * @see Object#hashCode()
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
	 * @see Object#equals(java.lang.Object)
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
