package com.electromagneticsoftware.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;

import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

public class LightList extends  JFrame {


	private static final long serialVersionUID = 8239876795636394848L;
	private PHHueSDK phHueSDK;
	private List<PHLight> allLights;
	private JList<String> lightIdentifiersList;


	public LightList()
	{
		// Set the frame characteristics
		setTitle( "Light List" );
		setSize( 400, 200 );
		// The the HueSDK singleton.
		phHueSDK = PHHueSDK.getInstance();

		Container content = getContentPane();

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
		lightIdentifiersList.setSelectedIndex(0);

		JScrollPane listPane = new JScrollPane(lightIdentifiersList);
		listPane.setPreferredSize(new Dimension(300,100));

		JPanel listPanel = new JPanel();
		listPanel.setBackground(Color.white);

		Border listPanelBorder = BorderFactory.createTitledBorder("My Lights");
		listPanel.setBorder(listPanelBorder);
		listPanel.add(listPane);
		content.add(listPanel, BorderLayout.CENTER);

		setPreferredSize(new Dimension(400,250));
		pack();
		setVisible(true);

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

		String name = light.getIdentifier() + "  " + light.getName() + "          " + effect;
		return name;
	}

}