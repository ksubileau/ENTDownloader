/*
 *  UndeterminateProgressBar.java
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class UndeterminateProgressBar implements IProgressBar {
	private boolean visible = true;
	private int width = 50;
	private int symbolePosition = 0;
	private int symbMoveSens = 1;
	private String symb = "<=>";
	private String progressLine = "";
	private Timer timer = null;

	public UndeterminateProgressBar() {
		timer = new Timer(110, null);
		timer.setInitialDelay(0);
		timer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (visible) {
					draw();
				}
			}
		});
		timer.restart();
	}

	@Override
	public void setValue(int value) {
	}

	@Override
	public int getValue() {
		return -1;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public synchronized void setVisible(boolean visible) {
		if (visible == this.visible)
			return;
		if (!visible) { //Effacement
			clear(true);
			timer.stop();
		}
		this.visible = visible;
		if (visible) {
			symbolePosition = 0;
			symbMoveSens = 1;
			timer.start();
		}
	}

	@Override
	public void setWidth(int width) {
		this.width = width;
	}

	@Override
	public int getWidth() {
		return width;
	}

	private synchronized void draw() {
		if (!visible)
			return;
		String newProgressLine = "      [";
		int i;
		for (i = 0; i < symbolePosition; i++) {
			newProgressLine += " ";
		}

		newProgressLine += symb;

		for (; i <= width - symb.length(); i++) {
			newProgressLine += " ";
		}
		symbolePosition = symbolePosition + symbMoveSens;
		if (symbolePosition == width - symb.length() + 1) {
			symbMoveSens = -1;
		} else if (symbolePosition == 0) {
			symbMoveSens = 1;
		}
		newProgressLine += "]";
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
