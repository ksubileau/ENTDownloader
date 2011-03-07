/*
 *  Broadcaster.java
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

import java.util.HashMap;
import java.util.Iterator;

/**
 * Enregistre et supprime les observateurs, et les averti lorsqu'un événement se
 * produit.
 */
public class Broadcaster {

	private static HashMap<Object, HashMap<BroadcastListener, BroadcastListener>> hash = new HashMap<Object, HashMap<BroadcastListener, BroadcastListener>>();

	private static HashMap<BroadcastListener, BroadcastListener> getHash(
			Object obj) {
		if (hash.containsKey(obj))
			return hash.get(obj);
		else {
			HashMap<BroadcastListener, BroadcastListener> wh = new HashMap<BroadcastListener, BroadcastListener>();
			hash.put(obj, wh);
			return wh;
		}
	}

	public static synchronized void addListener(Object listenerClass,
			BroadcastListener listener) {
		HashMap<BroadcastListener, BroadcastListener> wh = getHash(listenerClass);
		wh.put(listener, listener);
	}

	public static synchronized void removeListener(Object listenerClass,
			BroadcastListener listener) {
		if ((listenerClass != null) && (listener != null)) {
			HashMap<BroadcastListener, BroadcastListener> wh = getHash(listenerClass);
			wh.remove(listener);
			if (wh.size() == 0) {
				hash.remove(listenerClass);
			}
		}
	}

	public static Iterator<BroadcastListener> getListenerIterator(
			Object listenerClass) {
		HashMap<BroadcastListener, BroadcastListener> wh = getHash(listenerClass);
		return wh.values().iterator();
	}

	/** Ajoute un observateur sur l'événement DirectoryChanging */
	public static void addDirectoryChangingListener(
			DirectoryChangingListener listener) {
		addListener(DirectoryChangingListener.class, listener);
	}

	/** Supprime un observateur sur l'événement DirectoryChanging */
	public static void removeDirectoryChangingListener(
			DirectoryChangingListener listener) {
		removeListener(DirectoryChangingListener.class, listener);
	}

	/** Avertit tous les observateurs de l'événement DirectoryChanging */
	public static void fireDirectoryChanging(DirectoryChangingEvent event) {
		Iterator<BroadcastListener> it = getListenerIterator(DirectoryChangingListener.class);
		DirectoryChangingListener listener;
		while (it.hasNext()) {
			listener = (DirectoryChangingListener) it.next();
			listener.onDirectoryChanging(event);
		}
	}

	/** Ajoute un observateur sur l'événement DirectoryChanging */
	public static void addDirectoryChangedListener(
			DirectoryChangedListener listener) {
		addListener(DirectoryChangedListener.class, listener);
	}

	/** Supprime un observateur sur l'événement DirectoryChanging */
	public static void removeDirectoryChangedListener(
			DirectoryChangedListener listener) {
		removeListener(DirectoryChangedListener.class, listener);
	}

	/** Avertit tous les observateurs de l'événement DirectoryChanging */
	public static void fireDirectoryChanged(DirectoryChangedEvent event) {
		Iterator<BroadcastListener> it = getListenerIterator(DirectoryChangedListener.class);
		DirectoryChangedListener listener;
		while (it.hasNext()) {
			listener = (DirectoryChangedListener) it.next();
			listener.onDirectoryChanged(event);
		}
	}

	/** Ajoute un observateur sur l'événement DownloadedBytes */
	public static void addDownloadedBytesListener(
			DownloadedBytesListener listener) {
		addListener(DownloadedBytesListener.class, listener);
	}

	/** Supprime un observateur sur l'événement DownloadedBytes */
	public static void removeDownloadedBytesListener(
			DownloadedBytesListener listener) {
		removeListener(DownloadedBytesListener.class, listener);
	}

	/** Avertit tous les observateurs de l'événement DownloadedBytes */
	public static void fireDownloadedBytes(DownloadedBytesEvent event) {
		Iterator<BroadcastListener> it = getListenerIterator(DownloadedBytesListener.class);
		DownloadedBytesListener listener;
		while (it.hasNext()) {
			listener = (DownloadedBytesListener) it.next();
			listener.onDownloadedBytes(event);
		}
	}

	/** Ajoute un observateur sur l'événement StartDownload */
	public static void addStartDownloadListener(StartDownloadListener listener) {
		addListener(StartDownloadListener.class, listener);
	}

	/** Supprime un observateur sur l'événement StartDownload */
	public static void removeStartDownloadListener(
			StartDownloadListener listener) {
		removeListener(StartDownloadListener.class, listener);
	}

	/** Avertit tous les observateurs de l'événement StartDownload */
	public static void fireStartDownload(StartDownloadEvent event) {
		Iterator<BroadcastListener> it = getListenerIterator(StartDownloadListener.class);
		StartDownloadListener listener;
		while (it.hasNext()) {
			listener = (StartDownloadListener) it.next();
			listener.onStartDownload(event);
		}
	}

	/** Ajoute un observateur sur l'événement EndDownload */
	public static void addEndDownloadListener(EndDownloadListener listener) {
		addListener(EndDownloadListener.class, listener);
	}

	/** Supprime un observateur sur l'événement EndDownload */
	public static void removeEndDownloadListener(EndDownloadListener listener) {
		removeListener(EndDownloadListener.class, listener);
	}

	/** Avertit tous les observateurs de l'événement EndDownload */
	public static void fireEndDownload(EndDownloadEvent event) {
		Iterator<BroadcastListener> it = getListenerIterator(EndDownloadListener.class);
		EndDownloadListener listener;
		while (it.hasNext()) {
			listener = (EndDownloadListener) it.next();
			listener.onEndDownload(event);
		}
	}
}
