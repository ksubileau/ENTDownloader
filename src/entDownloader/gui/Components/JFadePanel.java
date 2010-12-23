/*
 *  JFadePanel.java
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
package entDownloader.gui.Components;

import java.awt.Color;
import java.awt.LayoutManager;

import javax.swing.JPanel;

/**
 * A {@link JPanel} with fade-in and fade-out effects.
 */
public class JFadePanel extends JPanel {
	private static final long serialVersionUID = -8973008279297176112L;
	private boolean fadein, fadeout;
	private int maxOpacity = 100;

	/**
	 * Creates a new JFadePanel with a double buffer and a flow layout. Fade in
	 * and fade out are enabled.
	 */
	public JFadePanel() {
		super();
		super.setOpaque(true);
		fadein = fadeout = true;
	}

	/**
	 * Create a new buffered JFadePanel with the specified layout manager. Fade
	 * in and fade out are enabled.
	 * 
	 * @param layout
	 *            the LayoutManager to use
	 */
	public JFadePanel(LayoutManager layout) {
		super(layout);
		super.setOpaque(true);
		fadein = fadeout = true;
	}

	/**
	 * Creates a new JPanel with FlowLayout and the specified buffering
	 * strategy. If isDoubleBuffered is true, the JPanel will use a double
	 * buffer. Fade in and fade out are enabled.
	 * 
	 * @param isDoubleBuffered
	 *            a boolean, true for double-buffering, which uses additional
	 *            memory space to achieve fast, flicker-free updates
	 */
	public JFadePanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		super.setOpaque(true);
		fadein = fadeout = true;
	}

	/**
	 * Creates a new JPanel with the specified layout manager and buffering
	 * strategy. Fade in and fade out are enabled.
	 * 
	 * @param layout
	 *            the LayoutManager to use
	 * @param isDoubleBuffered
	 *            a boolean, true for double-buffering, which uses additional
	 *            memory space to achieve fast, flicker-free updates
	 */
	public JFadePanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		super.setOpaque(true);
		fadein = fadeout = true;
	}

	/**
	 * Sets whether or not this component disappears with a fade out effect.
	 * 
	 * @param fadeout
	 *            true if this component should disappear with a fade out
	 *            effect, false otherwise
	 */
	public void setFadeOutEnabled(boolean fadeout) {
		this.fadeout = fadeout;
	}

	/**
	 * This component must always be opaque to work fine, this method is
	 * disabled.
	 */
	@Override
	@Deprecated
	public void setOpaque(boolean isOpaque) {
		super.setOpaque(true);
	}

	@Override
	public void setVisible(boolean aFlag) {
		if (aFlag == false && fadeout == true) {
			fadeOut(80, 1800);
		}
		super.setVisible(aFlag);
		if (aFlag == true && fadein == true) {
			fadeIn(80, 1800);
		}
	}

	/**
	 * Sets whether or not this component appears with a fade in effect.
	 * 
	 * @param fadein
	 *            true if this component should appear with a fade in effect,
	 *            false otherwise
	 */
	public void setFadeInEnabled(boolean fadein) {
		this.fadein = fadein;
	}

	private void fadeIn(final int nbstep, final int speed) {
		if (nbstep <= 0 || speed < 0)
			throw new IllegalArgumentException();
		Thread th = new Thread() {
			@Override
			public void run() {
				int transparency = 0;
				int step = (maxOpacity / nbstep);
				while (transparency <= maxOpacity) {
					setOpacity(transparency);
					transparency += step;
					try {
						Thread.sleep(speed / nbstep);
					} catch (InterruptedException e) {
					}
				}
			}

		};
		th.setDaemon(true);
		th.start();
	}

	private void fadeOut(final int nbstep, final int speed) {
		if (nbstep <= 0 || speed < 0)
			throw new IllegalArgumentException();
		Thread th = new Thread() {
			@Override
			public void run() {
				int opacity = getBackground().getAlpha() * 100 / 255;
				int step = (opacity / nbstep);
				opacity -= step;
				while (opacity >= 0) {
					setOpacity(opacity);
					opacity -= step;
					try {
						Thread.sleep(speed / nbstep);
					} catch (InterruptedException e) {
					}
				}
			}

		};
		th.setDaemon(true);
		th.start();
	}

	private void setOpacity(int opacityPercent) {
		if (opacityPercent < 0 || opacityPercent > 100)
			throw new IllegalArgumentException();
		Color current = getBackground();
		setBackground(new Color(current.getRed(), current.getGreen(),
				current.getBlue(), 255 * opacityPercent / 100));
	}

	/**
	 * Sets the maximum opacity of this JFadePanel.
	 * @param maxOpacityPercent The maximum opacity of this JFadePanel.
	 */
	public void setMaxOpacity(int maxOpacityPercent) {
		if (maxOpacityPercent < 0 || maxOpacityPercent > 100)
			throw new IllegalArgumentException();
		this.maxOpacity = maxOpacityPercent;
	}

}
