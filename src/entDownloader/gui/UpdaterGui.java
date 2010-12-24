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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
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

	public UpdaterGui(final JFrame owner, boolean showAlreadyUpToDateMessage)
			throws Exception {
		if (showAlreadyUpToDateMessage) {
			updater = new Updater(CoreConfig.updaterURL + "&manu");
		} else {
			updater = new Updater();
		}
		Runnable prompt = null;
		if (!updater.isUpToDate()) {
			JScrollPane editorPane = getChangeLogPane();
			final JPanel content = new JPanel();
			content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
			final JEditorPane text = new JEditorPane("text/html",
					"<html>Une nouvelle version de "
							+ CoreConfig.getString("ProductInfo.name")
							+ " est disponible ! La version <b>"
							+ updater.version()
							+ "</b> est téléchargeable sur <a href=\""
							+ updater.location()
							+ "\">"
							+ updater.location()
							+ "</a>."
							+ (editorPane == null ? ""
									: "<br><br>Principaux changements :"));
			//text.setAlignmentX( Component.RIGHT_ALIGNMENT);
			text.setEditable(false);
			text.setOpaque(false);
			text.setBackground(new Color(255, 255, 255, 0));
			//text.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE); 
			text.addHyperlinkListener(getHyperlinkListener(text));
			text.setMaximumSize(new Dimension(450, 1000));

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
			if (editorPane != null) {
				content.setPreferredSize(new Dimension(450, 150));
				content.add(editorPane);
			} else {
				content.setPreferredSize(new Dimension(400, 50));
			}

			prompt = new Runnable() {
				@Override
				public void run() {
					String[] options = new String[] { "Télécharger...",
							"Annuler" };
					int choose = JOptionPane.showOptionDialog(null, content,
							"ENTDownloader - Mise à jour disponible",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.INFORMATION_MESSAGE, null, options,
							options[0]);

					if (choose == JOptionPane.YES_OPTION) {
						Misc.browse(updater.location());
					}
				}
			};
		} else if (showAlreadyUpToDateMessage) {
			prompt = new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(owner,
							"Aucune mise à jour n'est disponible.",
							"ENTDownloader", JOptionPane.INFORMATION_MESSAGE);
				}
			};
		} else
			return;
		if (SwingUtilities.isEventDispatchThread()) {
			prompt.run();
		} else {
			SwingUtilities.invokeLater(prompt);
		}
	}

	private JScrollPane getChangeLogPane() {
		String changesText = getChangeLogText();
		if (changesText == null || changesText.isEmpty())
			return null;

		// Turn anti-aliasing on
		System.setProperty("awt.useSystemAAFontSettings", "on");
		JEditorPane changesPane = new JEditorPane();

		// Enable use of custom set fonts
		//changesPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);  
		//changesPane.setFont(new Font("Arial", Font.PLAIN, 13));

		changesPane.setEditable(false);
		changesPane.setContentType("text/html");
		changesPane.setText(changesText);

		changesPane.addHyperlinkListener(getHyperlinkListener(changesPane));
		changesPane.setOpaque(false);
		//changesPane.setBackground(new Color(255, 255, 255, 0));

		JScrollPane changesScroll = new JScrollPane(changesPane);
		changesScroll.setPreferredSize(new Dimension(450, 100));
		changesScroll.setMaximumSize(new Dimension(450, 1000));
		changesPane.setCaretPosition(0); //Scroll au début de la liste
		return changesScroll;
	}

	private String getChangeLogText() {
		if (updater == null || updater.isUpToDate())
			return null;

		ArrayList<String> added = updater.changelog_added();
		ArrayList<String> changed = updater.changelog_changed();
		ArrayList<String> fixed = updater.changelog_fixed();
		ArrayList<String> other = updater.changelog_other();

		if (added == null && changed == null && fixed == null && other == null)
			return "";

		String changesText = "<html>" + "<body><dl>";

		if (added != null) {
			changesText += "		<dt>Nouveau :</dt>";
			for (String add : added) {
				changesText += "			<dd>&ndash; " + add + "</dd>";
			}
			//changesText += "		</dl>";
		}
		if (fixed != null) {
			changesText += "		<dt>Corrigé :</dt>";
			for (String fix : fixed) {
				changesText += "			<dd>&ndash; " + fix + "</dd>";
			}
			//changesText += "		</ul>";
		}
		if (changed != null) {
			changesText += "		<dt>Modifié :</dt>";
			for (String change : changed) {
				changesText += "			<dd>&ndash; " + change + "</dd>";
			}
			//changesText += "		</ul>";
		}
		if (other != null) {
			changesText += "		<dt>Autres :</dt>";
			for (String other_change : other) {
				changesText += "			<dd>&ndash; " + other_change + "</dd>";
			}
			//changesText += "		</ul>";
		}
		changesText += "</dl></body>" + "</html>";
		return changesText;
	}

	private HyperlinkListener getHyperlinkListener(final JComponent source) {
		return new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(final HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ENTERED) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							// TIP: Show hand cursor
							SwingUtilities
									.getWindowAncestor(source)
									.setCursor(
											Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
							// TIP: Show URL as the tooltip
							source.setToolTipText(e.getURL().toExternalForm());
						}
					});
				} else if (e.getEventType() == HyperlinkEvent.EventType.EXITED) {
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							// Show default cursor
							SwingUtilities.getWindowAncestor(source).setCursor(
									Cursor.getDefaultCursor());

							// Reset tooltip
							source.setToolTipText(null);
						}
					});
				} else if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					Misc.browse(e.getURL());
				}
			}
		};
	}
}
