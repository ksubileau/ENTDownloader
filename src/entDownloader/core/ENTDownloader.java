/*
 *  ENTDownloader.java
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
package entDownloader.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Observable;

import com.ziesemer.utils.pacProxySelector.PacProxySelector;

import entDownloader.core.events.Broadcaster;
import entDownloader.core.events.DirectoryChangedEvent;
import entDownloader.core.events.DirectoryChangingEvent;
import entDownloader.core.events.DownloadedBytesEvent;
import entDownloader.core.events.DownloadedBytesListener;
import entDownloader.core.events.EndDownloadEvent;
import entDownloader.core.events.StartDownloadEvent;
import entDownloader.core.exceptions.ENTDirectoryNotFoundException;
import entDownloader.core.exceptions.ENTFileNotFoundException;
import entDownloader.core.exceptions.ENTInvalidFS_ElementTypeException;
import entDownloader.core.exceptions.ENTUnauthenticatedUserException;

/* Bug possible :
 * Statut incorrect après une exception quelconque : cela peut poser problème.
 */
public class ENTDownloader extends Observable implements
		DownloadedBytesListener {

	/** L'instance statique */
	private static ENTDownloader instance;

	/** Flag indiquant si l'utilisateur est connecté ou non */
	private boolean isLogin = false;

	/** Liste des dossiers et des fichiers du dossier courant */
	private List<FS_Element> directoryContent = null;
	/** Chemin vers le dossier courant */
	private ENTPath path = null;

	/** Paramètre de l'URL de la page de stockage **/
	private String uP_root;
	/** Paramètre de l'URL de la page de stockage **/
	private String tag;
	/** Identifiant de connexion */
	private String sessionid;
	/** Nom de l'utilisateur */
	private String username = null;
	/** Login */
	private String login = null;
	/** Espace disque utilisé */
	private int usedSpace = -1;
	/** Espace disque total */
	private int capacity = -1;

	/** Progression de téléchargement */
	private long sizeDownloaded = 0;

	/** Instance d'un navigateur utilisé pour la communication avec le serveur */
	private Browser browser;

	private ENTStatus status = ENTStatus.DISCONNECTED;

	/**
	 * Récupère l'instance unique de la class ENTDownloader.<br>
	 * Remarque : le constructeur est rendu inaccessible
	 */
	public static ENTDownloader getInstance() {
		if (null == instance) { // Premier appel
			instance = new ENTDownloader();
		}
		return instance;
	}

	private ENTDownloader() {
		browser = new Browser();
		Broadcaster.addDownloadedBytesListener(this);
	}

	/**
	 * Établit la connexion au serveur de l'ENT.
	 * 
	 * @param username
	 *            Le nom d'utilisatateur pour la connexion
	 * @param password
	 *            Mot de passe de connexion
	 * @return True en cas de réussite, ou false si l'authentification a échoué
	 * @throws ParseException
	 *             Voir {@link ENTDownloader#setStockageUrlParams(String)}
	 */
	public boolean login(String login, String password)
			throws java.io.IOException, ParseException {
		setStatus(ENTStatus.LOGIN);

		if (isLogin == true)
			return true;

		browser.setUrl(CoreConfig.loginURL);
		browser.setFollowRedirects(false);

		String loginPage = browser.perform();
		if (browser.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
			browser.setUrl(browser.getHeaderField("Location"));
			loginPage = browser.perform();
		}

		List<String> ticket = new ArrayList<String>();
		Misc.preg_match(
				"input type=\"hidden\" name=\"lt\" value=\"([0-9a-zA-Z\\-]+)\" />",
				loginPage, ticket);

		browser.setMethod(Browser.Method.POST);
		browser.setParam("_eventId", "submit");
		browser.setParam("username", login);
		browser.setParam("password", password);
		browser.setParam("lt", ticket.get(1).toString());
		browser.setFollowRedirects(false);
		loginPage = browser.perform();
		browser.setMethod(Browser.Method.GET);

		if (Misc.preg_match("<div id=\"erreur\">", loginPage)) {
			setStatus(ENTStatus.INVALID_CREDENTIALS);
			return false;
		}
		setStatus(ENTStatus.INITIALIZE);

		browser.setUrl(browser.getHeaderField("Location"));
		browser.clearParam();
		browser.perform();
		sessionid = browser.getCookieField("JSESSIONID");

		isLogin = true;

		browser.setFollowRedirects(true);
		browser.setUrl(CoreConfig.rootURL);
		browser.clearParam();
		String rootPage = null;
		rootPage = browser.perform();
		setStockageUrlParams(rootPage);
		this.login = login;
		setUserName(rootPage);
		directoryContent = null;
		path = null;
		setStatus(ENTStatus.INITIALIZING_END);
		return true;
	}

	private void setStockageUrlParams(String pageContent) throws ParseException {
		List<String> matches = new ArrayList<String>(5);
		if (!Misc
				.preg_match(
						"<a href=\"[\\w\\d:#@%/;$()~_?\\+-=\\\\.&]*?/tag\\.([0-9A-Fa-f]{13,17})\\.[\\w\\d:#@%/;$()~_?\\+-=\\\\.&]*?uP_root=([\\w\\|]+?)&[\\w\\d:#@%/;$()~_?\\+-=\\\\.&]*?\" title=\"Espace de stockage WebDAV\" id=\"chanLink\"><span>Mes Documents</span>",
						pageContent, matches))
			throw new ParseException(
					"Unable to find the URL parameters of the storage's service in this page.",
					-1);
		tag = matches.get(1);
		uP_root = matches.get(2);
	}

	/**
	 * Obtient et définie les propriétés de l'espace de stockage (espace total
	 * et utilisé)
	 * 
	 * @param pageContent
	 *            Code HTML d'une page de stockage.
	 * @return true si les propriétés ont été trouvés, false sinon
	 */
	private boolean setStorageProperties(String pageContent) {
		List<String> matches = new ArrayList<String>(4);
		if (!Misc.preg_match("([\\d]+) ?% utilis&eacute;s sur ([\\d]+).0 Mo",
				pageContent, matches))
			return false;

		try {
			capacity = Integer.parseInt(matches.get(2));
			usedSpace = Integer.parseInt(matches.get(1)) * capacity / 100;
		} catch (NumberFormatException e) {
			capacity = usedSpace = -1;
			return false;
		}
		return true;
	}

	/**
	 * Détermine le nom complet de l'utilisateur à partir du code HTML de la
	 * page.
	 * 
	 * @param pageContent
	 *            Code HTML d'une page de stockage.
	 * @return true si le nom a été trouvé, false sinon
	 */
	private boolean setUserName(String pageContent) {
		List<String> matches = new ArrayList<String>(4);
		if (!Misc.preg_match("&gt;</span> Bienvenue (.*?)</div><div",
				pageContent, matches))
			return false;
		username = matches.get(1);
		return true;
	}

	/**
	 * Change le répertoire courant
	 * 
	 * @param name
	 *            Nom du dossier ou directive de parcours. Le dossier . est le
	 *            dossier courant : appeler cette méthode avec ce paramètre ne
	 *            change donc pas le dossier courant
	 *            mais permet de rafraîchir son contenu. Le dossier .. est le
	 *            dossier parent, il permet donc de remonter dans
	 *            l'arborescence. Enfin, le dossier / est la racine
	 *            du service de stockage de l'utilisateur.
	 * @throws ENTDirectoryNotFoundException
	 *             Si le répertoire demandé n'existe pas dans le dossier courant
	 * @throws ENTInvalidFS_ElementTypeException
	 *             Si le nom d'un fichier a été passé en paramètre.
	 * @throws ParseException
	 *             En cas d'erreur d'analyse du contenu du dossier cible.
	 * @throws ENTUnauthenticatedUserException
	 *             Si l'utilisateur n'est pas authentifier lors de l'appel de la
	 *             méthode
	 * @throws IOException
	 *             Si le service est indisponible.
	 */
	public void changeDirectory(String path)
			throws ENTUnauthenticatedUserException,
			ENTDirectoryNotFoundException, ENTInvalidFS_ElementTypeException,
			ParseException, IOException {
		if (path == null)
			throw new NullPointerException();
		setStatus(ENTStatus.CHANGEDIR, path);

		DirectoryChangingEvent changingevent = new DirectoryChangingEvent(); //Préparation des événements
		changingevent.setSource(this);
		DirectoryChangedEvent changedevent = new DirectoryChangedEvent();
		changedevent.setSource(this);

		if (path.isEmpty()) {
			changingevent.setDirectory(path);
			Broadcaster.fireDirectoryChanging(changingevent);

			submitDirectory("");

			changedevent.setDirectory(path);
			Broadcaster.fireDirectoryChanged(changedevent);
			setStatus(ENTStatus.CHANGEDIR_END);
			return;
		}

		String splitPath[] = path.split("/");
		if (splitPath.length > 1) { //Si le chemin de destination est composé, on cherche à le simplifier
			ENTPath destination = new ENTPath(this.path);
			destination.goTo(path);
			String absDest = destination.toString(), relDest = this.path
					.getRelative(destination);
			int absc = destination.getNbRequests(), relc = ENTPath
					.getNbRequests(relDest);
			if (relc < absc) {
				path = relDest;
			} else {
				path = absDest;
			}
			splitPath = path.split("/");
		}

		changingevent.setDirectory(path);
		Broadcaster.fireDirectoryChanging(changingevent);

		boolean isAbsolute = ENTPath.isAbsolute(path);

		if (isAbsolute) {
			submitDirectory("/");
		}

		for (int i = isAbsolute ? 1 : 0; i < splitPath.length; ++i) {
			if (!splitPath[i].isEmpty()
					&& (i == 0 || !splitPath[i].equals("."))) {
				/* Corrige les slash multiple dans le chemin et ignore les . à l'intérieur :
				 * 		./toto/././////./titi est traité comme ./toto/titi
				 */
				submitDirectory(splitPath[i]);
			}
		}
		setStatus(ENTStatus.CHANGEDIR_END);

		changedevent.setDirectory(path);
		Broadcaster.fireDirectoryChanged(changedevent);
	}

	/**
	 * Descend ou remonte d'un pas dans l'aborescence à partir du dossier
	 * courant.
	 * 
	 * @param name
	 *            Nom du dossier ou directive de parcours. Le dossier . est le
	 *            dossier courant, appeler cette methode avec ce paramètre ne
	 *            change donc pas le dossier courant
	 *            mais permet de rafraîchir son contenu. Le dossier .. est le
	 *            dossier parent, il permet donc de remonter dans
	 *            l'arborescence. Enfin, le dossier / ou ~ est la racine
	 *            du service de stockage de l'utilisateur. Si <code>name</code>
	 *            est vide, le dossier / est chargé.
	 * @throws ENTDirectoryNotFoundException
	 *             Si le répertoire demandé n'existe pas dans le dossier courant
	 * @throws ENTInvalidFS_ElementTypeException
	 *             Si le nom d'un fichier a été passé en paramètre.
	 * @throws ParseException
	 *             {@link ENTDownloader#parsePage(String) Voir la méthode
	 *             parsePage}
	 * @throws ENTUnauthenticatedUserException
	 *             Si l'utilisateur n'est pas authentifier lors de l'appel de la
	 *             méthode
	 * @throws IOException
	 *             Si le service est indisponible.
	 */
	private void submitDirectory(String name)
			throws ENTDirectoryNotFoundException,
			ENTInvalidFS_ElementTypeException, ParseException,
			ENTUnauthenticatedUserException, IOException {
		if (isLogin == false)
			throw new ENTUnauthenticatedUserException(
					"Non-authenticated user.",
					ENTUnauthenticatedUserException.UNAUTHENTICATED);

		browser.clearParam();
		if (name.equals("/") || name.equals("~") || name.isEmpty()) {
			if (path == null) {
				path = new ENTPath();
				browser.setUrl(urlBuilder(CoreConfig.stockageURL));
			} else {
				path.clear();
				browser.setUrl(urlBuilder("http://ent.u-clermont1.fr/tag.{tag}.render.userLayoutRootNode.target.{uP_root}.uP?link=0#{uP_root}"));
			}
			name = "/";
			browser.setMethod(Browser.Method.GET);
		} else if (name.equals(".")) {
			browser.setMethod(Browser.Method.GET);
			browser.setUrl(urlBuilder(CoreConfig.refreshDirURL));
		} else if (name.equals("..")) {
			if (path.isRoot())
				return; //Déjà à la racine
			browser.setMethod(Browser.Method.GET);
			browser.setUrl(urlBuilder(CoreConfig.directoryBackURL));
		} else {
			//CRITICAL Bug critique avec les noms de dossiers portant un nom contenant un ou plusieurs accents
			int pos = indexOf(name);
			if (pos == -1)
				throw new ENTDirectoryNotFoundException(name);
			else if (!directoryContent.get(pos).isDirectory())
				throw new ENTInvalidFS_ElementTypeException(name);

			browser.setUrl(urlBuilder(CoreConfig.goIntoDirectoryURL));
			browser.setMethod(Browser.Method.POST);
			browser.setParam("targetDirectory", name);
		}
		browser.setFollowRedirects(false);
		browser.setCookieField("JSESSIONID", sessionid);
		String pageContent = null;
		pageContent = browser.perform();

		if (browser.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP
				&& browser.getHeaderField("Location").equals(
						CoreConfig.loginRequestURL)) {
			isLogin = false;
			throw new ENTUnauthenticatedUserException(
					"Session expired, please login again.",
					ENTUnauthenticatedUserException.SESSION_EXPIRED);
		}

		setStockageUrlParams(pageContent);
		if (capacity < 0) {
			setStorageProperties(pageContent);
		}

		if (pageContent.isEmpty()
				|| Misc.preg_match(
						"<font class=\"uportal-channel-strong\">La ressource sp&eacute;cifi&eacute;e n'existe pas.<br /></font>",
						pageContent))
			throw new ENTDirectoryNotFoundException(name);
		parsePage(pageContent);

		path.goTo(name);
	}

	/**
	 * Télécharge le fichier <i>name</i>.
	 * Le fichier sera enregistré sous le dossier et le nom spécifié dans le
	 * paramètre <i>destination</i>, ou sous le même nom que celui sous lequel
	 * il est stocké sur l'ENT si le nouveau nom n'est pas indiqué dans
	 * <i>destination</i>.<br>
	 * <br>
	 * Exemples pour un fichier "tp13.pdf":<br>
	 * <ul>
	 * <li>Si le chemin de destination est "/home/sasa/tps/tpNumero13.pdf", le
	 * fichier sera stocké sous "/home/sasa/tps/tpNumero13.pdf";</li>
	 * <li>Si le chemin de destination est "/home/sasa/bonjour/", le fichier
	 * sera stocké sous "/home/sasa/bonjour/tp13.pdf";</li>
	 * <li>Si le chemin de destination est "tp13.pdf", le fichier sera stocké
	 * sous System.getProperty("user.dir") + "tp13.pdf";</li>
	 * <li>Si le chemin de destination est vide ou null, le fichier sera stocké
	 * sous System.getProperty("user.dir") + nom utilisé sous l'ENT;</li>
	 * <li>Si le chemin de destination est "~/bonjour.pdf", le fichier sera
	 * stocké sous System.getProperty("user.home") + "bonjour.pdf;</li>
	 * </ul>
	 * 
	 * @param name
	 *            Nom du fichier à télécharger
	 * @param destination
	 *            Chemin de destination du fichier
	 * @throws IOException
	 * @see Misc#tildeToHome(String)
	 * @see Browser#downloadFile(String)
	 */
	public void getFile(String name, String destination) throws IOException {
		if (isLogin == false)
			throw new ENTUnauthenticatedUserException(
					"Non-authenticated user.",
					ENTUnauthenticatedUserException.UNAUTHENTICATED);
		final int pos = indexOf(name);
		if (pos == -1)
			throw new ENTFileNotFoundException("File not found");
		else if (!directoryContent.get(pos).isFile())
			throw new ENTInvalidFS_ElementTypeException(name + " isn't a file");

		FS_File file = (FS_File) directoryContent.get(pos);

		setStatus(ENTStatus.START_DOWNLOAD, file);
		Broadcaster.fireStartDownload(new StartDownloadEvent(file));

		if (destination == null || destination.isEmpty()) {
			destination = name;
		} else if (!destination.equals(name)) {
			destination = Misc.tildeToHome(destination);
			if (destination.substring(destination.length() - 1).equals(
					System.getProperty("file.separator"))) {
				destination += name;
			}
		}

		browser.clearParam();
		browser.setUrl(urlBuilder(CoreConfig.downloadFileURL));
		browser.setMethod(Browser.Method.POST);
		browser.setParam("downloadFile", HTMLEntities.htmlentities(name));
		browser.setFollowRedirects(false);
		browser.setCookieField("JSESSIONID", sessionid);
		sizeDownloaded = 0;
		browser.downloadFile(destination);
		setStatus(ENTStatus.END_DOWNLOAD, file);
		Broadcaster.fireEndDownload(new EndDownloadEvent(file));
	}

	/**
	 * Télécharge le fichier <i>name</i>.
	 * Le fichier sera enregistré dans le dossier local courant (généralement le
	 * dossier de l'application), sous le même nom que celui sous lequel il est
	 * stocké sur l'ENT.
	 * 
	 * @param name
	 *            Le nom du fichier à télécharger.
	 * @throws IOException
	 */
	public void getFile(String name) throws IOException {
		getFile(name, name);
	}

	/**
	 * Télécharge tous les fichiers contenus dans le dossier courant et ses sous
	 * dossiers.
	 * Les fichiers et dossiers seront enregistrés sous le dossier
	 * <i>destination</i>, sous le même nom que celui sous lequel il sont
	 * stockés sur l'ENT.
	 * 
	 * @param destination
	 *            Dossier de destination des fichiers et dossiers téléchargés.
	 *            Si null ou vide, ils seront enregistrés dans le répertoire
	 *            courant.
	 * @throws ENTInvalidFS_ElementTypeException
	 *             Lancée lorsque le paramètre <i>destination</i> désigne un
	 *             fichier existant.
	 * @throws IOException
	 * @see ENTDownloader#getFile(String, String)
	 * @return Le nombre de fichiers téléchargés
	 * @deprecated Remplacé par getAllFile(String destination, int maxdepth)
	 */
	@Deprecated
	public int getAllFiles(String destination) throws IOException {
		return getAllFiles(destination, -1);
	}

	/**
	 * Télécharge tous les fichiers contenus dans le dossier courant et ses sous
	 * dossiers.
	 * Les fichiers et dossiers seront enregistrés sous le dossier
	 * <i>destination</i>, sous le même nom que celui sous lequel il sont
	 * stockés sur l'ENT.
	 * 
	 * @param destination
	 *            Dossier de destination des fichiers et dossiers téléchargés.
	 *            Si null ou vide, ils seront enregistrés dans le répertoire
	 *            courant.
	 * @param maxdepth
	 *            Profondeur maximale de téléchargement. 0 (zéro) signifie que
	 *            la méthode ne va télécharger que les fichiers du dossier
	 *            courant, sans descendre dans les sous-dossiers. Une valeur
	 *            négative signifie aucune limite.
	 * @throws ENTInvalidFS_ElementTypeException
	 *             Lancée lorsque le paramètre <i>destination</i> désigne un
	 *             fichier existant.
	 * @throws IOException
	 * @see ENTDownloader#getFile(String, String)
	 * @return Le nombre de fichiers téléchargés
	 */
	public int getAllFiles(String destination, int maxdepth) throws IOException {
		int i = 0;
		if (directoryContent == null)
			throw new IllegalStateException(
					"Directory content hasn't been initialized");
		if (destination == null) {
			destination = "";
		} else if (!destination.isEmpty()) {
			if (!destination.substring(destination.length() - 1).equals(
					System.getProperty("file.separator"))) {
				destination += System.getProperty("file.separator");
			}
			destination = Misc.tildeToHome(destination);
			File dest = new File(destination);
			if (dest.isFile())
				throw new ENTInvalidFS_ElementTypeException(
						"Unable to create the required directory : a file with that name exists.");
		}
		List<FS_Element> directoryContentcp = new ArrayList<FS_Element>(
				directoryContent);
		for (FS_Element e : directoryContentcp)
			if (e.isFile()) {
				getFile(e.getName(), destination);
				++i;
			} else if (maxdepth != 0) {
				try {
					submitDirectory(e.getName());
				} catch (ParseException e1) {
					try {
						submitDirectory(e.getName());
					} catch (ParseException e2) {
						e2.printStackTrace();
					}
				}
				i += getAllFiles(destination + e.getName(), maxdepth - 1);
				try {
					submitDirectory("..");
				} catch (ParseException e1) {
					try {
						submitDirectory("..");
					} catch (ParseException e2) {
						e2.printStackTrace();
					}
				}
			}
		return i;
	}

	/**
	 * Analyse le contenu de la page passé en paramètre afin de déterminer les
	 * dossiers et fichiers contenus dans le dossier courant.
	 * 
	 * @param pageContent
	 *            Le contenu de la page à analyser
	 * @throws ParseException
	 *             Si l'analyse échoue
	 */
	private void parsePage(String pageContent) throws ParseException {
		List<List<String>> matches = new ArrayList<List<String>>();
		if (directoryContent == null) {
			directoryContent = new ArrayList<FS_Element>(50);
		} else {
			directoryContent.clear();
		}

		Misc.preg_match_all(
				"&nbsp;<a href=\"javascript:submit(File|Directory)\\('.+?'\\);\"\\s+class.*?nnel\">(.*?)</a></td><td class=\"uportal-crumbtrail\" align=\"right\">\\s+?&nbsp;([0-9][0-9]*\\.[0-9][0-9]? [MKGo]{1,2})?\\s*?</td><td class=\"uportal-crumbtrail\" align=\"right\">\\s+?&nbsp;([0-9]{2})-([0-9]{2})-([0-9]{4})&nbsp;([0-9]{2}):([0-9]{2})",
				pageContent, matches, Misc.PREG_ORDER.SET_ORDER);
		for (List<String> fileInfos : matches) {
			FS_Element file = null; // Fichier ou répertoire
			if (fileInfos.get(1).equals("Directory")) {
				file = new FS_Directory(HTMLEntities.unhtmlentities(fileInfos
						.get(2)), new GregorianCalendar(
						Integer.parseInt(fileInfos.get(6)),
						Integer.parseInt(fileInfos.get(5)),
						Integer.parseInt(fileInfos.get(4)),
						Integer.parseInt(fileInfos.get(7)),
						Integer.parseInt(fileInfos.get(8))));
			} else if (fileInfos.get(1).equals("File")) {
				file = new FS_File(
						HTMLEntities.unhtmlentities(fileInfos.get(2)),
						new GregorianCalendar(
								Integer.parseInt(fileInfos.get(6)), Integer
										.parseInt(fileInfos.get(5)), Integer
										.parseInt(fileInfos.get(4)), Integer
										.parseInt(fileInfos.get(7)), Integer
										.parseInt(fileInfos.get(8))),
						fileInfos.get(3));
			}
			if (file == null)
				throw new ParseException(
						"Error while parsing page content : unable to determine if \""
								+ fileInfos.get(2)
								+ "\" is a file or a directory.", -1);
			directoryContent.add(file);
		}
	}

	/**
	 * Construit l'url demandé en remplaçant les champs {...} par les valeurs
	 * correspondantes
	 * 
	 * @param url
	 *            URL à construire
	 * @return L'url passé en paramètre, après avoir remplacer les champs {...}.
	 */
	private String urlBuilder(String url) {
		return url.replaceAll("\\{tag\\}", tag).replaceAll("\\{uP_root\\}",
				uP_root);
	}

	/**
	 * @return Le contenu du répertoire courant
	 */
	public List<FS_Element> getDirectoryContent() {
		if (directoryContent == null)
			throw new IllegalStateException(
					"Directory content hasn't been initialized");
		return directoryContent;
	}

	/**
	 * @return Le nom du répertoire courant
	 */
	public String getDirectoryName() {
		return path.getDirectoryName();
	}

	/**
	 * @return Le nombre de dossiers dans le dossier courant.
	 */
	public int getNbDossiers() {
		int i = 0;
		for (FS_Element e : directoryContent) {
			if (e.isDirectory()) {
				++i;
			}
		}
		return i;
	}

	/**
	 * @return Le nombre de fichiers dans le dossier courant.
	 */
	public int getNbFiles() {
		int i = 0;
		for (FS_Element e : directoryContent) {
			if (e.isFile()) {
				++i;
			}
		}
		return i;
	}

	/**
	 * @return La taille totale des fichiers dans le dossier courant en octets.
	 */
	public long getFilesSize() {
		long s = 0;
		for (FS_Element e : directoryContent) {
			if (e.isFile()) {
				s += ((FS_File) e).getSize();
			}
		}
		return s;
	}

	/**
	 * @return Le chemin absolu permettant d'atteindre le répertoire courant
	 */
	public String getDirectoryPath() {
		if (path == null)
			return null;
		return path.toString();
	}

	/**
	 * @return Le nom complet de l'utilisateur, ou null si ce dernier est
	 *         inconnu
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return L'espace disque utilisé sur le service de stockage en Mo, ou -1
	 *         si ce dernier est inconnu
	 */
	public int getUsedSpace() {
		return usedSpace;
	}

	/**
	 * @return L'espace disque total sur le service de stockage en Mo, ou -1 si
	 *         ce dernier est inconnu
	 */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * @return le login utilisé pour la connexion, ou null si ce dernier est
	 *         inconnu
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * Returns the index of the first occurrence of the specified element in the
	 * {@link entDownloader.core.ENTDownloader#directoryContent
	 * directoryContent} list, or -1 if this list does not contain the element.
	 * More formally, returns the lowest index i such that (o==null ?
	 * get(i)==null : get(i).equals(o)), or -1 if there is no such index.
	 * 
	 * @param o
	 *            element to search for
	 * @return the index of the first occurrence of the specified element in the
	 *         {@link entDownloader.core.ENTDownloader#directoryContent
	 *         directoryContent} list, or -1 if this list does not contain the
	 *         element.
	 * @throws IllegalStateException
	 *             if the directory content hasn't been initialized.
	 */
	public int indexOf(Object o) throws IllegalStateException {
		if (directoryContent == null)
			throw new IllegalStateException(
					"Directory content hasn't been initialized");
		int i = 0;
		for (Object e : directoryContent) {
			if (e.equals(o))
				return i;
			++i;
		}
		return -1;
	}

	/**
	 * @deprecated Depuis la version 1.0.0 Release, utiliser les événements du
	 *             package <code>entDownloader.core.events</code>
	 */
	@Deprecated
	private void setStatus(ENTStatus status) {
		setStatus(status, null);
	}

	/**
	 * @deprecated Depuis la version 1.0.0 Release, utiliser les événements du
	 *             package <code>entDownloader.core.events</code>
	 */
	@Deprecated
	private void setStatus(ENTStatus status, Object arg) {
		this.status = status;
		setChanged();
		notifyObservers(arg);
		if (status == ENTStatus.END_DOWNLOAD
				|| status == ENTStatus.INITIALIZING_END
				|| status == ENTStatus.CHANGEDIR_END) {
			this.status = ENTStatus.READY;
		}
		if (status == ENTStatus.INVALID_CREDENTIALS) {
			this.status = ENTStatus.DISCONNECTED;
		}
	}

	/**
	 * @deprecated Depuis la version 1.0.0 Release, utiliser les événements du
	 *             package <code>entDownloader.core.events</code>
	 */
	@Deprecated
	public ENTStatus getStatus() {
		return status;
	}

	@Override
	public void onDownloadedBytes(DownloadedBytesEvent e) {
		sizeDownloaded += e.getBytesDownloaded();
		setStatus(ENTStatus.DOWNLOADING, sizeDownloaded);
	}

	/**
	 * Set a proxy to use for the ENT connection
	 * 
	 * @param host
	 *            the proxy hostname or address
	 * @param port
	 *            the port of the proxy
	 */
	public void setProxy(String host, int port) {
		browser.setHttpProxy(host, port);
	}

	/**
	 * Set a proxy to use for the ENT connection.
	 * 
	 * @param proxy
	 *            The proxy instance to use.
	 */
	public void setProxy(Proxy proxy) {
		browser.setHttpProxy(proxy);
	}

	/**
	 * Return the proxy used for the ENT connection.
	 * 
	 * @return the proxy used for the ENT connection
	 */
	public Proxy getProxy() {
		return browser.getProxy();
	}

	/**
	 * Clear the proxy configuration
	 */
	public void removeProxy() {
		browser.removeHttpProxy();
	}

	/**
	 * Set a proxy using a PAC (Proxy auto-configuration) file.
	 * 
	 * @param pacFile
	 *            PAC file location
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws IOException
	 * @see <a href="http://en.wikipedia.org/wiki/Proxy_auto-config"> PAC File
	 *      on Wikipedia</a>
	 */
	public void setProxy(String pacFile) throws Exception {
		//Efface la configuration précédente pour ne pas interférer avec l'accès au fichier PAC
		setProxy((Proxy) null);

		URL url = new URI(pacFile).toURL();
		URLConnection conn = url.openConnection();
		PacProxySelector a = new PacProxySelector(new BufferedReader(
				new InputStreamReader(conn.getInputStream())));
		Proxy proxy = a.select(new URI(CoreConfig.rootURL)).get(0);
		setProxy(proxy);
	}
}
