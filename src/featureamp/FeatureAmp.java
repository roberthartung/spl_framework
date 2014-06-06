package featureamp;

import java.awt.Window;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;

import featureamp.framework.Plugin;
import featureamp.framework.interfaces.WindowProvider;

public class FeatureAmp {
	
	LinkedList<Plugin> plugins = new LinkedList<>();
	
	Window currentWindow;
	
	public FeatureAmp() {
		
	}
	
	public void registerPlugin(Plugin p) {
		System.out.println("-I- Plugin registered: " + p.getClass().getSimpleName());
		plugins.add(p);
		p.registered();
	}
	
	public void launch() {
		/*
		for(Plugin plugin : plugins) {
			if(plugin instanceof WindowProvider) {
				Window[] windows = ((WindowProvider) plugin).createWindows();
				if(windows[0].isVisible()) {
					currentWindow = windows[0];
				}
			}
		}
		*/
		Object val = invokePluginMethod("createWindows", WindowProvider.class);
		System.out.println(val);
		
		// System.out.println(currentWindow);
	}
	
	private HashMap<Plugin, Object> invokePluginMethod(String name, Class requiredInterface, Object ...arguments) {
		HashMap<Plugin,Object> returnValues = null;
		LinkedList<Class> parameters = new LinkedList<>();
		for(Object arg : arguments) {
			parameters.add(arg.getClass());
		}
		Class[] parameterList = new Class[arguments.length];
		parameters.toArray(parameterList);
		Method m = null;
		try {
			m = requiredInterface.getMethod(name, parameterList);
			
			returnValues = new HashMap<>();
			
			for(Plugin plugin : plugins) {
				if(requiredInterface.isAssignableFrom(plugin.getClass())) {
					try {
						returnValues.put(plugin, m.invoke(plugin, arguments));
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
		return returnValues;
	}
	
	public static void main(String[] args) throws InstantiationException {
		FeatureAmp amp = new FeatureAmp();
		for(String arg : args) {
			try {
				Class<?> pluginClass = Class.forName("plugins." + arg);
				Constructor<?> constructor = pluginClass.getConstructor(FeatureAmp.class);
				amp.registerPlugin((Plugin) constructor.newInstance(amp));
			} catch(NoSuchMethodException ex) {
				System.err.println("Plugin '"+arg+"' does not have a FeatureAmp constructor!");
			} catch(Exception ex) {
				System.err.println("Unable to load Plugin " + arg + ": " + ex.getMessage());
			}
		}
		amp.launch();
		
		/*
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				GUI gui = new GUI();
				gui.setVisible(true);
				gui.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				gui.setSize(800,600);
			}
		});
		*/
	}
}
