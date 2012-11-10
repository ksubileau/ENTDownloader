/*
 *  AbortTransferRequestEvent.java
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
package net.sf.entDownloader.gui.events;

/**
 * Un événement qui indique que l'utilisateur a demandé
 * l'annulation du ou des téléchargement(s) ou envoi en cours.
 */
public class AbortTransferRequestEvent extends GuiEvent {

	/**
	 * Retourne le type d'événement porté par cette instance. Ici, retourne
	 * {@link GuiEvent#ABORT_TRANSFER_REQUEST_TYPE}
	 */
	@Override
	public int getType() {
		return GuiEvent.ABORT_TRANSFER_REQUEST_TYPE;
	}

	/**
	 * Construit un nouvel évènement AbortTransferRequestEvent.
	 */
	public AbortTransferRequestEvent() {
	}
}
