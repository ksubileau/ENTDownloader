/*
 *  Misc.java
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
package net.sf.entDownloader.gui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;

import javax.activation.MimetypesFileTypeMap;
import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;

import net.sf.entDownloader.core.FS_Element;

public class Misc {
	private static Hashtable<String, Icon> fileIcons = new Hashtable<String, Icon>();
	private static Hashtable<String, String> fileDescription = new Hashtable<String, String>();

	/**
	 * Retrieves the icon to associate with this file. If the icon returned by
	 * the file is <code>null</code> then
	 * ask the UIManager to give us an icon (from the LookAndFeel)
	 * 
	 * @param file
	 *            The file for which we want get the icon
	 * @return the icon associated with the file
	 */
	public static Icon getIcon(FS_Element file) {
		Icon icon = null;
		if (file.isDirectory()) {
			icon = (Icon) UIManager.get("FileView.directoryIcon");
		} else if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
			String extension = getExtension(file.getName());
			if ((icon = fileIcons.get(extension)) == null) {
				File tempfile;
				try {
					tempfile = File.createTempFile("icon", "." + extension);
					FileSystemView view = FileSystemView.getFileSystemView();
					icon = view.getSystemIcon(tempfile);
					tempfile.delete();
				} catch (IOException e) {
					icon = (Icon) UIManager.get("FileView.fileIcon");
				}
				fileIcons.put(extension, icon);
			}
		} else {
			icon = (Icon) UIManager.get("FileView.fileIcon");
		}
		return icon;
	}

	/**
	 * Retrieves the file type description associate with this file.
	 * 
	 * @param file
	 *            The file for which we want get the file type description.
	 * @return the file type description associated with the file
	 */
	public static String getFileTypeDescription(FS_Element file) {
		String des = "";
		if (file.isDirectory()) {
			des = "Dossier";
		} else if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
			String extension = getExtension(file.getName());
			if ((des = fileDescription.get(extension)) == null) {
				File tempfile;
				try {
					tempfile = File.createTempFile("description", "."
							+ extension);
					FileSystemView view = FileSystemView.getFileSystemView();
					des = view.getSystemTypeDescription(tempfile);
					tempfile.delete();
				} catch (IOException e) {
					des = "Fichier " + getExtension(file.getName());
				}
				fileDescription.put(extension, des);
			}
		} else {
			des = new MimetypesFileTypeMap().getContentType(file.getName());
		}
		return des;
	}

	public static String getExtension(String filename) {
		int pos = filename.lastIndexOf('.');
		if (pos == -1 || pos == filename.length())
			return "";
		return filename.substring(pos + 1);
	}

	public static String getFileNameWithoutExtension(String filename) {
		int pos = filename.lastIndexOf('.');
		if (pos == -1)
			return filename;
		return filename.substring(0, pos);
	}

	public static String[] splitFileName(String filename) {
		String[] result = new String[2];
		int pos = filename.lastIndexOf('.');
		if (pos == -1) {
			result[0] = filename;
			result[1] = "";
		} else {
			if (pos == filename.length()) {
				result[1] = "";
			} else {
				result[1] = filename.substring(pos + 1);
			}
			result[0] = filename.substring(0, pos);
		}
		return result;
	}

	/**
	 * Returns a vector that contains the same objects as the array.
	 * 
	 * @param anArray
	 *            the array to be converted
	 * @return the new vector; if <code>anArray</code> is <code>null</code>,
	 *         returns <code>null</code>
	 */
	public static <T> Vector<T> convertToVector(T[] anArray) {
		if (anArray == null)
			return null;

		Vector<T> v = new Vector<T>(anArray.length);
		for (int i = 0; i < anArray.length; i++) {
			v.addElement(anArray[i]);
		}
		return v;
	}

	/**
	 * Returns a vector of vectors that contains the same objects as the array.
	 * 
	 * @param anArray
	 *            the double array to be converted
	 * @return the new vector of vectors; if <code>anArray</code> is
	 *         <code>null</code>, returns <code>null</code>
	 */
	public static <T> Vector<Vector<T>> convertToVector(T[][] anArray) {
		if (anArray == null)
			return null;

		Vector<Vector<T>> v = new Vector<Vector<T>>(anArray.length);
		for (int i = 0; i < anArray.length; i++) {
			v.addElement(convertToVector(anArray[i]));
		}
		return v;
	}

	/**
	 * Ouvre le navigateur par défaut à l'adresse indiquée. Les exceptions
	 * lancées par java.awt.Desktop.browse(java.net.URI) sont rattrapés sans
	 * action.
	 * 
	 * @param url L'adresse à visiter.
	 * @return True si l'action est supportée et a réussi sans exceptions, false
	 *         sinon
	 */
	public static boolean browse(String url) {
		try {
			return browse(new URL(url));
		} catch (MalformedURLException e) {
			return false;
		}
	}

	/**
	 * Ouvre le navigateur par défaut à l'adresse indiquée. Les exceptions
	 * lancées par java.awt.Desktop.browse(java.net.URI) sont rattrapés sans
	 * action.
	 * 
	 * @param url L'URL à visiter.
	 * @return True si l'action est supportée et a réussi sans exceptions, false
	 *         sinon
	 */
	public static boolean browse(URL url) {
		try {
			return browse(url.toURI());
		} catch (URISyntaxException e) {
			return false;
		}
	}

	/**
	 * Ouvre le navigateur par défaut à l'adresse indiquée. Les exceptions
	 * lancées par java.awt.Desktop.browse(java.net.URI) sont rattrapés sans
	 * action.
	 * 
	 * @param uri L'URI à visiter.
	 * @return True si l'action est supportée et a réussi sans exceptions, false
	 *         sinon
	 */
	public static boolean browse(URI uri) {
		if (Desktop.isDesktopSupported()
				&& Desktop.getDesktop().isSupported(
						java.awt.Desktop.Action.BROWSE)) {
			try {
				java.awt.Desktop.getDesktop().browse(uri);
				return true;
			} catch (Exception ex) {
				return false;
			}
		} else
			return false;
	}
}
