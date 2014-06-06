package gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTable;

public class PlaylistMouseListener implements MouseListener {
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if(!(e.getSource() instanceof JTable))
			return;
		
		JTable playlistTable = (JTable) e.getSource();
		
		playlistTable.rowAtPoint(e.getPoint());
		if(e.getClickCount() == 2) {
			int[] selectedRows = playlistTable.getSelectedRows();
			for(int i : selectedRows) {
				playlistTable.convertRowIndexToModel(i);
				playlistTable.getModel();
			}
			//playlistTable.setSelectionMode(1);
			/*
			int row = playlistTable.rowAtPoint(e.getPoint());
			PlaylistSelectionModel selectionModel = (PlaylistSelectionModel) playlistTable.getSelectionModel();
			selectionModel.setSelectedRow(row);
			System.out.println("doubleClick on " + row);
			*/
		} else {
			//playlistTable.setSelectionMode(0);
			//e.consume();
		}
	}
}