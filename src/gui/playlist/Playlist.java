package gui.playlist;

import gui.Song;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class Playlist implements TableModel {
	
	private LinkedList<Song> songs;
	
	private ArrayList<TableModelListener> tableModelListeners = new ArrayList<>();
	
	private Map<String, String> columnsToFunctionsMap = new LinkedHashMap<>();
	
	public Playlist() {
		songs = new LinkedList<>();
		// #if PlaylistShowCover
//@		columnsToFunctionsMap.put("Image", "getImageIcon");
		// #endif
		columnsToFunctionsMap.put("Title", "getTitle");
		columnsToFunctionsMap.put("Length", "getLength");
		columnsToFunctionsMap.put("Artist", "getArtist");
		columnsToFunctionsMap.put("Album", "getAlbum");
		columnsToFunctionsMap.put("Filename", "getFileName");
	}
	
	public Song getSong(int index) {
		return songs.get(index);
	}
	
	@Override
	public void addTableModelListener(TableModelListener l) {
		tableModelListeners.add(l);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		String methodName = (String) columnsToFunctionsMap.values().toArray()[columnIndex];
		try {
			Method method = Song.class.getMethod(methodName);
			return method.getReturnType();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
		return String.class;
	}

	@Override
	public int getColumnCount() {
		return columnsToFunctionsMap.size();
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		return (String) columnsToFunctionsMap.keySet().toArray()[columnIndex];
	}

	@Override
	public int getRowCount() {
		return songs.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		String methodName = (String) columnsToFunctionsMap.values().toArray()[columnIndex];
		Song song = songs.get(rowIndex);
		
		Method method;
		try {
			method = Song.class.getMethod(methodName);
			return method.invoke(song);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		tableModelListeners.remove(l);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		
	}

	public void append(Song song) {
		songs.add(song);
		for(TableModelListener l : tableModelListeners) {
			l.tableChanged(new TableModelEvent(this, songs.size()));
		}
	}
	
	public int indexOf(Song song) {
		return songs.indexOf(song);
	}
}
