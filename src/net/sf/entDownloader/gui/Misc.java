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
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;

import net.sf.entDownloader.core.FS_Element;

public class Misc {
	private static Hashtable<String, Icon[]> fileIcons = new Hashtable<String, Icon[]>();
	private static Hashtable<String, String> fileDescription = new Hashtable<String, String>();

	private static Icon[] dirIcon = new Icon[2];
	private static Icon[] fileIcon = new Icon[2];

	public static final int SMALL = 0;
	public static final int MEDIUM = 1;

	/**
	 * Recherche et retourne l'icône associée à l'élément indiqué.
	 * Si cette icône n'existe pas ou qu'une erreur survient, une icône
	 * par défaut est retournée.
	 * 
	 * @param file
	 *            L'élément dont on recherche l'icône.
	 * @param size
	 * 			  La taille de l'icône souhaitée (SMALL ou MEDIUM)
	 * @return L'icône associée au type de l'élément indiqué.
	 */
	public static Icon getFileTypeIcon(FS_Element file, int size) {
		Icon icon = null;
		if (file.isDirectory()) {
			if(dirIcon[size] == null)
				dirIcon[size] = loadIcon("folder"+(size==SMALL?"16":"")+".png");
			icon = dirIcon[size];
		} else if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
			String extension = getExtension(file.getName());
			if (fileIcons.get(extension) == null)
				fileIcons.put(extension, new Icon[2]);
			if ((icon = fileIcons.get(extension)[size]) == null) {
				File tempfile;
				try {
					tempfile = File.createTempFile("icon", "." + extension);
					if(size == SMALL) {
						FileSystemView view = FileSystemView.getFileSystemView();
						icon = view.getSystemIcon(tempfile);
					} else {
						// Get metadata and create an icon
				        sun.awt.shell.ShellFolder sf =
				                sun.awt.shell.ShellFolder.getShellFolder(tempfile);
				        icon = new ImageIcon(sf.getIcon(true));
					}
					tempfile.delete();
				} catch (Exception e) {
					if(fileIcon[size] == null)
						fileIcon[size] = loadIcon("file"+(size==SMALL?"16":"")+".png");
					icon = fileIcon[size];
				}
				fileIcons.get(extension)[size] = icon;
			}
		} else {
			if(fileIcon[size] == null)
				fileIcon[size] = loadIcon("file"+(size==SMALL?"16":"")+".png");
			icon = fileIcon[size];
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

	/**
	 * Charge une icône à partir du fichier image indiqué.
	 * 
	 * @param imageName Nom du fichier image.
	 * @param altText Une courte description de l'image.
	 * @return L'instance de {@link #javax.swing.ImageIcon ImageIcon}
	 * 		   représentant l'icône demandée.
	 */
	public static ImageIcon loadIcon(String imageName, String altText) {
		String imgLocation = "net/sf/entDownloader/ressources/" + imageName;
		URL imageURL = MainFrame.class.getClassLoader()
				.getResource(imgLocation);
		if (imageURL != null) //image found
			return new ImageIcon(imageURL, altText);
		else { //no image found
			System.err.println("Resource not found: " + imgLocation);
			return null;
		}
	}

	/**
	 * Charge une icône à partir du fichier image indiqué.
	 * 
	 * @param imageName Nom du fichier image.
	 * @return L'instance de {@link #javax.swing.ImageIcon ImageIcon}
	 * 		   représentant l'icône demandée.
	 */
	public static ImageIcon loadIcon(String imageName) {
		return loadIcon(imageName, null);
	}

	/**
	 * Définit l'icône du bouton à partir du fichier image indiqué.
	 * 
	 * @param btn Le bouton dont l'icône est à définir.
	 * @param imageName Nom du fichier image.
	 */
	public static void setButtonIcon(AbstractButton btn, String imageName) {
		ImageIcon icon = loadIcon(imageName);
		if (icon != null) {
			btn.setIcon(icon);
		}
	}
}
