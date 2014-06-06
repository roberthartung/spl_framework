package gui;

import gui.playlist.Playlist;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontFormatException;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableRowSorter;

import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;

import players.MP3Player;
import players.Player.Status;
import util.Util;

@SuppressWarnings({ "serial", "unused" })
public class GUI extends JFrame implements ActionListener, ChangeListener {
	final static char ICON_CODE_PLAY = '\uf04b';
	
	final static char ICON_CODE_PAUSE = '\uf04c';
	
	private JButton playButton;
	
	//private JButton pauseButton;

	private JButton stopButton;
	
	// #if ShuffleRepeat
	private IconButton shuffleRepeatButton;
	
	private enum ShuffleType {REPEAT_ONCE, SHUFFLE_RANDOM, REPEAT_ONE, REPEAT_PLAYLIST};
	
	private ShuffleType shuffleType = ShuffleType.REPEAT_ONCE;
	
	// #endif

	private JFileChooser fileChooser;

	private static String[] allowedFileExtensions = {".mp3"};
	
	private JMenuItem menuItemOpenFile;
	
	// #if Playlist
	private JMenuItem menuItemOpenFolder;
	
	//private JSplitPane splitPaneWestCenter;
	// #endif
	
	private IconButton nextButton;
	
	private IconButton previousButton;
	
	// #if Mute
	private IconButton muteButton;
	// #endif
	
	//#if LoadFolder
	private boolean allowDirectories;
	//#endif

	private JLabel songNameLabel;
	
	// #if VolumeControl
	private JSlider volumeSlider;
	
	private JLabel volumeLabel;
	// #endif
	
	// #if ProgressBar
	private JSlider positionSlider;
	
	private JLabel label_time_played;
	
	private JLabel label_time_remaining;
	// #endif
	
	private JPanel controlsPanel;
	// #if Playlist
	private JTable playlistTable;
	
	private Playlist playlist;
	
	private JPanel playlistMenuPanel;
	
	private List<Integer> playlistRandomSongIndexes = null;
	
	private Iterator<Integer> playlistRandomSongIterator = null;
	// #endif
	
	private MP3Player player;
	
	private int componentColumn = 0;
	
	// #if ShowCover
	private JPanel coverPanel;
	
	private JLabel noCoverLabel = new JLabel("No cover available");
	// #endif
	
	public GUI() {
		initialize();
		// #if Resizable
		setResizable(true);
		// #else
//@		 setResizable(false);
		// #endif
	}
	
	private void initialize() {
		BorderLayout layout = new BorderLayout();
		setLayout(layout);
		
		initFileChooser();
		initControlsPanel();
		// #if Playlist
		initPlaylist();
		// #endif
		
		// #if ShowCover
		coverPanel = new JPanel();
		coverPanel.setLayout(new BoxLayout(coverPanel, BoxLayout.Y_AXIS));
		coverPanel.add(noCoverLabel);
		//coverPanel.setSize(100,100);
		//coverPanel.setPreferredSize(new Dimension(20,20));
		// #endif
		
		initMenu();
		
		// #if Playlist
		//initPlaylistMenu();
		add(controlsPanel, BorderLayout.SOUTH);
			// #if ShowCover
			/*splitPaneWestCenter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT) {
				final static int DIVIDER_MIN = 100;
				
				final static int DIVIDER_MAX = 250;
				
				private int location = DIVIDER_MIN;
			    @Override
			    public int getDividerLocation() {
			        return location;
			    }
			    @Override
			    public int getLastDividerLocation() {
			        return location;
			    }
			    @Override
			    public void setDividerLocation(int pos) {
			    	if(pos < DIVIDER_MIN) {
			    		location = DIVIDER_MIN;
			    	} else if(pos > DIVIDER_MAX) {
			    		location = DIVIDER_MAX;
			    	} else {
			    		location = pos;
			    	}
			    	setLastDividerLocation(location);
			    }
			};*/
			//splitPaneWestCenter.setLeftComponent(coverPanel);
			//splitPaneWestCenter.setRightComponent();
			//splitPaneWestCenter.setResizeWeight(0);
			//splitPaneWestCenter.setOneTouchExpandable(true);
			//add(splitPaneWestCenter, BorderLayout.CENTER);
			add(new JScrollPane(playlistTable), BorderLayout.CENTER);
			add(coverPanel, BorderLayout.WEST);
			// #else
//@			add(new JScrollPane(playlistTable), BorderLayout.CENTER);
			// #endif
		// #else
			// #if ShowCover
//@			add(coverPanel, BorderLayout.CENTER);
//@			add(controlsPanel, BorderLayout.SOUTH);
			// #else
//@			add(controlsPanel, BorderLayout.CENTER);
			// #endif
		// #endif
	}

	private void initMenu() {
		JMenuBar menu = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		
		menuItemOpenFile = new JMenuItem("Open file");
		fileMenu.add(menuItemOpenFile);
		menuItemOpenFile.addActionListener(this);
		
		// #if Playlist
		menuItemOpenFolder = new JMenuItem("Open folder");
		fileMenu.add(menuItemOpenFolder);
		menuItemOpenFolder.addActionListener(this);
		// #endif
		
		
		
		menu.add(fileMenu);
		setJMenuBar(menu);
	}

	private void initFileChooser() {
		fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File("N:\\"));
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.addChoosableFileFilter(new FileFilter() {
			
			@Override
			public String getDescription() {
				return null;
			}
			
			@Override
			public boolean accept(File arg0) {
				boolean validFileExtension = false;
				
				for(String ext : allowedFileExtensions) {
					if(arg0.getName().endsWith(ext)) {
						validFileExtension = true;
						break;
					}
				}
				
				return arg0.isDirectory() || validFileExtension;
			}
		});
	}

	// #if Playlist
	void initPlaylist() {
		playlist = new Playlist();
		playlistTable = new JTable(playlist);
		playlistTable.setRowHeight(40);
		playlistTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		playlistTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JTable playlistTable = (JTable) e.getSource();
				
				if(e.getClickCount() == 2) {
					// If we select a song and repeated the old song -> switch to default
					// #ifdef ShuffleRepeat
					if(shuffleType == ShuffleType.REPEAT_ONE) {
						setShuffleType(ShuffleType.REPEAT_ONCE);
					}
					// #endif
					
					System.out.println("Song selected");
					int[] selectedRows = playlistTable.getSelectedRows();
					for(int i : selectedRows) {
						int index = playlistTable.convertRowIndexToModel(i);
						Playlist p = (Playlist) playlistTable.getModel();
						Song song = p.getSong(index);
						playSong(song);
					}
				}
			}

			
		});
		playlistTable.setShowHorizontalLines(true);
		playlistTable.setGridColor(Color.gray);
	}
	// #endif
	
	/*
	private JButton createButton() {
		JButton btn = new JButton("Add Playlist", new TextIconButton('\uf067'));
		btn.setBorder(new MatteBorder(0,4,0,0, Color.green));
		btn.setIconTextGap(20);
		//btn.setMargin(new Insets(5,10,5,10));
		btn.setOpaque(true);
		btn.setBackground(Color.black);
		btn.setForeground(Color.white);
		btn.setAlignmentY(0);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.weightx = 1;
		//c.weighty = 1;
		c.gridy = componentColumn++;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.PAGE_START;
		//c.ipady = 5;
		//c.ipadx = 5;
		//c.insets = new Insets(20,20,20,20);
		// #if Playlist
		playlistMenuPanel.add(btn, c);
		// #endif
		
		//playlistMenuPanel.add(Box.createRigidArea(new Dimension(0,5)));
		
		return btn;
	}
	*/
	
	/*
	// #if Playlist
	private void initPlaylistMenu() {
		
		// playlistMenuPanel = new JToolBar(JToolBar.VERTICAL);
		// playlistMenuPanel.setLayout(new GridLayout(0,1));
		GridBagLayout layout = new GridBagLayout();
		
		playlistMenuPanel = new JPanel(layout);
		// layout.setConstraints(playlistMenuPanel, c);
		//BoxLayout layout = new BoxLayout(playlistMenuPanel, BoxLayout.Y_AXIS);
		//playlistMenuPanel.setLayout(layout);
		//playlistMenuPanel.setFloatable(false);
		playlistMenuPanel.setBackground(Color.black);
		//playlistMenuPanel.add(new JLabel("Playlists"));
		//playlistMenuPanel.addSeparator();
		for(int i=0;i<10;i++)
			createButton();
		
		GridBagConstraints c = new GridBagConstraints();
		JPanel panel = new JPanel();
		panel.setOpaque(true);
		c.weighty = 1;
		c.gridy = componentColumn++;
		playlistMenuPanel.add(panel, c);
	}
	// #endif
	 */
	
	private void initControlsPanel() {
		controlsPanel = new JPanel();
		controlsPanel.setBorder(new EmptyBorder(10, 10, 10, 10) );
		
		GroupLayout groupLayoutPanel = new GroupLayout(controlsPanel);
		controlsPanel.setLayout(groupLayoutPanel);
		
		songNameLabel = new JLabel();
		
		// #if ProgressBar
		positionSlider = new JSlider();
		positionSlider.setValue(0);
		positionSlider.setEnabled(false);
		positionSlider.addChangeListener(this);
		// #endif
		
		playButton = new IconButton(ICON_CODE_PLAY, 25);
		// pauseButton = new IconButton('\uf04c');
		stopButton = new IconButton('\uf04d');
		// #if Mute
		muteButton = new IconButton('\uf028');
		// #endif
		nextButton = new IconButton('\uf051');
		previousButton = new IconButton('\uf048');
		// #if ShuffleRepeat
		shuffleRepeatButton = new IconButton('\uf01e');
		shuffleRepeatButton.setToolTipText("Shuffle: Play playlist once.");
		// #endif
		
		playButton.setPreferredSize(new Dimension(35,35));
		//pauseButton.setPreferredSize(new Dimension(25,25));
		stopButton.setPreferredSize(new Dimension(25,25));
		// #if Mute
		muteButton.setPreferredSize(new Dimension(25,25));
		// #endif
		nextButton.setPreferredSize(new Dimension(25,25));
		previousButton.setPreferredSize(new Dimension(25,25));
		// #if ShuffleRepeat
		shuffleRepeatButton.setPreferredSize(new Dimension(25,25));
		// #endif
		
		playButton.setEnabled(false);
		//pauseButton.setEnabled(false);
		stopButton.setEnabled(false);
		nextButton.setEnabled(false);
		previousButton.setEnabled(false);
		
		playButton.addActionListener(this);
		//pauseButton.addActionListener(this);
		stopButton.addActionListener(this);
		// #if Mute
		muteButton.addActionListener(this);
		// #endif
		nextButton.addActionListener(this);
		previousButton.addActionListener(this);
		// #if ShuffleRepeat
		shuffleRepeatButton.addActionListener(this);
		// #endif
		
		// #if VolumeControl
		volumeLabel = new JLabel("100 %");
		volumeLabel.setPreferredSize(new Dimension(40, 20));
		volumeSlider = new JSlider();
		volumeSlider.addChangeListener(this);
		volumeSlider.setMinimum(0);
		volumeSlider.setMaximum(100);
		volumeSlider.setValue(100);
		// #endif
		
		// #if ProgressBar
		label_time_played = new JLabel("00:00");
		label_time_remaining = new JLabel("00:00");
		// #endif
		
		groupLayoutPanel.setHorizontalGroup(
			groupLayoutPanel.createSequentialGroup()
			.addComponent(previousButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			//.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(playButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			//.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(nextButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			//.addPreferredGap(ComponentPlacement.UNRELATED)
			//.addComponent(pauseButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
//			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(stopButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			// #if Mute
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(muteButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			// #endif
			// #if VolumeControl
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(volumeSlider, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(volumeLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			// #endif
			// #if ProgressBar
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(label_time_played, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			// ...
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(positionSlider, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			// ...
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(label_time_remaining, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			// #endif
			// #if ShuffleRepeat
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(shuffleRepeatButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			// #endif
		);
		
		groupLayoutPanel.setVerticalGroup(
			groupLayoutPanel.createParallelGroup(Alignment.CENTER)
				.addComponent(previousButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				//.addGap(15)
				.addComponent(playButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				//.addGap(15)
				.addComponent(nextButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				//.addGap(15)
				//.addComponent(pauseButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				//addGap(15)
				.addComponent(stopButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				// #if Mute
				.addGap(15)
				.addComponent(muteButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				// #endif
				// #if VolumeControl
				.addGap(15)
				.addComponent(volumeSlider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addGap(15)
				.addComponent(volumeLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				// #endif
				// #if ProgressBar
				.addGap(15)
				.addComponent(label_time_played, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addGap(15)
				.addComponent(positionSlider, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addGap(15)
				.addComponent(label_time_remaining, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				// #endif
				// #if ShuffleRepeat
				.addGap(15)
				.addComponent(shuffleRepeatButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				// #endif
		);
		
		/*
		if(true) return;
		
		groupLayoutPanel.setHorizontalGroup(
			groupLayoutPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayoutPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayoutPanel.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayoutPanel.createSequentialGroup()
							.addGroup(groupLayoutPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayoutPanel.createSequentialGroup()
									.addComponent(playButton, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(pauseButton, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(stopButton, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))
								.addComponent(songNameLabel, GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(muteButton, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(volumeSlider, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(volumeLabel, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
							)
						.addComponent(positionSlider, GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE))
					.addContainerGap())
		);
		
		groupLayoutPanel.setVerticalGroup(
			groupLayoutPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayoutPanel.createSequentialGroup()
					.addGap(15)
					.addGroup(groupLayoutPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(volumeSlider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(muteButton)
						.addComponent(volumeLabel)
						.addComponent(songNameLabel, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))
					.addGap(15)
					.addComponent(positionSlider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayoutPanel.createParallelGroup(Alignment.LEADING, false)
						.addComponent(playButton, 0, 25, Short.MAX_VALUE)
						.addGroup(groupLayoutPanel.createParallelGroup(Alignment.BASELINE)
							.addComponent(pauseButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(stopButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
					.addGap(15))
		);
		*/
	}
	
	// #ifdef ShuffleRepeat
	private void setShuffleType(ShuffleType newType) {
		switch(newType) {
		case REPEAT_ONCE:
			shuffleRepeatButton.setText("\uf01e");
			shuffleRepeatButton.setToolTipText("Shuffle: Play playlist once.");
			break;
		case REPEAT_ONE:
			shuffleRepeatButton.setText("\uf001");
			shuffleRepeatButton.setToolTipText("Shuffle: Repeat Song.");
			break;
		case REPEAT_PLAYLIST:
			shuffleRepeatButton.setText("\uf03a");
			shuffleRepeatButton.setToolTipText("Shuffle: Repeat Playlist.");
			break;
		case SHUFFLE_RANDOM:
			long seed = System.nanoTime();
			playlistRandomSongIndexes = new LinkedList<>();
			// If we are playing a song, insert at the beginning
			if(currentSongIndex != null)
				playlistRandomSongIndexes.add(currentSongIndex);
			
			for(int i=0; i<playlist.getRowCount();i++) {
				if(currentSongIndex != i)
					playlistRandomSongIndexes.add(i);
			}
			Collections.shuffle(playlistRandomSongIndexes, new Random(seed));
			playlistRandomSongIterator = playlistRandomSongIndexes.iterator();
			shuffleRepeatButton.setText("\uf074");
			shuffleRepeatButton.setToolTipText("Shuffle: Play Random.");
			break;
		}
		
		shuffleType = newType;
	}
	// #endif
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source == menuItemOpenFile) {
			// #if LoadFolder
			allowDirectories = false;
			// #endif
			openFileChooser();
		}
		else if(source == playButton) {
			if(player != null) {
				if(player.getPlayerStatus() == Status.PLAYING) {
					pause();
				} else if(player.getPlayerStatus() == Status.PAUSED) {
					resume();
				} else {
					playSong(currentSong);
				}
			} else {
				playSong(currentSong);
			}
		} else if(source == stopButton) {
			stop();
			// #ifdef ShuffleRepeat
			if(shuffleType == ShuffleType.REPEAT_ONE){
				setShuffleType(ShuffleType.REPEAT_ONCE);
			}
			// #endif
		}
		// #if Mute
		else if(source == muteButton) {
			if(player != null) {
				if(player.isMuted()) {
					player.unmute();
					// #if VolumeControl
					volumeSlider.setValue(volume);
					if(volumeSlider.getValue() >= volumeSlider.getMaximum()/2) {
						muteButton.setText("\uf028");
					} else {
						muteButton.setText("\uf027");
					}
					// #endif
					volumeLabel.setText("" + String.format("%3.0f", (volumeSlider.getValue() / ((float) volumeSlider.getMaximum())) * 100) + " %");
				} else {
					volume = volumeSlider.getValue();
					player.mute();
					volumeSlider.removeChangeListener(this);
					volumeSlider.setValue(0);
					volumeSlider.addChangeListener(this);
					// #if Mute
					muteButton.setText("\uf026");
					// #endif
					volumeLabel.setText("0 %");
				}
			}
		}
		// #endif
		// #if LoadFolder
		else if(source == menuItemOpenFolder) {
			allowDirectories = true;
			openFileChooser();
		}
		// #endif
		// #if ShuffleRepeat
		else if(source == shuffleRepeatButton) {
			// REPEAT_ONCE, SHUFFLE_RANDOM, REPEAT_ONE, REPEAT_PLAYLIST
			switch(shuffleType) {
				case REPEAT_ONCE:
					setShuffleType(ShuffleType.SHUFFLE_RANDOM);
				break;
				case SHUFFLE_RANDOM :
					setShuffleType(ShuffleType.REPEAT_ONE);
					break;
				case REPEAT_ONE :
					setShuffleType(ShuffleType.REPEAT_PLAYLIST);
					break;
				case REPEAT_PLAYLIST :
					setShuffleType(ShuffleType.REPEAT_ONCE);
					break;
			}
		}
		// #endif
	}
	
	// #if Playlist
	private Integer currentSongIndex = null;
	// #endif
	
	private Song currentSong;
	
	private Timer currentPositionTimer;
	
	private void playSong(Song song) {
		System.out.println("Playsong: " + song.getTitle());
		
		if(player != null) {
			unloadSong();
		}
		
		if(player == null || player.getPlayerStatus() == Status.READY || player.getPlayerStatus() == Status.STOPPED) {
			currentSong = song;
			// #if Playlist
			currentSongIndex = playlist.indexOf(song);
			// #endif
			
			try {
				// #if ProgressBar
				positionSlider.setEnabled(true);
				positionSlider.setMaximum(song.getLengthInSeconds() * 10);
				positionSlider.setValue(0);
				label_time_played.setText("00:00");
				label_time_remaining.setText(song.getLength());
				// #endif
				player = new MP3Player(song.getFilePath());
				// #if VolumeControl
				player.setVolume(volumeSlider.getValue() / 100f);
				// #endif
				player.play();
				playButton.setText(String.valueOf(ICON_CODE_PAUSE));
				playButton.setEnabled(true);
				
				songNameLabel.setText(song.getTitle());
				
				// #if ShowCover
				byte[] albumImageData = song.getImage();
				coverPanel.removeAll();
				if(albumImageData != null) {
					ImageIcon image = new ImageIcon(albumImageData);
					ImageIcon resized = new ImageIcon(image.getImage().getScaledInstance(250, 250, java.awt.Image.SCALE_SMOOTH));
					JLabel label = new JLabel(resized);
					label.setVerticalAlignment(SwingConstants.BOTTOM);
					label.setHorizontalAlignment(SwingConstants.LEFT);
					label.setMinimumSize(new Dimension(100, 100));
					label.setMaximumSize(new Dimension(250, 250));
					coverPanel.add(label);
					
				} else {
					coverPanel.add(new JLabel("No cover available"));
				}
				coverPanel.add(songNameLabel);
				// #endif
			} catch (UnsupportedAudioFileException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (LineUnavailableException e1) {
				e1.printStackTrace();
			}
		} else if(player.getPlayerStatus() == Status.PAUSED) {
			player.resume();
			
		}
		
		startTimer();
		//playButton.setEnabled(false);
		//pauseButton.setEnabled(true);
		stopButton.setEnabled(true);
		playButton.setText(String.valueOf(ICON_CODE_PAUSE));
	}
	
	private void startTimer() {
		currentPositionTimer = new Timer();
		currentPositionTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				timerTick();
			}
		}, 100, 100);
	}
	
	private void updateTitlesAndLabels() {
		long millis_current = player.getPosition() / 1000;
		String currentTime = Util.secondsToTimeString((int) (millis_current / 1000));
		
		setTitle(currentSong.getArtist() + " - " + currentSong.getTitle() 
				// #if ShowTime
				+ " " + currentTime + " / " +
				currentSong.getLength()
				// #endif
		);
		
		// #if ProgressBar
		label_time_played.setText(currentTime);
		// #endif
	}
	
	private void timerTick() {
		if(player != null) {
			long millis_current = player.getPosition() / 1000;
			long millis_total = player.getLength() / 1000;
			
			if(millis_current >= millis_total) {
				_onSongEnded();
			}
			else {
			// #if ProgressBar
				positionSlider.setValue( (int) (millis_current / 100) );
			// #endif
				updateTitlesAndLabels();
			}
			
		}
	}
	
	private void unloadSong() {
		if(player != null) {
			player.close();
			stop();
			player = null;
			playButton.setEnabled(false);
		}
	}
	
	private void stop() {
		stopTimer();
		player.stop();
		
		// #if ProgressBar
		positionSlider.setValue(0);
		positionSlider.setEnabled(false);
		// #endif
		
		playButton.setText(String.valueOf(ICON_CODE_PLAY));
		stopButton.setEnabled(false);
		updateTitlesAndLabels();
	}
	
	private void _onSongEnded() {
		stop();
		// automatic stop
		
		// #if ShuffleRepeat
		switch(shuffleType) {
			case REPEAT_ONCE :
				if(currentSongIndex < playlist.getRowCount() - 1) {
					currentSongIndex++;
					playSongAt(currentSongIndex);
				}
			break;
			case REPEAT_PLAYLIST :
				if(currentSongIndex < playlist.getRowCount() - 1) {
					currentSongIndex++;
					playSongAt(currentSongIndex);
				} else {
					playSongAt(0);
				}
				break;
			case REPEAT_ONE :
				playSongAt(currentSongIndex);
				break;
			case SHUFFLE_RANDOM :
				if(playlistRandomSongIterator.hasNext()) {
					int next = playlistRandomSongIterator.next();
					System.out.println("Playing next song: " + next);
					playSongAt(next);
				} else {
					System.err.println("No more songs to play.");
				}
				break;
		}
		// #endif
	}
	
	private void stopTimer() {
		currentPositionTimer.cancel();
		currentPositionTimer.purge();
	}
	
	// #if Playlist
	private void playSongAt(int pos) {
		Playlist p = (Playlist) playlistTable.getModel();
		Song song = p.getSong(pos);
		playSong(song);
	}
	// #endif
	
	private void _onFilesLoaded(int fileCount) {
		if(fileCount == 0)
			return;
		
		// #if Playlist
		if(fileCount == playlist.getRowCount()) {
			// All files added from last file dialog
			playSongAt(0);
		}
		// #else
//@		playSong(currentSong);
		// #endif
		
		// #if Playlist
		TableRowSorter<Playlist> sorter = new TableRowSorter<Playlist>((Playlist) playlistTable.getModel());
		// #if PlaylistShowCover
//@		sorter.setSortable(0, false);
		// #endif
		playlistTable.setRowSorter(sorter);
		// #endif
	}

	private void openFileChooser() {
		//#if LoadFolder
		if(allowDirectories) {
			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		} else {
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		}
		//#else
//@		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		//#endif
		
		//#if Playlist
		if(allowDirectories) {
			fileChooser.setMultiSelectionEnabled(true);
		} else {
			fileChooser.setMultiSelectionEnabled(false);
		}
		//#else
//@		fileChooser.setMultiSelectionEnabled(false);
		//#endif
		
		final JFrame frame = this;
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				fileChooser.showOpenDialog(frame);
				
				// #if Playlist
				playlistTable.setRowSorter(null);
				// #endif
				
				int fileCount = 0;
				// #if LoadFolder
				if(fileChooser.getSelectedFiles().length > 0) {
					System.out.println("Multiple");
					// #if SearchRecursive
//@					LinkedList<SongAppender> threads = new LinkedList<>();
					// #endif
					
					for(File f : fileChooser.getSelectedFiles()) {
						// #if SearchRecursive
//@						SongAppender sa = new SongAppender(f);
//@						threads.add(sa);
//@						sa.start();
						// #else
						if(f.isDirectory()) {
							File[] files = f.listFiles(new java.io.FileFilter() {
								@Override
								public boolean accept(File f) {
									return f.isFile() && f.getName().endsWith(".mp3");
								}
							});
							for(File file : files) {
								try {
									playlist.append(new Mp3Song(file));
									fileCount++;
								}
								// #if farng
//@								catch (TagException e) {
//@									e.printStackTrace();
//@								}
								// #endif
								catch (IOException e) {
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
							}
						} else {
							try {
								playlist.append(new Mp3Song(f));
								fileCount++;
							}
							// #if farng
//@							catch (TagException e) {
//@								e.printStackTrace();
//@							}
							// #endif
							catch (IOException e) {
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
						}
						// #endif
						
					}
					
					// #if SearchRecursive
//@					for(SongAppender sa : threads) {
//@						try {
//@							sa.join();
//@							for(Song song : sa.getSongs()) {
//@								playlist.append(song);
//@								fileCount++;
//@							}
//@						} catch (InterruptedException e) {
//@							e.printStackTrace();
//@						}
//@					}
					// #endif
				} else {
				// #endif
					File f = fileChooser.getSelectedFile();
					if(f != null) {
						try {
							// #if Playlist
							playlist.append(new Mp3Song(f));
							// #else
//@							currentSong = new Mp3Song(f);
							// #endif
							fileCount = 1;
						}
						// #if farng
//@						 catch (TagException e) {
//@							// TODO Auto-generated catch block
//@							e.printStackTrace();
//@						}
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
					}
				// #if LoadFolder
				}
				// #endif
				
				_onFilesLoaded(fileCount);
			}
		});
	}

	private int volume;
	
	@Override
	public void stateChanged(ChangeEvent e) {
		Object source = e.getSource();
		
		// #if ProgressBar
		if(source == positionSlider) {
			if(player != null) {
				if(positionSlider.getValue() != ((int) player.getPosition() / 1000 / 100)) {
					player.seek(positionSlider.getValue() * 1000 * 100);
				}
			}
		}
		// #endif
		// #if VolumeControl
		if(source == volumeSlider) {
			if(player != null && player.isMuted()) {
				player.unmute();
			}
			// #if Mute
			if(volumeSlider.getValue() >= volumeSlider.getMaximum()/2) {
				muteButton.setText("\uf028");
			} else {
				muteButton.setText("\uf027");
			}
			// #endif
			
			volumeLabel.setText("" + String.format("%3.0f", (volumeSlider.getValue() / ((float) volumeSlider.getMaximum())) * 100) + "%");
			
			if(player != null) {
				player.setVolume(volumeSlider.getValue() / ((float) volumeSlider.getMaximum()));
			}
		}
		// #endif
	}
	
	private void pause() {
		if(player != null) {
			if(player.getPlayerStatus() == Status.PLAYING) {
				player.pause();
				//pauseButton.setEnabled(false);
				//playButton.setEnabled(true);
				playButton.setText(String.valueOf(ICON_CODE_PLAY));
				stopButton.setEnabled(true);
				stopTimer();
			}
		}
	}
	
	private void resume() {
		if(player != null) {
			if(player.getPlayerStatus() == Status.PAUSED) {
				player.resume();
				playButton.setText(String.valueOf(ICON_CODE_PAUSE));
				stopButton.setEnabled(true);
				startTimer();
			}
		}
	}
}
