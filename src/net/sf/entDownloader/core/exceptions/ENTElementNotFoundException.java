/*
 *  ENTElementNotFoundException.java
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

import java.io.FileNotFoundException;

/**
 * Signal qu'une tentative d'accès à un élément stocké sur l'ENT a échoué.
 */
public class ENTElementNotFoundException extends FileNotFoundException {
	private static final long serialVersionUID = 2543153762500385784L;

	/**
	 * Construit une exception ENTElementNotFoundException sans message
	 * d'information.
	 */
	public ENTElementNotFoundException() {
		super();
	}

	/**
	 * Construit une exception ENTElementNotFoundException avec le message
	 * d'information spécifié. Le message d'erreur peut ensuite être retrouvé
	 * grâce à la méthode getMessage().
	 * 
	 * @param message Le message d'erreur.
	 */
	public ENTElementNotFoundException(String message) {
		super(message);
	}
}
