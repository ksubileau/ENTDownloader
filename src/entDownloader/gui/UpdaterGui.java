/*
 *  UpdaterGui.java
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
package entDownloader.gui;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.net.URI;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import entDownloader.core.CoreConfig;
import entDownloader.core.Updater;

public class UpdaterGui {
	
	Updater updater;
	
	public UpdaterGui(JFrame owner) throws Exception {
		this(owner, false);
	}
	
	public UpdaterGui(JFrame owner, boolean showAlreadyUpToDateMessage) throws Exception {
		updater = new Updater();
		if(!updater.isUpToDate()) {
			JScrollPane editorPane = getChangeLogPane();
			JPanel content = new JPanel();
			content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
			final JEditorPane text = new JEditorPane("text/html", "<html>Une nouvelle version de " + CoreConfig.getString("ProductInfo.name") + " est disponible ! La version <b>"+updater.version()+"</b> est téléchargeable sur <a href=\"" + updater.location() + "\">" + updater.location() + "</a>." + (editorPane==null?"":"<br><br>Principaux changements :"));
			//text.setAlignmentX( Component.RIGHT_ALIGNMENT);
			text.setEditable(false);
			text.setOpaque(false);
			//text.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE); 
			text.addHyperlinkListener(getHyperlinkListener(text));
			text.setMaximumSize(new Dimension(450,1000));

			// TIP: Make the JOptionPane resizable using the HierarchyListener
			/*text.addHierarchyListener(new HierarchyListener() {
				public void hierarchyChanged(HierarchyEvent e) {
					Window window = SwingUtilities.getWindowAncestor(text);
					if (window instanceof Dialog) {
						Dialog dialog = (Dialog)window;
						if (!dialog.isResizable()) {
							dialog.setResizable(true);
						}
					}
				}
			});*/
			content.add(text);
			//content.add(Box.createRigidArea(new Dimension(0,5)));
			if(editorPane != null) {
				content.setPreferredSize(new Dimension(450,150));
				content.add(editorPane);
			} else
				content.setPreferredSize(new Dimension(400,50));
			
			String[] options = new String[] {"Télécharger...", "Annuler"};
			int choose = JOptionPane.showOptionDialog(null,	content,"ENTDownloader - Mise à jour disponible",JOptionPane.YES_NO_OPTION,	JOptionPane.INFORMATION_MESSAGE,null,options, options[0]);
			if(choose == JOptionPane.YES_OPTION)
				if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
					try {
						java.awt.Desktop.getDesktop().browse(new URI(updater.location()));
					} catch (Exception ex) {
					}
				}
		}
		else if (showAlreadyUpToDateMessage) {
			JOptionPane.showMessageDialog(owner, "Aucune mise à jour n'est disponible.", "ENTDownloader", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private JScrollPane getChangeLogPane() {
		String changesText = getChangeLogText();
		if(changesText == null || changesText.isEmpty())
			return null;

		// Turn anti-aliasing on
		//System.setProperty("awt.useSystemAAFontSettings", "on");
		JEditorPane editorPane = new JEditorPane();

		// Enable use of custom set fonts
		//				editorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);  
		//editorPane.setFont(new Font("Arial", 13));

		editorPane.setEditable(false);
		editorPane.setContentType("text/html");
		editorPane.setText(changesText);

		// TIP: Add Hyperlink listener to process hyperlinks
		editorPane.setOpaque(false);
		editorPane.addHyperlinkListener(getHyperlinkListener(editorPane));

		JScrollPane changesScroll = new JScrollPane(editorPane);
		changesScroll.setPreferredSize(new Dimension(450,100));
		changesScroll.setMaximumSize(new Dimension(450,1000));
		return changesScroll;
	}

	private String getChangeLogText() {
		if(updater == null || updater.isUpToDate())
			return null;
		
		ArrayList<String> added = updater.changelog_added();
		ArrayList<String> changed = updater.changelog_changed();
		ArrayList<String> fixed = updater.changelog_fixed();
		ArrayList<String> other = updater.changelog_other();
		
		if(added == null && changed == null && fixed == null && other == null)
			return "";
		
		String changesText = "<html>"
			+ "<body>";

		if(added != null) {
			changesText += "	Nouveau :"
				+ "		<ul>";
			for (String add : added) {
				changesText += "			<li>" +add + "</li>";
			}
			changesText += "		</ul>";
		}
		if(fixed != null) {
			changesText += "	Corrigé :"
				+ "		<ul>";
			for (String fix : fixed) {
				changesText += "			<li>" +fix + "</li>";
			}
			changesText += "		</ul>";
		}
		if(changed != null) {
			changesText += "	Modifié :"
				+ "		<ul>";
			for (String change : changed) {
				changesText += "			<li>" +change + "</li>";
			}
			changesText += "		</ul>";
		}
		if(other != null) {
			changesText += "	Autres :"
				+ "		<ul>";
			for (String other_change : other) {
				changesText += "			<li>" +other_change + "</li>";
			} 
			changesText += "		</ul>";
		}
		changesText += "</body>"
			+ "</html>";
		return changesText;
	}

	private HyperlinkListener getHyperlinkListener(final JComponent source) {
		return new HyperlinkListener() {
			public void hyperlinkUpdate(final HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ENTERED) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							// TIP: Show hand cursor
							SwingUtilities.getWindowAncestor(source).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
							// TIP: Show URL as the tooltip
							source.setToolTipText(e.getURL().toExternalForm());
						}
					});
				} else if (e.getEventType() == HyperlinkEvent.EventType.EXITED) {
					SwingUtilities.invokeLater(new Runnable() {

						public void run() {
							// Show default cursor
							SwingUtilities.getWindowAncestor(source).setCursor(Cursor.getDefaultCursor());

							// Reset tooltip
							source.setToolTipText(null);
						}
					});
				} else if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					// TIP: Starting with JDK6 you can show the URL in desktop browser
					if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
						try {
							java.awt.Desktop.getDesktop().browse(e.getURL().toURI());
						} catch (Exception ex) {
						}
					}
				}
			}
		};
	}
}
