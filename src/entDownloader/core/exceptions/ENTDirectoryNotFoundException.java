/*
 *  ENTDirectoryNotFoundException.java
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
package entDownloader.core.exceptions;

import java.io.IOException;

/**
 * Signals that an attempt to open the directory denoted by a specified name has
 * failed
 */
public class ENTDirectoryNotFoundException extends IOException {
	private static final long serialVersionUID = 2543153762500385784L;

	/**
	 * Constructs an ENTDirectoryNotFoundException with null as its error detail
	 * message.
	 */
	ENTDirectoryNotFoundException() {
		super();
	}

	/**
	 * Constructs an ENTDirectoryNotFoundException with the specified detail
	 * message. The error message string s can later be retrieved by the
	 * {@link java.lang.Throwable#getMessage() Throwable.getMessage()} method of
	 * class java.lang.Throwable.
	 * 
	 * @param s
	 *            the detail message
	 */
	public ENTDirectoryNotFoundException(String s) {
		super(s);
	}
}