/*
 *  FileAlreadyExistsEvent.java
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
 * fichier débute.
 */
public class FileAlreadyExistsEvent extends Event {

	private FS_File file;
	public boolean abortDownload = false;

	/**
	 * Construit un nouvel évènement FileAlreadyExistsEvent.
	 */
	public FileAlreadyExistsEvent() {
		this(null);
	}

	/**
	 * Construit un nouvel évènement FileAlreadyExistsEvent.
	 * 
	 * @param file Le fichier dont le téléchargement débute.
	 */
	public FileAlreadyExistsEvent(FS_File file) {
		setFile(file);
	}

	/**
	 * Définit le fichier pour lequel le téléchargement débute.
	 * 
	 * @param file
	 */
	public void setFile(FS_File file) {
		this.file = file;
	}

	/**
	 * Retourne le fichier dont le téléchargement débute.
	 */
	public FS_File getFile() {
		return file;
	}

	/**
	 * Retourne le type d'événement porté par cette instance. Ici, retourne
	 * {@link Event#FILE_ALREADY_EXISTS_TYPE}
	 */
	@Override
	public int getType() {
		return FILE_ALREADY_EXISTS_TYPE;
	}

}
