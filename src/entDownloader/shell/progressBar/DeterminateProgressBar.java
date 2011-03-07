/*
 *  DeterminateProgressBar.java
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
package entDownloader.shell.progressBar;

public class DeterminateProgressBar implements IProgressBar {
	private int value = 0;
	private boolean visible = true;
	private int width = 50;
	private String progressLine = "";

	@Override
	public void setValue(int value) {
		this.value = value;
		draw();
	}

	@Override
	public int getValue() {
		return value;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean visible) {
		if (!visible) { //Effacement
			clear(true);
		} else {
			draw();
		}
		this.visible = visible;
	}

	@Override
	public void setWidth(int width) {
		this.width = width;
		draw();
	}

	@Override
	public int getWidth() {
		return width;
	}

	private void draw() {
		String newProgressLine = "      [";
		int i;
		for (i = 1; i <= value / (100f / width); i++) {
			newProgressLine += "=";
		}
		for (; i <= width; i++) {
			newProgressLine += " ";
		}
		newProgressLine += "]   ";
		newProgressLine += value + " %";
		clear(newProgressLine.length() < progressLine.length());
		System.out.flush();
		progressLine = newProgressLine;
		System.out.print(progressLine);
	}

	private void clear(boolean rewriteBlank) {
		int linelength = progressLine.length();
		int i;
		for (i = linelength; i > 0; i--) {
			System.out.print("\b");
		}
		if (rewriteBlank) {
			for (i = linelength; i > 0; i--) {
				System.out.print(" ");
			}
			for (i = linelength; i > 0; i--) {
				System.out.print("\b");
			}
		}
		progressLine = "";
	}

}
