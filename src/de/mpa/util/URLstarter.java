package de.mpa.util;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * starts url in a browser
 * @author Roman
 *
 */
public class URLstarter {

	
	/**
	 * starts with standard browser, or another wished browser
	 * @param url
	 */
	public static void openURL(String url){
		if(Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)){
			try {
				Desktop.getDesktop().browse(new URI(url));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			Runtime runtime = Runtime.getRuntime();
			try {
				//TODO analyze the System and choose the right browser
				runtime.exec("chromium-browser " + url);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
