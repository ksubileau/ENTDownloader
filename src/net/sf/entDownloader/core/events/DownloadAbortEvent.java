/*
 *  DownloadAbortEvent.java
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

import net.sf.entDownloader.core.FS_File;

/**
 * Un événement qui indique que le téléchargement d'un
 * fichier a été abandonné.
 */
public class DownloadAbortEvent extends Event {

	private FS_File file;

	/**
	 * Construit un nouvel évènement DownloadAbortEvent.
	 */
	public DownloadAbortEvent() {
		this(null);
	}

	/**
	 * Construit un nouvel évènement DownloadAbortEvent.
	 * 
	 * @param file Le fichier dont le téléchargement a été annulé.
	 */
	public DownloadAbortEvent(FS_File file) {
		setFile(file);
	}

	/**
	 * Définit le fichier pour lequel le téléchargement a été abandonné.
	 * 
	 * @param file
	 */
	public void setFile(FS_File file) {
		this.file = file;
	}

	/**
	 * Retourne le fichier dont le téléchargement a été annulé.
	 */
	public FS_File getFile() {
		return file;
	}

	/**
	 * Retourne le type d'événement porté par cette instance. Ici, retourne
	 * {@link Event#DOWNLOAD_ABORT_TYPE}
	 */
	@Override
	public int getType() {
		return DOWNLOAD_ABORT_TYPE;
	}

}
