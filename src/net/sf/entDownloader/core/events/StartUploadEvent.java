/*
 *  StartUploadEvent.java
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

import java.io.File;

/**
 * Un événement qui indique que l'envoi d'un fichier débute.
 * 
 * @since 2.0.0
 */
public class StartUploadEvent extends Event {

	private File file;

	/**
	 * Construit un nouvel évènement StartUploadEvent.
	 */
	public StartUploadEvent() {
		this(null);
	}

	/**
	 * Construit un nouvel évènement StartUploadEvent.
	 * 
	 * @param file Le fichier local dont l'envoi débute.
	 */
	public StartUploadEvent(File file) {
		setFile(file);
	}

	/**
	 * Définit le fichier pour lequel l'envoi débute.
	 * 
	 * @param file
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * Retourne le fichier dont le téléchargement débute.
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Retourne le type d'événement porté par cette instance. Ici, retourne
	 * {@link Event#START_UPLOAD_TYPE}
	 */
	@Override
	public int getType() {
		return START_UPLOAD_TYPE;
	}

}
