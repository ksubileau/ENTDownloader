/*
 *  EndUploadEvent.java
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
 * Un événement qui indique que l'envoi d'un fichier
 * s'est terminé normalement.
 * 
 * @since 2.0.0
 */
public class EndUploadEvent extends Event {

	private File file;

	/**
	 * Construit un nouvel évènement EndUploadEvent.
	 */
	public EndUploadEvent() {
		this(null);
	}

	/**
	 * Construit un nouvel évènement EndUploadEvent.
	 * 
	 * @param file Le fichier local dont l'envoi s'est terminé.
	 */
	public EndUploadEvent(File file) {
		setFile(file);
	}

	/**
	 * Définit le fichier local pour lequel l'envoi s'est terminé.
	 * 
	 * @param file
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * Retourne le fichier local dont l'envoi s'est terminé.
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Retourne le type d'événement porté par cette instance. Ici, retourne
	 * {@link Event#END_UPLOAD_TYPE}
	 */
	@Override
	public int getType() {
		return END_UPLOAD_TYPE;
	}

}
