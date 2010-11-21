/*
 *  Event.java
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

/** Classe de base pour tous les événements */
public abstract class Event {

	protected Object source;
	protected Object userObj;

	public static final int UNKNOWN_TYPE = 0;
	public static final int CHANGING_DIR_TYPE = 1;
	public static final int CHANGED_DIR_TYPE = 2;
	public static final int DOWNLOADED_BYTES_TYPE = 3;
	public static final int START_DOWNLOAD_TYPE = 4;
	public static final int END_DOWNLOAD_TYPE = 5;

	protected Event() {
	}

	/** Constructs an event with the specified source. */
	public Event(Object source) {
		this.source = source;
	}

	/** Returns the source. */
	public Object getSource() {
		return source;
	}

	/** Sets the source who generated the event. */
	public void setSource(Object newSource) {
		source = newSource;
	}

	/** Sets the user object. */
	public void setUserObject(Object obj) {
		userObj = obj;
	}

	/** Returns the user object. */
	public Object getUserObject() {
		return userObj;
	}

	/**
	 * @return Returns the type of the event.
	 */
	public abstract int getType();

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Event))
			return false;

		return getType() == ((Event) obj).getType();
	}
}
