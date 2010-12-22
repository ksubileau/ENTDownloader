/*
 *  ShellMain.java
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
package entDownloader.shell;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

import entDownloader.core.CoreConfig;
import static entDownloader.core.Misc.addZeroBefore;
import entDownloader.core.ENTDownloader;
import entDownloader.core.ENTPath;
import entDownloader.core.FS_Element;
import entDownloader.core.FS_File;
import entDownloader.core.Updater;
import entDownloader.core.exceptions.ENTDirectoryNotFoundException;
import entDownloader.core.exceptions.ENTFileNotFoundException;
import entDownloader.core.exceptions.ENTInvalidFS_ElementTypeException;
import entDownloader.core.exceptions.ENTUnauthenticatedUserException;
import entDownloader.shell.progressBar.ProgressBar;

public final class ShellMain {
	private static String login;
	private static String password;
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

	public ShellMain(String[] args) {
		System.out.println(productName + " v" + productVersion);
		entd = ENTDownloader.getInstance();
		pg = new ProgressBar(false);
		pg.setVisible(false);
		entd.addObserver(new Observer() {
			boolean enabledWrite = true;
			FS_File file = null;

			@Override
			public void update(Observable o, Object arg) {
				boolean pgvisible;
				String message = null;

				switch (((ENTDownloader) o).getStatus()) {
				case START_DOWNLOAD:
					file = (FS_File) arg;
					message = "Téléchargement du fichier " + file.getName()
							+ " en cours...";
					pg.setDeterminate(true);
					break;
				case END_DOWNLOAD:
					enabledWrite = true;
					file = null;
					message = "Téléchargement terminé.";
					pg.setVisible(false);
					pg.setDeterminate(false);
					pg.setVisible(false);
					break;
				case CHANGEDIR:
					pg.setVisible(true);
					String path = arg.toString();
					if (path.equals("..")) {
						path = "parent";
					} else if (path.equals("/") || path.equals("~")
							|| path.isEmpty()) {
						path = "racine";
					}

					if (path.equals(".")) {
						message = "Actualisation en cours...";
					} else {
						message = "Chargement du dossier " + path
								+ " en cours...";
					}
					break;
				case CHANGEDIR_END:
					pg.setVisible(false);
					message = "Chargement réussi.";
					break;
				case INITIALIZE:
					message = "Authentification réussie, initialisation...";
					break;
				case INITIALIZING_END:
					pg.setVisible(false);
					message = "Initialisation terminée.";
					break;
				case LOGIN:
					message = "Connexion en cours...";
					break;
				case INVALID_CREDENTIALS:
					pg.setVisible(false);
					break;
				default:
					message = (arg != null ? arg.toString() : "");
					break;
				}

				if (enabledWrite && message != null && !message.isEmpty()) {
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

				switch (((ENTDownloader) o).getStatus()) {
				case START_DOWNLOAD:
					enabledWrite = false;
					break;
				case DOWNLOADING:
					pg.setValue((int) (((Long) arg) * 100 / file.getSize()));
					break;
				case LOGIN:
					pg.setVisible(true);
					break;
				}
			}
		});

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

		while (password == null || password.isEmpty()) {
			/*if (debug) {
				System.out.print("Mot de passe : ");
				password = sc.nextLine();
			}
			else {
				try {
					password = PwdConsole.getPasswd("Mot de passe :  ");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}*/

			try {
				password = new String(System.console().readPassword(
						"Mot de passe : "));
			} catch (Exception e) {
				System.out.print("Mot de passe : ");
				password = sc.nextLine();
			}
		}

		try {
			if (!entd.login(login, password)) {
				System.err
						.println("Echec d'authentification : Login ou mot de passe incorrect.");
				return;
			}
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
		checkUpdate();
		//Effacer le mot de passe de la mémoire
		//password = null; //TODO Choix à faire : reconnection automatique => stockage du mot de passe non sécurisé ; ou effacement du mot de passe
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
					} else if (command[0].equals("getall")) { //TODO Creer un dossier eponyme au dossier courant comme destination si cette derniere n'est pas indiquée au lieu d'enregistrer directement dans le dossier courant local ??
						if (command.length > 1
								&& (command[1].startsWith("-R") || command[1]
										.startsWith("-r"))) {
							try {
								System.out
										.println(entd
												.getAllFiles(
														(command.length > 2) ? command[command.length - 1]
																: null,
														(command[1].length() > 2) ? Integer
																.parseInt(command[1]
																		.substring(2))
																: -1)
												+ " fichier(s) téléchargé(s)");
							} catch (NumberFormatException e2) {
								System.err
										.println("ENTDownloader: getall: Un nombre entier est attendu après l'option -R.");
							} catch (ENTInvalidFS_ElementTypeException e) {
								System.err
										.println("ENTDownloader: getall: Impossible de créer le répertoire requis : un fichier portant le même nom existe.");
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						} else {
							try {
								System.out.println(entd.getAllFiles(
										(command.length > 1) ? command[1]
												: null, 0)
										+ " fichier(s) téléchargé(s)");
							} catch (ENTInvalidFS_ElementTypeException e) {
								System.err
										.println("ENTDownloader: getall: Impossible de créer le répertoire requis : un fichier portant le même nom existe.");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} else if (command[0].equals("refresh")) {
						cd(".");
					} else if (command[0].equals("lpwd")) {
						System.out.println(System.getProperty("user.dir"));
					} else if (command[0].equals("info")) {
						infos(entd);
					} else if (command[0].equals("exit")) {
						break;
					} else if (!command[0].isEmpty() || command.length != 1) {
						System.err.println("ENTDownloader: " + command[0]
								+ ": command not found");
					}
				}
			} catch (IOException e3) {
				if (pg.isVisible()) {
					pg.setVisible(false);
				}
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
				System.out
						.println("Une nouvelle version de " + productName + " est disponible. La version "
								+ updater.version() + " du " + addZeroBefore(updater.datePublication().get(Calendar.DATE))
								+ "/" + addZeroBefore(updater.datePublication().get(Calendar.MONTH ) + 1) + "/"
								+ addZeroBefore(updater.datePublication().get(Calendar.YEAR))
								+ " est téléchargeable sur " + updater.location() + ".");
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
			if (CoreConfig.autoLogin) {
				String currentPath = entd.getDirectoryPath();
				try {
					if (!entd.login(login, password)) { //TODO Test expiration de session et généralisation aux autres méthodes.
						pg.setVisible(false);
						System.err
								.println("La session a expirée et la tentative de reconnection a échoué. Veuillez vous reconnectez manuellement en relançant le programme.");
						System.exit(1);
					} else {
						cd("/");
						cd((ENTPath.isAbsolute(name) ? "" : (currentPath + "/"))
								+ name); //TODO Risque de boucle !! (Peu probable mais pas impossible)
						pg.setVisible(false);
					}
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} else {
				System.err
						.println("La session a expirée, veuillez vous reconnectez en relançant le programme.");
				System.exit(1);
			}

		} catch (ENTDirectoryNotFoundException e) {
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
		try {
			entd.getFile(name, destination);
		} catch (ENTFileNotFoundException e) {
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

}

@Deprecated
class PwdConsole {
	@Deprecated
	public static String getPasswd(String prompt) throws Exception {
		ConsoleEraser consoleEraser = new ConsoleEraser();
		System.out.print(prompt);
		BufferedReader stdin = new BufferedReader(new InputStreamReader(
				System.in));
		consoleEraser.start();
		System.out.print("\b");
		String pass = stdin.readLine();
		consoleEraser.halt();
		System.out.print("\b");
		return pass;
	}
}

@Deprecated
class ConsoleEraser extends Thread {
	private boolean running = true;

	public void run() {
		while (running) {
			System.out.print("\b ");
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
	}

	public synchronized void halt() {
		running = false;
	}
}
