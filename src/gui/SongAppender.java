package gui;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;

public class SongAppender extends Thread {
	// #if SearchRecursive
//@	private LinkedList<SongAppender> threads = new LinkedList<>();
	// #endif
	
	private File file;
	
	private LinkedList<Song> songs = new LinkedList<>();
	
	public SongAppender(File f) {
		this.file = f;
	}
	
	public LinkedList<Song> getSongs() {
		return songs;
	}
	
	public void run() {
		if(file.isFile()) {
			try {
				songs.add(new Mp3Song(file));
			}
			// #if mp3agic
			//@				catch (UnsupportedTagException e) {
			//@					// TODO Auto-generated catch block
			//@					e.printStackTrace();
			//@				} catch (InvalidDataException e) {
			//@					// TODO Auto-generated catch block
			//@					e.printStackTrace();
			//@				}
			// #endif
			// #if farng
//@			catch(TagException e) {
//@				
//@			}
			// #endif
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// #if jaudiotagger
			catch (org.jaudiotagger.tag.TagException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ReadOnlyFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidAudioFrameException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// #endif
			return;
		}
		
		for(File f : file.listFiles()) {
			if(f.isFile()) {
				if(f.getName().endsWith(".mp3")) {
					try {
						songs.add(new Mp3Song(f));
					}
					// #if mp3agic
					//@				catch (UnsupportedTagException e) {
					//@					// TODO Auto-generated catch block
					//@					e.printStackTrace();
					//@				} catch (InvalidDataException e) {
					//@					// TODO Auto-generated catch block
					//@					e.printStackTrace();
					//@				}
					// #endif
					// #if farng
//@					catch(TagException e) {
//@						
//@					}
					// #endif
					catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch(UnsupportedOperationException e) {
						System.err.println("Unable to load mp3 file " + f.getName());
					}
					// #if jaudiotagger
					catch (org.jaudiotagger.tag.TagException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ReadOnlyFileException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvalidAudioFrameException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// #endif
				}
			}
			// #if SearchRecursive
//@			else {
//@				SongAppender sa = new SongAppender(f);
//@				threads.add(sa);
//@				sa.start();
//@			}
			// #endif
		}
		
		// #if SearchRecursive
//@		for(SongAppender sa : threads) {
//@			try {
//@				sa.join();
//@				songs.addAll(sa.getSongs());
//@			} catch (InterruptedException e) {
//@				// TODO Auto-generated catch block
//@				e.printStackTrace();
//@			}
//@		}
		// #endif
	}
}
