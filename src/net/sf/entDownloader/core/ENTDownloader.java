/*
 *  ENTDownloader.java
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
package net.sf.entDownloader.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
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

import net.sf.entDownloader.core.CountingMultipartEntity.ProgressListener;
import net.sf.entDownloader.core.events.AuthenticationSucceededEvent;
import net.sf.entDownloader.core.events.Broadcaster;
import net.sf.entDownloader.core.events.DirectoryChangedEvent;
import net.sf.entDownloader.core.events.DirectoryChangingEvent;
import net.sf.entDownloader.core.events.DirectoryCreatedEvent;
import net.sf.entDownloader.core.events.DownloadAbortEvent;
import net.sf.entDownloader.core.events.DownloadedBytesEvent;
import net.sf.entDownloader.core.events.ElementRenamedEvent;
import net.sf.entDownloader.core.events.ElementsDeletedEvent;
import net.sf.entDownloader.core.events.EndDownloadEvent;
import net.sf.entDownloader.core.events.EndUploadEvent;
import net.sf.entDownloader.core.events.FileAlreadyExistsEvent;
import net.sf.entDownloader.core.events.StartDownloadEvent;
import net.sf.entDownloader.core.events.StartUploadEvent;
import net.sf.entDownloader.core.events.UploadedBytesEvent;
import net.sf.entDownloader.core.exceptions.ENTElementNotFoundException;
import net.sf.entDownloader.core.exceptions.ENTInvalidElementNameException;
import net.sf.entDownloader.core.exceptions.ENTInvalidFS_ElementTypeException;
import net.sf.entDownloader.core.exceptions.ENTUnauthenticatedUserException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.ziesemer.utils.pacProxySelector.PacProxySelector;

/**
 * Classe principale de l'application, interface entre les classes externes et
 * l'ENT.<br>
 * <b>Classe singleton</b> : utilisez {@link ENTDownloader#getInstance()
 * getInstance()} pour obtenir l'instance de la classe.
 */
public class ENTDownloader {

	/** L'instance statique */
	private static ENTDownloader instance;

	/** Flag indiquant si l'utilisateur est connecté ou non */
	private boolean isLogged = false;

	/** Liste des dossiers et des fichiers du dossier courant */
	private List<FS_Element> directoryContent = null;
	/** Chemin vers le dossier courant */
	private ENTPath path = null;

	/** Paramètre de l'URL de la page de stockage **/
	private String uP_root;
	/** Paramètre de l'URL de la page de stockage **/
	private String tag;
	/** Nom de l'utilisateur */
	private String username = "";
	/** Login */
	private String login = null;
	/** Espace disque utilisé */
	private int usedSpace = -1;
	/** Espace disque total */
	private int capacity = -1;

	/** Instance d'un navigateur utilisé pour la communication avec le serveur */
	private DefaultHttpClient httpclient;
	private Proxy proxy = Proxy.NO_PROXY;

	/**
	 * Enregistre le fichier PAC utilisé pour la configuration du proxy le
	 * cas échéant. Si aucun proxy n'est utilisé ou si la configuration ne
	 * provient pas d'un fichier PAC, cette variable vaut null.
	 */
	private String proxyFile = null;

	/**
	 * Indique si des fichiers sont présents dans le presse-papier.
	 */
	private boolean canPaste = false;

	/**
	 * Variables utilisées pour permettre l'interruption
	 * des envois et téléchargements.
	 */
	private boolean transferAborted = false;
	private HttpPost uploadRequest = null;

	/**
	 * Récupère l'instance unique de la classe ENTDownloader.<br>
	 * Remarque : le constructeur est rendu inaccessible
	 */
	public static ENTDownloader getInstance() {
		if (null == instance) { // Premier appel
			instance = new ENTDownloader();
		}
		return instance;
	}

	/**
	 * Supprime l'instance en cours d'ENTDownloader.
	 * Le prochain appel à {@link ENTDownloader#getInstance() getInstance()}
	 * construira une nouvelle instance.
	 * 
	 * @since 2.0.0
	 */
	public static void reset() {
		instance = null;
	}

	private ENTDownloader() {
		httpclient = new DefaultHttpClient();

		//Définition de l'User-Agent. Ajout de la version Java, habituellement
		//automatiquement ajouté lorsque l'on utilise URLConnection.
		HttpProtocolParams.setUserAgent(
				httpclient.getParams(),
				System.getProperty("http.agent") + " Java/"
						+ System.getProperty("java.version"));

		//Utilisation de la configuration proxy système (System.setProperty)
		/*ProxySelectorRoutePlanner routePlanner = new ProxySelectorRoutePlanner(
				httpclient.getConnectionManager().getSchemeRegistry(),
				ProxySelector.getDefault());
		httpclient.setRoutePlanner(routePlanner);*/
	}

	/**
	 * Établit la connexion au serveur de l'ENT.
	 * 
	 * @param login Le nom d'utilisatateur pour la connexion.
	 * @param password Mot de passe de connexion.
	 * @return True en cas de réussite, ou false si l'authentification a échoué.
	 * @throws ParseException Impossible d'obtenir les informations de session.
	 */
	public boolean login(String login, char[] password)
			throws java.io.IOException, ParseException {
		if (isLogged())
			return true;

		//Chargement de la page de connexion.
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		String loginPage = httpclient.execute(new HttpGet(CoreConfig.loginURL),
				responseHandler);

		//Lecture du ticket d'authentification.
		List<String> ticket = new ArrayList<String>();
		Misc.preg_match(
				"input type=\"hidden\" name=\"lt\" value=\"([0-9a-zA-Z\\-]+)\" />",
				loginPage, ticket);

		List<String> execution = new ArrayList<String>();
		Misc.preg_match(
				"input type=\"hidden\" name=\"execution\" value=\"([0-9a-zA-Z]+)\" />",
				loginPage, execution);

		//Envoi des informations d'identification
		HttpPost postCredentials = new HttpPost(CoreConfig.loginURL);

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("_eventId", "submit"));
		nvps.add(new BasicNameValuePair("username", login));
		nvps.add(new BasicNameValuePair("password", new String(password)));
		nvps.add(new BasicNameValuePair("lt", ticket.get(1).toString()));
		if (execution.size() > 1) {
			nvps.add(new BasicNameValuePair("execution", execution.get(1)
					.toString()));
		}

		postCredentials.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

		HttpResponse loginResponse = httpclient.execute(postCredentials);
		EntityUtils.consume(loginResponse.getEntity());

		//Si l'authentification réussi, l'ENT renvoi une redirection 
		//temporaire (302) vers une URL du type 
		//http://ent.u-clermont1.fr/Login?ticket=XX-XX...
		//Sinon la page de connexion est retournée.
		if (loginResponse.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_MOVED_TEMP)
			return false;

		//Autre vérification possible
		/*
		if (Misc.preg_match("<div id=\"erreur\">", loginPage))
			return false;
		*/

		isLogged = true;
		this.login = login;
		Broadcaster
				.fireAuthenticationSucceeded(new AuthenticationSucceededEvent(
						login));
		
		EntityUtils.consume(loginResponse.getEntity());

		//Obtention de la page http://ent.u-clermont1.fr/Login?ticket=XX-XX...
		//Cette page renvoie un code 302 Moved Temporarily vers 
		//http://ent.u-clermont1.fr/render.userLayoutRootNode.uP
		//Dans le cas d'une requête GET, HttpClient suit cette redirection,
		//on obtient donc le code source de la page principale en mode connectée.
		loginResponse = httpclient.execute(new HttpGet(loginResponse
				.getFirstHeader("Location").getValue()));
		String rootPage = responseHandler.handleResponse(loginResponse);

		setStockageUrlParams(rootPage);
		setUserName(rootPage);
		directoryContent = null;
		path = null;
		return true;
	}

	private void setStockageUrlParams(String pageContent) throws ParseException {
		List<String> matches = new ArrayList<String>(5);
		if (!Misc
				.preg_match(
						"(?i)<a href=\"http://ent.u-clermont1.fr/render.userLayoutRootNode.uP\\?uP_root=([\\w\\|]+?)&[\\w\\d:#@%/;$()~_?\\+-=\\\\.&]*?\">Mes documents</a>",
						pageContent, matches))
			throw new ParseException(
					"Unable to find the URL parameters of the storage's service in this page.",
					-1);
		tag = "";//matches.get(1);
		uP_root = matches.get(1);
	}

	/**
	 * Obtient et définit les propriétés de l'espace de stockage (espace total
	 * et utilisé)
	 * 
	 * @param pageContent Code HTML d'une page de stockage.
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
	 * @param pageContent Code HTML d'une page de stockage.
	 * @return true si le nom a été trouvé, false sinon
	 */
	private boolean setUserName(String pageContent) {
		List<String> matches = new ArrayList<String>(4);
		if (!Misc.preg_match("(?im)<div id=\"welcome\">\\s*Bienvenue (.*?)</div>",
				pageContent, matches))
			return false;
		username = matches.get(1);
		return true;
	}

	/**
	 * Change le répertoire courant.
	 * 
	 * @param path Nom du dossier ou directive de parcours. Le dossier . est le
	 *            dossier courant : appeler cette méthode avec ce paramètre ne
	 *            change donc pas le dossier courant
	 *            mais permet de rafraîchir son contenu. Le dossier .. est le
	 *            dossier parent, il permet donc de remonter dans
	 *            l'arborescence. Enfin, le dossier / est la racine
	 *            du service de stockage de l'utilisateur.
	 * @throws ENTElementNotFoundException
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
			ENTElementNotFoundException, ENTInvalidFS_ElementTypeException,
			ParseException, IOException {
		if (path == null)
			throw new NullPointerException();

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

		changedevent.setDirectory(path);
		Broadcaster.fireDirectoryChanged(changedevent);
	}

	/**
	 * Descend ou remonte d'un pas dans l'aborescence à partir du dossier
	 * courant.
	 * 
	 * @param name
	 *            Nom du dossier ou directive de parcours. Le dossier . est le
	 *            dossier courant, appeler cette méthode avec ce paramètre ne
	 *            change donc pas le dossier courant
	 *            mais permet de rafraîchir son contenu. Le dossier .. est le
	 *            dossier parent, il permet donc de remonter dans
	 *            l'arborescence. Enfin, le dossier / ou ~ est la racine
	 *            du service de stockage de l'utilisateur. Si <code>name</code>
	 *            est vide, le dossier / est chargé.
	 * @throws ENTElementNotFoundException
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
			throws ENTElementNotFoundException,
			ENTInvalidFS_ElementTypeException, ParseException,
			ENTUnauthenticatedUserException, IOException {
		if (!isLogged())
			throw new ENTUnauthenticatedUserException(
					"Non-authenticated user.",
					ENTUnauthenticatedUserException.UNAUTHENTICATED);

		HttpUriRequest request;

		if (name.equals("/") || name.equals("~") || name.isEmpty()) {
			if (path == null) {
				path = new ENTPath();
				request = new HttpGet(urlBuilder(CoreConfig.stockageURL));
			} else {
				path.clear();
				request = new HttpGet(
						urlBuilder("http://ent.u-clermont1.fr/render.userLayoutRootNode.target.{uP_root}.uP?link=0"));
			}
			name = "/";
		} else if (name.equals(".")) {
			request = new HttpGet(urlBuilder(CoreConfig.refreshDirURL));
		} else if (name.equals("..")) {
			if (path.isRoot())
				return; //Déjà à la racine
			request = new HttpGet(urlBuilder(CoreConfig.directoryBackURL));
		} else {
			int pos = indexOf(name);
			if (pos == -1)
				throw new ENTElementNotFoundException(name);
			else if (!directoryContent.get(pos).isDirectory())
				throw new ENTInvalidFS_ElementTypeException(name);

			request = new HttpPost(urlBuilder(CoreConfig.goIntoDirectoryURL));
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("targetDirectory", name));

			((HttpPost) request).setEntity(new UrlEncodedFormEntity(params,
					HTTP.UTF_8));
		}
		String pageContent = null;
		HttpResponse response = httpclient.execute(request);

		if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_MOVED_TEMP
				&& response.getFirstHeader("Location").getValue()
						.equals(CoreConfig.loginRequestURL)) {
			isLogged = false;
			throw new ENTUnauthenticatedUserException(
					"Session expired, please login again.",
					ENTUnauthenticatedUserException.SESSION_EXPIRED);
		}

		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		pageContent = responseHandler.handleResponse(response);

		setStockageUrlParams(pageContent);
		if (capacity < 0) {
			setStorageProperties(pageContent);
		}

		if (pageContent.isEmpty()
				|| Misc.preg_match(
						"<font class=\"uportal-channel-strong\">La ressource sp&eacute;cifi&eacute;e n'existe pas.<br\\s?/?></font>",
						pageContent))
			throw new ENTElementNotFoundException(name);
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
	 * @return <code>True</code> si le téléchargement du fichier s'est terminé
	 *         normalement, <code>false</code> sinon.
	 */
	public boolean getFile(String name, String destination) throws IOException {
		transferAborted = false;
		return _getFile(name, destination);
	}

	/**
	 * @see ENTDownloader#getFile(String, String)
	 * @since 2.0.0
	 */
	private boolean _getFile(String name, String destination) throws IOException {
		if (!isLogged())
			throw new ENTUnauthenticatedUserException(
					"Non-authenticated user.",
					ENTUnauthenticatedUserException.UNAUTHENTICATED);
		final int pos = indexOf(name);
		if (pos == -1)
			throw new ENTElementNotFoundException("File not found");
		else if (!directoryContent.get(pos).isFile())
			throw new ENTInvalidFS_ElementTypeException(name + " isn't a file");

		if(transferAborted)
			return false;

		FS_File file = (FS_File) directoryContent.get(pos);

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

		//Vérification de l'existence d'un fichier portant le nom indiqué
		File fpath = new File(destination).getCanonicalFile();
		if (fpath.exists()) {
			FileAlreadyExistsEvent fileAlreadyExistsEvent = new FileAlreadyExistsEvent(
					file);
			Broadcaster.fireFileAlreadyExists(fileAlreadyExistsEvent);
			if (fileAlreadyExistsEvent.abortDownload) {
				Broadcaster.fireDownloadAbort(new DownloadAbortEvent(file));
				return false;
			}
		}

		HttpPost request = new HttpPost(urlBuilder(CoreConfig.downloadFileURL));
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("downloadFile", name));

		request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

		HttpResponse response = httpclient.execute(request);
		HttpEntity entity = response.getEntity();

		String destinationPath = fpath.getPath();
		File dpath = new File(fpath.getParent());
		dpath.mkdirs();

		InputStream responseContentStream = entity.getContent();
		FileOutputStream writeFile = new FileOutputStream(destinationPath);

		byte[] buffer = new byte[1024];
		long totalDownloaded = 0;
		int read;

		while ((read = responseContentStream.read(buffer)) > 0) {
			if(transferAborted)
			{
				writeFile.flush();
				writeFile.close();
				request.abort();
				return false;
			}
			writeFile.write(buffer, 0, read);
			totalDownloaded += read;
			Broadcaster.fireDownloadedBytes(new DownloadedBytesEvent(read, totalDownloaded));
		}
		writeFile.flush();
		writeFile.close();
		EntityUtils.consume(entity);

		Broadcaster.fireEndDownload(new EndDownloadEvent(file));
		return true;
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
	 * @return <code>True</code> si le téléchargement du fichier s'est terminé
	 *         normalement, <code>false</code> sinon.
	 */
	public boolean getFile(String name) throws IOException {
		return getFile(name, name);
	}

	/**
	 * Télécharge tous les fichiers contenus dans le dossier courant et ses sous
	 * dossiers.
	 * Les fichiers et dossiers seront enregistrés sous le dossier
	 * <i>destination</i>, sous le même nom que celui sous lequel ils sont
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
		transferAborted = false;
		return _getAllFiles(destination, maxdepth);
	}

	/**
	 * @see ENTDownloader#getAllFiles(String, int)
	 * @since 2.0.0
	 */
	private int _getAllFiles(String destination, int maxdepth) throws IOException {
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
		{
			if(transferAborted)
				return i;

			if (e.isFile()) {
				if (_getFile(e.getName(), destination)) {
					++i;
				}
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
				i += _getAllFiles(destination + e.getName(), maxdepth - 1);
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
		}
		return i;
	}

	/**
	 * Indique à l'instance que le ou les téléchargements en cours doivent être
	 * interrompus dès que possible.
	 *
	 * @since 2.0.0
	 */
	public void abortDownload() {
		transferAborted  = true;
	}

	/**
	 * Envoi le fichier local <i>filepath</i>. Le fichier sera enregistré
	 * dans le dossier courant et sous le même nom que le fichier local.
	 *
	 * @param filepath
	 *            Chemin du fichier local à envoyer.
	 * @throws FileNotFoundException Le fichier source n'existe pas ou n'est
	 * 			pas accessible.
	 * @since 2.0.0
	 */
	public void sendFile(String filepath) throws IOException, ParseException {
		sendFile(filepath, null);
	}

	/**
	 * Envoi le fichier local <i>filepath</i>. Le fichier sera enregistré
	 * dans le dossier courant et sous le nom spécifié dans le paramètre
	 * <i>name</i>, ou sous le même nom que le fichier local d'origine
	 * si le nouveau nom n'est pas indiqué dans<i>name</i>.
	 *
	 * @param filepath
	 *            Chemin du fichier local à envoyer.
	 * @param name
	 *            Nom sous lequel le fichier doit être enregistré sur l'ENT.
	 * @throws FileNotFoundException Le fichier source n'existe pas ou n'est
	 * 			pas accessible.
	 * @since 2.0.0
	 */
	public void sendFile(String filepath, String name) throws IOException, ParseException {
		sendFile(new File(filepath), name);
	}

	/**
	 * Envoi le fichier local <i>file</i>. Le fichier sera enregistré dans
	 * le dossier courant et sous le même nom que le fichier local.
	 *
	 * @param file
	 *            Fichier local à envoyer.
	 * @throws FileNotFoundException Le fichier source n'existe pas ou n'est
	 * 			pas accessible.
	 * @since 2.0.0
	 */
	public void sendFile(File file) throws IOException, ParseException {
		sendFile(file, null);
	}

	/**
	 * Envoi le fichier local <i>file</i>. Le fichier sera enregistré
	 * dans le dossier courant et sous le nom spécifié dans le paramètre
	 * <i>name</i>, ou sous le même nom que le fichier local d'origine
	 * si le nouveau nom n'est pas indiqué dans<i>name</i>.
	 *
	 * @param file
	 *            Fichier local à envoyer.
	 * @param name
	 *            Nom sous lequel le fichier doit être enregistré sur l'ENT.
	 * @throws FileNotFoundException Le fichier source n'existe pas ou n'est
	 * 			pas accessible.
	 * @since 2.0.0
	 */
	public void sendFile(File file, String name) throws IOException, ParseException {
		//TODO Vérifier présence chaine "Le fichier a bien été envoyé" dans pageContent pour valider l'envoi ?
		transferAborted = false;
		uploadRequest = null;

		if (!isLogged())
			throw new ENTUnauthenticatedUserException(
					"Non-authenticated user.",
					ENTUnauthenticatedUserException.UNAUTHENTICATED);

		//Vérification de l'existence d'un fichier portant le nom indiqué
		if (!file.canRead()) {
			throw new FileNotFoundException(file.getPath());
		}

		if (name == null || name.isEmpty())
			name = file.getName();

		if (indexOf(name) != -1)
			throw new ENTInvalidElementNameException(ENTInvalidElementNameException.ALREADY_USED, name);
		else if (!Misc.preg_match("[A-Za-z0-9 ()\\-'!°&#_àäâãéêèëîïìôöòõûüùçñÀÄÂÃÉÈËÊÌÏÎÒÖÔÕÙÜÛÇÑ.]+", name))
			throw new ENTInvalidElementNameException(ENTInvalidElementNameException.FORBIDDEN_CHAR, name);

		if(transferAborted)
			return;

		Broadcaster.fireStartUpload(new StartUploadEvent(file));

		CountingMultipartEntity mpEntity = new CountingMultipartEntity(new ProgressListener() {
			@Override
			public void transferred(long last, long total) {
				Broadcaster.fireUploadedBytes(new UploadedBytesEvent(last, total));
			}
		});
	    mpEntity.addPart("modeDav", new StringBody("upload_mode"));
	    mpEntity.addPart("Submit", new StringBody("Envoyer le fichier"));

	    ContentBody cbFile = new FileBody(file, name, "application/octet-stream", "UTF-8");
	    mpEntity.addPart("input_file", cbFile);

		if(transferAborted)
			return;

	    uploadRequest = new HttpPost(urlBuilder(CoreConfig.sendFileURL));
		uploadRequest.setEntity(mpEntity);

		HttpResponse response = httpclient.execute(uploadRequest);

		if(transferAborted || uploadRequest.isAborted())
		{
			uploadRequest = null;
			return;
		}

		if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_MOVED_TEMP
				&& response.getFirstHeader("Location").getValue()
				.equals(CoreConfig.loginRequestURL)) {
			isLogged = false;
			throw new ENTUnauthenticatedUserException(
					"Session expired, please login again.",
					ENTUnauthenticatedUserException.SESSION_EXPIRED);
		}

		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		String pageContent = null;
		pageContent = responseHandler.handleResponse(response);

		uploadRequest = null;

		parsePage(pageContent);

		if (Misc.preg_match(
						"(?i)<font class=\"uportal-channel-strong\">Impossible de traiter la requ&ecirc;te :<br\\s?/?> un fichier/dossier du m&ecirc;me nom existe d&eacute;j&agrave;.<br\\s?/?></font>",
						pageContent))
			throw new ENTInvalidElementNameException(ENTInvalidElementNameException.ALREADY_USED, name);

		if (Misc.preg_match(
						"(?i)<font class=\"uportal-channel-strong\">Il existe des caract&egrave;res non pris en charge dans le nom de votre ressource.<br\\s?/?></font>",
						pageContent))
			throw new ENTInvalidElementNameException(ENTInvalidElementNameException.FORBIDDEN_CHAR, name);

		Broadcaster.fireEndUpload(new EndUploadEvent(file));
	}

	/**
	 * Indique à l'instance que l'envoi en cours doit être interrompu dès que possible.
	 *
	 * @since 2.0.0
	 */
	public void abortUpload() {
		transferAborted = true;
		if(uploadRequest != null)
			uploadRequest.abort();
	}

	/**
	 * Créé un nouveau dossier dans le répertoire courant.
	 * 
	 * @param dirname Nom du nouveau dossier
	 * 
	 * @since 2.0.0
	 */
	public void createDirectory(String dirname) throws ParseException,
			IOException {
		if (!isLogged())
			throw new ENTUnauthenticatedUserException(
					"Non-authenticated user.",
					ENTUnauthenticatedUserException.UNAUTHENTICATED);

		if (indexOf(dirname) != -1)
			throw new ENTInvalidElementNameException(ENTInvalidElementNameException.ALREADY_USED, dirname);

		HttpPost request = new HttpPost(urlBuilder(CoreConfig.goIntoDirectoryURL));
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("Submit", "Créer le dossier"));
		params.add(new BasicNameValuePair("modeDav", "create_dir_mode"));
		params.add(new BasicNameValuePair("new_dir", dirname));

		request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

		HttpResponse response = httpclient.execute(request);

		if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_MOVED_TEMP
				&& response.getFirstHeader("Location").getValue()
						.equals(CoreConfig.loginRequestURL)) {
			isLogged = false;
			throw new ENTUnauthenticatedUserException(
					"Session expired, please login again.",
					ENTUnauthenticatedUserException.SESSION_EXPIRED);
		}
		
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		String pageContent = null;
		pageContent = responseHandler.handleResponse(response);

		if (Misc.preg_match(
						"(?i)<font class=\"uportal-channel-strong\">Impossible de traiter la requ&ecirc;te :<br\\s?/?> un fichier/dossier du m&ecirc;me nom existe d&eacute;j&agrave;.<br\\s?/?></font>",
						pageContent))
			throw new ENTInvalidElementNameException(ENTInvalidElementNameException.ALREADY_USED, dirname);

		if (Misc.preg_match(
						"(?i)<font class=\"uportal-channel-strong\">Il existe des caract&egrave;res non pris en charge dans le nom de votre ressource.<br\\s?/?></font>",
						pageContent))
			throw new ENTInvalidElementNameException(ENTInvalidElementNameException.FORBIDDEN_CHAR, dirname);

		parsePage(pageContent);

		Broadcaster.fireDirectoryCreated(new DirectoryCreatedEvent(dirname));
	}

	/**
	 * Renomme un fichier ou dossier du répertoire courant.
	 *
	 * @param oldname Nom actuel du dossier ou fichier à renommer.
	 * @param newname Nouveau nom du dossier ou fichier à renommer.
	 *
	 * @since 2.0.0
	 */
	public void rename(String oldname, String newname) throws ParseException,
			IOException {
		if (!isLogged())
			throw new ENTUnauthenticatedUserException(
					"Non-authenticated user.",
					ENTUnauthenticatedUserException.UNAUTHENTICATED);
		if(oldname.equals(newname))
			return;
		if (indexOf(oldname) == -1)
			throw new ENTElementNotFoundException(oldname);
		if (indexOf(newname) != -1)
			throw new ENTInvalidElementNameException(ENTInvalidElementNameException.ALREADY_USED, newname);

		HttpPost request = new HttpPost(urlBuilder(CoreConfig.renameURL));
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("listeFic", oldname));
		params.add(new BasicNameValuePair("modeDav", "set_name_for_rename_mode"));

		request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

		HttpResponse response = httpclient.execute(request);

		if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_MOVED_TEMP
				&& response.getFirstHeader("Location").getValue()
						.equals(CoreConfig.loginRequestURL)) {
			isLogged = false;
			throw new ENTUnauthenticatedUserException(
					"Session expired, please login again.",
					ENTUnauthenticatedUserException.SESSION_EXPIRED);
		}

		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		String pageContent = responseHandler.handleResponse(response);

		request = new HttpPost(urlBuilder(CoreConfig.renameURL));
		params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("new_name", newname));
		params.add(new BasicNameValuePair("modeDav", "rename_mode"));
		params.add(new BasicNameValuePair("Submit", "Renommer"));

		request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

		response = httpclient.execute(request);

		pageContent = responseHandler.handleResponse(response);

		if (Misc.preg_match(
						"<font class=\"uportal-channel-strong\">La ressource sp&eacute;cifi&eacute;e n'existe pas.<br\\s?/?></font>",
						pageContent))
			throw new ENTElementNotFoundException(oldname);
		if (Misc.preg_match(
						"(?i)<font class=\"uportal-channel-strong\">Impossible de traiter la requ&ecirc;te :<br\\s?/?> un fichier/dossier du m&ecirc;me nom existe d&eacute;j&agrave;.<br\\s?/?></font>",
						pageContent))
			throw new ENTInvalidElementNameException(ENTInvalidElementNameException.ALREADY_USED, newname);
		if (Misc.preg_match(
						"(?i)<font class=\"uportal-channel-strong\">Il existe des caract&egrave;res non pris en charge dans le nom de votre ressource.<br\\s?/?></font>",
						pageContent))
			throw new ENTInvalidElementNameException(ENTInvalidElementNameException.FORBIDDEN_CHAR, newname);

		parsePage(pageContent);

		Broadcaster.fireElementRenamed(new ElementRenamedEvent(oldname, newname));
	}

	/**
	 * Supprime un ou plusieurs fichiers ou dossiers du répertoire courant.
	 *
	 * @param elems Liste des dossiers ou fichiers à supprimer.
	 *
	 * @since 2.0.0
	 */
	public void delete(FS_Element[] elems) throws ParseException,
			IOException {
		delete(fsElemsToStrings(elems));
	}

	/**
	 * Supprime un ou plusieurs fichiers ou dossiers du répertoire courant.
	 *
	 * @param elems Liste des noms des dossiers ou fichiers à supprimer.
	 *
	 * @since 2.0.0
	 */
	public void delete(String[] elems) throws ParseException,
			IOException {
		if (!isLogged())
			throw new ENTUnauthenticatedUserException(
					"Non-authenticated user.",
					ENTUnauthenticatedUserException.UNAUTHENTICATED);

		for (String e : elems) {
			if (indexOf(e) == -1)
				throw new ENTElementNotFoundException(e);
		}

		HttpPost request = new HttpPost(urlBuilder(CoreConfig.deleteURL));
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (String name : elems) {
			params.add(new BasicNameValuePair("listeFic", name));
		}
		params.add(new BasicNameValuePair("modeDav", "confirm_delete_mode"));

		request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

		HttpResponse response = httpclient.execute(request);

		if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_MOVED_TEMP
				&& response.getFirstHeader("Location").getValue()
						.equals(CoreConfig.loginRequestURL)) {
			isLogged = false;
			throw new ENTUnauthenticatedUserException(
					"Session expired, please login again.",
					ENTUnauthenticatedUserException.SESSION_EXPIRED);
		}

		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		String pageContent = responseHandler.handleResponse(response);

		request = new HttpPost(urlBuilder(CoreConfig.deleteURL));
		params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("modeDav", "delete_mode"));
		params.add(new BasicNameValuePair("Submit", "Valider la suppression"));

		request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

		response = httpclient.execute(request);

		pageContent = responseHandler.handleResponse(response);

		parsePage(pageContent);

		if (Misc.preg_match(
						"<font class=\"uportal-channel-strong\">La ressource sp&eacute;cifi&eacute;e n'existe pas.<br\\s?/?></font>",
						pageContent))
			throw new ENTElementNotFoundException(elems.length == 1?elems[0]:null);

		Broadcaster.fireElementsDeleted(new ElementsDeletedEvent(elems));
	}

	/**
	 * Marque un ou plusieurs fichiers ou dossiers du répertoire courant
	 * pour la copie.
	 *
	 * @param elems Liste des noms des dossiers ou fichiers à copier.
	 *
	 * @throws ENTElementNotFoundException Un élément spécifié est introuvable.
	 *
	 * @since 2.0.0
	 */
	public void cut(String[] elems) throws ParseException,
			IOException {
		copyCut(elems, true);
	}

	/**
	 * Marque un ou plusieurs fichiers ou dossiers du répertoire courant
	 * pour la copie.
	 *
	 * @param elems Liste des dossiers ou fichiers à copier.
	 *
	 * @throws ENTElementNotFoundException Un élément spécifié est introuvable.
	 *
	 * @since 2.0.0
	 */
	public void cut(FS_Element[] elems) throws ParseException,
			IOException {
		copyCut(elems, true);
	}

	/**
	 * Marque un ou plusieurs fichiers ou dossiers du répertoire courant
	 * pour le déplacement.
	 *
	 * @param elems Liste des noms des dossiers ou fichiers à déplacer.
	 *
	 * @throws ENTElementNotFoundException Un élément spécifié est introuvable.
	 *
	 * @since 2.0.0
	 */
	public void copy(String[] elems) throws ParseException,
			IOException {
		copyCut(elems, false);
	}

	/**
	 * Marque un ou plusieurs fichiers ou dossiers du répertoire courant
	 * pour le déplacement.
	 *
	 * @param elems Liste des dossiers ou fichiers à déplacer.
	 *
	 * @throws ENTElementNotFoundException Un élément spécifié est introuvable.
	 *
	 * @since 2.0.0
	 */
	public void copy(FS_Element[] elems) throws ParseException,
			IOException {
		copyCut(elems, false);
	}

	private void copyCut(FS_Element[] elems, boolean cutMode) throws ParseException, IOException {
		copyCut(fsElemsToStrings(elems), cutMode);
	}

	private void copyCut(String[] elems, boolean cutMode) throws ParseException,
			IOException {

		if (!isLogged())
			throw new ENTUnauthenticatedUserException(
					"Non-authenticated user.",
					ENTUnauthenticatedUserException.UNAUTHENTICATED);

		HttpPost request = new HttpPost(urlBuilder(CoreConfig.copyMoveURL));
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (String name : elems) {
			params.add(new BasicNameValuePair("listeFic", name));
		}
		params.add(new BasicNameValuePair("modeDav", cutMode?"set_clipboard_for_move_mode":"set_clipboard_for_copy_mode"));

		request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

		HttpResponse response = httpclient.execute(request);

		if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_MOVED_TEMP
				&& response.getFirstHeader("Location").getValue()
						.equals(CoreConfig.loginRequestURL)) {
			isLogged = false;
			throw new ENTUnauthenticatedUserException(
					"Session expired, please login again.",
					ENTUnauthenticatedUserException.SESSION_EXPIRED);
		}

		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		String pageContent = responseHandler.handleResponse(response);

		if (Misc.preg_match(
				"(?i)<font class=\"uportal-channel-strong\">Vous n'avez pas le droit d'acc&eacute;der &agrave; une des ressources s&eacute;lectionn&eacute;es</font>",
				pageContent))
			throw new ENTElementNotFoundException();

		parsePage(pageContent);
	}

	/**
	 * Indique si le presse-papier contient un ou plusieurs éléments ou non.
	 *
	 * @return True si des éléments ont été marqués pour
	 * 		   la copie ou le déplacement.
	 */
	public boolean canPaste() {
		return canPaste;
	}

	/**
	 * Copie ou déplace les éléments précédemment marqués pour cette
	 * opération.
	 *
	 * @since 2.0.0
	 */
	public boolean paste() throws ParseException,
			IOException {

			if (!isLogged())
				throw new ENTUnauthenticatedUserException(
						"Non-authenticated user.",
						ENTUnauthenticatedUserException.UNAUTHENTICATED);

			if(!canPaste)
				return false;

			HttpPost request = new HttpPost(urlBuilder(CoreConfig.pasteURL));
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("modeDav", "confirm_paste_mode"));

			request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

			HttpResponse response = httpclient.execute(request);

			if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_MOVED_TEMP
					&& response.getFirstHeader("Location").getValue()
							.equals(CoreConfig.loginRequestURL)) {
				isLogged = false;
				throw new ENTUnauthenticatedUserException(
						"Session expired, please login again.",
						ENTUnauthenticatedUserException.SESSION_EXPIRED);
			}

			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String pageContent = responseHandler.handleResponse(response);

			if (Misc.preg_match(
					"(?i)<font class=\"uportal-channel-strong\">Impossible de (.*) :<br\\s?/?> ?un fichier/dossier du m&ecirc;me nom existe d&eacute;j&agrave;.<br\\s?/?></font>",
					pageContent))
				throw new ENTInvalidElementNameException(ENTInvalidElementNameException.ALREADY_USED);

			request = new HttpPost(urlBuilder(CoreConfig.pasteURL));
			params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("modeDav", "paste_mode"));
			params.add(new BasicNameValuePair("Submit", "Valider"));

			request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

			response = httpclient.execute(request);

			pageContent = responseHandler.handleResponse(response);

			parsePage(pageContent);

			return true;
	}

	/**
	 * Analyse le contenu de la page passé en paramètre afin de déterminer les
	 * dossiers et fichiers contenus dans le dossier courant.
	 * 
	 * @param pageContent
	 *            Le contenu de la page à analyser
	 * @throws ParseException
	 *             Si l'analyse échoue
	 * @throws ENTUnauthenticatedUserException
	 *             L'utilisateur n'a pas accès à un espace de stockage.
	 */
	private void parsePage(String pageContent) throws ParseException {
		if(Misc.preg_match("(?i)<font class=\"uportal-channel-strong\">Vous ne pouvez actuellement voir aucun espace.", pageContent))
			throw new ENTUnauthenticatedUserException(ENTUnauthenticatedUserException.UNALLOWED);

		List<List<String>> matches = new ArrayList<List<String>>();
		if (directoryContent == null) {
			directoryContent = new ArrayList<FS_Element>(50);
		} else {
			directoryContent.clear();
		}

		canPaste = Misc.preg_match("<br>Coller</a>", pageContent, null);

		Misc.preg_match_all(
				"&nbsp;<a href=\"javascript:submit(File|Directory)\\('.+?'\\);\"\\s+class.*?nnel\">(.*?)</a></td><td class=\"uportal-crumbtrail\" align=\"right\">\\s+?&nbsp;([0-9][0-9]*\\.[0-9][0-9]? [MKGo]{1,2})?\\s*?</td><td class=\"uportal-crumbtrail\" align=\"right\">\\s+?&nbsp;([0-9]{2})-([0-9]{2})-([0-9]{4})&nbsp;([0-9]{2}):([0-9]{2})",
				pageContent, matches, Misc.PREG_ORDER.SET_ORDER);
		for (List<String> fileInfos : matches) {
			FS_Element file = null; // Fichier ou répertoire
			if (fileInfos.get(1).equals("Directory")) {
				file = new FS_Directory(HTMLEntities.unhtmlentities(fileInfos
						.get(2)), new GregorianCalendar(
						Integer.parseInt(fileInfos.get(6)),
						Integer.parseInt(fileInfos.get(5)) - 1,
						Integer.parseInt(fileInfos.get(4)),
						Integer.parseInt(fileInfos.get(7)),
						Integer.parseInt(fileInfos.get(8))));
			} else if (fileInfos.get(1).equals("File")) {
				file = new FS_File(
						HTMLEntities.unhtmlentities(fileInfos.get(2)),
						new GregorianCalendar(
								Integer.parseInt(fileInfos.get(6)), Integer
										.parseInt(fileInfos.get(5)) - 1,
								Integer.parseInt(fileInfos.get(4)), Integer
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
	 * Retourne la liste des noms d'éléments à partir de la liste des éléments.
	 */
	private String[] fsElemsToStrings(FS_Element[] elems) {
		String[] elementsNames = new String[elems.length];
		int i=0;
		for (FS_Element elem : elems) {
			elementsNames[i] = elem.getName();
		}
		return elementsNames;
	}

	/**
	 * Retourne le contenu du répertoire courant.
	 */
	public List<FS_Element> getDirectoryContent() {
		if (directoryContent == null)
			throw new IllegalStateException(
					"Directory content hasn't been initialized");
		return directoryContent;
	}

	/**
	 * Obtient le nom du répertoire courant.
	 */
	public String getDirectoryName() {
		return path.getDirectoryName();
	}

	/**
	 * Retourne le nombre de dossiers dans le dossier courant.
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
	 * Retourne le nombre de fichiers dans le dossier courant.
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
	 * Retourne la taille totale des fichiers dans le dossier courant en octets.
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
	 * Obtient le chemin absolu permettant d'atteindre le répertoire courant.
	 */
	public String getDirectoryPath() {
		if (path == null)
			return null;
		return path.toString();
	}

	/**
	 * Retourne le nom complet de l'utilisateur, ou null si ce dernier est
	 * inconnu.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Retourne l'espace disque utilisé sur le service de stockage en Mo, ou -1
	 * si ce dernier est inconnu
	 */
	public int getUsedSpace() {
		return usedSpace;
	}

	/**
	 * Retourne l'espace disque total sur le service de stockage en Mo, ou -1 si
	 * ce dernier est inconnu.
	 */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * Retourne le login utilisé pour la connexion, ou null si ce dernier est
	 * inconnu.
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * Retourne vrai si l'utilisateur est connecté à l'ENT.
	 * 
	 * @since 2.0.0
	 */
	public boolean isLogged() {
		return isLogged;
	}

	/**
	 * Retourne l'index de la première occurrence de l'élément spécifié dans la
	 * liste {@link net.sf.entDownloader.core.ENTDownloader#directoryContent
	 * directoryContent}, ou -1 si la liste ne contient pas cet élément. Plus
	 * formellement, retourne le plus petit index i tel que (o==null ?
	 * get(i)==null : get(i).equals(o)), ou -1 si cet index n'existe pas.
	 * 
	 * @param o L'élément à rechercher
	 * @return L'index de la première occurrence de l'élément spécifié dans la
	 *         liste
	 *         {@link net.sf.entDownloader.core.ENTDownloader#directoryContent
	 *         directoryContent}, ou -1 si la liste ne contient pas cet élément.
	 * @throws IllegalStateException Si le répertoire courant n'a pas été
	 *             chargé.
	 */
	private int indexOf(Object o) throws IllegalStateException {
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
	 * Installe un proxy HTTP à utiliser pour la connexion à l'ENT.
	 * 
	 * @param host Le nom d'hôte ou l'adresse du proxy.
	 * @param port Le port du proxy.
	 */
	public void setProxy(String host, int port) {
		setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port)));
		proxyFile = null;
	}

	/**
	 * Installe un proxy HTTP à utiliser pour la connexion à l'ENT.
	 * 
	 * @param proxy L'instance de java.net.Proxy à utiliser.
	 * @see java.net.Proxy
	 */
	public void setProxy(Proxy proxy) {
		if (proxy == null || proxy.equals(Proxy.NO_PROXY)) {
			removeProxy();
			return;
		}

		InetSocketAddress proxySocket = ((InetSocketAddress) proxy.address());
		/*
		System.setProperty("http.proxyHost", proxySocket.getHostName());
		System.setProperty("http.proxyPort", proxySocket.getPort());
		System.setProperty("https.proxyHost", proxySocket.getHostName());
		System.setProperty("https.proxyPort", proxySocket.getPort());
		*/
		HttpHost proxyHost = new HttpHost(proxySocket.getHostName(),
				proxySocket.getPort());
		httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
				proxyHost);

		this.proxy = proxy;
		proxyFile = null;
	}

	/**
	 * Retourne le proxy HTTP utilisé pour la connexion à l'ENT.
	 * 
	 * @return Le proxy HTTP utilisé pour la connexion à l'ENT.
	 */
	public Proxy getProxy() {
		return proxy;
	}

	/**
	 * Retourne le fichier PAC utilisé pour la configuration du proxy le
	 * cas échéant.
	 * 
	 * Si aucun proxy n'est utilisé ou si la configuration ne
	 * provient pas d'un fichier PAC, cette méthode retourne <code>null</code>.
	 * 
	 * @return Le fichier PAC utilisé pour la configuration du proxy.
	 */
	public String getProxyFile() {
		return proxyFile;
	}

	/**
	 * Supprime la configuration de proxy précédemment installé.
	 */
	public void removeProxy() {
		/*
		System.setProperty("http.proxyHost", null);
		System.setProperty("http.proxyPort", null);
		System.setProperty("https.proxyHost", null);
		System.setProperty("https.proxyPort", null);
		*/
		httpclient.getParams()
				.setParameter(ConnRoutePNames.DEFAULT_PROXY, null);
		proxy = Proxy.NO_PROXY;
		proxyFile = null;
	}

	/**
	 * Installe un proxy HTTP à utiliser pour la connexion à l'ENT en utilisant
	 * un fichier PAC (Proxy auto-configuration).
	 * 
	 * @param pacFile Emplacement du fichier PAC
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws IOException
	 * @see <a href="http://en.wikipedia.org/wiki/Proxy_auto-config"> PAC File
	 *      on Wikipedia</a>
	 */
	public void setProxy(String pacFile) throws Exception {
		//Efface la configuration précédente pour ne pas interférer avec l'accès au fichier PAC
		setProxy((Proxy) null);

		File localFile = new File(pacFile);
		URL url;
		if (localFile.canRead()) {
			url = localFile.toURI().toURL();
		} else {
			url = new URI(pacFile).toURL();
		}
		URLConnection conn = url.openConnection();
		PacProxySelector a = new PacProxySelector(new BufferedReader(
				new InputStreamReader(conn.getInputStream())));
		Proxy proxy = a.select(new URI(CoreConfig.rootURL)).get(0);
		setProxy(proxy);
		proxyFile = pacFile;
	}
}
