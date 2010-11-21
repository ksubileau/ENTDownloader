/*
 *  ENTPath.java
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

import java.security.InvalidParameterException;
import java.util.Iterator;

/**
 * Repr�sente un chemin vers un fichier ou un dossier stock� sur l'ENT. Cette
 * classe ne fournit qu'une repr�sentation
 * locale du chemin, et ne fournit par cons�quent aucun contr�le sur l'existence
 * r�elle de la ressource repr�sent�e.
 */
public class ENTPath implements Iterable<String> {
	private Stack<String> path = null;

	/**
	 * Construit un nouveau chemin initialis� � la racine.
	 */
	public ENTPath() {
		path = new Stack<String>();
		path.push("/");
	}

	/**
	 * Construit un nouveau chemin � partir de sa repr�sentation textuelle. Le
	 * chemin donn� doit �tre absolu.
	 */
	public ENTPath(String path) {
		this();
		if (!isAbsolute(path))
			throw new InvalidParameterException();
		for (String name : path.split("/")) {
			if (name.isEmpty()) {
				continue;
			}
			if (name.equals("..")) {
				if (!this.path.isEmpty()) {
					this.path.pop();
				}
			} else if (!name.equals(".")) {
				this.path.push(name);
			}
		}
	}

	/**
	 * Copie le chemin donn� en param�tre
	 */
	public ENTPath(ENTPath path) {
		this.path = new Stack<String>(path.path);
	}

	/**
	 * Fusionne le chemin donn� en param�tre avec le chemin actuel
	 * 
	 * @param path
	 *            Le chemin de d�placement
	 */
	public void goTo(String path) {
		if (isAbsolute(path)) { //Si le chemin donn� est absolu
			this.path.clear();
			this.path.push("/"); //On repart de la racine
		}
		for (String name : path.split("/")) { //Pour chaque �l�ment du chemin
			if (name.isEmpty()) {
				continue; //Si l'�lement est vide, on passe au suivant
			}
			if (name.equals("..")) { //Sinon si on demande de remonter d'un noeud
				if (!isRoot()) {
					this.path.pop(); //On d�pile
				}
			} else if (!name.equals(".")) {
				this.path.push(name);
				//Sinon si l'�lement est . on ne fait rien
			}
		}
	}

	/**
	 * Calcul et retourne le chemin relatif le plus simple correspondant au
	 * chemin absolu donn� en param�tre par rapport
	 * au dossier courant actuellement repr�sent� par cette instance.
	 * 
	 * @param destination
	 *            le chemin absolu � relativiser.
	 * @return Le chemin relatif le plus simple permettant d'acc�der au dossier
	 *         <i>destination</i> � partir du dossier courant
	 */
	public String getRelative(ENTPath destination) {
		int nbremontes = compareTo(destination), pathLength = destination.path
				.size();
		String relpath = "";
		if (nbremontes > 0) {
			relpath += "../";
			for (int i = 2; i <= nbremontes; ++i) {
				relpath += "../";
			}
		}
		for (int i = path.size() - nbremontes + 1; i <= pathLength; ++i) {
			relpath += destination.path.get(pathLength - i) + "/";
		}

		if (relpath.isEmpty()) {
			relpath = ".";
		}
		return relpath;
	}

	/**
	 * Compare le chemin donn� avec le chemin de l'instance et retourne le
	 * nombre de dossiers devant �tre remont�s pour trouver un dossier commun au
	 * deux chemins.<br>
	 * Exemples :
	 * <ul>
	 * <li>Pour le dossier courant /home/user<i>/musics/playlists/artists</i> et
	 * le chemin /home/user<i>/settings</i>, la m�thode retournera 3.</li>
	 * <li>Pour le dossier courant /home/user<i>/settings</i> et le chemin
	 * /home/user<i>/musics/playlists/artists</i>, la m�thode retournera 1.</li>
	 * <li>Pour le dossier courant /<i>etc/acpi</i> et le chemin
	 * /<i>home/user/musics/playlists</i>, la m�thode retournera 2.</li>
	 * </ul>
	 * La m�thode ne retourne donc jamais plus de this.path.size()-1
	 * 
	 * @param absolutePath
	 *            le chemin absolu � comparer
	 * @return Le nombre de dossiers devant �tre remont�s pour trouver un
	 *         dossier commun au deux chemins.
	 */
	private int compareTo(ENTPath absolutePath) {
		int thisPathSize = this.path.size(), absolutePathSize = absolutePath.path
				.size(), nbdiff = thisPathSize, shorterPath = Math.min(
				thisPathSize, absolutePathSize);

		for (int i = 1; i <= shorterPath; ++i, --nbdiff)
			if (!path.get(thisPathSize - i).equals(
					absolutePath.path.get(absolutePathSize - i))) {
				break;
			}
		return nbdiff;
	}

	public static boolean isAbsolute(String path) {
		return path.charAt(0) == '/';
	}

	public static boolean isAbsolute(String[] path) {
		return path[0].equals("/");
	}

	public int getNbRequests() {
		return getNbRequests(this);
	}

	public static int getNbRequests(ENTPath path) {
		return path.path.size();
	}

	public static int getNbRequests(String path) {
		return getNbRequests(path.split("/"));
	}

	public static int getNbRequests(String[] path) {
		return path.length;
	}

	/**
	 * Returns a string representation of the absolute path.
	 * 
	 * @return a string representation of the absolute path.
	 */
	@Override
	public String toString() {
		String path = this.path.getLast(); //Racine
		for (int i = this.path.size() - 2; i >= 0; --i) {
			if (i != this.path.size() - 2) {
				path += "/";
			}
			path += this.path.get(i);
		}
		return path;
	}

	/**
	 * Retourne un it�rateur sur les �l�ments du chemin, partant de la racine.
	 * 
	 * @return un it�rateur sur les �l�ments du chemin, partant de la racine.
	 */
	@Override
	public Iterator<String> iterator() {
		return path.descendingIterator();
	}

	/**
	 * Retourne un it�rateur sur les �l�ments du chemin, partant du dossier
	 * courant jusqu'� la racine.
	 * 
	 * @return un it�rateur sur les �l�ments du chemin, partant du dossier
	 *         courant jusqu'� la racine.
	 */
	public Iterator<String> descendingIterator() {
		return path.iterator();
	}

	/**
	 * Supprime le chemin actuellement enregistr� et retourne � la racine
	 */
	public void clear() {
		goTo("/");
	}

	/**
	 * Retourne le nombre d'�l�ments constituant ce chemin.
	 * 
	 * @return le nombre d'�l�ments constituant ce chemin.
	 */
	public int size() {
		return path.size();
	}

	/**
	 * Retourne vrai si le chemin actuel d�signe la racine.
	 * 
	 * @return true si le chemin actuel d�signe la racine
	 */
	public boolean isRoot() {
		return size() == 1;
	}

	/**
	 * @return Le nom du r�pertoire courant
	 */
	public String getDirectoryName() {
		return path.peek();
	}
}
