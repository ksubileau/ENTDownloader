/*
 *  StartDownloadListener.java
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

/**
 * Informe les observateurs que le t�l�chargement d'un fichier d�bute.
 */
public interface StartDownloadListener extends BroadcastListener {

	/**
	 * Appel�e lorsque le t�l�chargement d'un fichier d�bute. Voir la
	 * documentation de {@link StartDownloadEvent} pour plus d'informations �
	 * propos de cet �v�nement.
	 */
	public void onStartDownload(StartDownloadEvent e);
}
