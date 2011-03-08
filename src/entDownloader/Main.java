/*
 *  Main.java
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
package entDownloader;

import static entDownloader.core.CoreConfig.debug;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import entDownloader.core.CoreConfig;
import entDownloader.gui.GuiMain;
import entDownloader.shell.ShellMain;

public class Main {

	public static void main(String[] args) {
		if (!debug && System.getProperty("os.name").contains("Windows")) {
			// Corrige un bug d'accents sous Windows
			PrintStream psout = null;
			PrintStream pserr = null;
			try {
				psout = new PrintStream(System.out, true, "IBM850");
				pserr = new PrintStream(System.err, true, "IBM850");
			} catch (UnsupportedEncodingException e1) {
			}
			// on commence par changer l'encodage en IBM850 (connu aussi sous
			// l'alias Cp850)
			if (psout != null) {
				System.setOut(psout);
			}
			if (pserr != null) {
				System.setErr(pserr);
			}
		}

		// Configuration de l'user-agent
		System.setProperty(
				"http.agent",
				CoreConfig.getString("ProductInfo.name") + "/"
						+ CoreConfig.getString("ProductInfo.version") + " ("
						+ System.getProperty("os.name") + "; "
						+ System.getProperty("os.version") + "; "
						+ System.getProperty("os.arch") + ")");

		boolean gui = true;
		for (String arg : args) {
			if (arg.equalsIgnoreCase("-nogui")) {
				gui = false;
			}
		}
		if (gui) {
			new GuiMain(args);
		} else {
			new ShellMain(args);
		}
	}

}
