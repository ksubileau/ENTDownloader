/*
 *  ENTInvalidFS_ElementTypeException.java
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

import java.io.IOException;

/**
 * Lanc� pour indiquer qu'un r�pertoire a �t� pass� en argument � une m�thode
 * qui attendait un fichier, ou r�ciproquement.
 * 
 */
public class ENTInvalidFS_ElementTypeException extends IOException {
	private static final long serialVersionUID = 763310137094181767L;

	/**
	 * Construit une exception ENTInvalidFS_ElementTypeException sans message
	 * d'information.
	 */
	public ENTInvalidFS_ElementTypeException() {
		super();
	}

	/**
	 * Construit une exception ENTInvalidFS_ElementTypeException avec le message
	 * d'information sp�cifi�. Le message d'erreur peut ensuite �tre retrouv�
	 * gr�ce � la m�thode getMessage().
	 * 
	 * @param message Le message d'erreur.
	 */
	public ENTInvalidFS_ElementTypeException(String message) {
		super(message);
	}
}
