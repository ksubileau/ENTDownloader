/*
 *  ShellMain.java
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
package net.sf.entDownloader.shell;

import static net.sf.entDownloader.core.Misc.addZeroBefore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import net.sf.entDownloader.core.CoreConfig;
import net.sf.entDownloader.core.ENTDownloader;
import net.sf.entDownloader.core.FS_Element;
import net.sf.entDownloader.core.FS_File;
import net.sf.entDownloader.core.Updater;
import net.sf.entDownloader.core.events.AuthenticationSucceededEvent;
import net.sf.entDownloader.core.events.AuthenticationSucceededListener;
import net.sf.entDownloader.core.events.Broadcaster;
import net.sf.entDownloader.core.events.DirectoryChangedEvent;
import net.sf.entDownloader.core.events.DirectoryChangedListener;
import net.sf.entDownloader.core.events.DirectoryChangingEvent;
import net.sf.entDownloader.core.events.DirectoryChangingListener;
import net.sf.entDownloader.core.events.DirectoryCreatedEvent;
import net.sf.entDownloader.core.events.DirectoryCreatedListener;
import net.sf.entDownloader.core.events.DownloadAbortEvent;
import net.sf.entDownloader.core.events.DownloadAbortListener;
import net.sf.entDownloader.core.events.DownloadedBytesEvent;
import net.sf.entDownloader.core.events.DownloadedBytesListener;
import net.sf.entDownloader.core.events.ElementRenamedEvent;
import net.sf.entDownloader.core.events.ElementRenamedListener;
import net.sf.entDownloader.core.events.ElementsDeletedEvent;
import net.sf.entDownloader.core.events.ElementsDeletedListener;
import net.sf.entDownloader.core.events.EndDownloadEvent;
import net.sf.entDownloader.core.events.EndDownloadListener;
import net.sf.entDownloader.core.events.EndUploadEvent;
import net.sf.entDownloader.core.events.EndUploadListener;
import net.sf.entDownloader.core.events.FileAlreadyExistsEvent;
import net.sf.entDownloader.core.events.FileAlreadyExistsListener;
import net.sf.entDownloader.core.events.StartDownloadEvent;
import net.sf.entDownloader.core.events.StartDownloadListener;
import net.sf.entDownloader.core.events.StartUploadEvent;
import net.sf.entDownloader.core.events.StartUploadListener;
import net.sf.entDownloader.core.events.UploadedBytesEvent;
import net.sf.entDownloader.core.events.UploadedBytesListener;
import net.sf.entDownloader.core.exceptions.ENTElementNotFoundException;
import net.sf.entDownloader.core.exceptions.ENTInvalidElementNameException;
import net.sf.entDownloader.core.exceptions.ENTInvalidFS_ElementTypeException;
import net.sf.entDownloader.core.exceptions.ENTUnauthenticatedUserException;
import net.sf.entDownloader.shell.progressBar.ProgressBar;

public final class ShellMain implements AuthenticationSucceededListener,
		DirectoryChangedListener, DirectoryChangingListener,
		FileAlreadyExistsListener, StartDownloadListener,
		DownloadedBytesListener, EndDownloadListener, 
		DownloadAbortListener, DirectoryCreatedListener,
		EndUploadListener, StartUploadListener,
		UploadedBytesListener,
		ElementRenamedListener, ElementsDeletedListener {
	private static String login;
	private static final String productName = CoreConfig
			.getString("ProductInfo.name");
	private static final String productVersion = CoreConfig
			.getString("ProductInfo.version");
	private static final String productAuthor = CoreConfig
			.getString("ProductInfo.author");
	private static final String productContact = CoreConfig
			.getString("ProductInfo.email");
	private static final String productSite = CoreConfig
			.getString("ProductInfo.website");
	private ProgressBar pg;
	private ENTDownloader entd;
	private FS_File downloadingFile;
	private File uploadingFile;
	
	public ShellMain(String[] args) {
		System.out.println(productName + " v" + productVersion);
		entd = ENTDownloader.getInstance();
		pg = new ProgressBar(false);
		boolean checkUpdate = true;
		Broadcaster.addAuthenticationSucceededListener(this);
		Broadcaster.addDirectoryChangedListener(this);
		Broadcaster.addDirectoryChangingListener(this);
		Broadcaster.addStartDownloadListener(this);
		Broadcaster.addDownloadedBytesListener(this);
		Broadcaster.addEndDownloadListener(this);
		Broadcaster.addStartUploadListener(this);
		Broadcaster.addUploadedBytesListener(this);
		Broadcaster.addEndUploadListener(this);
		Broadcaster.addDownloadAbortListener(this);
		Broadcaster.addElementRenamedListener(this);
		Broadcaster.addElementsDeletedListener(this);

		//Analyse des arguments
		for (int i = 0; i < args.length; i++) {
			String argv = args[i];
			if (argv.equalsIgnoreCase("-L")) {
				if (i >= args.length - 1) {
					System.err.println("Argument n°" + (i + 1)
							+ " incomplet : login attendu.");
				} else {
					++i;
					login = args[i];
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
						entd.setProxy(args[i]);
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
					System.err.println("Argument n°" + (i + 1)
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
						entd.setProxy(proxyAddr, proxyPort);
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
									+ proxyPort
									+ ".");
					i += 2;
				}
			} else if (argv.equalsIgnoreCase("-noupdate")) {
				checkUpdate = false;
			}
		}

		Scanner sc = new Scanner(System.in);
		if (login == null) {
			while (login == null || login.isEmpty()) {
				System.out.print("Veuillez saisir votre login ENT : ");
				try {
					login = sc.nextLine();
				} catch (NoSuchElementException e) {
					System.out.println();
					return;
				}
			}
		} else {
			System.out.println("Login ENT : " + login);
		}

		char[] password = null;
		while (password == null || password.length == 0) {
			try {
				password = System.console().readPassword("Mot de passe : ");
			} catch (Exception e) {
				System.out.print("Mot de passe : ");
				password = sc.nextLine().toCharArray();
			}
		}

		try {
			writeStatusMessage("Connexion en cours...");
			if(System.getProperty("sf.net.entDownloader.debug", "false").equals("false"))
				pg.setVisible(true);
			if (!entd.login(login, password)) {
				pg.setVisible(false);
				System.err
						.println("Echec d'authentification : Login ou mot de passe incorrect.");
				return;
			}
			pg.setVisible(false);
			writeStatusMessage("Initialisation terminée.");
			cd("/");
		} catch (java.io.IOException e2) {
			if (pg.isVisible()) {
				pg.setVisible(false);
			}
			System.err
					.println("ENTDownloader: Service indisponible: Votre connexion Internet semble rencontrer un problème. Assurez-vous que votre ordinateur est connecté à Internet.");
			return;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (checkUpdate) {
			checkUpdate();
		}

		//Effacement du mot de passe de la mémoire
		java.util.Arrays.fill(password, ' ');
		password = null;
		//Lecture des commandes
		System.out.print("\n" + login + "@ent.u-clermont1.fr:"
				+ entd.getDirectoryPath() + ">");
		while (sc.hasNextLine()) {
			String[] command = null;
			try {
				command = splitCommand(sc.nextLine());
			} catch (ParseException e3) {
				System.err.println("ENTDownloader: Commande incomplète.");
			}

			try {
				if (command != null && command.length != 0) {
					if (command[0].equals("ls")) {
						ls(entd.getDirectoryContent());
					} else if (command[0].equals("cd")) {
						cd((command.length > 1) ? command[1] : "");
					} else if (command[0].equals("get")) {
						get((command.length > 1) ? command[1] : "",
								(command.length > 2) ? command[2] : null);
					} else if (command[0].equals("getall")) {
						int maxdepth = 0;
						String destination = null;
						boolean overwrite = false;

						try {
							//Analyse des paramètres, le cas échéant.
							for (int i = 1; i < command.length; i++) {
								String param = command[i];
								//Active la récursivité.
								if (param.startsWith("-R")
										|| param.startsWith("-r")) {

									if (param.length() > 2) {
										//Lecture de la profondeur maximale 
										//de récursivité.
										maxdepth = Integer.parseInt(param
												.substring(2));
									} else {
										//Pas de profondeur maximale.
										maxdepth = -1;
									}
								} else if (param.equalsIgnoreCase("-y")) {
									overwrite = true;
								} else if (i == command.length - 1) {
									//Le dernier argument, s'il n'est pas reconnu 
									//ci-dessus, est le dossier de destination.
									destination = param;
								}
							}

							if (!overwrite) {
								Broadcaster.addFileAlreadyExistsListener(this);
							}

							System.out.println(entd.getAllFiles(destination,
									maxdepth) + " fichier(s) téléchargé(s)");

						} catch (NumberFormatException e2) {
							System.err
									.println("ENTDownloader: getall: Un nombre entier est attendu après l'option -R.");
						} catch (ENTInvalidFS_ElementTypeException e) {
							System.err
									.println("ENTDownloader: getall: Impossible de créer le répertoire requis : un fichier portant le même nom existe.");
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} finally {
							if (!overwrite) {
								Broadcaster
										.removeFileAlreadyExistsListener(this);
							}
						}
					} else if (command[0].equals("mkdir")) {
						mkdir((command.length > 1) ? command[1] : null);
					} else if (command[0].equals("send")) {
						send((command.length > 1) ? command[1] : "",
								(command.length > 2) ? command[2] : null);
					} else if (command[0].equals("rename")) {
						rename((command.length > 1) ? command[1] : null,
								(command.length > 2) ? command[2] : null);
					} else if (command[0].equals("rm") || command[0].equals("delete") || command[0].equals("del")) {
						String[] elems = null;
						if (command.length > 1)
						{
							List<String> lst = new ArrayList<String>(Arrays.asList(command));
							lst.remove(0);
							elems = lst.toArray(new String[0]);
						}
						delete(elems);
					} else if (command[0].equals("refresh")) {
						cd(".");
					} else if (command[0].equals("copy") || command[0].equals("cut")) {
						String[] elems = null;
						if (command.length > 1)
						{
							elems = Arrays.copyOfRange(command, 1, command.length);
							try {
								if(command[0].equals("copy"))
									entd.copy(elems);
								else
									entd.cut(elems);
							} catch (ENTElementNotFoundException e) {
								System.err.println("ENTDownloader: "+command[0]+": Un fichier ou dossier spécifié est inexistant.");
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						else
						{
							System.err.println("ENTDownloader: "+command[0]+": Nom de fichier ou de dossier manquant.");
						}
					} else if (command[0].equals("paste")) {
						if(entd.canPaste())
						{
							try {
								entd.paste();
							} catch (ENTInvalidElementNameException e) {
								System.err.println("ENTDownloader: Impossible de coller la sélection : un fichier/dossier du même nom existe déjà.");
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						else
							System.err.println("ENTDownloader: Le presse-papier est vide.");

					} else if (command[0].equals("lpwd")) {
						System.out.println(System.getProperty("user.dir"));
					} else if (command[0].equals("info")) {
						infos(entd);
					} else if (command[0].equals("exit") || command[0].equals("quit")) {
						System.exit(0);
					} else if (!command[0].isEmpty() || command.length != 1) {
						System.err.println("ENTDownloader: " + command[0]
								+ ": command not found");
					}
				}
			} catch (IOException e3) {
				if (pg.isVisible()) {
					pg.setVisible(false);
				}

				if(System.getProperty("sf.net.entDownloader.debug", "false").equals("true"))
					e3.printStackTrace();

				System.err
						.println("ENTDownloader: Service indisponible: Votre connexion Internet semble rencontrer un problème. Assurez-vous que votre ordinateur est connecté à Internet.");
				return;
			}
			System.out.print(login + "@ent.u-clermont1.fr:"
					+ entd.getDirectoryPath() + ">");
		}
	}

	private void checkUpdate() {
		try {
			Updater updater = new Updater();
			if (!updater.isUpToDate()) {
				//Mise à jour disponible
				System.out.println("Une nouvelle version de "
						+ productName
						+ " est disponible. La version "
						+ updater.version()
						+ " du "
						+ addZeroBefore(updater.datePublication().get(
								Calendar.DATE))
						+ "/"
						+ addZeroBefore(updater.datePublication().get(
								Calendar.MONTH) + 1)
						+ "/"
						+ addZeroBefore(updater.datePublication().get(
								Calendar.YEAR)) + " est téléchargeable sur "
						+ updater.location() + ".");
			}
		} catch (Exception e) {
		}
	}

	private static String[] splitCommand(String command) throws ParseException {
		List<String> argv = new LinkedList<String>();
		int argc = 0;
		command = command.trim();
		for (int i = 0; i < command.length(); ++i) {
			char c = command.charAt(i);
			if (c == ' ') {
				if (i > 0 && command.charAt(i - 1) != ' ') {
					++argc;
				} else {
					continue;
				}
			} else if (c == '"' && (i == 0 || command.charAt(i - 1) != '\\')) {
				int nexti;
				for (nexti = command.indexOf('"', i + 1);; nexti = command
						.indexOf('"', nexti + 1)) {
					if (nexti == -1)
						throw new ParseException("Unexpected end of command", 0);
					if (command.charAt(nexti - 1) != '\\') {
						break;
					}
				}
				argv.add(argv.size(), command.substring(i + 1, nexti));
				i = nexti;
			} else {
				if (argc >= argv.size()) {
					argv.add(argc, "" + c);
				} else {
					argv.set(argc, (argc >= argv.size()) ? "" : argv.get(argc)
							+ c);
				}
			}
		}
		return argv.toArray(new String[0]);
	}

	public void ls(List<FS_Element> directoryContent) {
		int nbdir = 0, nbfile = 0;
		for (FS_Element e : directoryContent) {
			if (e.isDirectory()) {
				System.out.println("Dossier " + e.getName());
				++nbdir;
			} else {
				System.out.println("Fichier " + e.getName());
				++nbfile;
			}
		}
		System.out.println("\nTotal : " + nbfile + " Fichier(s), " + nbdir
				+ " Dossier(s)");
	}

	public void cd(String name) throws IOException {
		try {
			entd.changeDirectory(name);
		} catch (ENTUnauthenticatedUserException e) {
			if (pg.isVisible()) {
				pg.setVisible(false);
			}
			if(e.getType() == ENTUnauthenticatedUserException.SESSION_EXPIRED)
				System.err
						.println("ENTDownloader: La session a expirée, veuillez vous reconnectez en relançant le programme.");
			else
				System.err
						.println("ENTDownloader: Vous n'avez actuellement accès à aucun espace de stockage sur l'ENT.");

			System.exit(1);
		} catch (ENTElementNotFoundException e) {
			if (pg.isVisible()) {
				pg.setVisible(false);
			}
			System.err.println("ENTDownloader: " + e.getMessage()
					+ ": Aucun répertoire de ce type");
		} catch (ENTInvalidFS_ElementTypeException e) {
			if (pg.isVisible()) {
				pg.setVisible(false);
			}
			System.err.println("ENTDownloader: " + e.getMessage()
					+ ": N'est pas un répertoire");
		} catch (ParseException e) {
			if (pg.isVisible()) {
				pg.setVisible(false);
			}
			System.err
					.println("ENTDownloader: "
							+ name
							+ ": Une erreur est survenue lors de l'accès à ce répertoire");
		}
	}

	public void get(String name, String destination) {
		if (name == null || name.isEmpty()) {
			System.err.println("ENTDownloader: get: Nom de fichier manquant");
			return;
		}
		Broadcaster.addFileAlreadyExistsListener(this);
		try {
			entd.getFile(name, destination);
		} catch (ENTElementNotFoundException e) {
			System.err.println("ENTDownloader: " + name
					+ ": Aucun fichier de ce type");
		} catch (FileNotFoundException e1) {
			System.err
					.println("ENTDownloader: "
							+ destination
							+ ": Accès refusé. Impossible d'écrire au chemin indiqué. Veuillez vérifier qu'un fichier"
							+ " portant ce nom n'existe pas déjà et que vous avez la permission d'écrire à cet emplacement.");
		} catch (ENTInvalidFS_ElementTypeException e2) {
			System.err.println("ENTDownloader: " + name
					+ ": N'est pas un fichier");
		} catch (IOException e3) {
			e3.getLocalizedMessage();
		} finally {
			Broadcaster.removeFileAlreadyExistsListener(this);
		}
	}

	private void mkdir(String dirname) {
		if (dirname == null || dirname.isEmpty()) {
			System.err
					.println("ENTDownloader: mkdir: Nom du dossier à créer manquant");
			return;
		}
		Broadcaster.addDirectoryCreatedListener(this);
		try {
			entd.createDirectory(dirname);
		} catch (ENTInvalidElementNameException e) {
			if (e.getType() == ENTInvalidElementNameException.ALREADY_USED)
				System.err
						.println("ENTDownloader: Impossible de créer le répertoire \""
								+ dirname + "\" : Le fichier existe.");
			else if (e.getType() == ENTInvalidElementNameException.FORBIDDEN_CHAR)
				System.err
				.println("ENTDownloader: Impossible de créer le répertoire \""
						+ dirname + "\" : Nom de dossier invalide.");
			else
				System.err
				.println("ENTDownloader: Impossible de créer le répertoire \""
						+ dirname + "\" : Erreur inconnue.");
		} catch (IOException e3) {
			e3.getLocalizedMessage();
		} catch (ParseException e) {
			if (pg.isVisible()) {
				pg.setVisible(false);
			}
			System.err
					.println("ENTDownloader: Une erreur est survenue lors de la création du répertoire");
		} finally {
			Broadcaster.removeDirectoryCreatedListener(this);
		}
	}

	private void send(String filepath, String name) {
		if (filepath == null || filepath.isEmpty()) {
			System.err.println("ENTDownloader: send: Chemin du fichier manquant");
			return;
		}
		//TODO Envoi multiple ?
		try {
			entd.sendFile(filepath, name);
		} catch (FileNotFoundException e) {
			System.err.println("ENTDownloader: send: Fichier introuvable ou inaccessible");
			return;
		} catch (ENTInvalidElementNameException e) {
			uploadingFile = null;
			pg.setVisible(false);
			pg.setDeterminate(false);
			if (e.getType() == ENTInvalidElementNameException.ALREADY_USED)
				System.err
						.println("ENTDownloader: Impossible d'envoyer le fichier \""
								+ e.getMessage() + "\" : Le fichier existe.");
			else if (e.getType() == ENTInvalidElementNameException.FORBIDDEN_CHAR)
				System.err
				.println("ENTDownloader: Impossible d'envoyer le fichier \""
						+ e.getMessage() + "\" : Nom de fichier invalide.");
		} catch (IOException e) {
			System.err.println("ENTDownloader: send: " + e.getLocalizedMessage());
			return;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void rename(String oldname, String newname) {
		if (oldname == null || oldname.isEmpty()) {
			System.err.println("ENTDownloader: rename: Nom du fichier ou dossier manquant");
			return;
		}
		if (newname == null || newname.isEmpty()) {
			System.err.println("ENTDownloader: rename: Nouveau nom manquant");
			return;
		}

		try {
			entd.rename(oldname, newname);
		} catch (ENTElementNotFoundException e) {
			System.err.println("ENTDownloader: " + e.getMessage()
					+ ": Aucun fichier ou dossier de ce type");
		} catch (ENTInvalidElementNameException e) {
			if (e.getType() == ENTInvalidElementNameException.ALREADY_USED)
				System.err
						.println("ENTDownloader: Impossible de renommer \""
								+ oldname + "\" en \""
								+ newname + "\" : Le fichier existe.");
			else if (e.getType() == ENTInvalidElementNameException.FORBIDDEN_CHAR)
				System.err
				.println("ENTDownloader: Impossible de renommer \""
						+ oldname + "\" en \""
						+ newname + "\" : Nouveau nom invalide.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void delete(String[] elems) {
		if (elems == null || elems.length == 0) {
			System.err.println("ENTDownloader: delete: Nom du fichier ou dossier manquant");
			return;
		}

		try {
			entd.delete(elems);
		} catch (ENTElementNotFoundException e) {
			System.err.println("ENTDownloader: delete: Impossible de supprimer: Aucun fichier ou dossier de ce type.");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void infos(ENTDownloader entd) {
		System.out.println(productName
				+ "  _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _");
		System.out.println("Version 	  : " + productVersion);
		System.out.println("Auteur  	  : " + productAuthor);
		System.out.println("Site Internet	  : " + productSite);
		System.out.println("Contact 	  : " + productContact);

		System.out
				.println("\nEspace de stockage _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _");
		System.out.print("Utilisateur       : ");
		if (entd.getUsername() != null) {
			System.out.print(entd.getUsername());
			if (entd.getLogin() != null) {
				System.out.print(" (" + entd.getLogin() + ")");
			}
			System.out.println();
		} else if (entd.getLogin() != null) {
			System.out.println(entd.getLogin());
		} else {
			System.out.println("Inconnu");
		}
		if (entd.getUsedSpace() != -1 && entd.getCapacity() != -1) {
			System.out.println("Espace utilisé    : " + entd.getUsedSpace()
					+ " Mo");
			System.out.println("Espace disponible : "
					+ (entd.getCapacity() - entd.getUsedSpace()) + " Mo");
			System.out.println("Espace total      : " + entd.getCapacity()
					+ " Mo");
		}
		System.out.println();
	}

	private void writeStatusMessage(String message) {
		boolean pgvisible;
		if (message != null && !message.isEmpty()) {
			pgvisible = pg.isVisible();
			if (pgvisible) {
				pg.setVisible(false);
			}
			System.out.flush();
			System.out.println("  => " + message);
			if (pgvisible) {
				pg.setVisible(pgvisible);
			}
		}
	}

	@Override
	public void onDirectoryChanged(DirectoryChangedEvent event) {
		pg.setVisible(false);
		writeStatusMessage("Chargement réussi.");
	}

	@Override
	public void onDirectoryChanging(DirectoryChangingEvent event) {
		if(System.getProperty("sf.net.entDownloader.debug", "false").equals("false"))
			pg.setVisible(true);
		String path = event.getDirectory().toString();
		if (path.equals("..")) {
			path = "parent";
		} else if (path.equals("/") || path.equals("~") || path.isEmpty()) {
			path = "racine";
		}

		if (path.equals(".")) {
			writeStatusMessage("Actualisation en cours...");
		} else {
			writeStatusMessage("Chargement du dossier " + path + " en cours...");
		}
	}

	@Override
	public void onDirectoryCreated(DirectoryCreatedEvent event) {
		writeStatusMessage("Création du dossier " + event.getName() + " réussie.");
	}

	@Override
	public void onStartDownload(StartDownloadEvent e) {
		downloadingFile = e.getFile();
		pg.setDeterminate(true);
		writeStatusMessage("Téléchargement du fichier "
				+ downloadingFile.getName() + " en cours...");
	}

	@Override
	public void onEndDownload(EndDownloadEvent e) {
		downloadingFile = null;
		pg.setVisible(false);
		pg.setDeterminate(false);
		writeStatusMessage("Téléchargement terminé.");
	}

	@Override
	public void onDownloadAbort(DownloadAbortEvent e) {
		downloadingFile = null;
		pg.setVisible(false);
		pg.setDeterminate(false);
		writeStatusMessage("Téléchargement annulé.");
	}

	@Override
	public void onDownloadedBytes(DownloadedBytesEvent e) {
		pg.setValue(Math.round(e.getTotalDownloaded() * 100f / downloadingFile
				.getSize()));
	}

	@Override
	public void onStartUpload(StartUploadEvent e) {
		uploadingFile = e.getFile();
		pg.setDeterminate(true);
		if(System.getProperty("sf.net.entDownloader.debug", "false").equals("false"))
			pg.setVisible(true);
		writeStatusMessage("Envoi du fichier "
				+ uploadingFile.getName() + " en cours...");
	}

	@Override
	public void onEndUpload(EndUploadEvent e) {
		uploadingFile = null;
		pg.setVisible(false);
		pg.setDeterminate(false);
		writeStatusMessage("Envoi terminé.");
	}

	@Override
	public void onUploadedBytes(UploadedBytesEvent e) {
		pg.setValue(Math.round(e.getTotalUploaded() * 100f / uploadingFile.length()));
	}

	@Override
	public void onAuthenticationSucceeded(AuthenticationSucceededEvent event) {
		writeStatusMessage("Authentification réussie, initialisation...");
	}

	@Override
	public void onFileAlreadyExists(FileAlreadyExistsEvent e) {
		boolean isDeterminate = pg.isDeterminate();
		boolean isVisible = pg.isVisible();
		pg.setVisible(false);
		pg.setDeterminate(false);

		Scanner sc = new Scanner(System.in);
		String choice = null;
		while (choice == null
				|| choice.isEmpty()
				|| (!choice.equalsIgnoreCase("o") && !choice
						.equalsIgnoreCase("n"))) {
			System.out.print("Un fichier portant le nom \""
					+ e.getFile().getName()
					+ "\" existe déjà. Voulez-vous l'écraser ? [Oui|Non] : ");
			try {
				choice = sc.nextLine();
			} catch (NoSuchElementException e1) {
				System.out.println();
				return;
			}
		}
		e.abortDownload = choice.equalsIgnoreCase("n");

		if (isDeterminate) {
			pg.setDeterminate(true);
		}
		if (isVisible && System.getProperty("sf.net.entDownloader.debug", "false").equals("false")) {
			pg.setVisible(true);
		}
	}

	@Override
	public void onElementRenamed(ElementRenamedEvent e) {
		writeStatusMessage("Le fichier " + e.getOldName() + " a été renommé.");
	}

	@Override
	public void onElementsDeleted(ElementsDeletedEvent e) {
		if(e.getTargets().length == 1)
			writeStatusMessage("L'élément " + e.getTargets()[0] + " a été supprimé.");
		else
			writeStatusMessage("Eléments supprimés.");
	}
}
