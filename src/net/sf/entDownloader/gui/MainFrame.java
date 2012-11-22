/*
 *  MainFrame.java
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
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
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
import net.sf.entDownloader.core.events.DirectoryCreatedEvent;
import net.sf.entDownloader.core.events.DirectoryCreatedListener;
import net.sf.entDownloader.core.events.ElementRenamedEvent;
import net.sf.entDownloader.core.events.ElementRenamedListener;
import net.sf.entDownloader.core.events.ElementsDeletedEvent;
import net.sf.entDownloader.core.events.ElementsDeletedListener;
import net.sf.entDownloader.core.exceptions.ENTElementNotFoundException;
import net.sf.entDownloader.core.exceptions.ENTInvalidElementNameException;
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
	private ENTDownloader entd = null;

	private JMenuItem dld;
	private JMenu navigationMenu;
	private JMenuItem onlineHelp;
	private JMenuItem dldAll;
	private JMenuItem website;
	private JMenuItem fbpage;
	private JMenuItem checkUpdate;
	private JToggleButton detailsViewBtn;
	private JToggleButton listViewBtn;
	private JLabel userNameLabel;
	private ButtonGroup affichGroup;
	private JRadioButtonMenuItem DetailItem;
	private JRadioButtonMenuItem ListItem;
	private JMenuItem zoom;
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
	private JLayeredPane browserLayeredPane;
	private JLabel emptyDirLabel;
	private JButton Download_tool;
	private JToolBar toolBar;
	private JMenuItem jMenuItem2;
	private JMenuItem jMenuItem1;
	private JPopupMenu PopupMenu;
	private JFileChooser fileChooser;
	private JMenuItem about;
	private JSeparator jSeparator2;
	private JMenu help;
	private JMenuItem exit;
	private JMenuItem createDir;
	private JMenuItem rename;
	private JMenuItem cut;
	private JMenuItem copy;
	private JMenuItem paste;
	private JMenuItem delete;
	private JMenuItem send;
	private JMenu fileMenu;
	private JMenuBar jMenuBar;
	private JButton DownloadAll_tool;
	private JButton createDir_tool;
	private JButton rename_tool;
	private JButton cut_tool;
	private JButton copy_tool;
	private JButton paste_tool;
	private JButton delete_tool;
	private JButton send_tool;
	private JStatusBar statusBar;
	private JButton refreshBtn;
	private JButton goDirBtn;
	private JButton resetPathBtn;
	private JMenuItem refreshItem;
	private OpenSelectedDirectoryAction openDirAction;
	private JMenuItem openDir;
	private DownloadAction dldAction;
	private DownloadAction dldAllAction;
	private UploadAction sendAction;
	private HomeDirAction homeDirAction;
	private ParentDirAction parentDirAction;
	private CreateDirAction createDirAction;
	private RenameAction renameAction;
	private CutAction cutAction;
	private CopyAction copyAction;
	private PasteAction pasteAction;
	private JMenuItem parentMenuIt;
	private PreviousDirAction prevDirAction;
	private NextDirAction nextDirAction;
	private CopyFilenameAction copyFilenameAction;
	private DeleteAction deleteAction;

	private LinkedList<String> historyList;
	/**
	 * Position du dossier courant dans la liste de l'historique
	 */
	private int historyPos;
	private JMenuItem homeMenuIt;
	private JMenuItem nextDirMenuIt;
	private JMenuItem prevDirMenuIt;
	private JMenuItem createDirPopupIt;
	private JMenuItem renamePopupIt;
	private JMenuItem copyPopupIt;
	private JMenuItem cutPopupIt;
	private JMenuItem pastePopupIt;
	private JMenuItem deletePopupIt;
	private JMenuItem sendPopupIt;
	private Action refreshAction;
	private JMenuItem jMenuItem3;
	private JMenuItem copyFilename;
	private JMenuItem openDirPopupIt;

	/**
	 * Navigue vers le dossier sélectionné.
	 * 
	 * @author Kévin Subileau
	 * @since 1.2.0
	 */
	private class OpenSelectedDirectoryAction extends AbstractAction {

		private static final long serialVersionUID = -2319890913151457469L;

		/**
		 * Construit une nouvelle action OpenSelectedDirectoryAction
		 */
		public OpenSelectedDirectoryAction() {
			putValue(Action.SHORT_DESCRIPTION,
					"Naviguer vers le dossier sélectionné.");
			putValue(Action.NAME, "Ouvrir");
			ImageIcon icon = Misc.loadIcon("folder-open.png");
			putValue(Action.LARGE_ICON_KEY, icon);
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
					java.awt.event.KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (fileView == null || fileView.getSelectedFilesCount() != 1)
				return;
			FS_Element selectedFile = fileView.getSelectedFile();
			if (!selectedFile.isDirectory())
				return;
			changeDirectory(entd.getDirectoryPath() + "/"
					+ selectedFile.getName());
		}

	}

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
			ImageIcon icon = Misc.loadIcon("refresh.png");
			putValue(Action.LARGE_ICON_KEY, icon);
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
			putValue(Action.ACCELERATOR_KEY,
					KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
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
			ImageIcon icon = Misc.loadIcon("home.png");
			putValue(Action.LARGE_ICON_KEY, icon);
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
					java.awt.event.KeyEvent.VK_HOME, ActionEvent.ALT_MASK));
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
			ImageIcon icon = Misc.loadIcon("folder-parent.png");
			putValue(Action.LARGE_ICON_KEY, icon);
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
					java.awt.event.KeyEvent.VK_UP, ActionEvent.ALT_MASK));
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
			ImageIcon icon = Misc.loadIcon("previous.png");
			putValue(Action.LARGE_ICON_KEY, icon);
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_P);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
					java.awt.event.KeyEvent.VK_LEFT, ActionEvent.ALT_MASK));
		}

		public void setNextDirAction(AbstractAction nextDirAction) {
			this.nextDirAction = nextDirAction;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (historyPos <= 0)
				return;

			changeDirectory(historyList.get(historyPos - 1), false);
			--historyPos;
			if (nextDirAction != null) {
				nextDirAction.setEnabled(true);
				nextDirAction.putValue(Action.SHORT_DESCRIPTION, "Avancer à "
						+ historyList.get(historyPos + 1));
			}
			if (historyPos < 1) {
				setEnabled(false);

				putValue(Action.SHORT_DESCRIPTION, "Précédent");
			} else {
				putValue(Action.SHORT_DESCRIPTION,
						"Retour à " + historyList.get(historyPos - 1));
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
			ImageIcon icon = Misc.loadIcon("next.png");
			putValue(Action.LARGE_ICON_KEY, icon);
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
					java.awt.event.KeyEvent.VK_RIGHT, ActionEvent.ALT_MASK));
		}

		public void setPreviousDirAction(AbstractAction prevDirAction) {
			this.prevDirAction = prevDirAction;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			int historySize = historyList.size();
			if (historyPos >= historySize - 1)
				return;

			changeDirectory(historyList.get(historyPos + 1), false);
			++historyPos;
			if (historyPos >= historySize - 1) {
				setEnabled(false);
			} else {
				putValue(Action.SHORT_DESCRIPTION,
						"Avancer à " + historyList.get(historyPos + 1));
			}
			if (prevDirAction != null) {
				prevDirAction.setEnabled(true);
				prevDirAction.putValue(Action.SHORT_DESCRIPTION, "Retour à "
						+ historyList.get(historyPos - 1));
			}
		}

	}

	/**
	 * Créé un nouveau dossier dans le dossier courant
	 * 
	 * @author Kévin Subileau
	 * @since 2.0.0
	 */
	private class CreateDirAction extends AbstractAction {

		private static final long serialVersionUID = 193366319192328568L;

		/**
		 * Construit une nouvelle action CreateDirAction
		 */
		public CreateDirAction() {
			putValue(Action.SHORT_DESCRIPTION, "Créer un nouveau dossier");
			putValue(Action.NAME, "Nouveau dossier");
			putValue(Action.LARGE_ICON_KEY, Misc.loadIcon("folder-new.png"));
			putValue(Action.SMALL_ICON, Misc.loadIcon("folder-new16.png"));
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_V);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
					java.awt.event.KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String dirname = (String)JOptionPane.showInputDialog(
                    MainFrame.this,
                    "Nom du nouveau dossier :",
                    "ENTDownloader - Nouveau dossier",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    null,
                    "Nouveau dossier");

			if ((dirname == null) || (dirname.length() == 0)) {
			    return;
			}

			try {
				entd.createDirectory(dirname);
			} catch (ENTInvalidElementNameException e) {
				String message;
				switch (e.getType()) {
				case ENTInvalidElementNameException.ALREADY_USED:
					message = "Un fichier ou dossier porte le même nom.";
					break;
				case ENTInvalidElementNameException.FORBIDDEN_CHAR:
					message = "Le nom spécifié contient un ou plusieurs caractères non autorisés.";
					break;
				default:
					message = "Erreur inconnue.";
					break;
				}
				JOptionPane
						.showMessageDialog(
								MainFrame.this,
								"Impossible de créer le dossier \""
										+ dirname
										+ "\" : " + message,
								"ENTDownloader", JOptionPane.ERROR_MESSAGE);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * Renomme un élément du dossier courant
	 *
	 * @author Kévin Subileau
	 * @since 2.0.0
	 */
	private class RenameAction extends AbstractAction {

		private static final long serialVersionUID = 193947319198628518L;

		/**
		 * Construit une nouvelle action RenameAction
		 */
		public RenameAction() {
			putValue(Action.SHORT_DESCRIPTION, "Renommer l'élément sélectionné");
			putValue(Action.NAME, "Renommer");
			putValue(Action.LARGE_ICON_KEY, Misc.loadIcon("rename.png"));
			putValue(Action.SMALL_ICON, Misc.loadIcon("rename16.png"));
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_M);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
					java.awt.event.KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (fileView == null || fileView.getSelectedFilesCount() != 1)
				return;
			FS_Element selectedFile = fileView.getSelectedFile();

			String newname = (String)JOptionPane.showInputDialog(
                    MainFrame.this,
                    "Nouveau nom :",
                    "ENTDownloader - Renommer",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    null,
                    selectedFile.getName());

			if ((newname == null) || (newname.length() == 0)) {
			    return;
			}

			try {
				entd.rename(selectedFile.getName(), newname);
			} catch (ENTInvalidElementNameException e) {
				String message;
				switch (e.getType()) {
				case ENTInvalidElementNameException.ALREADY_USED:
					message = "Un fichier ou dossier porte le même nom.";
					break;
				case ENTInvalidElementNameException.FORBIDDEN_CHAR:
					message = "Le nouveau nom spécifié contient un ou plusieurs caractères non autorisés.";
					break;
				default:
					message = "Erreur inconnue.";
					break;
				}
				JOptionPane
						.showMessageDialog(
								MainFrame.this,
								"Impossible de renommer \""
										+ selectedFile.getName()
										+ "\" : " + message,
								"ENTDownloader", JOptionPane.ERROR_MESSAGE);
			} catch (ENTElementNotFoundException e) {
				JOptionPane
						.showMessageDialog(
								MainFrame.this,
								"Impossible de renommer \""
										+ selectedFile.getName()
										+ "\" : l'élément n'existe plus.",
								"ENTDownloader", JOptionPane.ERROR_MESSAGE);
				// Actualiser la liste des fichiers
				changeDirectory(".");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * Marque les éléments sélectionnés pour le déplacement.
	 *
	 * @author Kévin Subileau
	 * @since 2.0.0
	 */
	private class CutAction extends AbstractAction {

		private static final long serialVersionUID = -5274579601506811635L;

		/**
		 * Construit une nouvelle action RenameAction
		 */
		public CutAction() {
			putValue(Action.SHORT_DESCRIPTION, "Coupe l'élément sélectionné");
			putValue(Action.NAME, "Couper");
			putValue(Action.LARGE_ICON_KEY, Misc.loadIcon("cut.png"));
			putValue(Action.SMALL_ICON, Misc.loadIcon("cut16.png"));
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_U);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
					java.awt.event.KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (fileView == null || fileView.getSelectedFilesCount() == 0)
				return;
			FS_Element[] selectedFiles = fileView.getSelectedFiles();
			String[] selectedFileNames = new String[selectedFiles.length];
			int i=0;
			for (FS_Element elem : selectedFiles) {
				selectedFileNames[i] = elem.getName();
			}

			try {
				entd.cut(selectedFileNames);
				String s = selectedFiles.length>1?"s":"";
				setTemporaryStatus("<html><b>"+selectedFiles.length + " élément"+s+" coupé"+s+"</b></html>", 4, dirInfos());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pasteAction.setEnabled(entd.canPaste());
		}

	}

	/**
	 * Marque les éléments sélectionnés pour la copie.
	 *
	 * @author Kévin Subileau
	 * @since 2.0.0
	 */
	private class CopyAction extends AbstractAction {

		private static final long serialVersionUID = -8274519609506811625L;

		/**
		 * Construit une nouvelle action RenameAction
		 */
		public CopyAction() {
			putValue(Action.SHORT_DESCRIPTION, "Copie l'élément sélectionné");
			putValue(Action.NAME, "Copier");
			putValue(Action.LARGE_ICON_KEY, Misc.loadIcon("copy.png"));
			putValue(Action.SMALL_ICON, Misc.loadIcon("copy16.png"));
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
					java.awt.event.KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (fileView == null || fileView.getSelectedFilesCount() == 0)
				return;
			FS_Element[] selectedFiles = fileView.getSelectedFiles();
			try {
				entd.copy(selectedFiles);
				String s = selectedFiles.length>1?"s":"";
				setTemporaryStatus("<html><b>"+selectedFiles.length + " élément"+s+" copié"+s+"</b></html>", 4, dirInfos());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pasteAction.setEnabled(entd.canPaste());
		}

	}

	/**
	 * Colle les éléments précédemment sélectionnés.
	 *
	 * @author Kévin Subileau
	 * @since 2.0.0
	 */
	private class PasteAction extends AbstractAction {

		private static final long serialVersionUID = -8256819600216811625L;

		/**
		 * Construit une nouvelle action RenameAction
		 */
		public PasteAction() {
			putValue(Action.SHORT_DESCRIPTION, "Colle les éléments précédemment sélectionnés.");
			putValue(Action.NAME, "Coller");
			putValue(Action.LARGE_ICON_KEY, Misc.loadIcon("paste.png"));
			putValue(Action.SMALL_ICON, Misc.loadIcon("paste16.png"));
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_P);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
					java.awt.event.KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!entd.canPaste())
				return;

			try {
				if(entd.paste()) {
					updateFrameData();
				}
			} catch (ENTInvalidElementNameException e) {
				JOptionPane
				.showMessageDialog(
						MainFrame.this,
						"Impossible de coller la sélection : un fichier/dossier du même nom existe déjà.",
						"ENTDownloader", JOptionPane.ERROR_MESSAGE);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * Supprime les éléments sélectionnés du dossier courant
	 *
	 * @author Kévin Subileau
	 * @since 2.0.0
	 */
	private class DeleteAction extends AbstractAction {

		private static final long serialVersionUID = 123949354198628688L;

		/**
		 * Construit une nouvelle action DeleteAction
		 */
		public DeleteAction() {
			putValue(Action.SHORT_DESCRIPTION, "Supprimer l'élément sélectionné");
			putValue(Action.NAME, "Supprimer");
			putValue(Action.LARGE_ICON_KEY, Misc.loadIcon("delete.png"));
			putValue(Action.SMALL_ICON, Misc.loadIcon("delete16.png"));
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
					java.awt.event.KeyEvent.VK_DELETE, 0));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			int nbtargets;
			if (fileView == null || (nbtargets = fileView.getSelectedFilesCount()) == 0)
				return;
			FS_Element[] selectedElems = fileView.getSelectedFiles();
			
			String confirmMessage;
			if(nbtargets == 1)
			{
				if(selectedElems[0].isFile())
					confirmMessage = "Voulez-vous vraiment supprimer ce fichier de façon permanente ?";
				else
					confirmMessage = "Voulez-vous vraiment supprimer ce dossier de façon permanente ?";
			}
			else
				confirmMessage = "Voulez-vous vraiment supprimer ces " + nbtargets + " éléments de façon permanente ?";

			if(JOptionPane
					.showConfirmDialog(
							MainFrame.this,
							confirmMessage,
							"ENTDownloader", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
				return;

			try {
				entd.delete(selectedElems);
			} catch (ENTElementNotFoundException e) {
				String message = null;
				if(selectedElems.length > 1 || e.getMessage() == null)
					message = "Impossible de supprimer la sélection : un élément n'existe plus.";
				else
					message = "Impossible de supprimer \"" + e.getMessage() + "\" : l'élément n'existe plus.";
				JOptionPane
						.showMessageDialog(
								MainFrame.this,message,
								"ENTDownloader", JOptionPane.ERROR_MESSAGE);
				// Actualiser la liste des fichiers
				changeDirectory(".");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * Demande l'envoi de un ou plusieurs fichiers
	 * 
	 * @author Kévin Subileau
	 * @since 2.0.0
	 */
	private class UploadAction extends AbstractAction {

		private static final long serialVersionUID = 2064025451072197209L;

		/**
		 * Construit une nouvelle action UploadAction
		 */
		public UploadAction() {
			putValue(Action.SHORT_DESCRIPTION, "Envoyer un ou plusieurs fichiers");
			putValue(Action.NAME, "Envoyer");
			putValue(Action.LARGE_ICON_KEY, Misc.loadIcon("upload.png"));
			putValue(Action.SMALL_ICON, Misc.loadIcon("upload16.png"));
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_E);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
					java.awt.event.KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			new Uploader(MainFrame.this, fileChooser).startUpload();
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
						"Télécharger tous les dossiers et fichiers"
								+ " du dossier courant");
				putValue(Action.NAME, "Télécharger le dossier courant");
				putValue(Action.LARGE_ICON_KEY, Misc.loadIcon("downall.png"));
				putValue(Action.SMALL_ICON, Misc.loadIcon("downall16.png"));
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_D);
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
						KeyEvent.VK_T, ActionEvent.CTRL_MASK
								| ActionEvent.SHIFT_MASK));
				break;
			case SELECTED:
				putValue(Action.SHORT_DESCRIPTION,
						"Télécharger le(s) dossier(s) et fichier(s) "
								+ "sélectionné(s)");
				putValue(Action.NAME, "Télécharger la sélection");
				putValue(Action.LARGE_ICON_KEY, Misc.loadIcon("down.png"));
				putValue(Action.SMALL_ICON, Misc.loadIcon("down16.png"));
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
						KeyEvent.VK_T, ActionEvent.CTRL_MASK));
				break;
			default:
				return;
			}
			this.mode = mode;
		}

	}

	/**
	 * Copie le nom du fichier sélectionné dans le presse-papier.
	 * 
	 * @author Kévin Subileau
	 * @since 1.2.0
	 */
	private class CopyFilenameAction extends AbstractAction {

		private static final long serialVersionUID = -5329890313141857468L;

		/**
		 * Construit une nouvelle action CopyFilenameAction
		 */
		public CopyFilenameAction() {
			putValue(Action.SHORT_DESCRIPTION,
					"Copier le nom du fichier sélectionné dans le presse-papier.");
			putValue(Action.NAME, "Copier le nom du fichier");
			ImageIcon icon = Misc.loadIcon("copyFilename.png");
			putValue(Action.LARGE_ICON_KEY, icon);
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
					java.awt.event.KeyEvent.VK_C, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (fileView == null || fileView.getSelectedFilesCount() != 1)
				return;
			String filename = fileView.getSelectedFile().getName();
			try {
				StringSelection ss = new StringSelection(filename);
				Toolkit.getDefaultToolkit().getSystemClipboard()
						.setContents(ss, null);
			} catch (IllegalStateException e) {
				/** Le presse-papier n'est peut-être pas disponible */
				JOptionPane.showMessageDialog(MainFrame.this, "Impossible de copier le nom du fichier : le presse papier n'est pas disponible.", "ENTDownloader", JOptionPane.ERROR_MESSAGE);
			}
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
			putValue(Action.NAME, "Quitter");
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_Q);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
					KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}

	public MainFrame() {
		super();
		openDirAction = new OpenSelectedDirectoryAction();
		openDirAction.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("enabled")) {
					if (openDirPopupIt != null) {
						openDirPopupIt.setVisible(openDirAction.isEnabled());
					}
					if (openDir != null) {
						openDir.setVisible(openDirAction.isEnabled());
					}
				}
			}
		});
		dldAllAction = new DownloadAction(DownloadAction.ALL);
		dldAction = new DownloadAction();
		sendAction = new UploadAction();
		refreshAction = new RefreshAction();
		homeDirAction = new HomeDirAction();
		createDirAction = new CreateDirAction();
		renameAction = new RenameAction();
		copyAction = new CopyAction();
		cutAction = new CutAction();
		pasteAction = new PasteAction();
		deleteAction = new DeleteAction();
		parentDirAction = new ParentDirAction();
		prevDirAction = new PreviousDirAction();
		copyFilenameAction = new CopyFilenameAction();
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

	/**
	 * @see javax.swing.JFrame#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visibility) throws ENTUnauthenticatedUserException {
		/* Empêche l'affichage de la fenêtre si l'utilisateur n'est pas connecté */
		if (visibility && entd.getLogin() == null)
			throw new ENTUnauthenticatedUserException(
					ENTUnauthenticatedUserException.UNAUTHENTICATED);
		super.setVisible(visibility);
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
						0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
				adressBarLayout.columnWidths = new int[] { 30, 30, 30, 30, 30,
						0, 0, 30, 30, 30 };
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
							} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
								adressField.setText(entd.getDirectoryPath());
							}
						}
					});
					adressField.getDocument().addDocumentListener(
							new DocumentListener() {
								private void update() {
									//Utilisation de invokeLater pour résoudre
									//un freeze de l'interface graphique après
									//plusieurs changement de dossier par 
									//double-clic.
									SwingUtilities.invokeLater(new Runnable() {
										@Override
										public void run() {
											String d = entd.getDirectoryPath();
											String i = adressField.getText();
											boolean b = !i.equals(d);
											goDirBtn.setVisible(b);
											resetPathBtn.setVisible(b);
										}
									});
								}

								@Override
								public void removeUpdate(DocumentEvent e) {
									update();
								}

								@Override
								public void insertUpdate(DocumentEvent e) {
									update();
								}

								@Override
								public void changedUpdate(DocumentEvent e) {
								}
							});
				}
				{
					goDirBtn = new JButton();
					Misc.setButtonIcon(goDirBtn, "go.png");
					goDirBtn.setMargin(new Insets(0, 0, 0, 0));
					goDirBtn.setToolTipText("Aller");
					goDirBtn.setMinimumSize(new Dimension(24, 24));
					goDirBtn.setPreferredSize(new Dimension(24, 24));
					goDirBtn.setMaximumSize(new Dimension(24, 24));
					goDirBtn.setFocusable(false);
					goDirBtn.setVisible(false);
					goDirBtn.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							changeDirectory(adressField.getText());
						}
					});
					adressBar.add(goDirBtn, new GridBagConstraints(5, 0, 1, 1,
							0.0, 0.0, GridBagConstraints.CENTER,
							GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0,
							0));
				}
				{
					resetPathBtn = new JButton();
					Misc.setButtonIcon(resetPathBtn, "x.png");
					resetPathBtn.setMargin(new Insets(0, 0, 0, 0));
					resetPathBtn.setToolTipText("Annuler");
					resetPathBtn.setMinimumSize(new Dimension(24, 24));
					resetPathBtn.setPreferredSize(new Dimension(24, 24));
					resetPathBtn.setMaximumSize(new Dimension(24, 24));
					resetPathBtn.setFocusable(false);
					resetPathBtn.setVisible(false);
					resetPathBtn.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							adressField.setText(entd.getDirectoryPath());
						}
					});
					adressBar.add(resetPathBtn, new GridBagConstraints(6, 0, 1,
							1, 0.0, 0.0, GridBagConstraints.CENTER,
							GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0,
							0));
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
					adressBar.add(refreshBtn, new GridBagConstraints(7, 0, 1,
							1, 0.0, 0.0, GridBagConstraints.CENTER,
							GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0,
							0));
				}

				{
					listViewBtn = new JToggleButton();
					adressBar.add(listViewBtn, new GridBagConstraints(8, 0, 1,
							1, 0.0, 0.0, GridBagConstraints.CENTER,
							GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0,
							0));
					Misc.setButtonIcon(listViewBtn, "listview.png");
					listViewBtn.setMargin(new Insets(0, 0, 0, 0));
					listViewBtn.setToolTipText("Vue liste");
					listViewBtn.setMinimumSize(new Dimension(24, 24));
					listViewBtn.setPreferredSize(new Dimension(24, 24));
					listViewBtn.setMaximumSize(new Dimension(24, 24));
					listViewBtn.setFocusable(false);
					listViewBtn.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							if (listViewBtn.isSelected()) {
								setFileView(BriefView.class);
							} else if (!detailsViewBtn.isSelected()) {
								listViewBtn.setSelected(true);
							}
						}
					});
				}

				{
					detailsViewBtn = new JToggleButton();
					adressBar.add(detailsViewBtn, new GridBagConstraints(9, 0,
							1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
							GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0,
							0));
					Misc.setButtonIcon(detailsViewBtn, "detailsview.png");
					detailsViewBtn.setMargin(new Insets(0, 0, 0, 0));
					detailsViewBtn.setToolTipText("Vue détails");
					detailsViewBtn.setMinimumSize(new Dimension(24, 24));
					detailsViewBtn.setPreferredSize(new Dimension(24, 24));
					detailsViewBtn.setMaximumSize(new Dimension(24, 24));
					detailsViewBtn.setFocusable(false);
					detailsViewBtn.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							if (detailsViewBtn.isSelected()) {
								setFileView(DetailView.class);
							} else if (!listViewBtn.isSelected()) {
								detailsViewBtn.setSelected(true);
							}
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
						copyFilename = new JMenuItem();
						copyFilename.setAction(copyFilenameAction);
						fileMenu.add(copyFilename);
					}
					{
						createDir = new JMenuItem();
						createDir.setAction(createDirAction);
						fileMenu.add(createDir);
					}
					{
						rename = new JMenuItem();
						rename.setAction(renameAction);
						fileMenu.add(rename);
					}
					{
						fileMenu.addSeparator();
					}
					{
						copy = new JMenuItem();
						copy.setAction(copyAction);
						fileMenu.add(copy);
					}
					{
						cut = new JMenuItem();
						cut.setAction(cutAction);
						fileMenu.add(cut);
					}
					{
						paste = new JMenuItem();
						paste.setAction(pasteAction);
						fileMenu.add(paste);
					}
					{
						fileMenu.addSeparator();
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
						send = new JMenuItem();
						send.setAction(sendAction);
						fileMenu.add(send);
					}
					{
						fileMenu.addSeparator();
					}
					{
						delete = new JMenuItem();
						delete.setAction(deleteAction);
						fileMenu.add(delete);
					}
					{
						fileMenu.addSeparator();
					}
					{
						exit = new JMenuItem();
						fileMenu.add(exit);
						exit.setAction(new ExitAction());
						Misc.setButtonIcon(exit, "quit.png");
					}
				}
				{
					navigationMenu = new JMenu();
					jMenuBar.add(navigationMenu);
					navigationMenu.setText("Navigation");
					navigationMenu.setMnemonic(KeyEvent.VK_N);
					{
						openDir = new JMenuItem();
						navigationMenu.add(openDir);
						openDir.setAction(openDirAction);
					}
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
						Misc.setButtonIcon(ListItem, "listview.png");
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
						Misc.setButtonIcon(DetailItem, "detailsview.png");
						DetailItem.setText("Détails");
						DetailItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								setFileView(DetailView.class);
							}
						});
						affichGroup.add(DetailItem);
					}
					{
						affichMenu.addSeparator();
					}
					{
						zoom = new JMenuItem();
						affichMenu.add(zoom);
						zoom.setText("Zoom -");
						zoom.setMnemonic(KeyEvent.VK_Z);
						zoom.setAccelerator(KeyStroke.getKeyStroke(
								java.awt.event.KeyEvent.VK_Z,ActionEvent.ALT_MASK));
						Misc.setButtonIcon(zoom, "zoomout.png");
						zoom.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								setZoomLevel((fileView.getZoomLevel()+1)%2);
							}
						});
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
						Misc.setButtonIcon(onlineHelp, "help.png");
						onlineHelp.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								try {
									Misc.browse(CoreConfig
											.getString("ProductInfo.manpage")
											.replaceAll(
													"\\{version\\}",
													java.net.URLEncoder.encode(
															CoreConfig
																	.getString("ProductInfo.version"),
															"UTF-8")));
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
						Misc.setButtonIcon(website, "website.png");
						website.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								Misc.browse(CoreConfig
										.getString("ProductInfo.website"));
							}
						});
					}
					{
						fbpage = new JMenuItem();
						help.add(fbpage);
						fbpage.setText("Page Facebook");
						fbpage.setMnemonic(KeyEvent.VK_K);
						Misc.setButtonIcon(fbpage, "fb.png");
						fbpage.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								Misc.browse(CoreConfig
										.getString("ProductInfo.fbpage"));
							}
						});
					}
					{
						checkUpdate = new JMenuItem();
						help.add(checkUpdate);
						checkUpdate.setText("Rechercher des mises à jour...");
						checkUpdate.setMnemonic(KeyEvent.VK_J);
						Misc.setButtonIcon(checkUpdate, "checkUpdate.png");
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
						Misc.setButtonIcon(about, "info.png");
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
					emptyDirLabel = new JLabel("Le dossier est vide.");
					emptyDirLabel.setHorizontalAlignment(SwingConstants.CENTER);
					emptyDirLabel.setForeground(new Color(109, 109, 109));
					emptyDirLabel.setVisible(false);
				}
				{
					browserLayeredPane = new JLayeredPane();
					browserLayeredPane.add(emptyDirLabel,
							JLayeredPane.DRAG_LAYER);
					browserLayeredPane
							.addComponentListener(new ComponentAdapter() {
								@Override
								public void componentResized(ComponentEvent e) {
									//Bind content size to parent.
									if (fileView != null) {
										fileView.setSize(e.getComponent()
												.getSize());
										fileView.revalidate();
									}
									emptyDirLabel.setSize(e.getComponent()
											.getSize());
								}
							});
					getContentPane().add(
							browserLayeredPane,
							new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
									GridBagConstraints.CENTER,
									GridBagConstraints.BOTH, new Insets(0, 0,
											0, 0), 0, 0));
				}
				toolBar.setFloatable(false);
				toolBar.setBackground(new Color(240, 240, 240));
				toolBar.setForeground(new Color(0, 0, 0));
				toolBar.setSize(810, 54);
				toolBar.setPreferredSize(new Dimension(810, 50));
				toolBar.setMinimumSize(new Dimension(15, 50));
				{
					Download_tool = makeToolbarButton(dldAction);
					DownloadAll_tool = makeToolbarButton(dldAllAction);
					send_tool = makeToolbarButton(sendAction);
					createDir_tool = makeToolbarButton(createDirAction);
					rename_tool = makeToolbarButton(renameAction);
					copy_tool = makeToolbarButton(copyAction);
					cut_tool = makeToolbarButton(cutAction);
					paste_tool = makeToolbarButton(pasteAction);
					delete_tool = makeToolbarButton(deleteAction);
					toolBar.add(createDir_tool);
					toolBar.add(rename_tool);
					toolBar.addSeparator();
					toolBar.add(copy_tool);
					toolBar.add(cut_tool);
					toolBar.add(paste_tool);
					toolBar.addSeparator();
					toolBar.add(Download_tool);
					toolBar.add(DownloadAll_tool);
					toolBar.add(send_tool);
					toolBar.addSeparator();
					toolBar.add(delete_tool);
				}
			}
			thisLayout.rowWeights = new double[] { 0.0, 0.0, 1.0, 0.0 };
			thisLayout.rowHeights = new int[] { 7, 7, 7, 7 };
			thisLayout.columnWeights = new double[] { 1.0 };
			thisLayout.columnWidths = new int[] { 7 };
			{
				PopupMenu = new JPopupMenu();
				{
					openDirPopupIt = new JMenuItem();
					openDirPopupIt.setAction(openDirAction);
					openDirPopupIt.setFont(openDirPopupIt.getFont().deriveFont(
							Font.BOLD));
					PopupMenu.add(openDirPopupIt);
				}
				{
					jMenuItem3 = new JMenuItem();
					jMenuItem3.setAction(copyFilenameAction);
					PopupMenu.add(jMenuItem3);
				}
				{
					createDirPopupIt = new JMenuItem();
					createDirPopupIt.setAction(createDirAction);
					PopupMenu.add(createDirPopupIt);
				}
				{
					renamePopupIt = new JMenuItem();
					renamePopupIt.setAction(renameAction);
					PopupMenu.add(renamePopupIt);
				}
				{
					PopupMenu.addSeparator();
				}
				{
					copyPopupIt = new JMenuItem();
					copyPopupIt.setAction(copyAction);
					PopupMenu.add(copyPopupIt);
				}
				{
					cutPopupIt = new JMenuItem();
					cutPopupIt.setAction(cutAction);
					PopupMenu.add(cutPopupIt);
				}
				{
					pastePopupIt = new JMenuItem();
					pastePopupIt.setAction(pasteAction);
					PopupMenu.add(pastePopupIt);
				}
				{
					PopupMenu.addSeparator();
				}
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
				{
					sendPopupIt = new JMenuItem();
					sendPopupIt.setAction(sendAction);
					PopupMenu.add(sendPopupIt);
				}
				{
					PopupMenu.addSeparator();
				}
				{
					deletePopupIt = new JMenuItem();
					deletePopupIt.setAction(deleteAction);
					PopupMenu.add(deletePopupIt);
				}
			}
			{
				fileChooser = new JFileChooser();
			}

			setFileView(BriefView.class);

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
					if(e1.getType() == ENTUnauthenticatedUserException.UNALLOWED)
					{
						statusInfo.setText("Indisponible");
						JOptionPane
								.showMessageDialog(
										MainFrame.this,
										"<html>Vous n'avez actuellement accès à aucun espace de stockage sur l'ENT.<br>Vous ne pouvez donc malheureusement pas utiliser ce logiciel.</html>",
										"ENTDownloader - Service indisponible",
										JOptionPane.ERROR_MESSAGE);
						new MainFrame.ExitAction().actionPerformed(null);
						return null;
					} else
					{
						e1.printStackTrace();
					}
				} catch (ENTElementNotFoundException e1) {

					//Rétablissement de la synchronisation vue <=> modèle.
					//En effet, le dossier courant peut avoir changer si, par
					//exemple, l'utilisateur demande le chemin /d1/d2 à partir
					//de la racine et que le dossier d2 n'existe pas.
					//  => Affichage du dossier d1
					updateFrameData();

					if (addHistory) {
						historyPush(entd.getDirectoryPath());
					}

					JOptionPane
							.showMessageDialog(
									MainFrame.this,
									"ENTDownloader ne parvient pas à trouver \""
											+ path
											+ "\". Vérifiez l'orthographe et réessayez.",
									"ENTDownloader", JOptionPane.ERROR_MESSAGE);
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
		ImageIcon icon = Misc.loadIcon(imageName, altText);

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
		Broadcaster.addDirectoryCreatedListener(new DirectoryCreatedListener() {
			@Override
			public void onDirectoryCreated(DirectoryCreatedEvent event) {
				updateFrameData();
				//TODO Sélectionner le dossier nouvellement créé.
			}
		});
		Broadcaster.addElementRenamedListener(new ElementRenamedListener() {
			@Override
			public void onElementRenamed(ElementRenamedEvent event) {
				updateFrameData();
				//TODO Sélectionner le dossier nouvellement renommé ?
			}
		});
		Broadcaster.addElementsDeletedListener(new ElementsDeletedListener() {
			@Override
			public void onElementsDeleted(ElementsDeletedEvent event) {
				updateFrameData();
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
							openDirAction.actionPerformed(null);
						} else {
							Downloader dld = new Downloader(MainFrame.this,
									target, fileChooser);
							try {
								dld.startDownload();
							} catch (ENTUnauthenticatedUserException e) {
								// TODO Gestion expiration de session
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
		int zoom = Misc.MEDIUM; //Valeur de zoom par défaut
		if (fileView != null && fileView.getClass() == view)
			return;
		if (fileView != null) {
			zoom = fileView.getZoomLevel();
			browserLayeredPane.remove(fileView);
		}
		try {
			fileView = view.newInstance();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if(entd != null)
		{
			try {
				fileView.browseDirectory(entd.getDirectoryContent());
			} catch (IllegalStateException e) {
			}
		}

		setZoomLevel(zoom);
		
		fileView.getViewInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
						"enterPressedAction");

		fileView.getViewInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0),
						"prevDirAction");

		fileView.getViewActionMap().put("prevDirAction", prevDirAction);
		fileView.getViewActionMap().put("enterPressedAction",
				new AbstractAction() {
					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						switch (fileView.getSelectedFilesCount()) {
						case 0:
							break;
						case 1:
							GuiBroadcaster
									.fireDoubleClickOnRow(new DoubleClickOnRowEvent(
											fileView.getSelectedFile()));
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

		browserLayeredPane.add(fileView, JLayeredPane.DEFAULT_LAYER);
		fileView.setSize(browserLayeredPane.getSize());
		browserLayeredPane.revalidate();
		fileView.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return;
				updatePopupMenu();
			}
		});
		updatePopupMenu();
	}

	protected void setZoomLevel(int zoom) {
		if(fileView == null)
			return;
		if(zoom == Misc.MEDIUM) {
			fileView.setZoomLevel(Misc.MEDIUM);
			this.zoom.setText("Zoom -");
			Misc.setButtonIcon(this.zoom, "zoomout.png");
		}
		else {
			fileView.setZoomLevel(Misc.SMALL);
			this.zoom.setText("Zoom +");
			Misc.setButtonIcon(this.zoom, "zoomin.png");
		}
	}

	public JPopupMenu getPopupMenu() {
		return PopupMenu;
	}

	public void updatePopupMenu() {
		boolean isSelectionEmpty = fileView.getSelectionModel().isSelectionEmpty();
		dldAction.setEnabled(!isSelectionEmpty);
		copyAction.setEnabled(!isSelectionEmpty);
		cutAction.setEnabled(!isSelectionEmpty);
		deleteAction.setEnabled(dldAction.isEnabled());
		if (fileView.getSelectedFilesCount() == 1) {
			copyFilenameAction.setEnabled(true);
			renameAction.setEnabled(true);
			String type = (fileView.getSelectedFile().isDirectory()) ? "dossier"
					: "fichier";
			copyFilenameAction.putValue(Action.SHORT_DESCRIPTION,
					"Copier le nom du " + type
							+ " sélectionné dans le presse-papier");
			copyFilenameAction
					.putValue(Action.NAME, "Copier le nom du " + type);
			renameAction.putValue(Action.SHORT_DESCRIPTION,
					"Renommer le " + type
							+ " sélectionné");
			deleteAction.putValue(Action.SHORT_DESCRIPTION,
					"Supprimer le " + type
							+ " sélectionné");
			openDirAction.setEnabled(fileView.getSelectedFile().isDirectory());
		} else {
			copyFilenameAction.setEnabled(false);
			openDirAction.setEnabled(false);
			renameAction.setEnabled(false);
			deleteAction.putValue(Action.SHORT_DESCRIPTION,
					"Supprimer les éléments sélectionnés");
		}
	}

	@Override
	public void onDirectoryChanged(DirectoryChangedEvent event) {
		updateFrameData();
	}
	
	/**
	 * Affiche un message d'état durant une durée déterminée.
	 * 
	 * @param message Le message temporaire à afficher
	 * @param delay La durée d'exposition de ce message
	 * @param after Le texte à afficher après le délai.
	 * 				Si null, le précédent texte sera restauré.
	 */
	protected void setTemporaryStatus(String message, int delay, String after) {
		if(after == null)
			after = statusInfo.getText();
		statusInfo.setText(message);
		final String afterS = after;
		Timer t = new Timer(delay*1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				statusInfo.setText(afterS);
			}
		});
		t.setRepeats(false);
		t.start();
	}

	/**
	 * Recharge l'ensemble des informations affichées dans la fenêtre
	 * principale à partir du modèle (instance unique de
	 * {@link net.sf.entDownloader.core.ENTDownloader ENTDownloader}),
	 * notamment la liste des fichiers du dossier courant, le chemin courant,
	 * et le nom de l'utilisateur.
	 * 
	 * Veuillez noter que cette méthode <u>n'actualise pas ces informations
	 * depuis l'ENT</u>, mais uniquement <u>localement depuis le modèle</u>.
	 */
	public void updateFrameData() {
		String dirPath = entd.getDirectoryPath();
		adressField.setText(dirPath);
		parentDirAction.setEnabled(!dirPath.equals("/"));
		statusInfo.setText(dirInfos());
		setUsedSpace();
		userNameLabel
				.setText(entd.getUsername() + " (" + entd.getLogin() + ")");
		fileView.browseDirectory(entd.getDirectoryContent());
		boolean isEmpty = entd.getDirectoryContent().size() == 0;
		emptyDirLabel.setVisible(isEmpty);
		dldAllAction.setEnabled(!isEmpty);
		pasteAction.setEnabled(entd.canPaste());
	}

	/**
	 * Recharge la référence à l'instance unique de
	 * {@link net.sf.entDownloader.core.ENTDownloader ENTDownloader}.
	 */
	public void updateENTDInstance() {
		entd = ENTDownloader.getInstance();
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
