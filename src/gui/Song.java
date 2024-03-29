package gui;

import javax.swing.ImageIcon;

public interface Song {
	public String getFileName();
	
	public String getFilePath();
	
	public String getDirectory();
	
	public String getTitle();
	
	public String getArtist();
	
	public String getAlbum();
	
	public int getLengthInSeconds();
	
	public String getLength();
	
	public byte[] getImage();
	
	public ImageIcon getImageIcon();
}