/*
 *  ProgressBar.java
 *      
 *  Copyright 2010-2011 Kévin Subileau. 
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
package entDownloader.shell.progressBar;

public class ProgressBar implements IProgressBar {
	private IProgressBar progBar = null;
	private boolean isDeterminate = true;

	public ProgressBar(boolean isDeterminate) {
		setDeterminate(isDeterminate);
	}

	public ProgressBar() {
		this(true);
	}

	public final boolean isDeterminate() {
		return isDeterminate;
	}

	public final void setDeterminate(boolean isDeterminate) {
		if (this.isDeterminate == isDeterminate && progBar != null)
			return;

		if (isDeterminate) {
			progBar = new DeterminateProgressBar();
		} else {
			progBar = new UndeterminateProgressBar();
		}
		this.isDeterminate = isDeterminate;
	}

	@Override
	public void setValue(int value) {
		if (value < 0) {
			value = 0;
		} else if (value > 100) {
			value = 100;
		}
		progBar.setValue(value);
	}

	@Override
	public int getValue() {
		return progBar.getValue();
	}

	@Override
	public boolean isVisible() {
		return progBar.isVisible();
	}

	@Override
	public void setVisible(boolean visible) {
		progBar.setVisible(visible);
	}

	@Override
	public void setWidth(int width) {
		progBar.setWidth(width);
	}

	@Override
	public int getWidth() {
		return progBar.getWidth();
	}

}
