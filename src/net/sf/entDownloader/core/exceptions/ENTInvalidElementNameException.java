/*
 *  ENTInvalidElementNameException.java
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
package net.sf.entDownloader.core.exceptions;

import java.io.IOException;

/**
 * Signal que le nom de la ressource envoyée est invalide.
 * Ex: Création d'un dossier dont le nom est déjà utilisé.
 */
public class ENTInvalidElementNameException extends IOException {
	private static final long serialVersionUID = 2542553762500385784L;
	/**
	 * Indique que le nom souhaité contient un ou plusieurs
	 * caractères interdits.
	 */
	public static final int FORBIDDEN_CHAR = 0;
	/**
	 * Indique que le nom souhaité est déjà utilisé.
	 */
	public static final int ALREADY_USED = 1;
	private int type = FORBIDDEN_CHAR;

	/**
	 * Construit une exception ENTInvalidElementNameException sans message
	 * d'information.
	 */
	public ENTInvalidElementNameException(int type) {
		super();
		this.type = type;
	}

	/**
	 * Construit une exception ENTInvalidElementNameException avec le message
	 * d'information spécifié. Le message d'erreur peut ensuite être retrouvé
	 * grâce à la méthode getMessage().
	 * 
	 * @param message Le message d'erreur.
	 */
	public ENTInvalidElementNameException(int type, String message) {
		super(message);
		this.type = type;
	}

	/**
	 * Retourne le code d'erreur indiquant la nature de l'échec.
	 * 
	 * @see ENTInvalidElementNameException#FORBIDDEN_CHAR
	 * @see ENTInvalidElementNameException#ALREADY_USED
	 */
	public int getType() {
		return type;
	}
}