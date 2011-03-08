/*
 *  DownloadedBytesListener.java
 *      
 *  Copyright 2010-2011 Kévin Subileau. 
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
 * Informe les observateurs que des octets ont été reçus lors d'un
 * téléchargement de fichier(s).
 */
public interface DownloadedBytesListener extends BroadcastListener {

	/**
	 * Appelée lorsque des octets ont été reçus lors d'un téléchargement de
	 * fichier(s). Voir la documentation de 
	 * {@link entDownloader.core.events.DownloadedBytesEvent DownloadedBytesEvent}
	 * pour plus d'informations à propos de cet événement.
	 */
	public void onDownloadedBytes(DownloadedBytesEvent e);

}
