package mediaplayer.view;

import java.io.File;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import mediaplayer.Main;
import mediaplayer.model.MediaItem;
import mediaplayer.util.DurationUtil;
 
/**
 * The Controller for the MediaPlayerView. Contains the UI functionality and
 * media playback logic.
 * 
 * @author Alex Hage
 *
 */
public class MediaPlayerViewController {
	@FXML
	private MediaView mediaView;
	
	@FXML
	private ProgressBar progBar;
	
	@FXML
	private Button addBtn;
	
	@FXML
	private Button backBtn;
	
	@FXML
	private Button playBtn;
	
	@FXML
	private Button nextBtn;
	
	@FXML
	private Button repeatBtn;
	
	@FXML
	private Button pListBtn;
	
	@FXML
	private Button fScreenBtn;
	
	@FXML
	private Button volBtn;
	
	@FXML
	private Label timeLabel;
	
	@FXML
	private Slider volSlider;
	
	/**
	 * A single MediaItem object.
	 */
	private MediaItem mediaItem;
	/**
	 * The reusable MediaPlayer.
	 */
	private MediaPlayer mediaPlayer;
	/**
	 * The reusable Media.
	 */
	private Media media;
	/**
	 * The iterator for the playlist location. Set by Main.
	 */
	private int current;
	/**
	 * The playback started flag. Initialized to <i>false</i> locally.
	 */
	private boolean playing;
	/**
	 * The playback paused flag. Initialized to <i>false</i> locally.
	 */
	private boolean paused;
	/**
	 * The media mute flag. Initialized to <i>false</i> locally.
	 */
	private boolean muted;
	/**
	 * The media repeat flag. Initialized to <i>false</i> locally.
	 */
	private boolean repeat;
	/**
	 * Reference to the main application.
	 */
	private Main main;
	
	/**
	 * The default constructor.
	 * Called before the <i>initialize()</i> method.
	 */
	public MediaPlayerViewController()
	{

	} //end ctor

	/**
	 * Initializes the controller class. Sets the controller-wise flags, UI
	 * tooltips and default values. Is automatically called after the fxml file
	 * has been loaded.
	 */
	@FXML
	public void initialize()
	{
		this.playing = false;
		this.paused = false;
		this.muted = false;
		this.repeat = false;
		
		//Adding tooltips
		addBtn.setTooltip(new Tooltip("Open..."));
		playBtn.setTooltip(new Tooltip("Play / Pause"));
		nextBtn.setTooltip(new Tooltip("Next"));
		backBtn.setTooltip(new Tooltip("Back"));
		repeatBtn.setTooltip(new Tooltip("Repeat"));
		pListBtn.setTooltip(new Tooltip("Show Playlist"));
		fScreenBtn.setTooltip(new Tooltip("Toggle Fullscreen"));
		volBtn.setTooltip(new Tooltip("Toggle Mute"));
		volSlider.setTooltip(new Tooltip("Volume"));
		
		progBar.setOnMouseClicked(progBarEventListener());
		progBar.setOnMouseDragged(progBarEventListener());
		
		volSlider.setValue(0.5);
        volSlider.valueProperty().addListener(volumeSliderChangedListener());
	}

	/**
	 * Handles the <i>Play/Pause</i> button click. Uses the flags <i>playing</i>
	 * and <i>paused</i> to initiate, pause or resume media item playback. Sets
	 * the <i>paused</i> flag by flipping it.
	 */
	@FXML
	public void playRequestHandler()
	{
		//If not playing anything, play the list.
		if(!playing)
		{
			playAll();
		}
		//Otherwise, check the paused flag.
		else
		{
			//If not paused, pause.
			if(!paused)
			{
				playBtn.setStyle("-fx-graphic: url('file:resources/images/playbtn.png'); -fx-padding: 2 4 2 4;");
				mediaPlayer.pause();
			}
			//Otherwise, resume.
			else
			{
				playBtn.setStyle("-fx-graphic: url('file:resources/images/pausebtn.png'); -fx-padding: 2 4 2 4;");
				mediaPlayer.play();
			}
			this.paused = !paused;
		}
	}
	
	/**
	 * Handles the <i>Add</i> button click. Uses <i>FileChooser</i> to populate
	 * the playlist. Starts playback if <i>playing</i> flag is set to
	 * <i>false</i>.
	 */
	@FXML
	public void openRequestHandler()
	{
		FileChooser fileChooser = new FileChooser();
		List<File> files = fileChooser.showOpenMultipleDialog(main.getPrimaryStage());
		if(files != null)
		{
			for(File f : files)
			{
				mediaItem = new MediaItem(f.getAbsolutePath());
				mediaItem.setTitle(formatFileName(f.getName()));
				mediaItem.setURI(f.toURI());
				main.getPlayList().add(mediaItem);
				
				System.out.println("Added " + f.getName() + " to playlist");
			}
			if(!playing)
			{
				playAll();
				
				System.out.println("Playing all items in playlist starting with index #" + current);
			}
		}
	}
	
	/**
	 * Handles the <i>Back</i> button click by setting the appropriate
	 * <i>current</i> value.
	 */
	@FXML
	public void backRequestHandler()
	{
		mediaPlayer.stop();
		if(current == 0)
		{
			current = main.getPlayList().size();
		}
		current--;
		main.getCurrent().set(current);
	}
	
	/**
	 * Handles the <i>Next</i> button click by setting the appropriate
	 * <i>current</i> value.
	 */
	@FXML
	public void nextRequestHandler()
	{
		mediaPlayer.stop();
		current++;
		if(current == main.getPlayList().size())
		{
			current = 0;
		}
		main.getCurrent().set(current);
	}
	
	/**
	 * Handles the <i>Repeat</i> button click. Toggles the repeat flag.
	 */
	@FXML
	public void repeatRequestHandler()
	{
		if(mediaPlayer != null)
		{
			repeat = !repeat;
			if(repeat)
			{
				repeatBtn.setStyle("-fx-graphic: url('file:resources/images/repeatonebtn.png'); -fx-padding: 2 4 2 4;");
			}
			else
			{
				repeatBtn.setStyle("-fx-graphic: url('file:resources/images/repeatbtn.png'); -fx-padding: 2 4 2 4;");
			}
		}
	}
	
	/**
	 * Handles the <i>Fullscreen</i> button click. Each click reverses the
	 * current fullscreen status of the primary stage.
	 */
	@FXML
	public void fullScreenRequestHandler()
	{
		main.getPrimaryStage().setFullScreen(!main.getPrimaryStage().isFullScreen());
	}

	/**
	 * Handles the <i>Mute</i> button click. Each click reverses the current
	 * status of MediaPlayer's muteProperty.
	 */
	@FXML
	public void muteRequestHandler()
	{
		//If not initialized, nothing happens. Otherwise,
		if(mediaPlayer != null)
		{
			//If not currently muted, then mute.
			if(!muted)
			{
				mediaPlayer.muteProperty().set(!muted);
				muted = !muted;
				volBtn.setStyle("-fx-graphic: url('file:resources/images/mutebtn.png'); -fx-padding: 2 4 2 4;");
			}
			//Otherwise, demute.
			else
			{
				mediaPlayer.muteProperty().set(!muted);
				muted = !muted;
				//Set the appropriate volume button icon on return, based on current volume.
                if(volSlider.getValue() > 0.6)
                {
                	volBtn.setStyle("-fx-graphic: url('file:resources/images/volbtn.png'); -fx-padding: 2 4 2 4;");
                }
                else if(volSlider.getValue() > 0)
                {
                	volBtn.setStyle("-fx-graphic: url('file:resources/images/halfVolbtn.png'); -fx-padding: 2 4 2 4;");
                }
			}
		}
	}
	
	/**
	 * Handles the <i>Playlist</i> button click. Calls to Main to show playlist.
	 */
	@FXML
	public void playListRequestHandler()
	{
		main.showPlayListView();
	}
	
	/**
	 * Initializes the MediaPlayer and plays every track in the playlist one
	 * after the other, starting from a preset current track. Sets the
	 * <i>playing</i> flag to denote that the initial playback has commenced
	 * <p>
	 * The playing of the next item is achieved via a recursive call. .
	 * </p>
	 */
	private void playAll()
	{
		//Get the playlist from Main.
		List<MediaItem> playList = main.getPlayList();
		
		if (playList.size() != 0) 
		{
			this.current = main.getCurrent().get();
			this.playing = true;
			playBtn.setStyle("-fx-graphic: url('file:resources/images/pausebtn.png'); -fx-padding: 2 4 2 4;");
			media = new Media(playList.get(current).getURI().toString());
			mediaPlayer = new MediaPlayer(media);
			mediaPlayer.setVolume(volSlider.getValue());
			mediaView.setMediaPlayer(mediaPlayer);
			mediaView.setFitWidth(main.getPrimaryStage().getScene().getWidth());
			mediaPlayer.play();
			mediaPlayer.currentTimeProperty().addListener(progressChangedListener());
			
			mediaPlayer.setOnEndOfMedia(new Runnable() 
			{
				@Override
				public void run()
				{
					mediaPlayer.stop();
					if(!repeat)
					{
						current++;
					}
					main.getCurrent().set(current);
					if(current == playList.size())
					{
						current = 0;
					}
					playAll();
				}
			});
		}
	}
	
	//TODO: Move to a helper class
	public static String formatFileName(String s) {

	    String separator = System.getProperty("file.separator");
	    String filename;

	    // Remove the path upto the filename.
	    int lastSeparatorIndex = s.lastIndexOf(separator);
	    if (lastSeparatorIndex == -1) {
	        filename = s;
	    } else {
	        filename = s.substring(lastSeparatorIndex + 1);
	    }

	    // Remove the extension.
	    int extensionIndex = filename.lastIndexOf(".");
	    if (extensionIndex == -1)
	        return filename;

	    return filename.substring(0, extensionIndex);
	}
	
	/**
	 * Invoked by the main app to give it a reference back to itself. Adds
	 * listeners contingent on main being set.
	 * 
	 * @param main
	 *            an instance of the main application.
	 */
    public void setMain(Main main) 
    {
        this.main = main;
        
		//Calling a listener for scene size change
		this.main.getPrimaryStage().getScene().widthProperty().addListener(sceneSizeChangedListener());
		
        //Listens for changes in current from playlist requests. 
        //TODO: Move to MediaPlayerViewController
        this.main.getCurrent().addListener(currentChangedListener());
    }
    
	/**
	 * Listens to changes in Main's <i>current</i>. On change, stops playback of
	 * the currently playing item and initiates playback starting with the new
	 * index of <i>current</i>.
	 * 
	 * @return {@code ChangeListener<Number>}
	 */
	private ChangeListener<Number> currentChangedListener() 
	{
		return new ChangeListener<Number>() 
				{
					@Override
					public void changed(
							ObservableValue<? extends Number> observableValue,
							Number oldSceneWidth, Number newSceneWidth) 
					{
						mediaPlayer.stop();
						current = main.getCurrent().get();
						playAll();
					}
				};
	}
	
	/**
	 * Listens to changes in Scene size. On change, assigns new values to
	 * MediaView's FitWidth property, thus resizing the viewport.
	 * 
	 * @return {@code ChangeListener<Number>}
	 */
	private ChangeListener<Number> sceneSizeChangedListener() {
		return new ChangeListener<Number>() 
		{
			@Override
			public void changed(
					ObservableValue<? extends Number> observableValue,
					Number oldSceneWidth, Number newSceneWidth) 
			{
				if(mediaView != null)
				{
					mediaView.setFitWidth(newSceneWidth.doubleValue());
				}
			}
		};
	}

	/**
	 * Listens to changes in volume Slider position. On change, assigns new
	 * values to MediaPlayer's volume property. Cancels mute status and sets the
	 * mute flag to <i>false</i>.
	 * 
	 * @return {@code ChangeListener<Number>}
	 */
	private ChangeListener<Number> volumeSliderChangedListener() {
		return new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> obervable,
                Number oldValue, Number newValue) {
                    if(mediaPlayer != null)
                    {
                    	mediaPlayer.setMute(false);
                    	muted = false;
                    	mediaPlayer.setVolume(newValue.doubleValue());
                    }
                    if(newValue.doubleValue() > 0.6)
                    {
                    	volBtn.setStyle("-fx-graphic: url('file:resources/images/volbtn.png'); -fx-padding: 2 4 2 4;");
                    }
                    else if(newValue.doubleValue() == 0)
                    {
                    	volBtn.setStyle("-fx-graphic: url('file:resources/images/mutebtn.png'); -fx-padding: 2 4 2 4;");
                    }
                    else
                    {
                        volBtn.setStyle("-fx-graphic: url('file:resources/images/halfvolbtn.png'); -fx-padding: 2 4 2 4;");
                    }
            }
        };
	}

	/**
	 * Listens to changes in media playback progress. On change, sets the
	 * progress bar and the progress clock accordingly.
	 * 
	 * @return {@code ChangeListener<Duration>}
	 */
	private ChangeListener<Duration> progressChangedListener() 
	{
		ChangeListener<Duration> progressChangeListener = new ChangeListener<Duration>() 
		{
			@Override
			public void changed(
					ObservableValue<? extends Duration> observableValue,
					Duration oldValue, Duration newValue) 
			{
				progBar.setProgress(1.0
						* mediaPlayer.getCurrentTime().toMillis()
						/ mediaPlayer.getTotalDuration().toMillis());

				timeLabel.setText(DurationUtil.format((int)newValue.toSeconds()));
			}
		};
		return progressChangeListener;
	}
	
	/**
	 * Listens for left mouse button click or drag action on the progress bar. Reacts
	 * by updating the media position index.
	 * 
	 * @return {@code EventHandler<MouseEvent>}
	 */
	private EventHandler<MouseEvent> progBarEventListener() 
	{
		return new EventHandler<MouseEvent>()
		{
        	@Override
        	public void handle(MouseEvent event) 
        	{
        		if(mediaPlayer == null)
        		{
        			event.consume();
        		}
        		else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED
        				|| event.getEventType() == MouseEvent.MOUSE_CLICKED) 
        		{
        			mediaPlayer.seek(mediaPlayer.getTotalDuration().multiply(
        					event.getX() / progBar.getWidth()));
        			
        			//Console printout for easier testing.
					System.out.println("Setting media progress to "
							+ (int)(event.getX() / progBar.getWidth() * 100)
							+ " %");
        		}
        	}
        };
	}
}
