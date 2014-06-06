package gui;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

//#if mp3agic
//@import com.mpatric.mp3agic.ID3v1;
//@import com.mpatric.mp3agic.ID3v2;
//@import com.mpatric.mp3agic.InvalidDataException;
//@import com.mpatric.mp3agic.Mp3File;
//@import com.mpatric.mp3agic.UnsupportedTagException;
// #endif

// #if farng
//@import org.farng.mp3.MP3File;
//@import org.farng.mp3.TagException;
//@import org.farng.mp3.id3.AbstractID3v2;
//@import org.farng.mp3.id3.ID3v1;
// #endif






import javax.swing.ImageIcon;


// #if jaudiotagger
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
// #endif
import org.tritonus.share.sampled.file.TAudioFileFormat;

import util.Util;

public class Mp3Song implements Song {
	private File file;
	
	// #if mp3agic
//@	private Mp3File mp3File;
//@	private ID3v1 id3v1Tag;
//@	
//@	private ID3v2 id3v2Tag;
	// #endif
	// #if farng
//@	private MP3File mp3File;
//@	
//@	private ID3v1 id3v1;
//@	
//@	private AbstractID3v2 id3v2;
	// #endif
	
	// #if jaudiotagger
	private MP3File mp3File;
	
	private ID3v1Tag id3v1;
	
	private AbstractID3v2Tag id3v2;
	// #endif
	
	private Integer milliseconds;
	
	public Mp3Song(File file) throws
	// #if mp3agic
//@	UnsupportedTagException, InvalidDataException,
	// #endif
	// #if farng
//@	TagException,
	// #endif
	// #if jaudiotagger
	TagException, ReadOnlyFileException, InvalidAudioFrameException, 
	// #endif
	IOException {
		this.file = file;
		// #if mp3agic
//@		mp3File = new Mp3File(file.getPath(), 65535, true);
//@		if(mp3File.hasId3v2Tag()) {
//@			id3v2Tag = mp3File.getId3v2Tag();
//@		} else if(mp3File.hasId3v1Tag()) {
//@			id3v1Tag = mp3File.getId3v1Tag();
//@		} else {
//@			System.err.println("[Mp3Song] No information im mp3 file.");
//@		}
		// #endif
		
		// #if farng
//@		mp3File = new MP3File(file);
//@		
//@		if(mp3File.hasID3v2Tag()) {
//@			try {
//@				id3v2 = mp3File.getID3v2Tag();
//@			} catch(UnsupportedOperationException e) {
//@				System.err.println("Unable to load mp3 ID3 information.");
//@			}
//@			
//@			System.out.println(id3v2.getFrame("PIC"));
//@			System.out.println(id3v2.getFrameOfType("PIC").hasNext());
//@			
//@			/*
//@			Iterator it = id3v2.getFrameOfType("TLEN");
//@			while(it.hasNext()) {
//@				ID3v2_3Frame len = (ID3v2_3Frame) it.next();
//@				milliseconds = Integer.parseInt(((FrameBodyTLEN) len.getBody()).getText());
//@				// System.out.println(  );
//@			}
//@			*/
//@		} else if(mp3File.hasID3v1Tag()) {
//@			id3v1 = mp3File.getID3v1Tag();
//@		}
		// #endif
		// #if jaudiotagger
		mp3File = new MP3File(file);
		if(mp3File.hasID3v2Tag()) {
			id3v2 = mp3File.getID3v2Tag();
		}
		
		if(mp3File.hasID3v1Tag()) {
			id3v1 = mp3File.getID3v1Tag();
		}
		
		// #endif
		if(milliseconds == null) {
			AudioFileFormat fileFormat;
			try {
				fileFormat = AudioSystem.getAudioFileFormat(file);
				if (fileFormat instanceof TAudioFileFormat) {
			        Map<?, ?> properties = ((TAudioFileFormat) fileFormat).properties();
			        String key = "duration";
			        Long microseconds = (Long) properties.get(key);
			        milliseconds = (int) (microseconds / 1000);
			    } else {
					System.err.println("[Mp3Song] No information about length in mp3 file " + file.getName());
				}
			} catch (UnsupportedAudioFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public String getFileName() {
		return file.getName();
	}

	@Override
	public String getFilePath() {
		// TODO Auto-generated method stub
		return file.getPath();
	}

	@Override
	public String getTitle() {
		// #if mp3agic
//@		if(id3v2Tag != null && id3v2Tag.getTitle() != null && id3v2Tag.getTitle() != "")
//@			return id3v2Tag.getTitle();
//@		
//@		if(id3v1Tag != null && id3v1Tag.getTitle() != null && id3v1Tag.getTitle() != "")
//@			return id3v1Tag.getTitle();
		// #endif
		// #if farng
//@		if(id3v1 != null)
//@			return id3v1.getTitle();
//@		if(id3v2 != null)
//@			return id3v2.getSongTitle();
		// #endif
		// #if jaudiotagger
		if(id3v1 != null)
			return id3v1.getFirstTitle();
		if(id3v2 != null)
			return id3v2.getFirst(FieldKey.TITLE);
		// #endif
		return getFileName();
	}

	@Override
	public String getArtist() {
		// #if mp3agic
//@		if(id3v2Tag != null && id3v2Tag.getArtist() != null && id3v2Tag.getArtist() != "")
//@			return id3v2Tag.getArtist();
//@		
//@		if(id3v1Tag != null && id3v1Tag.getArtist() != null && id3v1Tag.getArtist() != "")
//@			return id3v1Tag.getArtist();
		//#endif
		// #if farng
//@		if(id3v1 != null)
//@			return id3v1.getArtist();
//@		if(id3v2 != null)
//@			return id3v2.getLeadArtist();
		//#endif
		// #if jaudiotagger
		if(id3v1 != null)
			return id3v1.getFirstArtist();
		if(id3v2 != null)
			return id3v2.getFirst(FieldKey.ARTIST);
		// #endif
		return getFileName();
	}

	@Override
	public int getLengthInSeconds() {
		
		// #if mp3agic
//@		return (int) mp3File.getLengthInSeconds();
		// #endif
		// #if farng
//@		if(milliseconds == null) {
//@			try {
//@				long l = file.length() - mp3File.getMp3StartByte();
//@				return (int) (l / mp3File.getBitRate() / 1000);
//@			} catch (IOException e) {
//@				e.printStackTrace();
//@			}
//@		}
		// #endif
		
		return milliseconds/1000;
	}

	@Override
	public String getAlbum() {
		// #if mp3agic
//@		if(id3v2Tag != null)
//@			return id3v2Tag.getAlbum();
//@		
//@		if(id3v1Tag != null)
//@			return id3v1Tag.getAlbum();
		// #endif
		// #if farng
//@		if(id3v1 != null)
//@			return id3v1.getAlbumTitle();
//@		if(id3v2 != null)
//@			return id3v2.getAlbumTitle();
		// #endif
		// #if jaudiotagger
		if(id3v1 != null)
			return id3v1.getFirstAlbum();
		if(id3v2 != null)
			return id3v2.getFirst(FieldKey.ALBUM);
		// #endif
		
		return "";
	}
	
	public String toString() {
		return getTitle();
	}

	@Override
	public String getDirectory() {
		return file.getParent();
	}
	
	@Override
	public String getLength() {
		return Util.secondsToTimeString(getLengthInSeconds());
	}

	@Override
	public byte[] getImage() {
		// #if jaudiotagger
		if(id3v2 != null) {
			Artwork aw = id3v2.getFirstArtwork();
			if(aw != null)
				return aw.getBinaryData();
		}
		
		if(id3v1 != null) {
			Artwork aw = id3v1.getFirstArtwork();
			if(aw != null)
				return aw.getBinaryData();
		}
		
		// #endif
		
		/*
		 ID3v2 id3Tag = mp3file.getId3v2Tag();
		byte[] albumImageData = id3Tag.
		if(albumImageData != null) {
			ImageIcon image = new ImageIcon(albumImageData);
			
			int size = splitPane.getDividerLocation();
			if(size < 100) {
				size = 100;
			}
			ImageIcon resized = new ImageIcon(image.getImage().getScaledInstance(size, size, java.awt.Image.SCALE_SMOOTH));
			JLabel label = new JLabel(resized);
			// Hack to make the slider resizeable
			label.setMinimumSize(new Dimension(100, 100));
			splitPane.setRightComponent(label);
		} else {
			splitPane.setRightComponent(new JPanel());
		}
		 */
		
		return null;
	}

	@Override
	public ImageIcon getImageIcon() {
		byte[] data = getImage();
		if(data == null)
			return null;
		
		ImageIcon image = new ImageIcon(data);
		return new ImageIcon(image.getImage().getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH));
	}
}
