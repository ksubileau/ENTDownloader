/*
 *  Broadcaster.java
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
package net.sf.entDownloader.core.events;

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

	/** Ajoute un observateur sur l'événement AuthenticationSucceeded */
	public static void addAuthenticationSucceededListener(
			AuthenticationSucceededListener listener) {
		addListener(AuthenticationSucceededListener.class, listener);
	}

	/** Supprime un observateur sur l'événement AuthenticationSucceeded */
	public static void removeAuthenticationSucceededListener(
			AuthenticationSucceededListener listener) {
		removeListener(AuthenticationSucceededListener.class, listener);
	}

	/** Avertit tous les observateurs de l'événement AuthenticationSucceeded */
	public static void fireAuthenticationSucceeded(
			AuthenticationSucceededEvent event) {
		Iterator<BroadcastListener> it = getListenerIterator(AuthenticationSucceededListener.class);
		AuthenticationSucceededListener listener;
		while (it.hasNext()) {
			listener = (AuthenticationSucceededListener) it.next();
			listener.onAuthenticationSucceeded(event);
		}
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

	/** Ajoute un observateur sur l'événement DirectoryCreated */
	public static void addDirectoryCreatedListener(
			DirectoryCreatedListener listener) {
		addListener(DirectoryCreatedListener.class, listener);
	}

	/** Supprime un observateur sur l'événement DirectoryCreated */
	public static void removeDirectoryCreatedListener(
			DirectoryCreatedListener listener) {
		removeListener(DirectoryCreatedListener.class, listener);
	}

	/** Avertit tous les observateurs de l'événement DirectoryCreated */
	public static void fireDirectoryCreated(DirectoryCreatedEvent event) {
		Iterator<BroadcastListener> it = getListenerIterator(DirectoryCreatedListener.class);
		DirectoryCreatedListener listener;
		while (it.hasNext()) {
			listener = (DirectoryCreatedListener) it.next();
			listener.onDirectoryCreated(event);
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

	/** Ajoute un observateur sur l'événement DownloadAbort */
	public static void addDownloadAbortListener(DownloadAbortListener listener) {
		addListener(DownloadAbortListener.class, listener);
	}

	/** Supprime un observateur sur l'événement DownloadAbort */
	public static void removeDownloadAbortListener(
			DownloadAbortListener listener) {
		removeListener(DownloadAbortListener.class, listener);
	}

	/** Avertit tous les observateurs de l'événement DownloadAbort */
	public static void fireDownloadAbort(DownloadAbortEvent event) {
		Iterator<BroadcastListener> it = getListenerIterator(DownloadAbortListener.class);
		DownloadAbortListener listener;
		while (it.hasNext()) {
			listener = (DownloadAbortListener) it.next();
			listener.onDownloadAbort(event);
		}
	}

	/** Ajoute un observateur sur l'événement UploadedBytes */
	public static void addUploadedBytesListener(
			UploadedBytesListener listener) {
		addListener(UploadedBytesListener.class, listener);
	}

	/** Supprime un observateur sur l'événement UploadedBytes */
	public static void removeUploadedBytesListener(
			UploadedBytesListener listener) {
		removeListener(UploadedBytesListener.class, listener);
	}

	/** Avertit tous les observateurs de l'événement UploadedBytes */
	public static void fireUploadedBytes(UploadedBytesEvent event) {
		Iterator<BroadcastListener> it = getListenerIterator(UploadedBytesListener.class);
		UploadedBytesListener listener;
		while (it.hasNext()) {
			listener = (UploadedBytesListener) it.next();
			listener.onUploadedBytes(event);
		}
	}

	/** Ajoute un observateur sur l'événement StartUpload */
	public static void addStartUploadListener(StartUploadListener listener) {
		addListener(StartUploadListener.class, listener);
	}

	/** Supprime un observateur sur l'événement StartUpload */
	public static void removeStartUploadListener(
			StartUploadListener listener) {
		removeListener(StartUploadListener.class, listener);
	}

	/** Avertit tous les observateurs de l'événement StartUpload */
	public static void fireStartUpload(StartUploadEvent event) {
		Iterator<BroadcastListener> it = getListenerIterator(StartUploadListener.class);
		StartUploadListener listener;
		while (it.hasNext()) {
			listener = (StartUploadListener) it.next();
			listener.onStartUpload(event);
		}
	}

	/** Ajoute un observateur sur l'événement EndUpload */
	public static void addEndUploadListener(EndUploadListener listener) {
		addListener(EndUploadListener.class, listener);
	}

	/** Supprime un observateur sur l'événement EndUpload */
	public static void removeEndUploadListener(EndUploadListener listener) {
		removeListener(EndUploadListener.class, listener);
	}

	/** Avertit tous les observateurs de l'événement EndUpload */
	public static void fireEndUpload(EndUploadEvent event) {
		Iterator<BroadcastListener> it = getListenerIterator(EndUploadListener.class);
		EndUploadListener listener;
		while (it.hasNext()) {
			listener = (EndUploadListener) it.next();
			listener.onEndUpload(event);
		}
	}

	/** Ajoute un observateur sur l'événement FileAlreadyExists */
	public static void addFileAlreadyExistsListener(
			FileAlreadyExistsListener listener) {
		addListener(FileAlreadyExistsListener.class, listener);
	}

	/** Supprime un observateur sur l'événement FileAlreadyExists */
	public static void removeFileAlreadyExistsListener(
			FileAlreadyExistsListener listener) {
		removeListener(FileAlreadyExistsListener.class, listener);
	}

	/** Avertit tous les observateurs de l'événement FileAlreadyExists */
	public static void fireFileAlreadyExists(FileAlreadyExistsEvent event) {
		Iterator<BroadcastListener> it = getListenerIterator(FileAlreadyExistsListener.class);
		FileAlreadyExistsListener listener;
		while (it.hasNext()) {
			listener = (FileAlreadyExistsListener) it.next();
			listener.onFileAlreadyExists(event);
		}
	}

	/** Ajoute un observateur sur l'événement ElementRenamed */
	public static void addElementRenamedListener(
			ElementRenamedListener listener) {
		addListener(ElementRenamedListener.class, listener);
	}

	/** Supprime un observateur sur l'événement ElementRenamed */
	public static void removeElementRenamedListener(
			ElementRenamedListener listener) {
		removeListener(ElementRenamedListener.class, listener);
	}

	/** Avertit tous les observateurs de l'événement ElementRenamed */
	public static void fireElementRenamed(ElementRenamedEvent event) {
		Iterator<BroadcastListener> it = getListenerIterator(ElementRenamedListener.class);
		ElementRenamedListener listener;
		while (it.hasNext()) {
			listener = (ElementRenamedListener) it.next();
			listener.onElementRenamed(event);
		}
	}

	/** Ajoute un observateur sur l'événement ElementsDeleted */
	public static void addElementsDeletedListener(
			ElementsDeletedListener listener) {
		addListener(ElementsDeletedListener.class, listener);
	}

	/** Supprime un observateur sur l'événement ElementsDeleted */
	public static void removeElementsDeletedListener(
			ElementsDeletedListener listener) {
		removeListener(ElementsDeletedListener.class, listener);
	}

	/** Avertit tous les observateurs de l'événement ElementsDeleted */
	public static void fireElementsDeleted(ElementsDeletedEvent event) {
		Iterator<BroadcastListener> it = getListenerIterator(ElementsDeletedListener.class);
		ElementsDeletedListener listener;
		while (it.hasNext()) {
			listener = (ElementsDeletedListener) it.next();
			listener.onElementsDeleted(event);
		}
	}
}
