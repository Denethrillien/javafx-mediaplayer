package mediaplayer.view;

import java.io.File;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import mediaplayer.Main;
import mediaplayer.model.MediaItem;
import mediaplayer.util.ConversionUtils;

/**
 * The controller for the PlayListView. Contains playlist item selection and
 * media item information logic.
 * 
 * @author Alex Hage
 *
 */
public class PlayListViewController 
{
	@FXML
	private TableView<MediaItem> playListTable;
	
	@FXML
	private TableColumn<MediaItem, String> titleColumn;
	
	@FXML
	private Label pathLabel;
	
	@FXML
	private Label titleLabel;
	
	@FXML
	private Label durationLabel;
	
	/**
	 * The Stage of the playlist View.
	 */
	private Stage playListStage;
	
	/**
	 * The reference to the main class.
	 */
	private Main main;
	
    /**
     * The default constructor.
     * Called before the <i>initialize()</i> method.
     */
    public PlayListViewController() 
    {
    	
    } //end ctor
    
	/**
	 * Initializes the controller class. Is called automatically after the fxml
	 * file has been loaded.
	 */
    @FXML
    private void initialize() 
    {
        // Initialize the playlist table.
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().getTitleProperty());
        
        // Clear the media item details.
        showMediaInfo(null);

		// Listen for selection changes and show the appropriate media info when
		// changed.
        playListTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showMediaInfo(newValue));
        
        playListTable.setOnMouseClicked(playListTableDoubleClickListener());
        
        playListTable.setOnDragOver(playListFileDropListener());
        playListTable.setOnDragDropped(playListFileDropListener());
    }
    
    /**
     * Sets the stage of this View.
     * 
     * @param playListStage
     */
    public void setDialogStage(Stage playListStage) 
    {
        this.playListStage = playListStage;
        this.playListStage.getScene().setOnKeyReleased(keyReleasedListener());
    }
    
	/**
	 * Sets the text attribute of the labels to show media item information.
	 * Clears labels if null.
	 * 
	 * @param Track
	 *            the media item or <i>null</i>
	 */
    private void showMediaInfo(MediaItem track) 
    {
        if (track != null) 
        {
            // Fill the labels with info from the Track object.
        	pathLabel.setText(ConversionUtils.convertToFileName(track.getURI()));
        	titleLabel.setText(track.getTitle());
        } 
        else 
        {
        	pathLabel.setText("");
        	titleLabel.setText("");
        }
    }
    
	/**
	 * Called by the main application to give it a reference back to itself.
	 * 
	 * @param main the Main to set.
	 */
    public void setMain(Main main) 
    {
        this.main = main;

        // Add observable list data to the table
        playListTable.setItems(main.getPlayList());
        playListTable.getSelectionModel().select(main.getCurrent().get());
    }
	
	/**
	 * Listens for double left mouse button click in the playlist window. Reacts
	 * by updating the <i>current</i> index in the Main class.
	 * 
	 * @return {@code EventHandler<MouseEvent>}
	 */
	private EventHandler<MouseEvent> playListTableDoubleClickListener() 
	{
		return new EventHandler<MouseEvent>()
		{
        	@Override
        	public void handle(MouseEvent event) 
        	{
        	    if (event.getClickCount()>1) 
        	    {
					main.getCurrent().set(
							playListTable.getSelectionModel()
									.selectedIndexProperty().get());
        	    }
        	}
        };
	}
	
	/**
	 * Listens for and reacts to {@link KeyEvent}s. keyReleased is used due to
	 * triggering once per click.
	 * 
	 * @return {@code EventHandler<KeyEvent>}
	 */
	private EventHandler<KeyEvent> keyReleasedListener() 
	{
		return new EventHandler<KeyEvent>() 
		{
            public void handle(KeyEvent e) 
            {
            	if(e.getCode() == KeyCode.ESCAPE)
            	{
            		playListStage.close();
            	}
            	if(e.getCode() == KeyCode.ENTER)
            	{
            		main.getCurrent().set(
							playListTable.getSelectionModel()
									.selectedIndexProperty().get());
            	}
            }
        };
	}
	
	/**
	 * Listens for and reacts to {@link DragEvent}s. 
	 * 
	 * @return {@code EventHandler<DragEvent>}
	 */
	private EventHandler<DragEvent> playListFileDropListener() 
	{
		return new EventHandler<DragEvent>() 
        {
            @Override
            public void handle(DragEvent event) 
            {
                Dragboard db = event.getDragboard();
                if(event.getEventType() == DragEvent.DRAG_OVER){
                	if (db.hasFiles()) 
                    {
                        event.acceptTransferModes(TransferMode.COPY);
                    } 
                }
                else if(event.getEventType() == DragEvent.DRAG_DROPPED)
                {
                	if (db.hasFiles()) 
                    {
                        for (File file : db.getFiles()) 
                        {
                            MediaItem track = new MediaItem(file.toURI());
                            track.setURI(file.toURI());
                            track.setTitle(file.getName());
                            main.getPlayList().add(track);
                        }
                    }
                }
                else 
                {
                    event.consume();
                }
            }
        };
	}
}
