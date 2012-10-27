/*
 *  ElementsDeletedListener.java
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
 * Informe les observateurs qu'un ou plusieurs éléments
 * du dossier courant ont été supprimés.
 * 
 * @since 2.0.0
 */
public interface ElementsDeletedListener extends BroadcastListener {

	/**
	 * Appelée lorsqu'un ou plusieurs éléments du dossier courant
	 * ont été supprimés. Voir la documentation de
	 * {@link net.sf.entDownloader.core.events.ElementsDeletedEvent} 
	 * pour plus d'informations à propos de cet événement.
	 */
	public void onElementsDeleted(ElementsDeletedEvent e);
}
