/*
 *  ENTInvalidFS_ElementTypeException.java
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
 * Thrown to indicate that a method has been passed a directory instead of a
 * file, or reciprocally.
 */
public class ENTInvalidFS_ElementTypeException extends IOException {
	private static final long serialVersionUID = 763310137094181767L;

	/**
	 * Constructs an ENTInvalidFS_ElementTypeException with null as its error
	 * detail message.
	 */
	public ENTInvalidFS_ElementTypeException() {
		super();
	}

	/**
	 * Constructs an ENTInvalidFS_ElementTypeException with the specified detail
	 * message. The error message string s can later be retrieved by the
	 * {@link java.lang.Throwable#getMessage() Throwable.getMessage()} method of
	 * class java.lang.Throwable.
	 * 
	 * @param s
	 *            the detail message
	 */
	public ENTInvalidFS_ElementTypeException(String s) {
		super(s);
	}
}
