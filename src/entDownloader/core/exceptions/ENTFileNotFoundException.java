/*
 *  ENTFileNotFoundException.java
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

import java.io.FileNotFoundException;

/**
 * Signal qu'une tentative d'ouverture d'un fichier stocké sur l'ENT a échoué.
 */
public class ENTFileNotFoundException extends FileNotFoundException {
	private static final long serialVersionUID = 2543153762500385784L;

	/**
	 * Construit une exception ENTFileNotFoundException sans message
	 * d'information.
	 */
	public ENTFileNotFoundException() {
		super();
	}

	/**
	 * Construit une exception ENTDirectoryNotFoundException avec le message
	 * d'information spécifié. Le message d'erreur peut ensuite être retrouvé
	 * grâce à la méthode {@link java.lang.Throwable#getMessage()
	 * Throwable.getMessage()} de la classe {@link java.lang.Throwable}.
	 * 
	 * @param message Le message d'erreur.
	 */
	public ENTFileNotFoundException(String message) {
		super(message);
	}
}
