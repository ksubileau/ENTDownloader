/*
 *  Browser.java
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
package entDownloader.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import entDownloader.core.events.Broadcaster;
import entDownloader.core.events.DownloadedBytesEvent;

/**
 * Gère les connexions HTTP et permet des utilisations avancées tels que
 * l'obtention des sources HTML
 * et le téléchargement de fichiers.
 * 
 */
public class Browser {
	/**
	 * TODO Amélioration de la gestion des cookies : attribut path et secure,
	 * ...
	 * Voir :
	 * - http://www.java2s.com/Tutorial/Java/0320__Network/
	 * GettingtheCookiesfromanHTTPConnection.htm
	 * - http://www.google.fr/search?hl=fr&q=setting+cookie+httpurlconnection+
	 * java&aq=f&aqi=&aql=&oq=&gs_rfai=
	 */

	private Map<String, String> argv;
	private Map<String, String> cookies = new HashMap<String, String>(8);
	private Map<String, List<String>> headerFields = null;
	private String url;
	private String encodedParam;
	private Method method;
	private boolean followRedirects = true;
	private int responseCode = -1;
	private Proxy proxy = Proxy.NO_PROXY;

	public enum Method {
		GET, POST;
	}

	/**
	 * Constructeur par défaut de la classe Browser
	 */
	public Browser() {
		this.argv = new HashMap<String, String>(8);
		url = "";
		encodedParam = "";
		method = Method.GET;
	}

	/**
	 * Constructeur de la classe Browser
	 * 
	 * @param url
	 *            Adresse de la page web
	 */
	public Browser(String url) {
		this();
		setUrl(url);
	}

	/**
	 * Ajoute un argument à la requête. Si l'argument a déjà été défini,
	 * l'ancienne valeur sera écrasé.
	 * 
	 * @param name Nom du champ de l'argument.
	 * @param value Valeur de l'argument.
	 */
	public void setParam(String name, String value) {
		argv.put(name, value);
	}

	/**
	 * Supprime tous les arguments de requête précédemment définis
	 */
	public void clearParam() {
		argv.clear();
	}

	protected void encodeParam() throws UnsupportedEncodingException {
		int argc = 0;
		encodedParam = "";
		//Encodage des paramètres de la requête
		if (!argv.isEmpty()) {
			if (method == Method.GET) {
				encodedParam = "?";
			}
			for (Map.Entry<String, String> e : argv.entrySet()) {
				if (argc > 0) {
					encodedParam += "&";
				}
				encodedParam += URLEncoder.encode(e.getKey(), "UTF-8") + "="
						+ URLEncoder.encode(e.getValue(), "UTF-8");
				++argc;
			}
		}
	}

	/**
	 * Effectue la requête précédemment configuré et retourne le texte renvoyé
	 * par le serveur (code HTML par exemple).
	 * 
	 * @return Le texte renvoyé par le serveur (code HTML ou XML par exemple).
	 * @throws IOException La connexion a échoué.
	 */
	public String perform() throws IOException {
		OutputStreamWriter writer = null;
		BufferedReader reader = null;
		String response = "";
		try {
			//Encodage des paramètres de la requête
			encodeParam();

			//création de la connexion
			URL url;
			if (method == Method.POST) {
				url = new URL(this.url);
			} else {
				url = new URL(this.url + encodedParam);
			}

			HttpURLConnection conn = (HttpURLConnection) url
					.openConnection(proxy);
			conn.setInstanceFollowRedirects(followRedirects);
			conn.setDoOutput(true);
			if (cookies != null && !cookies.isEmpty()) {
				conn.setRequestProperty("Cookie", getCookie());
			}

			if (method == Method.POST) {
				//envoi de la requête
				writer = new OutputStreamWriter(conn.getOutputStream());
				writer.write(encodedParam);
				writer.flush();
			}

			//lecture de la réponse
			setCookies(conn.getHeaderField("Set-Cookie"));
			responseCode = conn.getResponseCode();
			headerFields = conn.getHeaderFields();

			reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String ligne;
			while ((ligne = reader.readLine()) != null) {
				response += ligne;
			}
		} catch (ConnectException e1) {
			//TODO Essayer de générer cette exception; elle permet de détecter un mauvais port sur proxy, mais besoin de la lancer tel quel pour cela, et pas dans une UnknownHost : est possible ss pb ?
			UnknownHostException ex = new UnknownHostException();
			ex.initCause(e1);
			throw new UnknownHostException();
		} catch (UnknownHostException e) {
			throw e;
		} catch (NoRouteToHostException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(writer != null)
					writer.close();
			} catch (IOException e) {
			}
			try {
				if(reader != null)
					reader.close();
			} catch (IOException e) {
			}
		}
		return response;
	}

	/**
	 * Télécharge le fichier défini par {@link #setUrl(String)} et l'enregistre
	 * à l'emplacement désigné par <code>destinationPath</code>.
	 * 
	 * @param destinationPath Le chemin où le fichier sera enregistrer
	 * @throws FileNotFoundException Voir le constructeur de
	 *             java.io.FileOutputStream
	 */
	public void downloadFile(String destinationPath)
			throws FileNotFoundException {
		//TODO Factorisation avec perform et lancement d'exception.
		OutputStreamWriter writer = null;
		InputStream reader = null;
		FileOutputStream writeFile = null;

		try {
			//Vérifie le chemin de destination donnée
			File fpath = new File(destinationPath).getCanonicalFile();
			destinationPath = fpath.getPath();
			File dpath = new File(fpath.getParent());
			dpath.mkdirs();

			//Encodage des paramètres de la requête
			encodeParam();

			//création de la connexion
			URL url;
			if (method == Method.POST) {
				url = new URL(this.url);
			} else {
				url = new URL(this.url + encodedParam);
			}

			HttpURLConnection conn = (HttpURLConnection) url
					.openConnection(proxy);
			conn.setInstanceFollowRedirects(followRedirects);
			conn.setDoOutput(true);
			if (cookies != null && !cookies.isEmpty()) {
				conn.setRequestProperty("Cookie", getCookie());
			}

			if (method == Method.POST) {
				//envoi de la requête
				writer = new OutputStreamWriter(conn.getOutputStream());
				writer.write(encodedParam);
				writer.flush();
			}

			//lecture de la réponse
			setCookies(conn.getHeaderField("Set-Cookie"));
			responseCode = conn.getResponseCode();
			headerFields = conn.getHeaderFields();
			reader = conn.getInputStream();

			writeFile = new FileOutputStream(destinationPath);
			byte[] buffer = new byte[1024];
			int read;

			while ((read = reader.read(buffer)) > 0) {
				writeFile.write(buffer, 0, read);
				Broadcaster.fireDownloadedBytes(new DownloadedBytesEvent(read));
			}
			writeFile.flush();
		} catch (FileNotFoundException e1) {
			throw e1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (Exception e) {
			}
			try {
				reader.close();
			} catch (Exception e) {
			}
			try {
				writeFile.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Retourne l'url courante de l'objet.
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Définit l'url cible de la requête.
	 * 
	 * @param url URL à définir
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Retourne la méthode HTTP actuellement utilisée.
	 * 
	 */
	public Method getMethod() {
		return method;
	}

	/**
	 * Configure la méthode HTTP à utiliser (POST ou GET).
	 * 
	 * @param method Méthode HTTP à utiliser.
	 */
	public void setMethod(Method method) {
		if (method == Method.POST || method == Method.GET) {
			this.method = method;
		} else
			throw new IllegalArgumentException();
	}

	/**
	 * Retourne l'état actuel du suivi des redirections.
	 * 
	 * @return True si le suivi des redirections est activé, false sinon.
	 */
	public boolean isFollowRedirects() {
		return followRedirects;
	}

	/**
	 * Active ou désactive le suivi des redirections HTTP (code 3xx)
	 * 
	 * @param followRedirects
	 *            Nouvelle valeur
	 */
	public void setFollowRedirects(boolean followRedirects) {
		this.followRedirects = followRedirects;
	}

	/**
	 * Retourne les cookies actuellement définis dans une Map
	 */
	public Map<String, String> getCookieMap() {
		return cookies;
	}

	/**
	 * Redéfinit les cookies envoyés dans les requêtes suivantes. Les précédents
	 * cookies sont écrasés
	 * 
	 * @param cookies
	 *            Cookies à définir, sous la forme d'une Map dont la clé
	 *            représente le nom du champ.
	 */
	public void setCookie(Map<String, String> cookies) {
		this.cookies = cookies;
	}

	/**
	 * Retourne les cookies actuellement définis tel qu'ils sont envoyés dans la
	 * requête HTTP.
	 */
	public String getCookie() {
		String cookie = "";
		boolean isFirst = true;
		if (!cookies.isEmpty()) {
			for (String string : cookies.keySet()) {
				if (!isFirst) {
					cookie += "; ";
				}
				String str = string;
				cookie += str;
				str = cookies.get(str);
				if (str != null) {
					cookie += "=" + str;
				}
				isFirst &= false;
			}
		}
		return cookie;
	}

	/**
	 * Retourne la valeur du champ de cookie portant le nom
	 * <code>fieldname</code>, ou null si ce champ n'existe pas.
	 * 
	 * @param fieldname Le nom du champ de cookie souhaité
	 * @return La valeur du champ de cookie demandé, ou null si le champ n'est
	 *         pas défini.
	 */
	public String getCookieField(String fieldname) {
		return cookies.get(fieldname);
	}

	/**
	 * Ajoute ou redéfinit la valeur du champ de cookie spécifié
	 * 
	 * @param fieldname Le nom du champ de cookie à définir
	 * @param value La valeur du champ de cookie <i>fieldname</i>.
	 */
	public void setCookieField(String fieldname, String value) {
		cookies.put(fieldname, value);
	}

	/**
	 * Supprime tous les cookies actuellement défini
	 */
	public void delCookie() {
		cookies.clear();
	}

	/**
	 * Supprime le champ de cookie spécifié
	 * 
	 * @param fieldname Le champ à supprimer
	 */
	public void delCookie(String fieldname) {
		cookies.remove(fieldname);
	}

	/**
	 * Définit les cookies envoyés dans les requêtes suivantes. Les précédents
	 * cookies sont écrasés
	 * 
	 * @param cookie Cookies à définir
	 */
	public void setCookies(String cookie) {
		if (cookie != null && !cookie.isEmpty()) {
			cookies.clear();
			StringTokenizer st = new StringTokenizer(cookie, ";");
			int equalPos;

			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				equalPos = token.indexOf("=");
				String name;
				String value;
				if (equalPos != -1) {
					name = token.substring(0, equalPos).trim();
					value = token.substring(equalPos + 1);
				} else {
					name = token.trim();
					value = null;
				}
				cookies.put(name, value);
			}
		}
	}

	/**
	 * Obtient le code de statut du message de réponse HTTP
	 * 
	 * @return Le code de statut HTTP, ou -1 si aucun code ne peut être discerné
	 *         de la réponse (la réponse n'est pas valide) ou si aucune requête
	 *         n'a été effectué.
	 * @see HttpURLConnection#getResponseCode()
	 */
	public int getResponseCode() {
		return responseCode;
	}

	/**
	 * Retourne une Map contenant l'ensemble des entêtes de la réponse HTTP, ou
	 * null si aucune requête n'a été effectué.
	 * 
	 * @see URLConnection#getHeaderFields()
	 */
	public Map<String, List<String>> getHeaderFields() {
		return headerFields;
	}

	/**
	 * Retourne la valeur du champ d'entête portant le nom désigné, ou null s'il
	 * n'y a pas ce champ dans la réponse.
	 * 
	 * @throws IllegalStateException Si aucune requête n'a été effectué.
	 * @see URLConnection#getHeaderField(String)
	 */
	public String getHeaderField(String name) { //Non débogué
		if (headerFields == null)
			throw new IllegalStateException("No request has been made.");
		List<String> values = headerFields.get(name);
		if (values == null)
			return null;
		String value = "";
		for (int i = 0; i < values.size() - 1; i++) {
			value += values.get(i) + ", ";
		}
		value += values.get(values.size() - 1);
		return value;
	}

	/**
	 * Installe un proxy HTTP à utiliser pour la connexion à Internet.
	 * 
	 * @param host Le nom d'hôte ou l'adresse du proxy.
	 * @param port Le port du proxy.
	 */
	public void setHttpProxy(String host, int port) {
		proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
	}

	/**
	 * Installe un proxy HTTP à utiliser pour la connexion à Internet.
	 * 
	 * @param proxy L'instance de java.net.Proxy à utiliser.
	 * @see java.net.Proxy
	 */
	public void setHttpProxy(Proxy proxy) {
		if (proxy == null) {
			proxy = Proxy.NO_PROXY;
		}
		this.proxy = proxy;
	}

	/**
	 * Retourne le proxy HTTP utilisé pour la connexion à Internet.
	 * 
	 * @return Le proxy HTTP utilisé pour la connexion à Internet.
	 */
	public Proxy getProxy() {
		return proxy;
	}

	/**
	 * Supprime la configuration de proxy précédemment installé.
	 */
	public void removeHttpProxy() {
		setHttpProxy(null);
	}
}
