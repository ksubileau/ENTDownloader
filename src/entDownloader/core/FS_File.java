/*
 *  FS_File.java
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
package entDownloader.core;

import java.util.GregorianCalendar;

/**
 * Repr�sente un fichier stock� sur l'ENT.
 */
public class FS_File extends FS_Element {

	long size;

	/**
	 * Construit un nouveau fichier.
	 * 
	 * @param name Nom du fichier.
	 * @param dateModif Date de modification du fichier
	 * @param size Taille du fichier en octets.
	 */
	public FS_File(String name, GregorianCalendar dateModif, long size) {
		super(name, dateModif);
		setSize(size);
	}

	/**
	 * Construit un nouveau fichier.
	 * 
	 * @param name Nom du fichier.
	 * @param dateModif Date de modification du fichier
	 * @param ssize Taille du fichier suivi de son unit� (Ex: 20 Mo).
	 */
	public FS_File(String name, GregorianCalendar dateModif, String ssize) {
		this(name, dateModif, size_StringToNumber(ssize));
	}

	/**
	 * Convertit une repr�sentation textuelle de la taille du fichier (Ex: 58
	 * Ko) en octets.
	 * 
	 * @param ssize La taille sous forme d'une cha�ne de caract�re.
	 * @return La taille convertit en octets.
	 * @throws FileSizeFormatException Le format de la cha�ne de caract�re est
	 *             invalide.
	 * @throws UnsupportedOperationException D�passement de capacit� du
	 *             {@link Long}.
	 */
	public static long size_StringToNumber(String ssize)
			throws FileSizeFormatException, UnsupportedOperationException {
		float fsize;
		boolean failed = true;
		String units[] = { "o", "ko", "mo", "go", "to", "po" };
		String[] spsize = ssize.trim().replaceAll("\\s{2,}", " ")
				.replaceAll(",", ".").split(" ");
		try {
			fsize = Float.parseFloat(spsize[0]);
		} catch (NumberFormatException e) {
			throw new FileSizeFormatException(e.getLocalizedMessage());
		}

		spsize[1] = spsize[1].toLowerCase();
		for (String unit : units) {
			if (unit.equals(spsize[1])) {
				failed = false;
				break;
			}
			fsize *= 1024;
		}

		if (failed)
			throw new FileSizeFormatException("Invalid SI prefix");
		if (fsize > Long.MAX_VALUE) // D�passement de la taille du long
			throw new UnsupportedOperationException(
					"Unable to convert "
							+ ssize
							+ " : too great. This method can't convert a size greater than "
							+ FS_File.size_Formatted(Long.MAX_VALUE));

		return Math.round((double) fsize);
	}

	/**
	 * Formate une taille en octets pour l'affichage.
	 * 
	 * @param size La taille en octets � formatter.
	 * @return La taille dans une unit� appropri�e pour l'affichage, suivi de
	 *         cette unit�.
	 */
	public static String size_Formatted(long size) {
		float fsize;
		int range;
		String units[] = { "o", "Ko", "Mo", "Go", "To", "Po", "Eo", "Zo", "Yo" };

		for (range = 0, fsize = size; fsize >= 1024f; ++range) {
			fsize /= 1024f;
		}

		return String.format("%.2f", fsize) + " " + units[range];
	}

	/**
	 * Retourne la taille du fichier en octets.
	 */
	public long getSize() {
		return size;
	}

	/**
	 * Retourne la taille du fichier sous sa repr�sentation textuelle format�.
	 */
	public String getFormattedSize() {
		return size_Formatted(size);
	}

	/**
	 * D�finit la taille du fichier � partir d'une repr�sentation textuelle
	 * format�.
	 * 
	 * @param ssize La taille du fichier sous sa repr�sentation textuelle.
	 */
	public void setSize(String ssize) {
		setSize(size_StringToNumber(ssize));
	}

	/**
	 * D�finit la taille du fichier en octets.
	 * 
	 * @param size La taille du fichier en octets.
	 */
	public void setSize(long size) {
		if (size < 0)
			throw new IllegalArgumentException(
					"File size must be a positive or null integer.");
		this.size = size;
	}

	/*@Override public String toString() {
		return super.toString() + "\t" + size_Formatted(size);
		//return "\n" + getName() + " " + dateModif.get(Calendar.DATE) + "-" + dateModif.get(Calendar.MONTH) + "-" + dateModif.get(Calendar.YEAR) + " " + dateModif.get(Calendar.HOUR) + ":" + dateModif.get(Calendar.MINUTE);
	}*/

	/**
	 * Retourne le type d'�l�ment repr�sent� par cette instance. En l'occurence,
	 * il s'agit d'un fichier.
	 * 
	 * @see FS_Element#getType()
	 */
	@Override
	short getType() {
		return FILE;
	}
}

class FileSizeFormatException extends IllegalArgumentException {
	private static final long serialVersionUID = 2607007976504592524L;

	public FileSizeFormatException() {
		super("Invalid file size format");
	}

	public FileSizeFormatException(String message) {
		super(message);
	}
}