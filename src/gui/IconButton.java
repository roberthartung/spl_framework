package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Insets;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JButton;

@SuppressWarnings("serial")
public class IconButton extends JButton {
	private static InputStream in;
	
	private static Font baseFont;
	
	private static Font iconFont;
	
	static {
		try {
			in = new FileInputStream(IconButton.class.getClassLoader().getResource("fontawesome.ttf").getPath());
			baseFont = Font.createFont(Font.TRUETYPE_FONT, in);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FontFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public IconButton(char c) {
		this(c, 15);
	}
	
	public IconButton(char c, int size)  {
		iconFont = baseFont.deriveFont(Font.PLAIN, size);
		setMargin(new Insets(0, 0, 0, 0));
		setSize(25,25);
		setFont(iconFont);
		setText(String.valueOf(c));
	    setForeground(Color.BLACK);
	    setBorder(null);
	}
	
	
}