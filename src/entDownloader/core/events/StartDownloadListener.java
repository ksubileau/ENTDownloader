/*
 *  StartDownloadListener.java
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
package entDownloader.core.events;

/**
 * Informe les observateurs que le téléchargement d'un fichier débute.
 */
public interface StartDownloadListener extends BroadcastListener {

	/**
	 * Appelée lorsque le téléchargement d'un fichier débute. Voir la
	 * documentation de {@link StartDownloadEvent} pour plus d'informations à
	 * propos de cet événement.
	 */
	public void onStartDownload(StartDownloadEvent e);
}
