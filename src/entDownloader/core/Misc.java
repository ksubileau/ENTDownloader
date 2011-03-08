/*
 *  Misc.java
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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Collection de méthodes utilitaires static.
 * 
 */
public final class Misc {

	/**
	 * Analyse <i>subject</i> pour trouver l'expression qui correspond à
	 * <i>pattern</i> et stocke les résultats dans <i>matches</i>.
	 * 
	 * @param pattern
	 *            Le masque à chercher, sous la forme d'une chaîne de
	 *            caractères.
	 * @param subject
	 *            La chaîne d'entrée.
	 * @param matches
	 *            Si différent de null, contient les résultats de la recherche.
	 *            <i>matches[0]</i> contiendra le texte qui satisfait le masque
	 *            complet, <i>matches[1]</i> contiendra le texte qui satisfait
	 *            la première parenthèse capturante, etc. Le contenu de
	 *            <i>matches</i> précédent l'appel à cette méthode est écrasé.
	 * @return <b>preg_match()</b> retourne un booléen indiquant si le masque
	 *         <i>pattern</i> a été trouvé : false si aucune solution n'a été
	 *         trouvé, true dans le cas contraire.
	 */
	public static boolean preg_match(String pattern, String subject,
			List<String> matches) {
		Pattern pattern2 = Pattern.compile(pattern);
		Matcher matcher = pattern2.matcher(subject);
		if (matches != null) {
			matches.clear();
		}
		if (matcher.find()) {
			if (matches != null) {
				for (int i = 0; i <= matcher.groupCount(); i++) {
					matches.add(matcher.group(i));
				}
			}
			return true;
		} else
			return false;
	}

	/**
	 * Analyse <i>subject</i> pour trouver l'expression qui correspond à
	 * <i>pattern</i>.
	 * 
	 * @param pattern
	 *            Le masque à chercher, sous la forme d'une chaîne de
	 *            caractères.
	 * @param subject
	 *            La chaîne d'entrée.
	 * @return <b>preg_match()</b> retourne un booléen indiquant si le masque
	 *         <i>pattern</i> a été trouvé : false si aucune solution n'a été
	 *         trouvé, true dans le cas contraire.
	 */
	public static boolean preg_match(String pattern, String subject) {
		return preg_match(pattern, subject, null);
	}

	/**
	 * Analyse <i>subject</i> pour trouver l'expression <i>pattern</i> et met
	 * les résultats dans <i>matches</i>, dans l'ordre spécifié par
	 * <i>flags</i>.<br>
	 * Après avoir trouvé un premier résultat, la recherche continue jusqu'à la
	 * fin de la chaîne.
	 * 
	 * @param pattern
	 *            Le masque à chercher, sous la forme d'une chaîne de
	 *            caractères.
	 * @param subject
	 *            La chaîne d'entrée.
	 * @param matches
	 *            Tableau contenant tous les résultats, dans un tableau
	 *            multidimensionnel ordonné suivant le paramètre <i>flags</i>.
	 * @param flags
	 *            Peut prendre une des deux valeurs suivantes :
	 *            <ul>
	 *            <li><b>PREG_ORDER.PATTERN_ORDER</b><br>
	 *            L'ordre est tel que <i>matches[0]</i> est un tableau qui
	 *            contient les résultats qui satisfont le masque complet,
	 *            <i>matches[1]</i> est un tableau qui contient les résultats
	 *            qui satisfont la première parenthèse capturante, etc.</li>
	 *            <li><b>PREG_ORDER.SET_ORDER</b><br>
	 *            Les résultats sont classés de telle façon que
	 *            <i>matches[0]</i> contient la première série de résultats,
	 *            <i>matches[1]</i> contient la deuxième, etc.</li>
	 *            </ul>
	 * @return <b>preg_match_all()</b> retourne le nombre de résultats qui
	 *         satisfont le masque complet.
	 */
	public static int preg_match_all(String pattern, String subject,
			List<List<String>> matches, PREG_ORDER flags) {
		Pattern pattern2 = Pattern.compile(pattern);
		Matcher matcher = pattern2.matcher(subject);
		int nbresults = 0;
		matches.clear();
		while (matcher.find()) {
			if (flags == PREG_ORDER.SET_ORDER) {
				matches.add(new ArrayList<String>(matcher.groupCount() + 1));
				for (int i = 0; i <= matcher.groupCount(); i++) {
					matches.get(nbresults).add(matcher.group(i));
				}
			} else {
				if (nbresults == 0) {
					matches.add(new ArrayList<String>());
				}
				for (int i = 0; i <= matcher.groupCount(); i++) {
					matches.get(i).add(matcher.group(i));
				}
			}
			++nbresults;
		}
		return nbresults;
	}

	/**
	 * Analyse <i>subject</i> pour trouver l'expression <i>pattern</i> et met
	 * les résultats dans <i>matches</i>, dans l'ordre spécifié par
	 * <i>flags</i>.<br>
	 * Après avoir trouvé un premier résultat, la recherche continue jusqu'à la
	 * fin de la chaîne.
	 * 
	 * @param pattern
	 *            Le masque à chercher, sous la forme d'une chaîne de
	 *            caractères.
	 * @param subject
	 *            La chaîne d'entrée.
	 * @param matches
	 *            Tableau contenant tous les résultats, dans un tableau
	 *            multidimensionnel ordonné tel que <i>matches[0]</i> est un
	 *            tableau qui contient les résultats qui satisfont le masque
	 *            complet, <i>matches[1]</i> est un tableau qui contient les
	 *            résultats qui satisfont la première parenthèse capturante,
	 *            etc..
	 * @return <b>preg_match_all()</b> retourne le nombre de résultats qui
	 *         satisfont le masque complet.
	 */
	public static int preg_match_all(String pattern, String subject,
			List<List<String>> matches) {
		return preg_match_all(pattern, subject, matches,
				PREG_ORDER.PATTERN_ORDER);
	}

	public enum PREG_ORDER {
		PATTERN_ORDER, SET_ORDER;
	}

	/**
	 * Replace the tilde character (~) by the user's home directory, if the
	 * tilde is well placed (first character, followed by the file separator or
	 * nothing).
	 * 
	 * @param path
	 *            the path to parse.
	 * @return the path given in parameter after replacing the tilde character
	 *         by the user's home directory, ending by the file separator
	 *         character.
	 */
	public static String tildeToHome(String path) {
		if (path == null
				|| path.isEmpty()
				|| path.charAt(0) != '~'
				|| (path.length() > 1 && !path.substring(1, 2).equals(
						System.getProperty("file.separator"))))
			return path;

		String newpath = System.getProperty("user.home");
		if (path.length() > 1) {
			newpath += path.substring(1);
		} else {
			newpath += System.getProperty("file.separator");
		}

		return newpath;
	}

	/**
	 * Retourne le nombre passé en argument en le préfixant d'un zéro s'il est
	 * inférieur à 10.
	 */
	public static String addZeroBefore(int nb) {
		String nbs = "";
		if (nb < 10) {
			nbs += "0";
		}
		nbs += nb;
		return nbs;
	}
}
