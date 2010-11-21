/*
 *  DoubleClickOnRowEvent.java
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
package entDownloader.gui.events;

import entDownloader.core.FS_Element;

/**
 * Informe les observateurs que le r�pertoire courant est en train de changer
 */
public class DoubleClickOnRowEvent extends GuiEvent {

	/* (non-Javadoc)
	 * @see entDownloader.core.events.Event#getType()
	 */
	@Override
	public int getType() {
		return GuiEvent.DOUBLE_CLICK_ON_ROW_TYPE;
	}

	public DoubleClickOnRowEvent() {
		this(null);
	}

	public DoubleClickOnRowEvent(FS_Element target) {
		setTarget(target);
	}

	private FS_Element target;

	public FS_Element getTarget() {
		return target;
	}

	public void setTarget(FS_Element target) {
		this.target = target;
	}
}
