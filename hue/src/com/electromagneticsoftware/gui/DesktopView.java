package com.electromagneticsoftware.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;

import org.apache.commons.lang.StringUtils;

import layout.TableLayout;

import com.electromagneticsoftware.Controller;
import com.electromagneticsoftware.data.HueProperties;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

/**
 * DesktopView.java
 * 
 * The main GUI showing last connected IP/Username and buttons for Finding Bridges and Changing the Hue Lights, once connected to a bridge.
 *
 */
public class DesktopView extends JFrame {

	private static final long serialVersionUID = -7469471678945429320L;  
	private Controller controller;
	//    private JButton setLightsButton;
	//    private JButton randomLightsButton;
	private JButton findBridgesButton;
	private JButton connectToLastBridgeButton;
	private JProgressBar findingBridgeProgressBar;

	private JPanel listPanel;
	private List<PHLight> allLights;
	private JList<String> lightIdentifiersList;

	private JTextField lastConnectedIP;
	private JTextField lastUserName;
	private PHHueSDK phHueSDK;

	public DesktopView(){
		setTitle("Hue Desktop");
		JPanel mainPanel = new JPanel();

		// TODO - Move to another class
		JPanel controls = new JPanel();
		controls.setLayout(new GridLayout(2,3));

		findingBridgeProgressBar = new JProgressBar();
		findingBridgeProgressBar.setBorderPainted(false);
		findingBridgeProgressBar.setIndeterminate(true);
		findingBridgeProgressBar.setVisible(false);

		//Set up components preferred size
		String lastUsername = HueProperties.getUsername(); 
		String lastConnectedIPStr = HueProperties.getLastConnectedIP();

		JLabel labelLastConIP    = new JLabel("Last Connected IP:");
		lastConnectedIP = new JTextField(lastConnectedIPStr);

		lastConnectedIP.setEditable(false);
		JLabel labelLastUsername = new JLabel("Last UserName:");
		lastUserName = new JTextField(lastUsername);
		lastUserName.setEditable(false);
		findBridgesButton = new JButton("Find New Bridges");
		findBridgesButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				findBridgesButton.setEnabled(false);
				connectToLastBridgeButton.setEnabled(false);
				controller.findBridges();
				findingBridgeProgressBar.setBorderPainted(true);
				findingBridgeProgressBar.setVisible(true);
			}
		});

		connectToLastBridgeButton = new JButton("Auto Connect");
		connectToLastBridgeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ae) {
				if (controller.connectToLastKnownAccessPoint()) {
					connectToLastBridgeButton.setEnabled(false);
					findBridgesButton.setEnabled(false);
					findingBridgeProgressBar.setBorderPainted(true);
					findingBridgeProgressBar.setVisible(true);
				}
			}
		});


		listPanel = new JPanel();
		//        setLightsButton = new JButton("Change Light Colours");
		//        setLightsButton.setEnabled(false);
		//        setLightsButton.addActionListener(new ActionListener() {
		//            
		//            @Override
		//            public void actionPerformed(ActionEvent arg0) {
		//                controller.showControlLightsWindow();
		//            }
		//        });
		//        
		//        randomLightsButton = new JButton("Randomize Lights");
		//        randomLightsButton.setEnabled(false);
		//        randomLightsButton.addActionListener(new ActionListener() {
		//            
		//            @Override
		//            public void actionPerformed(ActionEvent arg0) {
		//                controller.randomLights();
		//            }
		//        });
		//        
		double border = 10;
		double size[][] =
			{{border, 160, 20, 300, 20, 160},                 // Columns
					{border, 26,  10, 26, 26, 26}}; // Rows

		mainPanel.setLayout (new TableLayout(size));


		mainPanel.add(labelLastConIP,            " 1, 1");
		mainPanel.add(lastConnectedIP,           " 3, 1");

		mainPanel.add(labelLastUsername,         " 1, 3");
		mainPanel.add(lastUserName,              " 3, 3");

		mainPanel.add(findingBridgeProgressBar,  " 3, 5");

		mainPanel.add(connectToLastBridgeButton, " 5, 1");
		mainPanel.add(findBridgesButton,         " 5, 3");

		//        mainPanel.add(randomLightsButton,        " 5, 5");
		//        mainPanel.add(setLightsButton,           " 5, 7");
		

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(700,400));

		getContentPane().add( new JLabel("   Control your Hue Lights."), BorderLayout.NORTH);
		getContentPane().add( mainPanel, BorderLayout.CENTER);
		getContentPane().add( listPanel, BorderLayout.SOUTH );


		//4. Size the frame.
		pack();
		setLocationRelativeTo(null); // Centre the window.
		setVisible(true);        

	}

	public void setController(Controller controller) {
		this.controller = controller;
	}

	//    public JButton getSetLightsButton() {
	//        return setLightsButton;
	//    }
	//
	//    public JButton getRandomLightsButton() {
	//        return randomLightsButton;
	//    } 

	public JButton getFindBridgesButton() {
		return findBridgesButton;
	} 

	public JButton getConnectToLastBridgeButton() {
		return connectToLastBridgeButton;
	} 

	public void showDialog(String message) {
		JOptionPane.showMessageDialog(this, message);
	}

	public JProgressBar getFindingBridgeProgressBar() {
		return findingBridgeProgressBar;
	}

	public JTextField getLastConnectedIP() {
		return lastConnectedIP;
	}

	public JTextField getLastUserName() {
		return lastUserName;
	}

	public void enableLightList() {
		// The the HueSDK singleton.
		phHueSDK = PHHueSDK.getInstance();

		// Get the selected bridge.
		PHBridge bridge = phHueSDK.getSelectedBridge(); 

		// To get lights use the Resource Cache.  
		allLights = bridge.getResourceCache().getAllLights();

		DefaultListModel <String> listListStr = new DefaultListModel<String>();

		for (PHLight light : allLights) {
			listListStr.addElement( buildName( light ) );
		}

		lightIdentifiersList = new JList<String>(listListStr);
		lightIdentifiersList.setVisibleRowCount(20);
		Font myFont = new Font( "Courier", Font.PLAIN, 12);
		lightIdentifiersList.setFont( myFont );
		lightIdentifiersList.setSelectedIndex(0);

		JScrollPane listPane = new JScrollPane(lightIdentifiersList);
		listPane.setPreferredSize(new Dimension(300,200));
//
//		JPanel listPanel = new JPanel();
//		listPanel.setBackground(Color.white);

		Border listPanelBorder = BorderFactory.createTitledBorder("My Lights");
		listPanel.setBorder(listPanelBorder);
		listPanel.add(listPane);
//		content.add(listPanel, BorderLayout.CENTER);

		listPanel.setPreferredSize(new Dimension(400,250));
		listPanel.setVisible(true);
		pack();

		lightIdentifiersList.addMouseListener(new MouseInputAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int selectedIndex = lightIdentifiersList.getSelectedIndex();
				if (selectedIndex !=-1) {
					PHLight light = allLights.get(selectedIndex);
					String lightIdentifer = light.getIdentifier();
					PHLightState lightState = light.getLastKnownLightState();
					if ( lightState.isOn() == false ) {
						lightState.setOn( true );
					}
					if ( lightState.getEffectMode().equals(PHLight.PHLightEffectMode.EFFECT_COLORLOOP) ) {
						lightState.setEffectMode(PHLight.PHLightEffectMode.EFFECT_NONE);
					}
					else {
						lightState.setEffectMode(PHLight.PHLightEffectMode.EFFECT_COLORLOOP);              	  
					}
					bridge.updateLightState(lightIdentifer, lightState, null);  // null is passed here as we are not interested in the response from the Bridge. 
					DefaultListModel <String> listListStr = new DefaultListModel<String>();

					for (PHLight myLight : allLights) {
						listListStr.addElement( buildName( myLight ) );
					}
					lightIdentifiersList.setModel( listListStr );
				}
			}

		}); 


	}


	
private String buildName( PHLight light ) {
	PHLightState state = light.getLastKnownLightState();
	String effect = "None";
	if ( state.getEffectMode().equals( PHLight.PHLightEffectMode.EFFECT_COLORLOOP) ) {
		effect = "Loop";
	}
	String lightName = StringUtils.rightPad( light.getName(), 30);
	String name = light.getIdentifier() + "  " + lightName + "  " + effect;
	return name;
}
	
}