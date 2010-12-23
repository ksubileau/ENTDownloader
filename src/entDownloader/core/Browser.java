/*
 *  Browser.java
 *      
 *  Copyright 2010 K�vin Subileau. 
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
 * G�re les connections HTTP et permet des utilisations avanc�es tels que
 * l'obtention des sources HTML
 * et le t�l�chargement de fichiers.
 * 
 * @author K�vin Subileau
 * 
 */
public class Browser {
	/**
	 * TODO Am�lioration de la gestion des cookies : attribut path et secure,
	 * ...
	 * Voir :
	 * - http://www.java2s.com/Tutorial/Java/0320__Network/
	 * GettingtheCookiesfromanHTTPConnection.htm
	 * - http://www.google.fr/search?hl=fr&q=setting+cookie+httpurlconnection+
	 * java&aq=f&aqi=&aql=&oq=&gs_rfai=
	 */

	private Map<String, String> argv;
	Map<String, String> cookies = new HashMap<String, String>(8);
	Map<String, List<String>> headerFields = null;
	private String url;
	private String encodedParam;
	Method method;
	private boolean followRedirects = true;
	private int responseCode = -1;
	private Proxy proxy = Proxy.NO_PROXY;

	public enum Method {
		GET, POST;
	}

	/**
	 * Constructeur par d�faut de la classe Browser
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
	 * Ajoute un argument � la requ�te. Si l'argument a d�j� �t� d�fini,
	 * l'ancienne valeur sera �cras�.
	 * 
	 * @param name Nom du champ de l'argument.
	 * @param value Valeur de l'argument.
	 */
	public void setParam(String name, String value) {
		argv.put(name, value);
	}

	/**
	 * Supprime tous les arguments de requ�te pr�c�demment d�finis
	 */
	public void clearParam() {
		argv.clear();
	}

	protected void encodeParam() throws UnsupportedEncodingException {
		int argc = 0;
		encodedParam = "";
		//Encodage des param�tres de la requ�te
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
	 * Effectue la requ�te pr�c�demment configur� et retourne le texte renvoy�
	 * par le serveur (code HTML par exemple).
	 * 
	 * @return Le texte renvoy� par le serveur (code HTML ou XML par exemple).
	 * @throws IOException La connexion a �chou�.
	 */
	public String perform() throws IOException {
		OutputStreamWriter writer = null;
		BufferedReader reader = null;
		String response = "";
		try {
			//Encodage des param�tres de la requ�te
			encodeParam();

			//cr�ation de la connection
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
				//envoi de la requ�te
				writer = new OutputStreamWriter(conn.getOutputStream());
				writer.write(encodedParam);
				writer.flush();
			}

			//lecture de la r�ponse
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
				writer.close();
			} catch (Exception e) {
			}
			try {
				reader.close();
			} catch (Exception e) {
			}
		}
		return response;
	}

	/**
	 * T�l�charge le fichier d�fini par {@link #setUrl(String)} et l'enregistre
	 * � l'emplacement d�sign� par <code>destinationPath</code>.
	 * 
	 * @param destinationPath Le chemin o� le fichier sera enregistrer
	 * @throws FileNotFoundException Voir le constructeur de
	 *             {@link FileOutputStream#FileOutputStream(String)
	 *             FileOutputStream}
	 */
	public void downloadFile(String destinationPath)
			throws FileNotFoundException {
		//TODO Factorisation avec perform et lancement d'exception.
		OutputStreamWriter writer = null;
		InputStream reader = null;
		FileOutputStream writeFile = null;

		try {
			//V�rifie le chemin de destination donn�e
			File fpath = new File(destinationPath).getCanonicalFile();
			destinationPath = fpath.getPath();
			File dpath = new File(fpath.getParent());
			dpath.mkdirs();

			//Encodage des param�tres de la requ�te
			encodeParam();

			//cr�ation de la connection
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
				//envoi de la requ�te
				writer = new OutputStreamWriter(conn.getOutputStream());
				writer.write(encodedParam);
				writer.flush();
			}

			//lecture de la r�ponse
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
	 * @return L'url courante de l'objet
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url URL � d�finir
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return M�thode HTTP actuellement utilis�e
	 */
	public Method getMethod() {
		return method;
	}

	/**
	 * @param method
	 *            M�thode HTTP � utiliser
	 */
	public void setMethod(Method method) {
		if (method == Method.POST || method == Method.GET) {
			this.method = method;
		} else
			throw new IllegalArgumentException();
	}

	/**
	 * @return L'�tat actuel du suivi des redirections
	 */
	public boolean isFollowRedirects() {
		return followRedirects;
	}

	/**
	 * Active ou d�sactive le suivi des redirections HTTP (code 3xx)
	 * 
	 * @param followRedirects
	 *            Nouvelle valeur
	 */
	public void setFollowRedirects(boolean followRedirects) {
		this.followRedirects = followRedirects;
	}

	/**
	 * @return Les cookies actuellement d�finis dans une Map
	 */
	public Map<String, String> getCookieMap() {
		return cookies;
	}

	/**
	 * Red�finit les cookies envoy�s dans les requ�tes suivantes. Les pr�c�dents
	 * cookies sont �cras�s
	 * 
	 * @param cookies
	 *            Cookies � d�finir, sous la forme d'une Map dont la cl�
	 *            repr�sente le nom du champ.
	 */
	public void setCookie(Map<String, String> cookies) {
		this.cookies = cookies;
	}

	/**
	 * @return Les cookies actuellement d�finis tel qu'il sont envoy�s dans la
	 *         requ�te HTTP
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
	 * @param fieldname Le nom du champ de cookie souhait�
	 * @return La valeur du champ de cookie demand�, ou null si le champ n'est
	 *         pas d�fini.
	 */
	public String getCookieField(String fieldname) {
		return cookies.get(fieldname);
	}

	/**
	 * Ajoute ou red�finit la valeur du champ de cookie sp�cifi�
	 * 
	 * @param fieldname Le nom du champ de cookie � d�finir
	 * @param value La valeur du champ de cookie <i>fieldname</i>.
	 */
	public void setCookieField(String fieldname, String value) {
		cookies.put(fieldname, value);
	}

	/**
	 * Supprime tous les cookies actuellement d�fini
	 */
	public void delCookie() {
		cookies.clear();
	}

	/**
	 * Supprime le champ de cookie sp�cifi�
	 * 
	 * @param fieldname Le champ � supprimer
	 */
	public void delCookie(String fieldname) {
		cookies.remove(fieldname);
	}

	/**
	 * D�finit les cookies envoy�s dans les requ�tes suivantes. Les pr�c�dents
	 * cookies sont �cras�s
	 * 
	 * @param cookie Cookies � d�finir
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
	 * Obtient le code de statut du message de r�ponse HTTP
	 * 
	 * @return Le code de statut HTTP, ou -1 si aucun code ne peut �tre discern�
	 *         de la r�ponse (la r�ponse n'est pas valide) ou si aucune requ�te
	 *         n'a �t� effectu�.
	 * @see HttpURLConnection#getResponseCode()
	 */
	public int getResponseCode() {
		return responseCode;
	}

	/**
	 * @return Une Map contenant l'ensemble des ent�tes de la r�ponse HTTP, ou
	 *         null si aucune requ�te n'a �t� effectu�.
	 * @see URLConnection#getHeaderFields()
	 */
	public Map<String, List<String>> getHeaderFields() {
		return headerFields;
	}

	/**
	 * @return La valeur du champ d'ent�te portant le nom d�sign�, or null s'il
	 *         n'y a pas ce champ dans la r�ponse.
	 * @throws IllegalStateException Si aucune requ�te n'a �t� effectu�.
	 * @see URLConnection#getHeaderField(String)
	 */
	public String getHeaderField(String name) { //Non d�bogu�
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
	 * Installe un proxy HTTP � utiliser pour la connection � Internet.
	 * 
	 * @param host Le nom d'h�te ou l'adresse du proxy.
	 * @param port Le port du proxy.
	 */
	public void setHttpProxy(String host, int port) {
		proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
	}

	/**
	 * Installe un proxy HTTP � utiliser pour la connection � Internet.
	 * 
	 * @param proxy L'instance de {@link Proxy} � utiliser.
	 * @see java.net.Proxy
	 */
	public void setHttpProxy(Proxy proxy) {
		if (proxy == null) {
			proxy = Proxy.NO_PROXY;
		}
		this.proxy = proxy;
	}

	/**
	 * Retourne le proxy HTTP utilis� pour la connection � Internet.
	 * 
	 * @return Le proxy HTTP utilis� pour la connection � Internet.
	 */
	public Proxy getProxy() {
		return proxy;
	}

	/**
	 * Supprime la configuration de proxy pr�c�demment install�.
	 */
	public void removeHttpProxy() {
		setHttpProxy(null);
	}
}
