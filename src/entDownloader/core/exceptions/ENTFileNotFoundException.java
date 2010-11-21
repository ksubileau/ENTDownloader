/*
 *  ENTFileNotFoundException.java
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
package entDownloader.core.exceptions;

import java.io.FileNotFoundException;

/**
 * Signals that an attempt to open the file denoted by a specified pathname has
 * failed.
 */
public class ENTFileNotFoundException extends FileNotFoundException {
	private static final long serialVersionUID = 2543153762500385784L;

	/**
	 * Constructs an ENTFileNotFoundException with null as its error detail
	 * message.
	 */
	ENTFileNotFoundException() {
		super();
	}

	/**
	 * Constructs an ENTFileNotFoundException with the specified detail message.
	 * The error message string s can later be retrieved by the
	 * {@link java.lang.Throwable#getMessage() Throwable.getMessage()} method of
	 * class java.lang.Throwable.
	 * 
	 * @param s
	 *            the detail message
	 */
	public ENTFileNotFoundException(String s) {
		super(s);
	}
}
