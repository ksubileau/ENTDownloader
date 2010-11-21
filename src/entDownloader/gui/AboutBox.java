/*
 *  AboutBox.java
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
package entDownloader.gui;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import entDownloader.core.CoreConfig;

public class AboutBox extends javax.swing.JDialog {
	private static final long serialVersionUID = -3485384134597214576L;
	private JLabel logo;
	private JLabel author;
	private JButton close;
	private JLabel email;
	private JLabel contact;
	private JLabel authorName;
	private JLabel copyright;
	private JLabel productVersion;
	private JLabel productName;

	/**
	 * Auto-generated main method to display this JDialog
	 */

	public AboutBox(JFrame frame) {
		super(frame);
		initGUI();
	}

	private void initGUI() {
		this.setResizable(false);
		this.setTitle("A propos de " + CoreConfig.getString("ProductInfo.name"));
		getContentPane().setLayout(null);
		{
			logo = new JLabel();
			getContentPane().add(logo, "North");
			logo.setIcon(new ImageIcon(getClass().getClassLoader().getResource(
					"entDownloader/ressources/appico.png")));
			logo.setBounds(12, 12, 50, 48);
		}
		{
			productName = new JLabel();
			getContentPane().add(productName);
			productName.setText("ENTDownloader");
			productName.setBounds(74, 27, 117, 16);
			productName.setFont(new java.awt.Font("Segoe UI", 1, 14));
		}
		{
			productVersion = new JLabel();
			getContentPane().add(productVersion);
			productVersion.setText(CoreConfig.getString("ProductInfo.version"));
			productVersion.setBounds(191, 27, 96, 16);
			productVersion.setFont(new java.awt.Font("Segoe UI", 1, 14));
		}
		{
			copyright = new JLabel();
			getContentPane().add(copyright);
			copyright
					.setText("<html>Copyright (c) 2010 "
							+ CoreConfig.getString("ProductInfo.author")
							+ ". "
							+ CoreConfig.getString("ProductInfo.name")
							+ " est un logiciel libre. Vous pouvez le redistribuez et/ou le modifiez sous les termes de la licence GNU General Public License.</html>");
			copyright.setBounds(12, 118, 296, 43);
			copyright.setFont(new java.awt.Font("Segoe UI", 0, 10));
		}
		{
			author = new JLabel();
			getContentPane().add(author);
			author.setText("Auteur  :");
			author.setBounds(15, 72, 56, 16);
		}
		{
			authorName = new JLabel();
			getContentPane().add(authorName);
			authorName.setText(CoreConfig.getString("ProductInfo.author"));
			authorName.setBounds(80, 72, 241, 16);
		}
		{
			contact = new JLabel();
			getContentPane().add(contact);
			contact.setText("Contact :");
			contact.setBounds(15, 93, 59, 16);
		}
		{
			email = new JLabel();
			getContentPane().add(email);
			email.setText("<html><a href=\"mailto:"
					+ CoreConfig.getString("ProductInfo.email") + "\">"
					+ CoreConfig.getString("ProductInfo.email") + "</a></html>");
			email.setBounds(80, 93, 253, 16);
			email.setCursor(new Cursor(Cursor.HAND_CURSOR));
			email.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (Desktop.isDesktopSupported()
							&& Desktop.getDesktop().isSupported(
									java.awt.Desktop.Action.MAIL)) {
						try {
							java.awt.Desktop
									.getDesktop()
									.mail(new java.net.URI(
											"mailto:"
													+ CoreConfig
															.getString("ProductInfo.email")));
						} catch (Exception ex) {
						}
					}
				}
			});
		}
		{
			close = new JButton();
			getContentPane().add(close);
			close.setText("Fermer");
			close.setBounds(253, 163, 80, 27);
			close.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					AboutBox.this.dispose();
				}
			});
		}
		Frame owner = (Frame) getOwner();
		if (owner != null) {
			setIconImage(owner.getIconImage());
		}
		this.setSize(350, 225);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setModal(true);
		setLocationRelativeTo(owner);
		setVisible(true);
	}
}
