package com.electromagneticsoftware;

import com.electromagneticsoftware.data.HueProperties;
import com.electromagneticsoftware.gui.DesktopView;
import com.philips.lighting.hue.sdk.PHHueSDK;

public class HueDesktop {

	public static void main(String[] args) {
		new HueDesktop();

	}
	   public HueDesktop() {
	        PHHueSDK phHueSDK = PHHueSDK.create();

	        HueProperties.loadProperties();  // Load in HueProperties, if first time use a properties file is created.


	        //  Set Up the View (A JFrame, MenuBar and Console).
	        DesktopView desktopView = new DesktopView();

	        // Bind the Model and View
	        Controller controller = new Controller(desktopView);
	        desktopView.setController(controller);

	        phHueSDK.getNotificationManager().registerSDKListener(controller.getListener());

	    }

}
