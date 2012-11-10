package net.sf.entDownloader.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

import net.sf.entDownloader.core.FS_File;
import net.sf.entDownloader.gui.events.AbortTransferRequestEvent;
import net.sf.entDownloader.gui.events.GuiBroadcaster;

/**
 * Classe de base pour l'affichage de la progression d'un transfert.
 *
 * @author Kévin
 * @since 2.0.0
 */
public abstract class ProgressFrame extends JDialog {

	private static final long serialVersionUID = -2004622024581545650L;

	/**
	 * Composants de la boîte de dialogue
	 */
	protected JProgressBar progressBar;
	protected JLabel jLabel3;
	protected JLabel jLabel2;
	protected JLabel jLabel5;
	protected JLabel remaining;
	protected JLabel globalProgress;
	protected JLabel filename;
	protected JButton abort;

	/**
	 * Informations de progression
	 */
	protected String currentFileName;
	protected long currentFileSize;
	protected long currentFileBytesTransferred = 0;
	protected int nbFilesTransferred = 0;
	protected long sizeTransferred = 0;
	protected int nbFiles;
	protected long size;

	/** 
	 * Constante utilisée pour indiquer que le nombre de fichiers 
	 * total ou la taille totale à transférer sont indéterminés. 
	 */
	public static final int UNDEFINED = -1;

	public ProgressFrame(JFrame owner) {
		super(owner);
		initGUI();
	}

	protected void initGUI() {
		this.setTitle("Transfert en cours...");
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
			abort.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						GuiBroadcaster
								.fireAbortTransferRequest(new AbortTransferRequestEvent());
					}
				});
			abort.setText("Annuler");
			abort.setBounds(308, 95, 79, 26);
		}
		{
			progressBar = new JProgressBar();
			getContentPane().add(progressBar);
			progressBar.setBounds(8, 26, 380, 22);
			progressBar.setStringPainted(true);
		}
		{
			filename = new JLabel();
			getContentPane().add(filename);
			filename.setBounds(118, 7, 261, 16);
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
			jLabel3.setText("Envoyé(s) :");
			jLabel3.setBounds(8, 51, 97, 16);
		}
		{
			globalProgress = new JLabel();
			getContentPane().add(globalProgress);
			globalProgress.setBounds(105, 51, 270, 16);
		}
		{
			jLabel5 = new JLabel();
			getContentPane().add(jLabel5);
			jLabel5.setText("Total :");
			jLabel5.setBounds(8, 71, 41, 16);
		}
		{
			remaining = new JLabel();
			getContentPane().add(remaining);
			remaining.setBounds(53, 71, 320, 16);
		}
		JFrame owner = (JFrame) getOwner();
		if (owner != null) {
			setIconImage(owner.getIconImage());
		}
		this.setPreferredSize(new java.awt.Dimension(404, 157));
		this.setSize(404, 157);
		this.setResizable(false);
		setLocationRelativeTo(owner);
		pack();
	}

	/**
	 * Définit le nom affiché du fichier en cours de transfert.
	 */
	public void setCurrentFileName(String currentFileName) {
		this.currentFileName = currentFileName;
		filename.setText(this.currentFileName);
	}

	/**
	 * Définit la taille affichée du fichier en cours de transfert.
	 */
	public void setCurrentFileSize(long currentFileSize) {
		this.currentFileSize = currentFileSize;
		updateProgressBar();
	}

	/**
	 * Définit le nombre d'octets transférés du fichier en cours.
	 */
	public void setCurrentBytesTransferred(long currentFileBytesTransferred) {
		this.currentFileBytesTransferred = currentFileBytesTransferred;
		updateProgressBar();
	}

	/**
	 * Définit le nombre de fichiers et d'octets transférés au total
	 * depuis le début du transfert.
	 */
	public void setTotalTransferred(int nbFilesTransferred, long sizeTransferred) {
		this.nbFilesTransferred = nbFilesTransferred;
		this.sizeTransferred = sizeTransferred;
		if (nbFilesTransferred == 0) {
			globalProgress.setText("Aucun");
		} else {
			globalProgress.setText(this.nbFilesTransferred
					+ " fichier(s) transféré(s) ("
					+ FS_File.size_Formatted(this.sizeTransferred) + ")");
		}
	}

	/**
	 * Définit le nombre de fichiers et d'octets à transferrer au total.
	 */
	public void setTotalInfos(int nbFiles, long size) {
		this.nbFiles = nbFiles;
		this.size = size;
		if (this.size == UNDEFINED || this.nbFiles == UNDEFINED) {
			remaining.setText("Inconnu");
		} else {
			remaining.setText((this.nbFiles - nbFilesTransferred)
					+ " fichier(s) ("
					+ FS_File.size_Formatted(this.size - sizeTransferred) + ")");
		}
	}

	/**
	 * Actualise la progression de la barre de progression.
	 */
	protected void updateProgressBar() {
		int value = (int) (currentFileBytesTransferred * 100F / currentFileSize);
		progressBar.setValue(value);
		progressBar.setString(value + " % ("
				+ FS_File.size_Formatted(this.currentFileBytesTransferred) + " sur "
				+ FS_File.size_Formatted(this.currentFileSize) + ")");
	}

}
