/*
 *  LoginFrame.java
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
package entDownloader.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.ParseException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import entDownloader.core.CoreConfig;
import entDownloader.core.ENTDownloader;
import entDownloader.gui.Components.JFadePanel;

/**
 * Fenêtre de connexion.
 */
public class LoginFrame extends javax.swing.JFrame {
	private static final long serialVersionUID = 6012478520927856073L;
	/**
	 * Invite de saisie.
	 */
	private JLabel invite;
	private JButton proxyBtn;
	/**
	 * Bouton d'annulation
	 */
	private JButton cancel;
	private JPanel loginPane;
	private JTextField id;
	private JLabel wait2;
	private JLabel wait;
	private JPanel jPanel1;
	private JFadePanel overlay;
	private JLabel imgPass;
	private JLabel invalidCredentials;
	private JButton confirm;
	private JButton quit;
	private JPasswordField mdp;
	private JLabel passLabel;
	private JLabel idLabel;
	private Thread loginThread = null;

	/**
	 * Auto-generated main method to display this JDialog
	 */

	public LoginFrame() {
		super();
		initGUI();
	}

	private void initGUI() {
		try {
			getContentPane().setLayout(null);
			this.setTitle(CoreConfig.getString("LoginFrame.title")); //$NON-NLS-1$
			this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			this.setResizable(false);
			this.setName("login"); //$NON-NLS-1$
			{
				overlay = new JFadePanel();
				getContentPane().add(overlay);
				overlay.setBackground(new Color(255, 255, 255, 200));
				overlay.setFadeOutEnabled(false);
				overlay.setVisible(false);
				overlay.setMaxOpacity(100);
				overlay.setBounds(0, 0, 435, 173);
				overlay.setLayout(null);
				overlay.setSize(434, 173);
				{
					wait = new JLabel();
					overlay.add(wait);
					wait.setIcon(new ImageIcon(getClass().getClassLoader()
							.getResource("entDownloader/ressources/wait.gif"))); //$NON-NLS-1$
					wait.setBounds(184, 16, 64, 64);
				}
				{
					wait2 = new JLabel();
					overlay.add(wait2);
					wait2.setText(CoreConfig
							.getString("LoginFrame.waitMessage")); //$NON-NLS-1$
					wait2.setBounds(6, 92, 423, 14);
					wait2.setHorizontalAlignment(SwingConstants.CENTER);
				}
				{
					cancel = new JButton();
					overlay.add(cancel);
					cancel.setText(CoreConfig
							.getString("LoginFrame.btnAnnuler")); //$NON-NLS-1$
					cancel.setBounds(170, 118, 81, 29);
					cancel.setMnemonic(java.awt.event.KeyEvent.VK_A);
					cancel.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							loginThread.stop();
							restartAfterFailed(false);
						}
					});
				}
			}
			{
				loginPane = new JPanel();
				getContentPane().add(loginPane);
				GridBagLayout loginPaneLayout = new GridBagLayout();
				loginPane.setLayout(loginPaneLayout);
				loginPane.setBounds(0, 0, 434, 173);
				{
					passLabel = new JLabel();
					passLabel.setText(CoreConfig
							.getString("LoginFrame.passLabel")); //$NON-NLS-1$
				}
				{
					jPanel1 = new JPanel();
					jPanel1.setLayout(null);
					jPanel1.setSize(70, 70);
					jPanel1.setOpaque(false);
					{
						imgPass = new JLabel();
						jPanel1.add(imgPass, "Center"); //$NON-NLS-1$
						imgPass.setIcon(new ImageIcon(
								getClass()
										.getClassLoader()
										.getResource(
												"entDownloader/ressources/padlockandkeys.png"))); //$NON-NLS-1$
						imgPass.setBounds(0, 0, 78, 74);
						imgPass.setBackground(new java.awt.Color(255, 255, 255));
						imgPass.setHorizontalAlignment(SwingConstants.CENTER);
					}
				}
				{
					invite = new JLabel();
					invite.setText("<html>" + CoreConfig.getString("LoginFrame.winMessage") + "</html>"); //$NON-NLS-1$
				}
				{
					invalidCredentials = new JLabel();
					invalidCredentials.setText(CoreConfig
							.getString("LoginFrame.erreur")); //$NON-NLS-1$
					invalidCredentials.setFont(new java.awt.Font(
							"Tahoma", 1, 11)); //$NON-NLS-1$
					invalidCredentials.setForeground(new java.awt.Color(255, 0,
							0));
					invalidCredentials.setVisible(false);
				}
				{
					idLabel = new JLabel();
					idLabel.setText(CoreConfig.getString("LoginFrame.idLabel")); //$NON-NLS-1$
				}
				{
					id = new JTextField();
					idLabel.setLabelFor(id);
					id.addKeyListener(new KeyAdapter() {
						@Override
						public void keyReleased(KeyEvent e) {
							if (!id.getText().isEmpty()) {
								if (e.getKeyCode() == KeyEvent.VK_ENTER) {
									mdp.requestFocus();
								}
								confirm.setEnabled(mdp.getPassword().length != 0);
							} else {
								confirm.setEnabled(false);
							}
						}
					});
				}
				{
					confirm = new JButton();
					loginPane.add(confirm, new GridBagConstraints(3, 5, 1, 1,
							0.0, 0.0, GridBagConstraints.CENTER,
							GridBagConstraints.NONE, new Insets(0, 0, 10, 5),
							0, 0));
					confirm.setText(CoreConfig.getString("LoginFrame.okLabel")); //$NON-NLS-1$
					confirm.setBounds(0, 0, 100, 100);
					confirm.setPreferredSize(new java.awt.Dimension(47, 23));
					confirm.setMnemonic(java.awt.event.KeyEvent.VK_O);
					confirm.addActionListener(new DoLogin());
					confirm.setEnabled(false);
				}
				{
					mdp = new JPasswordField();
					mdp.setEchoChar('•');
					passLabel.setLabelFor(mdp);
					mdp.addKeyListener(new KeyAdapter() {
						@Override
						public void keyReleased(KeyEvent e) {
							confirm.setEnabled(!id.getText().isEmpty()
									&& mdp.getPassword().length != 0);
							if (e.getKeyCode() == KeyEvent.VK_ENTER
									&& mdp.getPassword().length != 0) {
								confirm.doClick();
							}
						}
					});
				}
				{
					quit = new JButton();
					loginPane.add(quit, new GridBagConstraints(4, 5, 2, 1, 0.0,
							0.0, GridBagConstraints.CENTER,
							GridBagConstraints.NONE, new Insets(0, 5, 10, 3),
							0, 0));
					loginPane.add(id, new GridBagConstraints(2, 2, 3, 1, 0.0,
							0.0, GridBagConstraints.CENTER,
							GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0,
									0), 0, 0));
					loginPane.add(mdp, new GridBagConstraints(2, 3, 3, 1, 0.0,
							0.0, GridBagConstraints.CENTER,
							GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0,
									0), 0, 0));
					loginPane.add(idLabel, new GridBagConstraints(1, 2, 1, 1,
							0.0, 0.0, GridBagConstraints.EAST,
							GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0,
							0));
					loginPane.add(passLabel, new GridBagConstraints(1, 3, 1, 1,
							0.0, 0.0, GridBagConstraints.EAST,
							GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0,
							0));
					loginPane.add(jPanel1, new GridBagConstraints(0, 1, 1, 4,
							0.0, 0.0, GridBagConstraints.CENTER,
							GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0,
							0));
					loginPane.add(invalidCredentials, new GridBagConstraints(1,
							1, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
							GridBagConstraints.NONE, new Insets(0, 0, 6, 0), 0,
							0));
					loginPane.add(invite, new GridBagConstraints(0, 0, 5, 1,
							0.0, 0.0, GridBagConstraints.CENTER,
							GridBagConstraints.HORIZONTAL, new Insets(9, 10, 4,
									10), 0, 0));
					{
						proxyBtn = new JButton();
						loginPane.add(proxyBtn, new GridBagConstraints(0, 5, 1,
								1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
								GridBagConstraints.NONE,
								new Insets(0, 5, 5, 0), 0, 0));
						proxyBtn.setText("Proxy...");
						proxyBtn.setMnemonic(java.awt.event.KeyEvent.VK_P);
						proxyBtn.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								ProxyDialog pxDial = new ProxyDialog(
										LoginFrame.this);
								pxDial.setLocationRelativeTo(LoginFrame.this);
								pxDial.setVisible(true);
							}
						});
					}
					quit.setAction(new MainFrame.ExitAction());
				}
				loginPaneLayout.rowWeights = new double[] { 0.03, 0.0, 0.01,
						0.01, 0.1, 0.0 };
				loginPaneLayout.rowHeights = new int[] { 7, 10, 7, 7, 20, 7 };
				loginPaneLayout.columnWeights = new double[] { 0.0, 0.0, 0.1,
						0.0, 0.0, 0.0, 0.0 };
				loginPaneLayout.columnWidths = new int[] { 77, 130, 20, 7, 38,
						20, 8 };
			}
			this.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					new MainFrame.ExitAction().actionPerformed(null);
				}
			});
			this.setIconImage(GuiMain.getAppIcon());
			{
				jPanel1.setBounds(18, 55, 78, 74);
				invalidCredentials.setBounds(147, 55, 0, 0);
				invalidCredentials
						.setHorizontalTextPosition(SwingConstants.CENTER);
				invalidCredentials.setVisible(false);
				idLabel.setBounds(102, 61, 80, 16);
				id.setBounds(188, 55, 194, 28);
				passLabel.setBounds(0, 0, 100, 100);
				mdp.setBounds(188, 83, 194, 28);
				quit.setBounds(0, 0, 100, 100);
				quit.setPreferredSize(new java.awt.Dimension(47, 23));
			}

			pack();
			this.setSize(438, 200);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//FIXME Le bouton annuler n'imterompt pas le thread : la connexion continue
	class DoLogin implements ActionListener {
		@Override
		public synchronized void actionPerformed(ActionEvent e) {
			loginThread = new Thread() {
				@Override
				public void run() {
					overlay.setVisible(true);
					loginPane.setVisible(false);
					try {
						if (ENTDownloader.getInstance().login(id.getText(),
								new String(mdp.getPassword()))) {
							MainFrame mainFrame = (MainFrame) GuiMain.getMainFrame();
							mainFrame.changeDirectory("/");
							mainFrame.setExtendedState(mainFrame.getExtendedState()
									| javax.swing.JFrame.MAXIMIZED_BOTH);;
							mainFrame.setVisible(true);
							dispose();
						} else {
							restartAfterFailed(true);
						}
					} catch (IOException e) {
						JOptionPane
								.showMessageDialog(
										loginPane,
										"Votre connexion Internet semble rencontrer un problème.\nAssurez-vous que votre ordinateur est connecté à Internet, "
												+ "\net que vous avez correctement configuré les paramètres de proxy.",
										"ENTDownloader - Service indisponible",
										JOptionPane.ERROR_MESSAGE);
						System.exit(1);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			};
			loginThread.setDaemon(true);
			loginThread.start();
		}
	}

	private void restartAfterFailed(boolean showInvalidCredentialMessage) {
		mdp.setText(""); //$NON-NLS-1$
		confirm.setEnabled(false);
		invalidCredentials.setVisible(showInvalidCredentialMessage);
		loginPane.setVisible(true);
		overlay.setVisible(false);
		mdp.grabFocus();
	}

	public void setLogin(String defaultLogin) {
		id.setText(defaultLogin);
		mdp.grabFocus();
	}

}
