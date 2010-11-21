/*
 *  EndDownloadEvent.java
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
package entDownloader.core.events;

import entDownloader.core.FS_File;

public class EndDownloadEvent extends Event {

	private FS_File file;

	public EndDownloadEvent() {
		this(null);
	}

	public EndDownloadEvent(FS_File file) {
		setFile(file);
	}

	public void setFile(FS_File file) {
		this.file = file;
	}

	public FS_File getFile() {
		return file;
	}

	@Override
	public int getType() {
		return END_DOWNLOAD_TYPE;
	}

}
