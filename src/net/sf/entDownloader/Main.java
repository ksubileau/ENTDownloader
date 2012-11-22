/*
 *  Main.java
 *      
 *  Copyright 2010-2012 Kévin Subileau. 
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
package net.sf.entDownloader;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import net.sf.entDownloader.core.CoreConfig;
import net.sf.entDownloader.gui.GuiMain;
import net.sf.entDownloader.shell.ShellMain;

public class Main {

	public static void main(String[] args) {
		if (System.getProperty("os.name").contains("Windows")) {
			// Corrige un bug d'accents sous Windows
			// En changeant l'encodage de la sortie standard en IBM850
			PrintStream psout = null;
			PrintStream pserr = null;
			try {
				psout = new PrintStream(System.out, true, "IBM850");
				pserr = new PrintStream(System.err, true, "IBM850");
			} catch (UnsupportedEncodingException e1) {
			}
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

		//Désactive le mode de débogage par défaut
		System.setProperty("sf.net.entDownloader.debug", "false");

		boolean gui = true;
		for (String arg : args) {
			if (arg.equalsIgnoreCase("-nogui")) {
				gui = false;
			} else if (arg.equalsIgnoreCase("-debug") || arg.equalsIgnoreCase("-debug2")) {
				System.setProperty("sf.net.entDownloader.debug", "true");
				System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
				System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
				System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.client", "debug");
				System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.impl.conn", "debug");
				System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.impl.client", "debug");
				System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.headers", "debug");
				if(arg.equalsIgnoreCase("-debug2"))
					System.setProperty("org.apache.commons.logging.simplelog.defaultlog", "debug");
			}
		}
		if (gui) {
			new GuiMain(args);
		} else {
			new ShellMain(args);
		}
	}

}
