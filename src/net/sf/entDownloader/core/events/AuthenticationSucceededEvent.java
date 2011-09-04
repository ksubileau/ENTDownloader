/*
 *  AuthenticationSucceededEvent.java
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
package net.sf.entDownloader.core.events;

/**
 * Un événement qui indique que l'authentification a réussi,
 * c'est à dire que le couple login/password est validé.
 */
public class AuthenticationSucceededEvent extends Event {

	/**
	 * Retourne le type d'événement porté par cette instance. Ici, retourne
	 * {@link Event#AUTHENTICATION_SUCCEEDED_TYPE}
	 */
	@Override
	public int getType() {
		return Event.AUTHENTICATION_SUCCEEDED_TYPE;
	}

	/**
	 * Construit un nouvel évènement AuthenticationSucceededEvent.
	 * 
	 * @param login Le nom d'utilisateur utilisé pour la connexion.
	 */
	public AuthenticationSucceededEvent(String login) {
		setLogin(login);
	}

	private String login;

	/**
	 * Retourne le nom d'utilisateur utilisé pour la connexion.
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * Définit le nom d'utilisateur utilisé pour la connexion.
	 */
	public void setLogin(String login) {
		this.login = login;
	}
}
