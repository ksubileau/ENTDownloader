/*
 *  Downloader.java
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

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import net.sf.entDownloader.core.ENTDownloader;
import net.sf.entDownloader.core.FS_Element;
import net.sf.entDownloader.core.FS_File;
import net.sf.entDownloader.core.events.Broadcaster;
import net.sf.entDownloader.core.events.DownloadedBytesEvent;
import net.sf.entDownloader.core.events.DownloadedBytesListener;
import net.sf.entDownloader.core.events.EndDownloadEvent;
import net.sf.entDownloader.core.events.EndDownloadListener;
import net.sf.entDownloader.core.events.FileAlreadyExistsEvent;
import net.sf.entDownloader.core.events.FileAlreadyExistsListener;
import net.sf.entDownloader.core.events.StartDownloadEvent;
import net.sf.entDownloader.core.events.StartDownloadListener;
import net.sf.entDownloader.core.exceptions.ENTUnauthenticatedUserException;
import net.sf.entDownloader.gui.events.GuiBroadcaster;
import net.sf.entDownloader.gui.events.RequestDownloadAbortEvent;
import net.sf.entDownloader.gui.events.RequestDownloadAbortListener;

public class Downloader extends SwingWorker<Void, Void> implements
		StartDownloadListener, DownloadedBytesListener, EndDownloadListener,
		FileAlreadyExistsListener, RequestDownloadAbortListener {

	private DownloadFrame downloadFrame;
	private List<FS_Element> downList;
	private JFileChooser fileChooser;
	private JFrame parent;
	private int nbFilesDownloaded;
	private long totalSizeDownloaded;

	public Downloader(JFrame owner) {
		this(owner, (Collection<FS_Element>) null, null);
	}

	public Downloader(JFrame owner, JFileChooser saveasChooser) {
		this(owner, (Collection<FS_Element>) null, saveasChooser);
	}

	public Downloader(JFrame owner, Collection<FS_Element> downloadList) {
		this(owner, downloadList, null);
	}

	public Downloader(JFrame owner, FS_Element downloadFile) {
		this(owner, downloadFile, null);
	}

	public Downloader(JFrame owner, FS_Element downloadFile,
			JFileChooser saveasChooser) {
		this(owner, (Collection<FS_Element>) null, saveasChooser);
		downList.add(downloadFile);
	}

	public Downloader(JFrame owner, Collection<FS_Element> downloadList,
			JFileChooser saveasChooser) {
		this.parent = owner;
		downloadFrame = new DownloadFrame(owner);
		GuiBroadcaster.addRequestDownloadAbortListener(this);

		if (downloadList != null) {
			downList = new ArrayList<FS_Element>(downloadList);
		} else {
			downList = new ArrayList<FS_Element>();
		}

		if (saveasChooser == null) {
			saveasChooser = new JFileChooser();
		}

		fileChooser = saveasChooser;
		fileChooser.setDialogTitle("Enregistrer sous...");
	}

	public Downloader(JFrame owner, FS_Element[] selectedFiles,
			JFileChooser saveasChooser) {
		this(owner, Misc.convertToVector(selectedFiles), saveasChooser);
	}

	/*
	 * Cette méthode ne devrait pas être appelé dans l'EDT
	 */
	private void download() throws IOException, ENTUnauthenticatedUserException {
		ENTDownloader entd = ENTDownloader.getInstance();

		boolean isThereDirectories = isThereDirectories();
		boolean isMultiple = downList.size() > 1;
		/*	Pour optimisation, on considère que tout télécharger 
		 *  dans un dossier contenant seulement un élément revient à un téléchargement unique,
		 *  et non à un "télécharger tout"
		 */
		boolean isDownloadAll = (isMultiple && downList.size() == entd
				.getDirectoryContent().size());

		if (downList == null || downList.isEmpty())
			return;

		if (isMultiple || isThereDirectories) { //Sélection multiple ou un dossier seulement
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			downloadFrame
					.setOpenWhenFinishedText("Ouvrir le dossier à la fin du téléchargement");
		} else { //Selection unique d'un fichier
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			downloadFrame
					.setOpenWhenFinishedText("Ouvrir le fichier à la fin du téléchargement");
			if (!isThereDirectories) {
				fileChooser.setSelectedFile(new File(fileChooser
						.getSelectedFile(), downList.get(0).getName()));
			}
		}

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

		int saveResult = fileChooser.showSaveDialog(parent);

		//Restore Look & Feel if it was changed
		if (lookfeel != null) {
			try {
				javax.swing.UIManager.setLookAndFeel(lookfeel);
			} catch (Exception e) {
			}
		}

		if (saveResult != JFileChooser.APPROVE_OPTION)
			return;
		File saveas = fileChooser.getSelectedFile();

		if (!isThereDirectories) { //Téléchargement de fichiers seulement
			long totalsize = 0;
			int nbFiles = 0;
			for (Iterator<FS_Element> file = downList.iterator(); file
					.hasNext();) {
				totalsize += ((FS_File) file.next()).getSize();
				nbFiles++;
			}
			downloadFrame.setTotalInfos(nbFiles, totalsize);
		} else {
			downloadFrame.setTotalInfos(DownloadFrame.UNKNOWN,
					DownloadFrame.UNKNOWN);
		}

		downloadFrame.setTotalDownloaded(0, 0);
		downloadFrame.setOpenWhenFinishedVisible(Desktop.isDesktopSupported()
				&& Desktop.getDesktop().isSupported(
						java.awt.Desktop.Action.BROWSE)
				&& Desktop.getDesktop().isSupported(
						java.awt.Desktop.Action.OPEN));
		downloadFrame.setVisible(true);

		Broadcaster.addDownloadedBytesListener(this);
		Broadcaster.addStartDownloadListener(this);
		Broadcaster.addEndDownloadListener(this);
		Broadcaster.addFileAlreadyExistsListener(this);

		totalSizeDownloaded = nbFilesDownloaded = 0;

		String savePath = saveas.getPath();
		if (isMultiple
				&& !savePath.substring(savePath.length() - 1).equals(
						System.getProperty("file.separator"))) {
			savePath += System.getProperty("file.separator");
		}

		if (isDownloadAll) {
			entd.getAllFiles(savePath, -1);
		} else { //Téléchargement unique ou partiel
			for (Iterator<FS_Element> it = downList.iterator(); it.hasNext();) {
				FS_Element el = it.next();
				if (el.isFile()) {
					entd.getFile(el.getName(), savePath);
				} else {
					try {
						entd.changeDirectory(el.getName());
						entd.getAllFiles(
								new File(saveas, el.getName()).getPath(), -1); //TODO : Gestion profondeur maximale (EX : case à cocher "récursif ?" dans JFileChooser)
						entd.changeDirectory("..");
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						dispose();
					}
				}
			}
		}

		downloadFrame.setVisible(false);

		if (downloadFrame.openWhenFinished()) {
			try {
				if (isMultiple || isThereDirectories) {
					java.awt.Desktop.getDesktop().browse(saveas.toURI());
				} else {
					java.awt.Desktop.getDesktop().open(saveas);
				}
			} catch (Exception e) {
				System.err.println("Impossible d'ouvrir " + saveas.getPath());
			}
		}

		dispose();
	}

	private void dispose() {
		Broadcaster.removeDownloadedBytesListener(this);
		Broadcaster.removeStartDownloadListener(this);
		Broadcaster.removeEndDownloadListener(this);
		Broadcaster.removeFileAlreadyExistsListener(this);
		GuiBroadcaster.removeRequestDownloadAbortListener(this);
	}

	private boolean isThereDirectories() {
		for (Iterator<FS_Element> it = downList.iterator(); it.hasNext();) {
			if (it.next().isDirectory())
				return true;
		}
		return false;
	}

	@Override
	public void onEndDownload(EndDownloadEvent e) {
		totalSizeDownloaded += e.getFile().getSize();
		nbFilesDownloaded++;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				downloadFrame.setTotalDownloaded(nbFilesDownloaded,
						totalSizeDownloaded);
			}
		});
	}

	@Override
	public void onDownloadedBytes(final DownloadedBytesEvent e) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				downloadFrame.setCurrentFileDownloaded(e.getTotalDownloaded());
			}
		});
	}

	@Override
	public void onStartDownload(final StartDownloadEvent e) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				downloadFrame.setCurrentFileName(e.getFile().getName());
				downloadFrame.setCurrentFileSize(e.getFile().getSize());
				downloadFrame.setCurrentFileDownloaded(0);
			}
		});
	}

	@Override
	public void onFileAlreadyExists(FileAlreadyExistsEvent e) {
		//TODO Amélioration : Oui pour tous / non pour tous (ou case a cocher ne plus demander)
		int choice = JOptionPane
				.showConfirmDialog(
						downloadFrame,
						"<html>Un fichier portant le nom \""
								+ e.getFile().getName()
								+ "\" existe déjà à cet emplacement.<br>Voulez-vous écraser le fichier existant et le remplacer par le fichier en cours de téléchargement ?<b></html>",
						"ENTDownloader - Téléchargement",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

		e.abortDownload = (choice == JOptionPane.NO_OPTION);
	}

	@Override
	public void onRequestDownloadAbort(RequestDownloadAbortEvent event) {
		downloadFrame.dispose();
		dispose();
	}

	public void startDownload() {
		this.execute();
	}

	@Override
	protected Void doInBackground() throws Exception {
		download();
		return null;
	}
}
