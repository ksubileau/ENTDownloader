/*
 *  DownloadedBytesEvent.java
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

/**
 * Un événement qui indique que des octets ont été reçu durant
 * le téléchargement d'un fichier.
 */
public class DownloadedBytesEvent extends Event {
	private long bytesDownloaded = 0;

	/**
	 * Retourne le type d'événement porté par cette instance. Ici, retourne
	 * {@link Event#DOWNLOADED_BYTES_TYPE}
	 */
	@Override
	public int getType() {
		return DOWNLOADED_BYTES_TYPE;
	}

	/**
	 * Construit un nouvel évènement DownloadedBytesEvent
	 */
	public DownloadedBytesEvent() {
		this(-1);
	}

	/**
	 * Construit un nouvel évènement DownloadedBytesEvent
	 * 
	 * @param bytesDownloaded Nombre d'octets du fichier téléchargés depuis le
	 *            dernier DownloadedBytesEvent.
	 */
	public DownloadedBytesEvent(long bytesDownloaded) {
		setBytesDownloaded(bytesDownloaded);
	}

	/**
	 * Retourne le nombre d'octets du fichier téléchargés depuis le dernier
	 * DownloadedBytesEvent.
	 */
	public long getBytesDownloaded() {
		return bytesDownloaded;
	}

	/**
	 * Définit le nombre d'octets du fichier téléchargés depuis le dernier
	 * DownloadedBytesEvent.
	 * 
	 * @param bytesDownloaded Nombre d'octets du fichier téléchargés depuis le
	 *            dernier DownloadedBytesEvent.
	 */
	public void setBytesDownloaded(long bytesDownloaded) {
		this.bytesDownloaded = bytesDownloaded;
	}

}
