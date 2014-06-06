package gui;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.Icon;

public class TextIconButton implements Icon {
	private char chr;
	
	private static InputStream in;
	
	private static Font baseFont;
	
	private static Font iconFont;
	
	public TextIconButton(char chr) {
		this.chr = chr;
	}
	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		Font f = g.getFont();
		if(in == null) {
			try {
				in = new FileInputStream(IconButton.class.getClassLoader().getResource("fontawesome.ttf").getPath());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				baseFont = Font.createFont(Font.TRUETYPE_FONT, in);
			} catch (FontFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			iconFont = baseFont.deriveFont(Font.PLAIN, 20);
		}
		g.setFont(iconFont);
		FontMetrics m = g.getFontMetrics();
		/*
		System.out.println(m.getHeight());
		System.out.println(m.getWidths()[0]);
		*/
		g.drawString(String.valueOf(chr), x, y+m.getHeight()-6);
		g.setFont(f);
	}

	@Override
	public int getIconWidth() {
		// TODO Auto-generated method stub
		return 10;
	}

	@Override
	public int getIconHeight() {
		// TODO Auto-generated method stub
		return 22;
	}
}
