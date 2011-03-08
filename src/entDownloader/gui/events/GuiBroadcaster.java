/*
 *  GuiBroadcaster.java
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
package entDownloader.gui.events;

import java.util.Iterator;

import entDownloader.core.events.BroadcastListener;
import entDownloader.core.events.Broadcaster;

public class GuiBroadcaster {

	/** Ajoute un observateur sur l'événement DoubleClickOnRow */
	public static void addDoubleClickOnRowListener(
			DoubleClickOnRowListener listener) {
		Broadcaster.addListener(DoubleClickOnRowListener.class, listener);
	}

	/** Supprime un observateur sur l'événement DoubleClickOnRow */
	public static void removeDoubleClickOnRowListener(
			DoubleClickOnRowListener listener) {
		Broadcaster.removeListener(DoubleClickOnRowListener.class, listener);
	}

	/** Avertit tous les observateurs de l'événement DoubleClickOnRow */
	public static void fireDoubleClickOnRow(DoubleClickOnRowEvent event) {
		Iterator<BroadcastListener> it = Broadcaster
				.getListenerIterator(DoubleClickOnRowListener.class);
		DoubleClickOnRowListener listener;
		while (it.hasNext()) {
			listener = (DoubleClickOnRowListener) it.next();
			listener.onDoubleClickOnRow(event);
		}
	}
}
