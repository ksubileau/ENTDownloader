/*
 *  GuiMain.java
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

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import entDownloader.core.ENTDownloader;

public class GuiMain {
	private static Image appIcon;
	private static JFrame mainFrame;

	public GuiMain(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				//Set Look & Feel
				try {
					javax.swing.UIManager
							.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
				} catch (Exception e) {
					try {
						javax.swing.UIManager
								.setLookAndFeel(javax.swing.UIManager
										.getSystemLookAndFeelClassName());
					} catch (Exception e1) {
						System.err
								.println("Impossible de définir le Look & Feel de l'application. "
										+ "Le rendu visuel peut être dégradé.");
					}
				}
				appIcon = new ImageIcon(getClass().getClassLoader()
						.getResource("entDownloader/ressources/appico.png"))
						.getImage();
				LoginFrame loginFrame = new LoginFrame();
				loginFrame.setLocationRelativeTo(null);
				loginFrame.setVisible(true);
				mainFrame = new MainFrame();
				mainFrame.setLocationRelativeTo(null);
				//Analyse des arguments
				for (int i = 0; i < args.length; i++) {
					String argv = args[i];
					if (argv.equalsIgnoreCase("-L")) {
						if (i >= args.length - 1) {
							System.err.println("Argument n°" + (i + 1)
									+ " incomplet : login attendu.");
						} else {
							++i;
							loginFrame.setLogin(args[i]);
						}
					} else if (argv.equalsIgnoreCase("-pac")) {
						if (i >= args.length - 1) {
							System.err
									.println("Argument n°"
											+ (i + 1)
											+ " incomplet : emplacement du fichier PAC attendu.");
						} else {
							++i;
							try {
								ENTDownloader.getInstance().setProxy(args[i]); //TODO Penser à adapter ici pour garder le nom du fichier pac
							} catch (Exception e) {
								System.err
										.println("Impossible de définir le proxy à utiliser à partir du fichier"
												+ " PAC spécifié.");
								continue;
							}
							System.out
									.println("Paramètrage du serveur proxy à partir du fichier \""
											+ args[i] + "\".");
						}
					} else if (argv.equalsIgnoreCase("-proxy")) {
						if (i >= args.length - 2) {
							System.err
									.println("Argument n°"
											+ (i + 1)
											+ " incomplet : adresse du proxy et port attendu. "
											+ "Consultez le manuel pour plus d'informations.");
						} else {
							String proxyAddr = args[i + 1];
							int proxyPort;
							try {
								proxyPort = Integer.parseInt(args[i + 2]);
							} catch (NumberFormatException e1) {
								System.err.println("Argument n°" + (i + 3)
										+ " invalide : port du proxy attendu.");
								continue;
							}
							try {
								ENTDownloader.getInstance().setProxy(proxyAddr,
										proxyPort);
							} catch (IllegalArgumentException e) {
								System.err
										.println("Argument n°"
												+ (i + 3)
												+ " invalide : port du proxy attendu (entre 1 et 65535 compris).");
								continue;
							} catch (Exception e) {
								System.err
										.println("Impossible de définir le proxy demandé.");
								continue;
							}
							System.out
									.println("Paramètrage du serveur proxy : \n\tAdresse : "
											+ proxyAddr
											+ "\n\tPort : "
											+ proxyPort + ".");
							i += 2;
						}
					}
				}
				//Lancement de l'updater dans un thread séparé.
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							new UpdaterGui(mainFrame);
						} catch (Exception e) {
						}
					}
				}, "Updater").start();
			}

		});

	}

	public static JFrame getMainFrame() {
		return mainFrame;
	}

	public static Image getAppIcon() {
		return appIcon;
	}
}
