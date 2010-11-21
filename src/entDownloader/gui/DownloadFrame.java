/*
 *  DownloadFrame.java
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
package entDownloader.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

import entDownloader.core.FS_File;

public class DownloadFrame extends javax.swing.JDialog {
	//TODO checkbox fermer apr�s t�l�chargement
	private static final long serialVersionUID = -2404112024501545610L;
	private JProgressBar downProgress;
	private JLabel jLabel3;
	private JLabel jLabel5;
	private JCheckBox openWhenFinished;
	private JLabel remaining;
	private JLabel globalProgress;
	private JLabel jLabel2;
	private JLabel filename;
	private JButton abort;

	/**
	 * Informations de progression
	 */
	private String currentFileName;

	private long currentFileSize;
	private long currentFileDownloaded = 0;

	private int nbFilesDownloaded = 0;
	private long sizeDownloaded = 0;

	private int nbFiles;
	private long size;

	public static final int UNKNOWN = -1;

	/**
	 * Cr�e une fen�tre de t�l�chargement modale, avec la JFrame sp�cifi�e comme
	 * propri�taire.
	 * 
	 * @param owner
	 *            La JFrame � partir duquel la fen�tre de t�l�chargement est
	 *            affich�
	 */
	public DownloadFrame(JFrame owner) {
		super(owner);
		initDialog();
	}

	private void initDialog() {
		initGUI();
	}

	private void initGUI() {
		this.setTitle("T�l�chargement en cours...");
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		new GridBagLayout();
		getContentPane().setLayout(null);
		{
			abort = new JButton();
			getContentPane().add(
					abort,
					new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0,
							GridBagConstraints.CENTER, GridBagConstraints.NONE,
							new Insets(0, 0, 0, 0), 0, 0));
			abort.setText("Annuler");
			abort.setLocation(306, 227);
			abort.setBounds(290, 95, 79, 26);
			abort.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					DownloadFrame.this.dispose();
				}
			});
		}
		{
			downProgress = new JProgressBar();
			getContentPane().add(downProgress);
			downProgress.setBounds(2, 26, 380, 22);
			downProgress.setStringPainted(true);
		}
		{
			filename = new JLabel();
			getContentPane().add(filename);
			filename.setBounds(106, 7, 261, 16);
		}
		{
			jLabel2 = new JLabel();
			getContentPane().add(jLabel2);
			jLabel2.setText("Fichier en cours :");
			jLabel2.setBounds(8, 7, 108, 16);
		}
		{
			jLabel3 = new JLabel();
			getContentPane().add(jLabel3);
			jLabel3.setText("T�l�charg�(s) :");
			jLabel3.setBounds(8, 51, 83, 16);
		}
		{
			globalProgress = new JLabel();
			getContentPane().add(globalProgress);
			globalProgress.setBounds(97, 51, 270, 16);
		}
		{
			jLabel5 = new JLabel();
			getContentPane().add(jLabel5);
			jLabel5.setText("Total :");
			jLabel5.setBounds(8, 71, 33, 16);
		}
		{
			remaining = new JLabel();
			getContentPane().add(remaining);
			remaining.setBounds(47, 71, 320, 16);
		}
		{
			openWhenFinished = new JCheckBox();
			getContentPane().add(openWhenFinished);
			openWhenFinished
					.setText("Ouvrir le dossier � la fin du t�l�chargement");
			openWhenFinished.setBounds(10, 100, 269, 18);
			openWhenFinished.setVisible(false);
		}
		JFrame owner = (JFrame) getOwner();
		if (owner != null) {
			setIconImage(owner.getIconImage());
		}
		this.setPreferredSize(new java.awt.Dimension(404, 164));
		this.setSize(404, 164);
		this.setResizable(false);
		setLocationRelativeTo(owner);
		pack();
	}

	public void setCurrentFileName(String currentFileName) {
		this.currentFileName = currentFileName;
		filename.setText(this.currentFileName);
	}

	public void setCurrentFileSize(long currentFileSize) {
		this.currentFileSize = currentFileSize;
		updateProgressBar();
	}

	public void setCurrentFileDownloaded(long currentFileDownloaded) {
		this.currentFileDownloaded = currentFileDownloaded;
		updateProgressBar();
	}

	public void setTotalDownloaded(int nbFilesDownloaded, long sizeDownloaded) {
		this.nbFilesDownloaded = nbFilesDownloaded;
		this.sizeDownloaded = sizeDownloaded;
		if (nbFilesDownloaded == 0) {
			globalProgress.setText("Aucun");
		} else {
			globalProgress.setText(this.nbFilesDownloaded
					+ " fichier(s) t�l�charg�s ("
					+ FS_File.size_Formatted(this.sizeDownloaded) + ")");
		}
	}

	public void setTotalInfos(int nbFiles, long size) {
		this.nbFiles = nbFiles;
		this.size = size;
		if (this.size == UNKNOWN || this.nbFiles == UNKNOWN) {
			remaining.setText("Inconnu");
		} else {
			remaining.setText((this.nbFiles - nbFilesDownloaded)
					+ " fichier(s) ("
					+ FS_File.size_Formatted(this.size - sizeDownloaded) + ")");
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

	private void updateProgressBar() {
		int value = (int) (currentFileDownloaded * 100F / currentFileSize);
		downProgress.setValue(value);
		downProgress.setString(value + " % ("
				+ FS_File.size_Formatted(this.currentFileDownloaded) + " sur "
				+ FS_File.size_Formatted(this.currentFileSize) + ")");
	}
}