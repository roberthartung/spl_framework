package plugins;

import java.awt.Window;

import javax.swing.JFrame;

import featureamp.FeatureAmp;
import featureamp.framework.Plugin;
import featureamp.framework.interfaces.WindowProvider;

public class GUI implements Plugin, WindowProvider {
	JFrame window;
	
	public GUI(FeatureAmp amp) {
		window = new JFrame();
		window.setVisible(true);
	}

	@Override
	public void registered() {
		
	}

	@Override
	public Window[] createWindows() {
		Window[] windows = new Window[1];
		windows[0] = window;
		return windows;
	}
}
