/*
 *  Uploader.java
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ConcurrentModificationException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import net.sf.entDownloader.core.ENTDownloader;
import net.sf.entDownloader.core.events.Broadcaster;
import net.sf.entDownloader.core.events.EndUploadEvent;
import net.sf.entDownloader.core.events.EndUploadListener;
import net.sf.entDownloader.core.events.StartUploadEvent;
import net.sf.entDownloader.core.events.StartUploadListener;
import net.sf.entDownloader.core.events.UploadedBytesEvent;
import net.sf.entDownloader.core.events.UploadedBytesListener;
import net.sf.entDownloader.core.exceptions.ENTInvalidElementNameException;
import net.sf.entDownloader.core.exceptions.ENTUnauthenticatedUserException;
import net.sf.entDownloader.gui.events.GuiBroadcaster;
import net.sf.entDownloader.gui.events.AbortTransferRequestEvent;
import net.sf.entDownloader.gui.events.AbortTransferRequestListener;

/**
 * Gère l'envoi d'un fichier en mode graphique,
 * et informe l'utilisateur de la progression de l'opération.
 * 
 * @author Kévin
 * @since 2.0.0
 */
public class Uploader extends SwingWorker<Void, Void> implements
		StartUploadListener, UploadedBytesListener, EndUploadListener,
		AbortTransferRequestListener {

	private UploadFrame uploadFrame;
	private JFileChooser fileChooser;
	private MainFrame parent;

	public Uploader(MainFrame owner) {
		this(owner, null);
	}

	public Uploader(MainFrame owner, JFileChooser saveasChooser) {
		this.parent = owner;
		uploadFrame = new UploadFrame(owner);
		GuiBroadcaster.addAbortTransferRequestListener(this);

		if (saveasChooser == null) {
			saveasChooser = new JFileChooser();
		}

		fileChooser = saveasChooser;
	}

	/*
	 * Cette méthode ne devrait pas être appelé dans l'EDT
	 */
	private void upload() throws IOException, ENTUnauthenticatedUserException {
		ENTDownloader entd = ENTDownloader.getInstance();

		//Save Look & Feel
		LookAndFeel lookfeel = null;
		//Il est préférable d'utiliser le filechooser correspondant au SystemLookAndFeel
		//Sauf pour Linux sur lequel il est très mal fait
		if (!System.getProperty("os.name").contains("Linux")) {
			lookfeel = javax.swing.UIManager.getLookAndFeel();
			//Set Look & Feel
			try {
				javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager
						.getSystemLookAndFeelClassName());
				SwingUtilities.updateComponentTreeUI(fileChooser);
			} catch (Exception e) {
			}
		}

		fileChooser.setDialogTitle("Envoyer le fichier...");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setApproveButtonText("Envoyer");
		fileChooser.setApproveButtonToolTipText("Envoi le fichier sélectionné");
		
		int saveResult = fileChooser.showOpenDialog(parent);

		//Restore Look & Feel if it was changed
		if (lookfeel != null) {
			try {
				javax.swing.UIManager.setLookAndFeel(lookfeel);
			} catch (Exception e) {
			}
		}

		if (saveResult != JFileChooser.APPROVE_OPTION)
			return;
		File targetFile = fileChooser.getSelectedFile();

		uploadFrame.setTotalInfos(1, targetFile.length());

		uploadFrame.setTotalTransferred(0, 0);
		uploadFrame.setVisible(true);
		
		if(isCancelled())
			return;

		Broadcaster.addUploadedBytesListener(this);
		Broadcaster.addStartUploadListener(this);
		Broadcaster.addEndUploadListener(this);

		boolean retry = false;
		String saveAs = null;
		
		do {
			retry = false;
			try {
				entd.sendFile(targetFile, saveAs);
			} catch (ENTInvalidElementNameException e) {
				String message = "";
				switch (e.getType()) {
				case ENTInvalidElementNameException.ALREADY_USED:
					message = "Un autre élément porte le même nom que le fichier que vous tentez d'envoyer.<br>";
					break;
				case ENTInvalidElementNameException.FORBIDDEN_CHAR:
					message = "Le nom du fichier contient un ou plusieurs caractères non autorisés sur l'ENT.<br>";
					break;
				}
				saveAs = (String)JOptionPane.showInputDialog(
	                    uploadFrame,
	                    "<html><b>" + message + "</b>Veuillez indiquer un nom différent pour enregistrer votre fichier sur l'ENT ou<br>cliquez sur Annuler pour interrompre l'envoi :</html>",
	                    "ENTDownloader - Nom de fichier invalide",
	                    JOptionPane.WARNING_MESSAGE,
	                    null,
	                    null,
	                    (saveAs == null?targetFile.getName():saveAs));

				if (saveAs != null && saveAs.length() != 0)
					retry = true;
			} catch (FileNotFoundException e) {
				JOptionPane
				.showMessageDialog(
						parent,
						"<html>" + targetFile.getName() + "<br>Fichier introuvable ou illisible.<br>Vérifier que le nom du fichier est correct et que<br>vous disposez des autorisations appropriées.</html>",
						"ENTDownloader", JOptionPane.ERROR_MESSAGE);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while(retry);

		uploadFrame.dispose();

		dispose();
	}

	private void dispose() {
		Broadcaster.removeUploadedBytesListener(this);
		Broadcaster.removeStartUploadListener(this);
		Broadcaster.removeEndUploadListener(this);
		try {
			GuiBroadcaster.removeAbortTransferRequestListener(this);
		} catch (ConcurrentModificationException e) {
			e.printStackTrace();
			//TODO: ConcurrentModificationException (suppression d'un el de la liste en cours de parcours (fireRequestDownloadAbort->onRequestDownloadAbort->dispose)
		}
		parent.updateFrameData();
	}

	@Override
	public void onEndUpload(final EndUploadEvent e) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				uploadFrame.setTotalTransferred(1,
						e.getFile().length());
			}
		});
	}

	@Override
	public void onUploadedBytes(final UploadedBytesEvent e) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				uploadFrame.setCurrentBytesTransferred(e.getTotalUploaded());
			}
		});
	}

	@Override
	public void onStartUpload(final StartUploadEvent e) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				uploadFrame.setCurrentFileName(e.getFile().getName());
				uploadFrame.setCurrentFileSize(e.getFile().length());
				uploadFrame.setCurrentBytesTransferred(0);
			}
		});
	}

	@Override
	public void onAbortTransferRequest(AbortTransferRequestEvent event) {
		cancel(true);
		ENTDownloader.getInstance().abortUpload();
		uploadFrame.dispose();
		dispose();
	}

	public void startUpload() {
		this.execute();
	}

	@Override
	protected Void doInBackground() throws Exception {
		upload();
		return null;
	}
}
