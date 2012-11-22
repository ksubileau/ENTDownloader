/*
 *  UploadFrame.java
 *      
 *  Copyright 2010-2012 Kévin Subileau. 
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


import javax.swing.JFrame;
import net.sf.entDownloader.core.FS_File;

/**
 * Informe l'utilisateur sur la progression de l'envoi
 * d'un ou de plusieurs fichiers.
 * 
 * @author Kévin
 * @since 2.0.0
 */
public class UploadFrame extends ProgressFrame {
	private static final long serialVersionUID = 7524375489500153295L;

	public UploadFrame(JFrame owner) {
		super(owner);
	}
	
	protected void initGUI() {
		super.initGUI();
		this.setTitle("Envoi en cours...");
		
		jLabel3.setText("Envoyé(s) :");
	}

	public void setTotalTransferred(int nbFilesDownloaded, long sizeDownloaded) {
		this.nbFilesTransferred = nbFilesDownloaded;
		this.sizeTransferred = sizeDownloaded;
		if (nbFilesDownloaded == 0) {
			globalProgress.setText("Aucun");
		} else {
			globalProgress.setText(this.nbFilesTransferred
					+ " fichier(s) envoyé(s) ("
					+ FS_File.size_Formatted(this.sizeTransferred) + ")");
		}
	}
}