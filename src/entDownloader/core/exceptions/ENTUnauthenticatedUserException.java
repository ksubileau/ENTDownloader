/*
 *  ENTUnauthenticatedUserException.java
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

/**
 * Thrown to indicate that a user's authentication control has failed (i.e. user
 * isn't authenticated or session has expired).
 */
public class ENTUnauthenticatedUserException extends IllegalStateException {

	private static final long serialVersionUID = -6029089402020384239L;
	public static final int UNAUTHENTICATED = 0;
	public static final int SESSION_EXPIRED = 1;
	private int type = UNAUTHENTICATED;

	/**
	 * Constructs an ENTUnauthenticatedUserException with null as its error
	 * detail message and unauthenticated as its detail code.
	 */
	public ENTUnauthenticatedUserException() {
		super();
	}

	/**
	 * Constructs an ENTUnauthenticatedUserException with the specified detail
	 * message and unauthenticated as its detail code. The error message string
	 * s can later be retrieved by the {@link java.lang.Throwable#getMessage()
	 * Throwable.getMessage()} method of class java.lang.Throwable.
	 * 
	 * @param s
	 *            the detail message
	 */
	public ENTUnauthenticatedUserException(String s) {
		super(s);
	}

	/**
	 * Constructs an ENTUnauthenticatedUserException with the specified detail
	 * message and detail code. The error message string s can later be
	 * retrieved by the {@link java.lang.Throwable#getMessage()
	 * Throwable.getMessage()} method of class java.lang.Throwable.
	 * 
	 * @param s
	 *            the detail message
	 * @param type
	 *            the detail code
	 */
	public ENTUnauthenticatedUserException(String s, int type) {
		super(s);
		if (type == SESSION_EXPIRED) {
			this.type = type;
		}
	}

	/**
	 * Constructs an ENTUnauthenticatedUserException with null as its error
	 * detail message and the specified detail code.
	 * 
	 * @param type
	 *            the detail code
	 */
	public ENTUnauthenticatedUserException(int type) {
		super();
		if (type == SESSION_EXPIRED) {
			this.type = type;
		}
	}

	/**
	 * @return The detail code of the exception
	 */
	public int getType() {
		return type;
	}
}
