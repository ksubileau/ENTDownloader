/*
 *  DownloadFrame.java
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
package net.sf.entDownloader.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JFrame;

import net.sf.entDownloader.core.FS_File;
import net.sf.entDownloader.gui.events.GuiBroadcaster;
import net.sf.entDownloader.gui.events.RequestDownloadAbortEvent;

public class DownloadFrame extends ProgressFrame {
	private static final long serialVersionUID = -1434579599151767167L;
	private JCheckBox openWhenFinished;

	public DownloadFrame(JFrame owner) {
		super(owner);
	}

	protected void initGUI() {
		super.initGUI();
		this.setTitle("Téléchargement en cours...");
		abort.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					GuiBroadcaster
							.fireRequestDownloadAbort(new RequestDownloadAbortEvent());
				}
			});

		jLabel3.setText("Téléchargé(s) :");
		{
			openWhenFinished = new JCheckBox();
			getContentPane().add(openWhenFinished);
			openWhenFinished
					.setText("Ouvrir le dossier à la fin du téléchargement");
			openWhenFinished.setBounds(8, 100, 295, 18);
			openWhenFinished.setVisible(false);
		}
	}

	public void setTotalTransferred(int nbFilesDownloaded, long sizeDownloaded) {
		this.nbFilesTransferred = nbFilesDownloaded;
		this.sizeTransferred = sizeDownloaded;
		if (nbFilesDownloaded == 0) {
			globalProgress.setText("Aucun");
		} else {
			globalProgress.setText(this.nbFilesTransferred
					+ " fichier(s) téléchargés ("
					+ FS_File.size_Formatted(this.sizeTransferred) + ")");
		}
	}

	public boolean openWhenFinished() {
		return openWhenFinished.isEnabled() && openWhenFinished.isVisible()
				&& openWhenFinished.isSelected();
	}

	public void setOpenWhenFinishedEnabled(boolean flag) {
		openWhenFinished.setEnabled(flag);
	}

	public void setOpenWhenFinishedVisible(boolean flag) {
		openWhenFinished.setVisible(flag);
	}

	public void setOpenWhenFinishedText(String label) {
		openWhenFinished.setText(label);
	}
}
