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
package net.sf.entDownloader.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.ParseException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sf.entDownloader.core.CoreConfig;
import net.sf.entDownloader.core.ENTDownloader;
import net.sf.entDownloader.core.events.AuthenticationSucceededEvent;
import net.sf.entDownloader.core.events.AuthenticationSucceededListener;
import net.sf.entDownloader.core.events.Broadcaster;
import net.sf.entDownloader.gui.Components.JFadePanel;

/**
 * Fenêtre de connexion.
 */
public class LoginFrame extends javax.swing.JFrame implements ActionListener, AuthenticationSucceededListener {
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
	private SwingWorker<Void, Void> loginThread;

	/**
	 * Auto-generated main method to display this JDialog
	 */

	public LoginFrame() {
		super();
		initGUI();
	}

	private void initGUI() {
		try {
			GridBagLayout defaultLayout = new GridBagLayout();
			getContentPane().setLayout(defaultLayout);
			this.setTitle(CoreConfig.getString("ProductInfo.name") + " - Connexion");
			this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			this.setResizable(false);
			this.setName("login"); //$NON-NLS-1$
			{
				
				overlay = new JFadePanel();
				setGlassPane(overlay);
				overlay.setBackground(new Color(255, 255, 255, 0));
				overlay.setFadeOutEnabled(false);
				overlay.setVisible(false);
				overlay.setMaxOpacity(100);
				overlay.setLayout(null); 
				// blocks all user input
				overlay.addMouseListener(new MouseAdapter() { });
				overlay.addMouseMotionListener(new MouseMotionAdapter() { });
				overlay.addKeyListener(new KeyAdapter() { });

				{
					wait = new JLabel();
					overlay.add(wait);
					wait.setIcon(new ImageIcon(getClass().getClassLoader()
							.getResource("net/sf/entDownloader/ressources/wait.gif"))); //$NON-NLS-1$
					wait.setBounds(184, 16, 64, 64);
				}
				{
					wait2 = new JLabel();
					overlay.add(wait2);
					wait2.setBounds(6, 92, 423, 14);
					wait2.setHorizontalAlignment(SwingConstants.CENTER);
				}
				{
					cancel = new JButton();
					overlay.add(cancel);
					cancel.setText("Annuler");
					cancel.setBounds(170, 118, 81, 29);
					cancel.setMnemonic(java.awt.event.KeyEvent.VK_A);
					cancel.addActionListener(this);
				}
			}
			{
				{
					passLabel = new JLabel();
					passLabel.setText("Mot de passe :");
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
												"net/sf/entDownloader/ressources/padlockandkeys.png"))); //$NON-NLS-1$
						imgPass.setBounds(0, 0, 78, 74);
						imgPass.setBackground(new java.awt.Color(255, 255, 255));
						imgPass.setHorizontalAlignment(SwingConstants.CENTER);
					}
				}
				{
					invite = new JLabel();
					invite.setText("<html>Veuillez saisir votre identifiant et votre mot de passe afin de vous connecter au service de stockage de documents de l'ENT :</html>");
				}
				{
					invalidCredentials = new JLabel();
					invalidCredentials.setText("L'identifiant ou le mot de passe est incorrect.");
					invalidCredentials.setFont(new java.awt.Font(
							"Tahoma", 1, 11)); //$NON-NLS-1$
					invalidCredentials.setForeground(new java.awt.Color(255, 0,
							0));
					invalidCredentials.setVisible(false);
				}
				{
					idLabel = new JLabel();
					idLabel.setText("Identifiant :");
				}
				{
					id = new JTextField();
					idLabel.setLabelFor(id);
					id.addActionListener(this);
					DocumentListener activator = new DocumentListener() {
						@Override
						public void removeUpdate(DocumentEvent e) {
							confirm.setEnabled(!id.getText().isEmpty()
									&& mdp.getPassword().length != 0);
						}
						
						@Override
						public void insertUpdate(DocumentEvent e) {
							confirm.setEnabled(!id.getText().isEmpty()
									&& mdp.getPassword().length != 0);
						}
						
						@Override
						public void changedUpdate(DocumentEvent e) {
						}
					};
					id.getDocument().addDocumentListener(activator);

					mdp = new JPasswordField();
					mdp.setEchoChar('\u2022');
					passLabel.setLabelFor(mdp);
					mdp.getDocument().addDocumentListener(activator);
					mdp.addActionListener(this);
				}
				{
					confirm = new JButton();
					getContentPane().add(confirm, new GridBagConstraints(3, 5, 1, 1,
							0.0, 0.0, GridBagConstraints.CENTER,
							GridBagConstraints.NONE, new Insets(0, 0, 10, 5),
							0, 0));
					confirm.setText("OK");
					confirm.setBounds(0, 0, 100, 100);
					confirm.setPreferredSize(new java.awt.Dimension(47, 23));
					confirm.setMnemonic(java.awt.event.KeyEvent.VK_O);
					confirm.addActionListener(this);
					confirm.setEnabled(false);
				}
				{
					quit = new JButton();
					getContentPane().add(quit, new GridBagConstraints(4, 5, 2, 1, 0.0,
							0.0, GridBagConstraints.CENTER,
							GridBagConstraints.NONE, new Insets(0, 5, 10, 3),
							0, 0));
					getContentPane().add(id, new GridBagConstraints(2, 2, 3, 1, 0.0,
							0.0, GridBagConstraints.CENTER,
							GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0,
									0), 0, 0));
					getContentPane().add(mdp, new GridBagConstraints(2, 3, 3, 1, 0.0,
							0.0, GridBagConstraints.CENTER,
							GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0,
									0), 0, 0));
					getContentPane().add(idLabel, new GridBagConstraints(1, 2, 1, 1,
							0.0, 0.0, GridBagConstraints.EAST,
							GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0,
							0));
					getContentPane().add(passLabel, new GridBagConstraints(1, 3, 1, 1,
							0.0, 0.0, GridBagConstraints.EAST,
							GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0,
							0));
					getContentPane().add(jPanel1, new GridBagConstraints(0, 1, 1, 4,
							0.0, 0.0, GridBagConstraints.CENTER,
							GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0,
							0));
					getContentPane().add(invalidCredentials, new GridBagConstraints(1,
							1, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
							GridBagConstraints.NONE, new Insets(0, 0, 6, 0), 0,
							0));
					getContentPane().add(invite, new GridBagConstraints(0, 0, 5, 1,
							0.0, 0.0, GridBagConstraints.CENTER,
							GridBagConstraints.HORIZONTAL, new Insets(9, 10, 4,
									10), 0, 0));
					{
						proxyBtn = new JButton();
						getContentPane().add(proxyBtn, new GridBagConstraints(0, 5, 1,
								1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
								GridBagConstraints.NONE,
								new Insets(0, 5, 5, 0), 0, 0));
						proxyBtn.setText("Proxy...");
						proxyBtn.setMnemonic(java.awt.event.KeyEvent.VK_P);
						proxyBtn.addActionListener(this);
					}
					quit.setAction(new MainFrame.ExitAction());
				}
				defaultLayout.rowWeights = new double[] { 0.03, 0.0, 0.01,
						0.01, 0.1, 0.0 };
				defaultLayout.rowHeights = new int[] { 7, 10, 7, 7, 20, 7 };
				defaultLayout.columnWeights = new double[] { 0.0, 0.0, 0.1,
						0.0, 0.0, 0.0, 0.0 };
				defaultLayout.columnWidths = new int[] { 77, 130, 20, 7, 38,
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
	
	@Override
	public void onAuthenticationSucceeded(AuthenticationSucceededEvent event) {
		wait2.setText("Authentification réussie. Initialisation en cours...");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == confirm || 
				(e.getSource() == mdp && mdp.getPassword().length != 0))
		{
			wait2.setText("Connexion en cours, veuillez patientez...");
			Broadcaster.addAuthenticationSucceededListener(this);
			loginThread = new SwingWorker<Void, Void>() {
				private boolean exceptionRaised  = false;
				@Override
				protected Void doInBackground() {
					overlay.setVisible(true);
					try {
						//TODO En cas d'annulation, la méthode login poursuit son execution inutilement.
						//Des requêtes HTTP sont effectuées après l'annulation. Solution : Rendre la méthode
						//login interruptible en ajoutant une logique d'arrêt dans la classe ENTDownloader.
						//(Pb non critique)
						ENTDownloader.getInstance().login(id.getText(), mdp.getPassword());
					} catch (IOException e) {
						if(isCancelled()) //Si la connexion a été annulée
							return null; //On ignore l'erreur qui s'est produite post-annulation
						exceptionRaised = true;
						InetSocketAddress proxyAddress = 
							(InetSocketAddress) ENTDownloader.getInstance().
								getProxy().address();
						
						if (proxyAddress != null && e.getClass() == 
								java.net.UnknownHostException.class
								&& e.getMessage() == proxyAddress.getHostName()) 
						{
							//L'erreur est due à une mauvaise configuration
							//de proxy.
							JOptionPane
							.showMessageDialog(
									LoginFrame.this,
									"<html><b>ENTDownloader est configuré pour utiliser un serveur proxy mais celui-ci est introuvable.</b><br>" +
									"<ul>" +
										"<li style=\"list-style-type:none;\">" +
											"- Assurez-vous que les paramètres de proxy sont corrects ;" +
										"</li>" +
										"<li style=\"list-style-type:none;\">" +
											"- Vérifiez que la connexion réseau de votre ordinateur fonctionne ;" +
										"</li>" +
										"<li style=\"list-style-type:none;\">" +
											"- Si votre ordinateur ou votre réseau est protégé par un pare-feu,<br>" +
											"&nbsp;&nbsp;&nbsp;assurez-vous qu'ENTDownloader a l'autorisation " +
											"d'accéder au Web." +
										"</li>" +
									"</ul></html>",
									"ENTDownloader - Serveur proxy introuvable",
									JOptionPane.ERROR_MESSAGE);
						}
						else //Une autre erreur, non liée au proxy.
						{
							JOptionPane
									.showMessageDialog(
											LoginFrame.this,
											"Votre connexion Internet semble rencontrer un problème.\nAssurez-vous que votre ordinateur est connecté à Internet, "
													+ "\net que vous avez correctement configuré les paramètres de proxy.",
											"ENTDownloader - Service indisponible",
											JOptionPane.ERROR_MESSAGE);
						}
					} catch (ParseException e) {
						if(isCancelled()) //Si la connexion a été annulée
							return null; //On ignore l'erreur qui s'est produite post-annulation
						exceptionRaised = true;
						JOptionPane
						.showMessageDialog(
								LoginFrame.this,
								"<html>Une erreur est survenue durant la procédure de connexion. Veuillez réessayer ultérieurement.<br><br>"
										+ "Si le problème persiste, merci de nous le signaler à l'adresse <a href=\"mailto:"
										+ CoreConfig.getString("ProductInfo.email") + "\">"
										+ CoreConfig.getString("ProductInfo.email") + "</a>.</html>",
								"ENTDownloader - Connexion impossible",
								JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
						System.exit(1);
					}
					return null;
				}

				@Override
				protected void done() {
					if(isCancelled())
					{
						//Sauvegarde la configuration de proxy
						String pacFile = ENTDownloader.getInstance().getProxyFile();
						java.net.Proxy proxy = null;
						if(pacFile == null)
							proxy = ENTDownloader.getInstance().getProxy();

						//Réinitialise l'instance d'ENTDownloader
						ENTDownloader.reset();

						//Restaure la configuration de proxy
						if(pacFile != null)
							try {
								ENTDownloader.getInstance().setProxy(pacFile);
							} catch (Exception e) {
								ENTDownloader.getInstance().setProxy(proxy);
							}
						else
							ENTDownloader.getInstance().setProxy(proxy);

						restartAfterFailed(false);
					}
					else
					{
						if (ENTDownloader.getInstance().isLogged()) {
							MainFrame mainFrame = (MainFrame) GuiMain.getMainFrame();
							mainFrame.updateENTDInstance();
							mainFrame.changeDirectory("/");
							mainFrame.setExtendedState(mainFrame.getExtendedState()
									| javax.swing.JFrame.MAXIMIZED_BOTH);;
							mainFrame.setVisible(true);
							dispose();
						} else {
							restartAfterFailed(!exceptionRaised);
						}
					}
				};
			};
			loginThread.execute();
		} else if (e.getSource() == id && !id.getText().isEmpty()) {
			mdp.requestFocus();
		} else if (e.getSource() == proxyBtn) {
			ProxyDialog pxDial = new ProxyDialog(
					LoginFrame.this);
			pxDial.setLocationRelativeTo(LoginFrame.this);
			pxDial.setVisible(true);
		} else if (e.getSource() == cancel) {
			loginThread.cancel(true);
		}
	}

	private void restartAfterFailed(boolean showInvalidCredentialMessage) {
		mdp.setText(""); //$NON-NLS-1$
		confirm.setEnabled(false);
		invalidCredentials.setVisible(showInvalidCredentialMessage);
		overlay.setVisible(false);
		mdp.grabFocus();
	}

	public void setLogin(String defaultLogin) {
		id.setText(defaultLogin);
		mdp.grabFocus();
	}

}
