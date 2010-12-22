/*
 *  Updater.java
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

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class Updater {
	private boolean available;
	private String version;
	private Document xmlUpdateInformation;
	private String location;
	private ArrayList<String> added;
	private ArrayList<String> changed;
	private ArrayList<String> fixed;
	private ArrayList<String> other;
	private Calendar datePub;

	/**
	 * Construit une nouvelle instance d'Updater et charge le fichier de version
	 * par défaut.
	 * 
	 * @throws Exception
	 *             URL incorrecte, format de fichier invalide ...
	 */
	public Updater() throws Exception {
		this("http://entdownloader.sourceforge.net/checkUpdate.php?v="
				+ java.net.URLEncoder.encode(
						CoreConfig.getString("ProductInfo.version"), "UTF-8"));
	}

	/**
	 * Construit une nouvelle instance d'Updater et charge le fichier de version
	 * indiqué en argument.
	 * 
	 * @param updateURL
	 *            L'URL du fichier XML contenant les informations de version.
	 * @throws Exception
	 *             URL incorrecte, format de fichier invalide ...
	 */
	public Updater(String updateURL) throws Exception {
		available = false;
		version = null;
		location = null;
		InputStream stream = null;
		try {
			DocumentBuilderFactory fabrique = DocumentBuilderFactory
					.newInstance();

			DocumentBuilder constructeur = fabrique.newDocumentBuilder();
			//Désactive l'affichage des erreurs sur la sortie d'erreur
			constructeur.setErrorHandler(new ErrorHandler() {
				@Override
				public void warning(SAXParseException exception)
						throws SAXException {
				}

				@Override
				public void fatalError(SAXParseException exception)
						throws SAXException {
				}

				@Override
				public void error(SAXParseException exception)
						throws SAXException {
				}
			});

			URL url = new URL(updateURL);
			stream = url.openStream();
			xmlUpdateInformation = constructeur.parse(stream);

			Element racine = xmlUpdateInformation.getDocumentElement();
			NodeList liste = racine.getElementsByTagName("NeedToBeUpdated");

			if (liste.getLength() != 0) {
				Element e = (Element) liste.item(0);
				if (e.getTextContent().equals("yes")) {
					available = true;
				}
			}
		} catch (Exception e) {
			try {
				stream.close();
			} catch (Exception e1) {
			}
			throw e;
		}
	}

	/**
	 * Détermine si le programme est à jour ou non.
	 * 
	 * @return True si le logiciel est à jour, false sinon.
	 */
	public boolean isUpToDate() {
		return !available;
	}

	/**
	 * Retourne le numéro de la nouvelle version, ou null si aucune mise à jour
	 * n'est disponible.
	 */
	public String version() {
		if (!available)
			return null;
		if (version == null) {
			version = getElementTextContentByTagName("Version");
			if (version == null) {
				version = "";
			}
		}
		return version;
	}

	/**
	 * Retourne l'adresse de téléchargement de la mise à jour, ou null si le
	 * programme est à jour.
	 */
	public String location() {
		if (!available)
			return null;
		if (location == null) {
			location = getElementTextContentByTagName("Location");
			if (location == null) {
				location = "";
			}
		}
		return location;
	}

	/**
	 * Retourne la date de publication de la nouvelle version, ou null si aucune
	 * mise à jour n'est disponible.
	 */
	public Calendar datePublication() {
		if (!available)
			return null;
		if (datePub == null) {
			long timestamp = Long
					.parseLong(getElementTextContentByTagName("Date"));
			datePub = Calendar.getInstance();
			datePub.setTimeInMillis(timestamp * 1000);
		}
		return datePub;
	}

	/**
	 * Retourne les nouveautés de la mise à jour, ou null si le programme est à
	 * jour.
	 */
	public ArrayList<String> changelog_added() {
		if (!available)
			return null;
		if (added == null) {
			added = getElementsTextContentByTagName("Add");
		}

		return added;
	}

	/**
	 * Retourne les fonctionnalités modifiées dans la nouvelle version, ou null
	 * si aucune mise à jour n'est disponible.
	 */
	public ArrayList<String> changelog_changed() {
		if (!available)
			return null;
		if (changed == null) {
			changed = getElementsTextContentByTagName("Change");
		}

		return changed;
	}

	/**
	 * Retourne les bogues corrigés par la mise à jour, ou null si le programme
	 * est à jour.
	 */
	public ArrayList<String> changelog_fixed() {
		if (!available)
			return null;
		if (fixed == null) {
			fixed = getElementsTextContentByTagName("Fix");
		}

		return fixed;
	}

	/**
	 * Retourne les autres changements de la nouvelle version, ou null si aucune
	 * mise à jour n'est disponible.
	 */
	public ArrayList<String> changelog_other() {
		if (!available)
			return null;
		if (other == null) {
			other = getElementsTextContentByTagName("Other");
		}

		return other;
	}

	/**
	 * Retourne le contenu textuel du premier noeud portant le nom indiqué en
	 * paramètre, ou null si aucun noeud correspondant n'est trouvé.
	 * 
	 * @param tagName
	 *            Le nom du noeud désiré.
	 * @throws DOMException
	 */
	private String getElementTextContentByTagName(String tagName)
			throws DOMException {
		Element racine = xmlUpdateInformation.getDocumentElement();
		NodeList liste = racine.getElementsByTagName(tagName);

		if (liste.getLength() != 0) {
			Element e = (Element) liste.item(0);
			return e.getTextContent();
		} else
			return null;
	}

	/**
	 * Retourne les contenus textuels des noeuds portant le nom indiqué en
	 * paramètre, ou null si aucun noeud correspondant n'est trouvé.
	 * 
	 * @param tagName
	 *            Le nom du noeud désiré.
	 * @throws DOMException
	 */
	private ArrayList<String> getElementsTextContentByTagName(String tagName)
			throws DOMException {
		Element racine = xmlUpdateInformation.getDocumentElement();
		NodeList liste = racine.getElementsByTagName(tagName);

		if (liste.getLength() != 0) {
			ArrayList<String> list = new ArrayList<String>();
			int nbElements = liste.getLength();
			for (int i = 0; i < nbElements; ++i) {
				Element e = (Element) liste.item(i);
				list.add(e.getTextContent());
			}
			return list;
		} else
			return null;
	}
}
