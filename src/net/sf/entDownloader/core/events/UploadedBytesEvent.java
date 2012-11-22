/*
 *  UploadedBytesEvent.java
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
 * Un événement qui indique que des octets ont été envoyés durant
 * l'envoi d'un fichier.
 * 
 * @since 2.0.0
 */
public class UploadedBytesEvent extends Event {
	private long bytesUploaded = 0;
	private long totalUploaded = 0;

	/**
	 * Retourne le type d'événement porté par cette instance. Ici, retourne
	 * {@link Event#UPLOADED_BYTES_TYPE}
	 */
	@Override
	public int getType() {
		return UPLOADED_BYTES_TYPE;
	}

	/**
	 * Construit un nouvel évènement UploadedBytesEvent
	 */
	public UploadedBytesEvent() {
		this(-1,-1);
	}

	/**
	 * Construit un nouvel évènement UploadedBytesEvent
	 * 
	 * @param bytesUploaded Nombre d'octets du fichier envoyés depuis le
	 *            dernier UploadedBytesEvent.
	 */
	public UploadedBytesEvent(long bytesUploaded) {
		this(bytesUploaded,-1);
	}

	/**
	 * Construit un nouvel évènement UploadedBytesEvent
	 * 
	 * @param bytesUploaded Nombre d'octets du fichier envoyés depuis le
	 *            dernier UploadedBytesEvent.
	 * @param totalUploaded Nombre d'octets du fichier envoyés au total.
	 */
	public UploadedBytesEvent(long bytesUploaded, long totalUploaded) {
		setBytesUploaded(bytesUploaded);
		setTotalUploaded(totalUploaded);
	}

	/**
	 * Retourne le nombre d'octets du fichier envoyés depuis le dernier
	 * UploadedBytesEvent.
	 */
	public long getBytesUploaded() {
		return bytesUploaded;
	}

	/**
	 * Définit le nombre d'octets du fichier envoyés depuis le dernier
	 * UploadedBytesEvent.
	 * 
	 * @param bytesUploaded Nombre d'octets du fichier envoyés depuis le
	 *            dernier UploadedBytesEvent.
	 */
	public void setBytesUploaded(long bytesUploaded) {
		this.bytesUploaded = bytesUploaded;
	}

	/**
	 * Retourne le nombre d'octets du fichier envoyés au total.
	 */
	public long getTotalUploaded() {
		return totalUploaded;
	}

	/**
	 * Définit le nombre d'octets du fichier envoyés au total.
	 * 
	 * @param totalUploaded Nombre d'octets du fichier envoyés au total.
	 */
	public void setTotalUploaded(long totalUploaded) {
		this.totalUploaded = totalUploaded;
	}

}
