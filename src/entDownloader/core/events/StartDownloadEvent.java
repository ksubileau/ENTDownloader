/*
 *  StartDownloadEvent.java
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

import entDownloader.core.FS_File;

public class StartDownloadEvent extends Event {

	private FS_File file;

	/**
	 * Construit un nouvel �v�nement StartDownloadEvent.
	 */
	public StartDownloadEvent() {
		this(null);
	}

	/**
	 * Construit un nouvel �v�nement StartDownloadEvent.
	 * @param file Le fichier dont le t�l�chargement d�bute.
	 */
	public StartDownloadEvent(FS_File file) {
		setFile(file);
	}

	/**
	 * D�finit le fichier pour lequel le t�l�chargement d�bute.
	 * @param file
	 */
	public void setFile(FS_File file) {
		this.file = file;
	}

	/**
	 * Retourne le fichier dont le t�l�chargement d�bute.
	 */
	public FS_File getFile() {
		return file;
	}

	/**
	 * Retourne le type d'�v�nement port� par cette instance. Ici, retourne
	 * {@link Event#START_DOWNLOAD_TYPE}
	 */
	@Override
	public int getType() {
		return START_DOWNLOAD_TYPE;
	}

}
