package gui;

import java.util.ArrayList;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class PlaylistSelectionModel implements ListSelectionModel {	
	int selectedRow = -1;
	
	ArrayList<ListSelectionListener> listSelectionListeners = new ArrayList<>();
	
	ArrayList<Integer> selectedRows = new ArrayList<>();
	
	@Override
	public void setValueIsAdjusting(boolean valueIsAdjusting) {
		// TODO Auto-generated method stub
		System.out.println("setValueIsAdjusting " + valueIsAdjusting);
	}
	
	@Override
	public void setSelectionMode(int selectionMode) {
		// TODO Auto-generated method stub
		System.out.println("setSelectionMode");
	}
	
	@Override
	public void setSelectionInterval(int index0, int index1) {
		// TODO Auto-generated method stub
		System.out.println("setSelectionInterval " + index0 + " - " + index1);
	}
	
	@Override
	public void setLeadSelectionIndex(int index) {
		System.out.println("setLeadSelectionIndex " + index);
	}
	
	@Override
	public void setAnchorSelectionIndex(int index) {
		// TODO Auto-generated method stub
		System.out.println("setAnchorSelectionIndex " + index);
		setSelectedRow(index);
	}
	
	@Override
	public void removeSelectionInterval(int index0, int index1) {
		System.out.println("removeSelectionInterval: " + index0 + " - " + index1);
	}
	
	@Override
	public void removeListSelectionListener(ListSelectionListener x) {
		listSelectionListeners.remove(x);
	}
	
	@Override
	public void removeIndexInterval(int index0, int index1) {
		System.out.println("removeIndexInterval: " + index0 + " - " + index1);
	}
	
	@Override
	public boolean isSelectionEmpty() {
		return selectedRow == -1;
	}
	
	@Override
	public boolean isSelectedIndex(int index) {
		// TODO Auto-generated method stub
		return (index == selectedRow);
	}
	
	@Override
	public void insertIndexInterval(int index, int length, boolean before) {
		System.out.println("insertIndexInterval: " + index + " - " + length);
	}
	
	@Override
	public boolean getValueIsAdjusting() {
		return false;
	}
	
	@Override
	public int getSelectionMode() {
		return 0;
	}
	
	@Override
	public int getMinSelectionIndex() {
		return selectedRow;
	}
	
	@Override
	public int getMaxSelectionIndex() {
		return selectedRow;
	}
	
	@Override
	public int getLeadSelectionIndex() {
		return selectedRow;
	}
	
	@Override
	public int getAnchorSelectionIndex() {
		return selectedRow;
	}
	
	@Override
	public void clearSelection() {
		setSelectedRow(-1);
	}
	
	@Override
	public void addSelectionInterval(int index0, int index1) {
		// TODO Auto-generated method stub
		System.out.println("addSelectionInterval: " + index0 + " - " + index1);
	}
	
	@Override
	public void addListSelectionListener(ListSelectionListener x) {
		listSelectionListeners.add(x);
	}

	public void setSelectedRow(int row) {
		if(selectedRow != -1) {
			notifySelectionListeners();
		}
		
		selectedRow = row;
		
		//for(ListSelectionListener x : listSelectionListeners) {
		//	x.valueChanged(new ListSelectionEvent(this, row, row, false));
		//}
		notifySelectionListeners();
	}
	
	private void notifySelectionListeners() {
		for(ListSelectionListener x : listSelectionListeners)
			x.valueChanged(new ListSelectionEvent(this, selectedRow, selectedRow, false));
	}
}
