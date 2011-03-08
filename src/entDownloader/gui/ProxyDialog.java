/*
 *  ProxyDialog.java
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
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import entDownloader.core.ENTDownloader;

public class ProxyDialog extends javax.swing.JDialog {
	private static final long serialVersionUID = 1L;
	private JPanel optionGroup;
	private JButton okBtn;
	private JTextField proxyPort;
	private JTextField proxyAddr;
	private JLabel jLabel2;
	private JLabel jLabel1;
	private JTextField pacLocation;
	private JRadioButton pacBtn;
	private ButtonGroup confTypeGroup;
	private JRadioButton manualBtn;
	private JRadioButton directBtn;
	private JButton cancelBtn;
	private LookAndFeel lookfeel;

	/**
	 * Auto-generated main method to display this JDialog
	 */

	public ProxyDialog(JFrame frame) {
		super(frame);
		//Save Look & Feel
		lookfeel = javax.swing.UIManager.getLookAndFeel();
		//Set Look & Feel
		try {
			javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager
					.getSystemLookAndFeelClassName());
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception e) {
		}
		initGUI();
		setCurrentConfig();
		this.addWindowListener(new WindowAdapter() { //Restaure le look and feel précédent à la fermeture
			@Override
			public void windowClosed(WindowEvent arg0) {
				try {
					javax.swing.UIManager.setLookAndFeel(lookfeel);
				} catch (Exception e) {
				} finally {
					SwingUtilities.updateComponentTreeUI(ProxyDialog.this
							.getParent());
				}
			}
		});
	}

	private void setCurrentConfig() {
		Proxy proxy = ENTDownloader.getInstance().getProxy();
		if (proxy == null || proxy == Proxy.NO_PROXY) {
			directBtn.doClick();
		} else {
			//TODO Minimaliste : Perte de la configuration par PAC (emplacement du fichier)
			InetSocketAddress addr = (InetSocketAddress) proxy.address();
			manualBtn.doClick();
			proxyAddr.setText(addr.getHostName());
			proxyPort.setText(String.valueOf(addr.getPort()));
		}
	}

	private void initGUI() {
		try {
			{
				this.setTitle("Paramètres de connexion");
				this.setIconImage(GuiMain.getAppIcon());
				this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
				this.setModal(true);
				this.setName("proxyDialog");
				GridBagLayout thisLayout = new GridBagLayout();
				thisLayout.rowWeights = new double[] { 0.1, 0.0 };
				thisLayout.rowHeights = new int[] { 7, 7 };
				thisLayout.columnWeights = new double[] { 0.1, 0.0, 0.0 };
				thisLayout.columnWidths = new int[] { 352, 7, 7 };
				getContentPane().setLayout(thisLayout);
				this.setResizable(false);
				{
					optionGroup = new JPanel();
					getContentPane().add(
							optionGroup,
							new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0,
									GridBagConstraints.CENTER,
									GridBagConstraints.BOTH, new Insets(5, 5,
											5, 5), 0, 0));
					GridBagLayout optionGroupLayout = new GridBagLayout();
					optionGroupLayout.rowWeights = new double[] { 0.0, 0.0,
							0.0, 0.0, 0.0, 0.0 };
					optionGroupLayout.rowHeights = new int[] { 7, 7, 20, 7, 20,
							7 };
					optionGroupLayout.columnWeights = new double[] { 0.0, 0.0,
							0.2, 0.0, 0.0, 0.0 };
					optionGroupLayout.columnWidths = new int[] { 31, 31, 7, 7,
							52, 6 };
					optionGroup
							.setBorder(BorderFactory
									.createTitledBorder(
											null,
											"Configuration du serveur proxy pour accéder à Internet",
											TitledBorder.LEADING,
											TitledBorder.DEFAULT_POSITION,
											new java.awt.Font("Segoe UI", 0, 12),
											new java.awt.Color(0, 0, 0)));
					optionGroup.setLayout(optionGroupLayout);
					{
						directBtn = new JRadioButton();
						optionGroup.add(directBtn, new GridBagConstraints(0, 0,
								5, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
								GridBagConstraints.NONE,
								new Insets(0, 0, 0, 0), 0, 0));
						directBtn
								.setText("Connection directe à Internet ou détection automatique des paramètres");
						directBtn.setMnemonic(java.awt.event.KeyEvent.VK_D);
						getConfTypeGroup().add(directBtn);
						directBtn.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								boolean selected = directBtn.isSelected();
								proxyAddr.setEnabled(!selected);
								proxyPort.setEnabled(!selected);
								jLabel1.setEnabled(!selected);
								jLabel2.setEnabled(!selected);

								pacLocation.setEnabled(!selected);
							}
						});
					}
					{
						manualBtn = new JRadioButton();
						optionGroup.add(manualBtn, new GridBagConstraints(0, 1,
								6, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
								GridBagConstraints.NONE,
								new Insets(0, 0, 0, 0), 0, 0));
						manualBtn.setText("Configuration manuelle du proxy :");
						manualBtn.setMnemonic(java.awt.event.KeyEvent.VK_M);
						getConfTypeGroup().add(manualBtn);
						manualBtn.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								boolean selected = manualBtn.isSelected();
								proxyAddr.setEnabled(selected);
								proxyPort.setEnabled(selected);
								jLabel1.setEnabled(selected);
								jLabel2.setEnabled(selected);

								pacLocation.setEnabled(!selected);
							}
						});
					}
					{
						pacBtn = new JRadioButton();
						optionGroup.add(pacBtn, new GridBagConstraints(0, 3, 5,
								1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
								GridBagConstraints.NONE,
								new Insets(0, 0, 0, 0), 0, 0));
						optionGroup.add(getPacLocation(),
								new GridBagConstraints(1, 4, 4, 1, 0.0, 0.0,
										GridBagConstraints.CENTER,
										GridBagConstraints.HORIZONTAL,
										new Insets(0, 0, 0, 0), 0, 0));
						optionGroup.add(getJLabel1(), new GridBagConstraints(1,
								2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
								GridBagConstraints.VERTICAL, new Insets(0, 0,
										0, 2), 0, 0));
						optionGroup.add(getJLabel2(), new GridBagConstraints(3,
								2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
								GridBagConstraints.HORIZONTAL, new Insets(0,
										10, 0, 2), 0, 0));
						optionGroup.add(getProxyAddr(), new GridBagConstraints(
								2, 2, 1, 1, 0.0, 0.0,
								GridBagConstraints.CENTER,
								GridBagConstraints.HORIZONTAL, new Insets(0, 0,
										0, 0), 0, 0));
						optionGroup.add(getProxyPort(), new GridBagConstraints(
								4, 2, 1, 1, 0.0, 0.0,
								GridBagConstraints.CENTER,
								GridBagConstraints.HORIZONTAL, new Insets(0, 0,
										0, 0), 0, 0));
						pacBtn.setText("Adresse de configuration automatique du proxy (PAC) :");
						pacBtn.setMnemonic(java.awt.event.KeyEvent.VK_C);
						getConfTypeGroup().add(pacBtn);
						pacBtn.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								boolean selected = pacBtn.isSelected();
								proxyAddr.setEnabled(!selected);
								proxyPort.setEnabled(!selected);
								jLabel1.setEnabled(!selected);
								jLabel2.setEnabled(!selected);

								pacLocation.setEnabled(selected);
							}
						});
					}
				}
				{
					okBtn = new JButton();
					getContentPane().add(
							okBtn,
							new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
									GridBagConstraints.CENTER,
									GridBagConstraints.NONE, new Insets(2, 2,
											5, 7), 0, 0));
					okBtn.setText("OK");
					okBtn.setMnemonic(java.awt.event.KeyEvent.VK_O);
					okBtn.setPreferredSize(new java.awt.Dimension(70, 23));
					okBtn.setSize(70, 23);
					okBtn.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent arg0) {
							if (directBtn.isSelected()) {
								ENTDownloader.getInstance().removeProxy();
							} else if (manualBtn.isSelected()) {
								int port = -1;
								boolean result = false;
								try {
									port = Integer.parseInt(proxyPort.getText());
									result = true;
								} catch (NumberFormatException e) {
									result = false;
								}
								if (result && (port <= 0 || port > 65535)) {
									result = false;
								}
								if (!result) {
									JOptionPane
											.showMessageDialog(
													ProxyDialog.this,
													"Le port du serveur proxy est invalide. "
															+ "Veuillez indiquez un nombre compris entre 1 et 65535 inclus.",
													"Port incorrect",
													JOptionPane.ERROR_MESSAGE);
									proxyPort.setSelectionStart(0);
									proxyPort.setSelectionEnd(65000);
									proxyPort.grabFocus();
									return;
								}
								if (proxyAddr.getText().isEmpty()) {
									JOptionPane
											.showMessageDialog(
													ProxyDialog.this,
													"Veuillez indiquez l'adresse ou le nom de domaine du serveur proxy.",
													"Adresse incorrecte",
													JOptionPane.ERROR_MESSAGE);
									proxyAddr.grabFocus();
									return;
								}

								ENTDownloader.getInstance().setProxy(
										proxyAddr.getText(), port); //TODO Gestion erreur de saisi => Exception OutOfRangeException, UnknownHostException
							} else if (pacBtn.isSelected()) {
								if (pacLocation.getText().isEmpty()) {
									JOptionPane
											.showMessageDialog(
													ProxyDialog.this,
													"Veuillez indiquez l'emplacement du fichier de configuration automatique (PAC).",
													"Paramètre invalide",
													JOptionPane.ERROR_MESSAGE);
									pacLocation.grabFocus();
									return;
								}
								//TODO Valider formulaire (gestion des exceptions)
								try {
									ENTDownloader.getInstance().setProxy(
											pacLocation.getText());
								} catch (URISyntaxException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IllegalArgumentException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							ProxyDialog.this.setVisible(false);
						}
					});
				}
				{
					cancelBtn = new JButton();
					getContentPane().add(
							cancelBtn,
							new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
									GridBagConstraints.CENTER,
									GridBagConstraints.NONE, new Insets(2, 7,
											5, 10), 0, 0));
					cancelBtn.setText("Annuler");
					cancelBtn.setMnemonic(java.awt.event.KeyEvent.VK_A);
					cancelBtn.setPreferredSize(new java.awt.Dimension(70, 23));
					cancelBtn.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							ProxyDialog.this.setVisible(false);
						}
					});
				}
			}
			pack();
			this.setSize(530, 218);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ButtonGroup getConfTypeGroup() {
		if (confTypeGroup == null) {
			confTypeGroup = new ButtonGroup();
		}
		return confTypeGroup;
	}

	private JTextField getPacLocation() {
		if (pacLocation == null) {
			pacLocation = new JTextField();
		}
		return pacLocation;
	}

	private JLabel getJLabel1() {
		if (jLabel1 == null) {
			jLabel1 = new JLabel();
			jLabel1.setText("Proxy HTTP :");
		}
		return jLabel1;
	}

	private JLabel getJLabel2() {
		if (jLabel2 == null) {
			jLabel2 = new JLabel();
			jLabel2.setText("Port :");
		}
		return jLabel2;
	}

	private JTextField getProxyAddr() {
		if (proxyAddr == null) {
			proxyAddr = new JTextField();
		}
		return proxyAddr;
	}

	private JTextField getProxyPort() {
		if (proxyPort == null) {
			proxyPort = new JTextField();
			proxyPort.addKeyListener(new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent e) {
					if (proxyPort.getText().length() >= 5) {
						e.consume();
					}
					char key = e.getKeyChar();
					if (key != KeyEvent.VK_BACK_SPACE
							&& key != KeyEvent.VK_DELETE
							&& (key < KeyEvent.VK_0 || key > KeyEvent.VK_9)) {
						e.consume();
					}
				}
			});
		}
		return proxyPort;
	}

}
