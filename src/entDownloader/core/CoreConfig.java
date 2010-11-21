/*
 *  CoreConfig.java
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

import java.awt.Color;
import java.awt.Font;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class CoreConfig {
	// {tag}, {uP_root} seront remplacés par les valeurs des variables correspondantes;
	
	/**URL de la racine du service */
	public static final String rootURL = "http://ent.u-clermont1.fr/";
	/**Adresse de la page de connexion */
	public static final String loginURL = "https://cas.u-clermont1.fr/cas/login?service=http://ent.u-clermont1.fr/Login";
	/**Adresse utilisée dans la redirection http pour indiquer que la session a expiré*/
	public static final String loginRequestURL = rootURL + "Login";
	/**Adresse du service de stockage de documents*/
	public static final String stockageURL = rootURL + "tag.{tag}.render.userLayoutRootNode.uP?uP_root={uP_root}&uP_sparam=activeTab&activeTab=8";
	/**Adresse de soumission du formulaire permettant la descente de l'arborescence*/
	public static final String goIntoDirectoryURL = rootURL + "tag.{tag}.render.userLayoutRootNode.target.{uP_root}.uP#{uP_root}";
	/**Adresse de téléchargement d'un fichier*/
	public static final String downloadFileURL = rootURL + "worker/download/tag.idempotent.worker.download.target.{uP_root}.uP";
	/**Adresse de retour dans le dossier parent*/
	public static final String directoryBackURL = rootURL + "tag.{tag}.render.userLayoutRootNode.target.{uP_root}.uP?modeDav=directory_back#{uP_root}";
	/**Adresse d'actualisation du dossier courant*/
	public static final String refreshDirURL = rootURL + "tag.{tag}.render.userLayoutRootNode.target.{uP_root}.uP?modeDav=show_current_dir_mode#{uP_root}";
	/**Active ou désactive la reconnection automatique après l'expiration de la session*/
	public static final boolean autoLogin = true;
	public static boolean optimizePath = true;
	
	public static final boolean debug = false;
	

	private static final String BUNDLE_NAME = "entDownloader.ressources.configuration";

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	public CoreConfig() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	public static Color getForegroundColor() {
		return new Color(0,0,0,255);
	}

	public static Color getBackgroundColor() {
		return new Color(255,255,255,255);
	}

	public static Font getPanelsFont() {
		return Font.decode("Arial-PLAIN-12");
	}
}
