/*
 *  FS_Directory.java
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
package entDownloader.core;

import java.util.GregorianCalendar;

public class FS_Directory extends FS_Element {

	/**
	 * @param name
	 *            Nom du dossier
	 * @param dateModif
	 *            Date de modification du dossier
	 */
	public FS_Directory(String name, GregorianCalendar dateModif) {
		super(name, dateModif);
	}

	@Override
	short getType() {
		return DIRECTORY;
	}

}
