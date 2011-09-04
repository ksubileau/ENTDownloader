/*
 *  MainFrame.java
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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.ParseException;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.entDownloader.core.CoreConfig;
import net.sf.entDownloader.core.ENTDownloader;
import net.sf.entDownloader.core.FS_Element;
import net.sf.entDownloader.core.events.Broadcaster;
import net.sf.entDownloader.core.events.DirectoryChangedEvent;
import net.sf.entDownloader.core.events.DirectoryChangedListener;
import net.sf.entDownloader.core.events.DirectoryChangingEvent;
import net.sf.entDownloader.core.events.DirectoryChangingListener;
import net.sf.entDownloader.core.exceptions.ENTDirectoryNotFoundException;
import net.sf.entDownloader.core.exceptions.ENTInvalidFS_ElementTypeException;
import net.sf.entDownloader.core.exceptions.ENTUnauthenticatedUserException;
import net.sf.entDownloader.gui.Components.JStatusBar;
import net.sf.entDownloader.gui.Components.filesViews.ListView;
import net.sf.entDownloader.gui.Components.filesViews.briefview.BriefView;
import net.sf.entDownloader.gui.Components.filesViews.detailview.DetailView;
import net.sf.entDownloader.gui.events.DoubleClickOnRowEvent;
import net.sf.entDownloader.gui.events.DoubleClickOnRowListener;
import net.sf.entDownloader.gui.events.GuiBroadcaster;

public class MainFrame extends javax.swing.JFrame implements
		DirectoryChangedListener {

	private static final long serialVersionUID = 925222114370143696L;
	private ENTDownloader entd = ENTDownloader.getInstance();

	private JMenuItem dld;
	private JMenu navigationMenu;
	private JMenuItem onlineHelp;
	private JMenuItem dldAll;
	private JMenuItem website;
	private JMenuItem checkUpdate;
	private JToggleButton detailsViewBtn;
	private JToggleButton listViewBtn;
	private JLabel userNameLabel;
	private ButtonGroup affichGroup;
	private JRadioButtonMenuItem DetailItem;
	private JRadioButtonMenuItem ListItem;
	private JMenu affichMenu;
	private ListView fileView;
	private JButton parentBtn;
	private JButton homeBtn;
	private JTextField adressField;
	private JButton nextBtn;
	private JButton prevBtn;
	private JToolBar adressBar;
	private JProgressBar usedSpaceProgress;
	private JLabel storageInfosLabel;
	private JSeparator jSeparator3;
	private JLabel statusInfo;
	private JButton Download_tool;
	private JToolBar toolBar;
	private JMenuItem jMenuItem2;
	private JMenuItem jMenuItem1;
	private JPopupMenu PopupMenu;
	private JFileChooser fileChooser;
	private JMenuItem about;
	private JSeparator jSeparator2;
	private JMenu help;
	private JSeparator jSeparator1;
	private JMenuItem exit;
	private JMenu fileMenu;
	private JMenuBar jMenuBar;
	private JButton DownloadAll_tool;
	private JStatusBar statusBar;
	private JButton refreshBtn;
	private JMenuItem refreshItem;
	private DownloadAction dldAction;
	private DownloadAction dldAllAction;
	private HomeDirAction homeDirAction;
	private ParentDirAction parentDirAction;
	private JMenuItem parentMenuIt;
	private PreviousDirAction prevDirAction;
	private NextDirAction nextDirAction;

	private LinkedList<String> historyList;
	/**
	 * Position du dossier courant dans la liste de l'historique
	 */
	private int historyPos;
	private JMenuItem homeMenuIt;
	private JMenuItem nextDirMenuIt;
	private JMenuItem prevDirMenuIt;
	private Action refreshAction;
	
	/**
	 * Actualise l'affichage du dossier courant.
	 * 
	 * @author Kévin Subileau
	 * @since 1.0.2
	 */
	private class RefreshAction extends AbstractAction {

		private static final long serialVersionUID = -2395918860597268331L;

		/**
		 * Construit une nouvelle action RefreshAction
		 */
		public RefreshAction() {
			putValue(Action.SHORT_DESCRIPTION, "Actualiser");
			putValue(Action.NAME, "Actualiser");
			ImageIcon icon = loadIcon("refresh.png");
			putValue(Action.LARGE_ICON_KEY, icon);
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
					KeyEvent.VK_F5, 0));
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			changeDirectory(".");
		}
		
	}
	
	/**
	 * Navigue vers le dossier racine.
	 * 
	 * @author Kévin Subileau
	 * @since 1.0.2
	 */
	private class HomeDirAction extends AbstractAction {
		
		private static final long serialVersionUID = -2395918860597268331L;
		
		/**
		 * Construit une nouvelle action HomeDirAction
		 */
		public HomeDirAction() {
			putValue(Action.SHORT_DESCRIPTION, "Dossier racine");
			putValue(Action.NAME, "Dossier racine");
			ImageIcon icon = loadIcon("home.png");
			putValue(Action.LARGE_ICON_KEY, icon);
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
					java.awt.event.KeyEvent.VK_HOME,
					ActionEvent.ALT_MASK
			));
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			changeDirectory("/");
		}
		
	}
	
	/**
	 * Navigue vers le dossier parent.
	 * 
	 * @author Kévin Subileau
	 * @since 1.0.2
	 */
	private class ParentDirAction extends AbstractAction {
		
		private static final long serialVersionUID = 113366319192328568L;

		/**
		 * Construit une nouvelle action ParentDirAction
		 */
		public ParentDirAction() {
			putValue(Action.SHORT_DESCRIPTION, "Dossier parent");
			putValue(Action.NAME, "Dossier parent");
			ImageIcon icon = loadIcon("parent.png");
			putValue(Action.LARGE_ICON_KEY, icon);
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
					java.awt.event.KeyEvent.VK_UP,
					ActionEvent.ALT_MASK
			));
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			changeDirectory("..");
		}
		
	}
	
	/**
	 * Retourne au dossier précédent dans l'historique de navigation.
	 * Si l'on est déjà au plus ancien dossier visité, aucune action n'est
	 * effectuée.
	 * 
	 * @author Kévin Subileau
	 * @since 1.0.2
	 */
	private class PreviousDirAction extends AbstractAction {
		
		private static final long serialVersionUID = 193366319192328568L;
		private AbstractAction nextDirAction;

		/**
		 * Construit une nouvelle action PreviousDirAction
		 */
		public PreviousDirAction() {
			putValue(Action.SHORT_DESCRIPTION, "Précédent");
			putValue(Action.NAME, "Précédent");
			ImageIcon icon = loadIcon("previous.png");
			putValue(Action.LARGE_ICON_KEY, icon);
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_P);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
					java.awt.event.KeyEvent.VK_LEFT,
					ActionEvent.ALT_MASK
			));
		}
		
		public void setNextDirAction(AbstractAction nextDirAction) {
			this.nextDirAction = nextDirAction;
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(historyPos <= 0)
				return;

			changeDirectory(historyList.get(historyPos - 1),
					false);
			--historyPos;
			if(nextDirAction != null)
			{
				nextDirAction.setEnabled(true);
				nextDirAction.putValue(Action.SHORT_DESCRIPTION, "Avancer à "
						+ historyList.get(historyPos + 1));
			}
			if (historyPos < 1) {
				setEnabled(false);
				
				putValue(Action.SHORT_DESCRIPTION, "Précédent");
			} else {
				putValue(Action.SHORT_DESCRIPTION, "Retour à "
						+ historyList.get(historyPos - 1));
			}
		}
		
	}
	
	/**
	 * Navigue vers le dossier suivant dans l'historique de navigation.
	 * Si l'on est déjà au dossier le plus récemment visité, aucune action 
	 * n'est effectuée.
	 * 
	 * @author Kévin Subileau
	 * @since 1.0.2
	 */
	private class NextDirAction extends AbstractAction {
		
		private static final long serialVersionUID = 193366319192328568L;
		private AbstractAction prevDirAction;
		
		/**
		 * Construit une nouvelle action NextDirAction
		 */
		public NextDirAction() {
			putValue(Action.SHORT_DESCRIPTION, "Suivant");
			putValue(Action.NAME, "Suivant");
			ImageIcon icon = loadIcon("next.png");
			putValue(Action.LARGE_ICON_KEY, icon);
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
					java.awt.event.KeyEvent.VK_RIGHT,
					ActionEvent.ALT_MASK
			));
		}
		
		public void setPreviousDirAction(AbstractAction prevDirAction) {
			this.prevDirAction = prevDirAction;
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			int historySize = historyList.size();
			if(historyPos >= historySize -1)
				return;

			changeDirectory(historyList.get(historyPos + 1),
					false);
			++historyPos;
			if (historyPos >= historySize - 1) {
				setEnabled(false);
			} else {
				putValue(Action.SHORT_DESCRIPTION, "Avancer à "
						+ historyList.get(historyPos + 1));
			}
			if(prevDirAction != null)
			{
				prevDirAction.setEnabled(true);
				prevDirAction.putValue(Action.SHORT_DESCRIPTION, "Retour à "
						+ historyList.get(historyPos - 1));
			}
		}
		
	}
	
	/**
	 * Demande de téléchargement de un ou plusieurs fichiers
	 * 
	 * @author Kévin Subileau
	 */
	private class DownloadAction extends AbstractAction {

		private static final long serialVersionUID = 2064025451072197209L;
		public static final short ALL = 0;
		public static final short SELECTED = 1;

		public short mode;

		/**
		 * Initialise un nouvel objet DownloadAction.
		 */
		public DownloadAction() {
			this(SELECTED);
		}

		/**
		 * Initialise un nouvel objet DownloadAction
		 * 
		 * @param mode
		 *            Définit la source de la liste des fichiers à téléchargés
		 *            (sélection ou tous les fichiers).
		 */
		public DownloadAction(short mode) {
			super();
			setMode(mode);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Downloader dld = null;
			if (mode == SELECTED) {
				dld = new Downloader(MainFrame.this,
						fileView.getSelectedFiles(), fileChooser);
			} else if (mode == ALL) {
				dld = new Downloader(MainFrame.this,
						entd.getDirectoryContent(), fileChooser);
			}
			try {
				dld.startDownload();
			} catch (ENTUnauthenticatedUserException e1) {
				e1.printStackTrace();
			}
		}

		public void setMode(short mode) {
			switch (mode) {
			case ALL:
				putValue(Action.SHORT_DESCRIPTION, 
						"Télécharger tous les dossiers et fichiers" +
						" du dossier courant");
				putValue(Action.NAME, "Télécharger le dossier courant");
				putValue(Action.LARGE_ICON_KEY, loadIcon("downall.png"));
				putValue(Action.SMALL_ICON, loadIcon("downall16.png"));
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_D);
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
						KeyEvent.VK_T,
						ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK)
				);
				break;
			case SELECTED:
				putValue(Action.SHORT_DESCRIPTION, 
						"Télécharger le(s) dossier(s) et fichier(s) " +
						"sélectionné(s)");
				putValue(Action.NAME, "Télécharger la sélection");
				putValue(Action.LARGE_ICON_KEY, loadIcon("down.png"));
				putValue(Action.SMALL_ICON, loadIcon("down16.png"));
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
						KeyEvent.VK_T, ActionEvent.CTRL_MASK)
				);
				break;
			default:
				return;
			}
			this.mode = mode;
		}

	}

	/**
	 * Quitte le programme.
	 * 
	 * @author Kévin Subileau
	 * @since 1.0.2
	 */
	public static class ExitAction extends AbstractAction {
		private static final long serialVersionUID = 4633538322805800580L;
		//Attention : cette classe est également utilisée par le bouton quitter
		//de la fenêtre de connexion.
		public ExitAction() {
			super();
			putValue(Action.NAME, 
					CoreConfig.getString("LoginFrame.exitLabel"));//$NON-NLS-1$
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_Q);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
					KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}

	/**
	 * Auto-generated main method to display this JFrame
	 */

	public MainFrame() {
		super();
		dldAllAction = new DownloadAction(DownloadAction.ALL);
		dldAction = new DownloadAction();
		refreshAction = new RefreshAction();
		homeDirAction = new HomeDirAction();
		parentDirAction = new ParentDirAction();
		prevDirAction = new PreviousDirAction();
		prevDirAction.setEnabled(false);
		nextDirAction = new NextDirAction();
		nextDirAction.setEnabled(false);
		prevDirAction.setNextDirAction(nextDirAction);
		nextDirAction.setPreviousDirAction(prevDirAction);
		historyList = new LinkedList<String>();
		historyPos = 0;
		initGUI();
		setCoreListener();
		setGuiEventsListener();
	}

	@Override
	public void setVisible(boolean arg0) throws ENTUnauthenticatedUserException {
		if (arg0 && entd.getLogin() == null)
			throw new ENTUnauthenticatedUserException(
					ENTUnauthenticatedUserException.UNAUTHENTICATED);
		super.setVisible(arg0);
	}

	private void initGUI() {
		try {
			GridBagLayout thisLayout = new GridBagLayout();
			getContentPane().setLayout(thisLayout);
			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			this.setTitle("ENTDownloader");
			this.setIconImage(GuiMain.getAppIcon());
			getContentPane().setBackground(new java.awt.Color(240, 240, 240));
			this.setJMenuBar(jMenuBar);
			this.setMinimumSize(new Dimension(500, 350));
			affichGroup = new ButtonGroup();
			{
				statusBar = new JStatusBar();
				GridBagLayout statusBarLayout = new GridBagLayout();
				statusBarLayout.rowWeights = new double[] { 0.1 };
				statusBarLayout.rowHeights = new int[] { 7 };
				statusBarLayout.columnWeights = new double[] { 1.0, 0.0, 0.0 };
				statusBarLayout.columnWidths = new int[] { 50, 50, 7 };
				statusBar.setLayout(statusBarLayout);
				statusBar.setBackground(new java.awt.Color(240, 240, 240));
				statusBar.setMaximumSize(new Dimension(2147483647, 46));
				{
					statusInfo = new JLabel();
					statusBar.add(statusInfo, new GridBagConstraints(0, 0, 1,
							1, 0.0, 0.0, GridBagConstraints.WEST,
							GridBagConstraints.VERTICAL,
							new Insets(0, 5, 0, 0), 0, 0));
					statusInfo.setText("Initialisation");
				}
				{
					jSeparator3 = new JSeparator();
					statusBar.add(jSeparator3, new GridBagConstraints(1, 0, 1,
							1, 0.0, 0.0, GridBagConstraints.WEST,
							GridBagConstraints.VERTICAL,
							new Insets(3, 0, 3, 0), 0, 0));
					jSeparator3.setOrientation(SwingConstants.VERTICAL);
					jSeparator3.setVisible(false);
				}
				{
					storageInfosLabel = new JLabel();
					statusBar.add(storageInfosLabel, new GridBagConstraints(1,
							0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
							GridBagConstraints.VERTICAL,
							new Insets(0, 10, 0, 3), 0, 0));
					storageInfosLabel.setText("Espace disque utilisé :");
					storageInfosLabel.setVisible(false);
				}
				{
					usedSpaceProgress = new JProgressBar();
					statusBar.add(usedSpaceProgress, new GridBagConstraints(2,
							0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
							GridBagConstraints.HORIZONTAL, new Insets(0, 3, 0,
									10), 0, 0));
					usedSpaceProgress.setPreferredSize(new Dimension(162, 23));
					usedSpaceProgress.setMinimumSize(new Dimension(162, 19));
					usedSpaceProgress.setStringPainted(true);
					usedSpaceProgress.setVisible(false);
				}
				{
					userNameLabel = new JLabel();
					userNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
					statusBar.add(userNameLabel, new GridBagConstraints(1, 0,
							2, 1, 0.0, 0.0, GridBagConstraints.EAST,
							GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0,
									6), 0, 0));
				}
			}
			{
				adressBar = new JToolBar();
				adressBar.setFloatable(false);
				adressBar.setSize(810, 20);
				adressBar.setMargin(new Insets(0, 5, 0, 5));
				GridBagLayout adressBarLayout = new GridBagLayout();
				adressBarLayout.rowWeights = new double[] { 1.0 };
				adressBarLayout.rowHeights = new int[] { 20 };
				adressBarLayout.columnWeights = new double[] { 0.0, 0.0, 0.0,
						0.0, 1.0, 0.0, 0.0, 0.0 };
				adressBarLayout.columnWidths = new int[] { 30, 30, 30, 30, 30,
						30, 30, 30 };
				adressBar.setLayout(adressBarLayout);
				{
					prevBtn = new JButton();
					prevBtn.setMinimumSize(new Dimension(24, 24));
					prevBtn.setPreferredSize(new Dimension(21, 21));
					prevBtn.setMaximumSize(new Dimension(24, 24));
					prevBtn.setFocusable(false);
					prevBtn.setMargin(new Insets(0, 0, 0, 0));
					prevBtn.setAction(prevDirAction);
					prevBtn.setHideActionText(true);
					adressBar.add(prevBtn, new GridBagConstraints(0, 0, 1, 1,
							0.0, 0.0, GridBagConstraints.CENTER,
							GridBagConstraints.NONE, new Insets(0, 1, 0, 0), 0,
							0));
				}
				{
					nextBtn = new JButton();
					nextBtn.setMargin(new Insets(0, 0, 0, 0));
					nextBtn.setMinimumSize(new Dimension(24, 24));
					nextBtn.setPreferredSize(new Dimension(24, 24));
					nextBtn.setMaximumSize(new Dimension(24, 24));
					nextBtn.setFocusable(false);
					nextBtn.setAction(nextDirAction);
					nextBtn.setHideActionText(true);
					adressBar.add(nextBtn, new GridBagConstraints(1, 0, 1, 1,
							0.0, 0.0, GridBagConstraints.CENTER,
							GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0,
							0));
				}
				{
					homeBtn = new JButton();
					homeBtn.setMargin(new Insets(0, 0, 0, 0));
					homeBtn.setMinimumSize(new Dimension(24, 24));
					homeBtn.setPreferredSize(new Dimension(24, 24));
					homeBtn.setMaximumSize(new Dimension(24, 24));
					homeBtn.setFocusable(false);
					homeBtn.setAction(homeDirAction);
					homeBtn.setHideActionText(true);
					adressBar.add(homeBtn, new GridBagConstraints(2, 0, 1, 1,
							0.0, 0.0, GridBagConstraints.CENTER,
							GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0,
							0));
				}
				{
					parentBtn = new JButton();
					parentBtn.setMargin(new Insets(0, 0, 0, 0));
					parentBtn.setMinimumSize(new Dimension(24, 24));
					parentBtn.setPreferredSize(new Dimension(24, 24));
					parentBtn.setMaximumSize(new Dimension(24, 24));
					parentBtn.setFocusable(false);
					parentBtn.setAction(parentDirAction);
					parentBtn.setHideActionText(true);
					adressBar.add(parentBtn, new GridBagConstraints(3, 0, 1, 1,
							0.0, 0.0, GridBagConstraints.CENTER,
							GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0,
							0));
				}
				{
					adressField = new JTextField();
					adressBar.add(adressField, new GridBagConstraints(4, 0, 1,
							1, 0.0, 0.0, GridBagConstraints.CENTER,
							GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0,
									0), 0, 0));
					adressField.addKeyListener(new KeyAdapter() {
						@Override
						public void keyReleased(KeyEvent e) {
							if (e.getKeyCode() == KeyEvent.VK_ENTER
									&& !adressField.getText().isEmpty()) {
								changeDirectory(adressField.getText());
							}
						}
					});
				}
				{
					refreshBtn = new JButton();
					refreshBtn.setMargin(new Insets(0, 0, 0, 0));
					refreshBtn.setMinimumSize(new Dimension(24, 24));
					refreshBtn.setPreferredSize(new Dimension(24, 24));
					refreshBtn.setMaximumSize(new Dimension(24, 24));
					refreshBtn.setFocusable(false);
					refreshBtn.setAction(refreshAction);
					refreshBtn.setHideActionText(true);
					adressBar.add(refreshBtn, new GridBagConstraints(5, 0, 1,
							1, 0.0, 0.0, GridBagConstraints.CENTER,
							GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0,
							0));
				}

				{
					listViewBtn = new JToggleButton();
					adressBar.add(listViewBtn, new GridBagConstraints(6, 0, 1,
							1, 0.0, 0.0, GridBagConstraints.CENTER,
							GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0,
							0));
					setIcon(listViewBtn, "listview.png");
					listViewBtn.setMargin(new Insets(0, 0, 0, 0));
					listViewBtn.setToolTipText("Vue liste");
					listViewBtn.setMinimumSize(new Dimension(24, 24));
					listViewBtn
							.setPreferredSize(new Dimension(24, 24));
					listViewBtn.setMaximumSize(new Dimension(24, 24));
					listViewBtn.setFocusable(false);
					listViewBtn.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							setFileView(BriefView.class);
						}
					});
				}

				{
					detailsViewBtn = new JToggleButton();
					adressBar.add(detailsViewBtn, new GridBagConstraints(7, 0,
							1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
							GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0,
							0));
					setIcon(detailsViewBtn, "detailsview.png");
					detailsViewBtn.setMargin(new Insets(0, 0, 0, 0));
					detailsViewBtn.setToolTipText("Vue détails");
					detailsViewBtn
							.setMinimumSize(new Dimension(24, 24));
					detailsViewBtn.setPreferredSize(new Dimension(24,
							24));
					detailsViewBtn
							.setMaximumSize(new Dimension(24, 24));
					detailsViewBtn.setFocusable(false);
					detailsViewBtn.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							setFileView(DetailView.class);
						}
					});
				}

			}
			{
				jMenuBar = new JMenuBar();
				setJMenuBar(jMenuBar);
				{
					fileMenu = new JMenu();
					jMenuBar.add(fileMenu);
					fileMenu.setText("Fichier");
					fileMenu.setMnemonic(KeyEvent.VK_F);
					{
						refreshItem = new JMenuItem();
						fileMenu.add(refreshItem);
						refreshItem.setAction(refreshAction);
					}
					{
						dld = new JMenuItem();
						dld.setAction(dldAction);
						fileMenu.add(dld);
					}
					{
						dldAll = new JMenuItem();
						dldAll.setAction(dldAllAction);
						fileMenu.add(dldAll);
					}
					{
						jSeparator1 = new JSeparator();
						fileMenu.add(jSeparator1);
					}
					{
						exit = new JMenuItem();
						fileMenu.add(exit);
						exit.setAction(new ExitAction());
					}
				}
				{
					navigationMenu = new JMenu();
					jMenuBar.add(navigationMenu);
					navigationMenu.setText("Navigation");
					navigationMenu.setMnemonic(KeyEvent.VK_N);
					{
						homeMenuIt = new JMenuItem();
						homeMenuIt.setAction(homeDirAction);
						navigationMenu.add(homeMenuIt);
					}
					{
						parentMenuIt = new JMenuItem();
						parentMenuIt.setAction(parentDirAction);
						navigationMenu.add(parentMenuIt);
					}
					{
						prevDirMenuIt = new JMenuItem();
						prevDirMenuIt.setAction(prevDirAction);
						navigationMenu.add(prevDirMenuIt);
					}
					{
						nextDirMenuIt = new JMenuItem();
						nextDirMenuIt.setAction(nextDirAction);
						navigationMenu.add(nextDirMenuIt);
					}
				}
				{
					affichMenu = new JMenu();
					jMenuBar.add(affichMenu);
					affichMenu.setText("Affichage");
					affichMenu.setMnemonic(KeyEvent.VK_A);
					{
						ListItem = new JRadioButtonMenuItem();
						affichMenu.add(ListItem);
						setIcon(ListItem, "listview.png");
						ListItem.setText("Liste");
						ListItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								setFileView(BriefView.class);
							}
						});
						affichGroup.add(ListItem);
					}
					{
						DetailItem = new JRadioButtonMenuItem();
						affichMenu.add(DetailItem);
						setIcon(DetailItem, "detailsview.png");
						DetailItem.setText("Détails");
						DetailItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								setFileView(DetailView.class);
							}
						});
						affichGroup.add(DetailItem);
					}
				}
				{
					help = new JMenu();
					jMenuBar.add(help);
					help.setText("Aide");
					help.setMnemonic(KeyEvent.VK_I);
					{
						onlineHelp = new JMenuItem();
						help.add(onlineHelp);
						onlineHelp.setText("Aide en ligne...");
						onlineHelp.setMnemonic(KeyEvent.VK_I);
						onlineHelp.setAccelerator(KeyStroke.getKeyStroke(
								KeyEvent.VK_F1, 0));
						onlineHelp.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								try {
									Misc.browse(CoreConfig
											.getString("ProductInfo.manpage")
											.replaceAll("\\{version\\}",
												java.net.URLEncoder.encode(
													CoreConfig.getString("ProductInfo.version"),
													"UTF-8")
												)
											);
								} catch (UnsupportedEncodingException e1) {
									// TODO Auto-generated catch block
								}
							}
						});
					}
					{
						website = new JMenuItem();
						help.add(website);
						website.setText("Site Web");
						website.setMnemonic(KeyEvent.VK_W);
						website.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								Misc.browse(CoreConfig
										.getString("ProductInfo.website"));
							}
						});
					}
					{
						checkUpdate = new JMenuItem();
						help.add(checkUpdate);
						checkUpdate.setText("Rechercher des mises à jour...");
						checkUpdate.setMnemonic(KeyEvent.VK_M);
						checkUpdate.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								//Lancement de l'updater dans un thread séparé.
								new Thread(new Runnable() {
									@Override
									public void run() {
										try {
											new UpdaterGui(MainFrame.this, true);
										} catch (final Exception e) {
											SwingUtilities
											.invokeLater(new Runnable() {
												@Override
												public void run() {
													JOptionPane
													.showMessageDialog(
															MainFrame.this,
															"<html>Les informations de mise à jour n'ont pas pu être obtenues à cause de l'erreur suivante : <br><b>"
															+ e.toString()
															+ "</b></html>",
															"ENTDownloader - Erreur",
															JOptionPane.ERROR_MESSAGE);
												}
											});
										}
									}
								}, "Updater").start();
							}
						});
					}
					{
						jSeparator2 = new JSeparator();
						help.add(jSeparator2);
					}

					{
						about = new JMenuItem();
						help.add(about);
						about.setText("A propos");
						about.setMnemonic(KeyEvent.VK_P);
						about.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								new AboutBox(MainFrame.this);
							}
						});
						setIcon(about, "info.png");
					}
				}
			}
			{
				toolBar = new JToolBar();
				getContentPane().add(
						toolBar,
						new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.HORIZONTAL, new Insets(0, 0,
										0, 0), 0, 0));
				getContentPane().add(
						adressBar,
						new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.HORIZONTAL, new Insets(0, 0,
										0, 0), 0, 0));
				adressBar.setPreferredSize(new Dimension(810, 30));
				adressBar.setMinimumSize(new Dimension(15, 30));
				getContentPane().add(
						statusBar,
						new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
								GridBagConstraints.SOUTHWEST,
								GridBagConstraints.HORIZONTAL, new Insets(0, 0,
										0, 0), 0, 0));
				statusBar.setPreferredSize(new Dimension(810, 25));
				statusBar.setMinimumSize(new Dimension(0, 25));
				{
					setFileView(BriefView.class);
					getContentPane().add(
							fileView,
							new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
									GridBagConstraints.CENTER,
									GridBagConstraints.BOTH, new Insets(0, 0,
											0, 0), 0, 0));
				}
				toolBar.setFloatable(false);
				toolBar.setBackground(new java.awt.Color(240, 240, 240));
				toolBar.setForeground(new java.awt.Color(0, 0, 0));
				toolBar.setSize(810, 54);
				toolBar.setPreferredSize(new Dimension(810, 50));
				toolBar.setMinimumSize(new Dimension(15, 50));
				{
					Download_tool = makeToolbarButton(dldAction);
					DownloadAll_tool = makeToolbarButton(dldAllAction);
					toolBar.add(Download_tool);
					toolBar.add(DownloadAll_tool);
				}
			}
			thisLayout.rowWeights = new double[] { 0.0, 0.0, 1.0, 0.0 };
			thisLayout.rowHeights = new int[] { 7, 7, 7, 7 };
			thisLayout.columnWeights = new double[] { 1.0 };
			thisLayout.columnWidths = new int[] { 7 };
			{
				PopupMenu = new JPopupMenu();
				{
					jMenuItem1 = new JMenuItem();
					jMenuItem1.setAction(dldAction);
					PopupMenu.add(jMenuItem1);
				}
				{
					jMenuItem2 = new JMenuItem();
					jMenuItem2.setAction(dldAllAction);
					PopupMenu.add(jMenuItem2);
				}
			}
			{
				fileChooser = new JFileChooser();
			}

			pack();
			this.setSize(826, 449);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void changeDirectory(final String path) {
		changeDirectory(path, true);
	}

	protected synchronized void changeDirectory(final String path,
			final boolean addHistory) {
		new SwingWorker<Void, Void>() {
			@Override
			public Void doInBackground() {
				String prevDir = entd.getDirectoryPath();
				try {
					entd.changeDirectory(path);
					if (addHistory) {
						historyPush(entd.getDirectoryPath());
					}
				} catch (ENTUnauthenticatedUserException e1) {
					// TODO Gestion Utilisateur non connecté
					e1.printStackTrace();
				} catch (ENTDirectoryNotFoundException e1) {
					statusInfo.setText(dirInfos());
					JOptionPane
							.showMessageDialog(
									MainFrame.this,
									"ENTDownloader ne parvient pas à trouver \""
											+ path
											+ "\". Vérifiez l'orthographe et réessayez.",
									"ENTDownloader", JOptionPane.ERROR_MESSAGE);
					adressField.setText(prevDir);
					return null;
				} catch (ENTInvalidFS_ElementTypeException e1) {
					statusInfo.setText(dirInfos());
					JOptionPane.showMessageDialog(MainFrame.this, "\"" + path
							+ "\" n'est pas un dossier.", "ENTDownloader",
							JOptionPane.ERROR_MESSAGE);
					adressField.setText(prevDir);
					return null;
				} catch (IOException e1) {
					statusInfo.setText("Hors ligne");
					JOptionPane
							.showMessageDialog(
									MainFrame.this,
									"Votre connexion Internet semble rencontrer un problème.\nAssurez-vous que votre ordinateur est connecté à Internet et que votre pare-feu est correctement configuré.",
									"ENTDownloader - Service indisponible",
									JOptionPane.ERROR_MESSAGE);
					adressField.setText(prevDir);
					return null;
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				return null;
			}
		}.execute();
	}

	protected JButton makeToolbarButton(String imageName,
			ActionListener listener, String actionCommand, String toolTipText,
			String altText) {
		//Look for the image.
		ImageIcon icon = loadIcon(imageName, altText);

		//Create and initialize the button.
		JButton button = new JButton();
		button.setPreferredSize(new Dimension(50, 50));
		button.setSize(50, 50);
		button.setFocusable(false);
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.addActionListener(listener);

		if (icon != null) {
			button.setIcon(icon);
		} else {
			button.setText(altText);
		}

		return button;
	}

	protected JButton makeToolbarButton(Action action) {
		//Create and initialize the button.
		JButton button = new JButton(action);
		button.setPreferredSize(new Dimension(50, 50));
		button.setSize(50, 50);
		button.setFocusable(false);
		button.setHideActionText(true);
		return button;
	}

	protected ImageIcon loadIcon(String imageName, String altText) {
		String imgLocation = "net/sf/entDownloader/ressources/" + imageName;
		URL imageURL = getClass().getClassLoader().getResource(imgLocation);
		if (imageURL != null) //image found
			return new ImageIcon(imageURL, altText);
		else { //no image found
			System.err.println("Resource not found: " + imgLocation);
			return null;
		}
	}

	protected ImageIcon loadIcon(String imageName) {
		return loadIcon(imageName, null);
	}

	protected void setIcon(AbstractButton btn, String imageName) {
		ImageIcon icon = loadIcon(imageName);
		if (icon != null) {
			btn.setIcon(icon);
		}
	}

	private String dirInfos() {
		return entd.getNbDossiers() + " Dossier(s), " + entd.getNbFiles()
				+ " Fichier(s)";
	}

	private void setCoreListener() {
		Broadcaster.addDirectoryChangedListener(this);
		Broadcaster
				.addDirectoryChangingListener(new DirectoryChangingListener() {
					@Override
					public void onDirectoryChanging(DirectoryChangingEvent event) {
						statusInfo.setText("Chargement en cours...");
					}
				});
	}

	private void setGuiEventsListener() {
		GuiBroadcaster
				.addDoubleClickOnRowListener(new DoubleClickOnRowListener() {
					@Override
					public void onDoubleClickOnRow(DoubleClickOnRowEvent event) {
						FS_Element target = event.getTarget();
						if (target.isDirectory()) {
							changeDirectory(adressField.getText() + "/"
									+ target.getName());
						} else {
							Downloader dld = new Downloader(MainFrame.this,
									target, fileChooser);
							try {
								dld.startDownload();
							} catch (ENTUnauthenticatedUserException e) {
								// TODO Gestion exception ENTUnauthenticatedUserException
								e.printStackTrace();
							}
						}
					}
				});
	}

	private void setUsedSpace() {
		int usedSpace = entd.getUsedSpace(), capacity = entd.getCapacity();
		if (usedSpace >= 0 && capacity >= 0) {
			int value = (int) (usedSpace * 100f / capacity);
			usedSpaceProgress.setValue(value);
			usedSpaceProgress.setString(value + " % (" + usedSpace + " Mo sur "
					+ capacity + " Mo)");

			usedSpaceProgress.setVisible(true);
			storageInfosLabel.setVisible(true);
			jSeparator3.setVisible(true);
			userNameLabel.setVisible(false);
		} else {
			usedSpaceProgress.setVisible(false);
			storageInfosLabel.setVisible(false);
			jSeparator3.setVisible(false);
			userNameLabel.setVisible(true);
		}
	}

	protected void setFileView(Class<? extends ListView> view) {
		if (fileView != null && fileView.getClass() == view)
			return;
		if (fileView != null) {
			getContentPane().remove(fileView);
		}
		try {
			fileView = view.newInstance();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			fileView.browseDirectory(entd.getDirectoryContent());
		} catch (IllegalStateException e) {
		}
		
		fileView.getViewInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
				put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0),"enterPressedAction");
		
		fileView.getViewInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
				put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE,0),"prevDirAction");

		fileView.getViewActionMap().put("prevDirAction", prevDirAction);
		fileView.getViewActionMap().put("enterPressedAction", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				switch(fileView.getSelectedFilesCount()) {
				case 0:
					break;
				case 1:
					GuiBroadcaster
					.fireDoubleClickOnRow(new DoubleClickOnRowEvent(
							(FS_Element) fileView.getSelectedFile()));
					break;
				default:
					dldAction.actionPerformed(null);
					break;
				}
			}
		});
		
		if (BriefView.class == view) {
			listViewBtn.setSelected(true);
			detailsViewBtn.setSelected(false);
			ListItem.setSelected(true);
			DetailItem.setSelected(false);
		} else if (DetailView.class == view) {
			listViewBtn.setSelected(false);
			detailsViewBtn.setSelected(true);
			ListItem.setSelected(false);
			DetailItem.setSelected(true);
		}

		getContentPane().add(
				fileView,
				new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 0), 0, 0));
		getContentPane().validate();
		fileView.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return;
				dldAction.setEnabled(!fileView.getSelectionModel()
						.isSelectionEmpty());
			}
		});
		dldAction.setEnabled(!fileView.getSelectionModel().isSelectionEmpty());
	}

	public JPopupMenu getPopupMenu() {
		return PopupMenu;
	}

	@Override
	public void onDirectoryChanged(DirectoryChangedEvent event) {
		adressField.setText(entd.getDirectoryPath());
		parentDirAction.setEnabled(!adressField.getText().equals("/"));
		statusInfo.setText(dirInfos());
		setUsedSpace();
		userNameLabel.setText(entd.getUsername() + " (" + entd.getLogin() + ")");
		fileView.browseDirectory(entd.getDirectoryContent());
	}

	/**
	 * Ajoute le chemin <code>path</code> à l'historique de navigation, et
	 * définit le curseur de
	 * position dans l'historique à la fin de celui-ci. Désactive par conséquent
	 * le bouton suivant.
	 * Doit généralement être appelé suite à un changement de dossier initié par
	 * l'utilisateur
	 * 
	 * @param path
	 *            Le chemin à ajouter à l'historique
	 */
	private void historyPush(String path) {
		if (path.isEmpty()
				|| path.equals(".")
				|| (!historyList.isEmpty() && historyList.getLast()
						.equals(path)))
			return;
		int histSize = historyList.size();
		try {
			historyList.set(historyPos + 1, path);
			for (int i = histSize - 1; i > historyPos + 1; i--) {
				historyList.removeLast();
			}
		} catch (IndexOutOfBoundsException e) {
			if (historyPos >= -1) {
				historyList.add(path);
			} else
				throw e;
		}
		histSize = historyList.size();
		while (histSize > 10) {
			historyList.remove();
			--histSize;
		}
		historyPos = histSize - 1;
		nextDirAction.setEnabled(false);
		nextDirAction.putValue(Action.SHORT_DESCRIPTION, "Suivant");
		if (histSize > 1) {
			prevDirAction.setEnabled(true);
			prevDirAction.putValue(Action.SHORT_DESCRIPTION, "Retour à "
					+ historyList.get(historyPos - 1));
		}
	}
}