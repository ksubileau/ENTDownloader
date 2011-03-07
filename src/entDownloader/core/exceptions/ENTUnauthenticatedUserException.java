/*
 *  ENTUnauthenticatedUserException.java
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

/**
 * Lanc� pour indiquer qu'un contr�le d'authentification de l'utilisateur a
 * �chou� (c'est � dire que l'utilisateur n'est pas connect� ou que sa session a
 * expir�).
 */
public class ENTUnauthenticatedUserException extends IllegalStateException {

	private static final long serialVersionUID = -6029089402020384239L;
	/**
	 * Indique que l'utilisateur ne s'est pas authentifi�.
	 */
	public static final int UNAUTHENTICATED = 0;
	/**
	 * Indique que la session de l'utilisateur a expir�.
	 */
	public static final int SESSION_EXPIRED = 1;
	private int type = UNAUTHENTICATED;

	/**
	 * Construit une exception ENTUnauthenticatedUserException sans message
	 * d'information et avec un code d'erreur indiquant que l'utilisateur ne
	 * s'est pas authentifi�.
	 */
	public ENTUnauthenticatedUserException() {
		super();
	}

	/**
	 * Construit une exception ENTUnauthenticatedUserException avec le message
	 * d'information sp�cifi� et un code d'erreur indiquant que l'utilisateur ne
	 * s'est pas authentifi�. Le message d'erreur peut ensuite �tre retrouv�
	 * gr�ce � la m�thode getMessage().
	 * 
	 * @param message Le message d'erreur.
	 */
	public ENTUnauthenticatedUserException(String message) {
		super(message);
	}

	/**
	 * Construit une exception ENTUnauthenticatedUserException avec le message
	 * d'information et le code d'erreur sp�cifi�. Le message d'erreur peut
	 * ensuite �tre retrouv� gr�ce � la m�thode getMessage().
	 * 
	 * @param message Le message d'erreur.
	 * @param type Le code d'erreur, qui indique s'il s'agit d'une expiration de
	 *            session ou si l'utilisateur ne s'est pas authentifi�.
	 */
	public ENTUnauthenticatedUserException(String message, int type) {
		super(message);
		if (type == SESSION_EXPIRED) {
			this.type = type;
		}
	}

	/**
	 * Construit une exception ENTUnauthenticatedUserException sans message
	 * d'information et avec le code d'erreur sp�cifi�.
	 * 
	 * @param type Le code d'erreur, qui indique s'il s'agit d'une expiration de
	 *            session ou si l'utilisateur ne s'est pas authentifi�.
	 */
	public ENTUnauthenticatedUserException(int type) {
		super();
		if (type == SESSION_EXPIRED) {
			this.type = type;
		}
	}

	/**
	 * Retourne le code d'erreur indiquant la nature de l'�chec
	 * d'authentification.
	 * 
	 * @see ENTUnauthenticatedUserException#UNAUTHENTICATED
	 * @see ENTUnauthenticatedUserException#SESSION_EXPIRED
	 */
	public int getType() {
		return type;
	}
}
