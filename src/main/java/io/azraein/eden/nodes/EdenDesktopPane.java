package io.azraein.eden.nodes;

import java.io.File;

import org.kordamp.desktoppanefx.scene.layout.DesktopPane;
import org.kordamp.desktoppanefx.scene.layout.InternalWindow;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;

import io.azraein.eden.EdenApp;
import io.azraein.eden.logic.EdenFile;
import io.azraein.eden.logic.EdenFile.EdenFileType;
import io.azraein.eden.utils.EdenFileUtils;
import io.azraein.eden.utils.EdenUtils;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class EdenDesktopPane {

	public static int INTERNAL_COUNT = 0;

	private DesktopPane edenDesktopPane;

	private EdenApp edenApp;

	public EdenDesktopPane(EdenApp edenApp) {
		this.edenApp = edenApp;

		edenDesktopPane = new DesktopPane();
		edenDesktopPane.setPrefWidth(edenApp.getEdenAppStage().getWidth());
		edenDesktopPane.addInternalWindow(new EdenFileBrowserWindow(INTERNAL_COUNT++, edenApp));
	}

	public DesktopPane getEdenDesktopPane() {
		return edenDesktopPane;
	}

	public class EdenFileBrowserWindow extends InternalWindow {

		public EdenFileBrowserWindow(int count, EdenApp edenApp) {
			super("eden_fileBrowser_" + count, FontIcon.of(Material2AL.FOLDER_OPEN), "Eden FileBrowser",
					new BorderPane());
			BorderPane contentPane = (BorderPane) getContent();
			contentPane.setPrefWidth(640);
			contentPane.setPrefHeight(480);

			FlowPane directoryFlowPane = new FlowPane();
			directoryFlowPane.setHgap(20);
			directoryFlowPane.setVgap(20);

			ScrollPane directoryScrollPane = new ScrollPane(directoryFlowPane);
			directoryScrollPane.setPrefWidth(contentPane.getPrefWidth());
			directoryScrollPane.setPrefHeight(contentPane.getPrefHeight());

			HBox fileBrowserControlHBox = new HBox();
			fileBrowserControlHBox.setSpacing(20);

			VBox fileBrowserVBox = new VBox();
			fileBrowserVBox.setSpacing(20);
			fileBrowserVBox.setPadding(new Insets(5, 10, 5, 10));

			Button fbHomeScreenBtn = new Button("", FontIcon.of(Material2AL.HOME));
			fbHomeScreenBtn.setOnAction(event -> {
				directoryFlowPane.getChildren().clear();
				addEdenFilesToBrowser(directoryFlowPane,
						EdenFileUtils.getUserDirectoryFiles(edenApp.getEden().getCurrentLoggedInUser()));
			});

			fileBrowserControlHBox.getChildren().add(fbHomeScreenBtn);

			edenApp.getEden().getCurrentEdenUser().addListener((obs, oldValue, newValue) -> {

				if (newValue == null)
					directoryFlowPane.getChildren().clear();
				else {
					directoryFlowPane.getChildren().clear();
					addEdenFilesToBrowser(directoryFlowPane,
							EdenFileUtils.getUserDirectoryFiles(edenApp.getEden().getCurrentLoggedInUser()));
				}

			});

			directoryFlowPane.getChildren().addListener(new ListChangeListener<Node>() {

				@Override
				public void onChanged(Change<? extends Node> c) {

					while (c.next())
						if (c.wasAdded())
							for (Node node : c.getAddedSubList())
								if (node instanceof EdenFile) {

									EdenFile newEdenFile = (EdenFile) node;
									newEdenFile.setOnMouseClicked(event -> {
										switch (newEdenFile.getEdenFileType()) {
										case FILE:
											checkFileType(newEdenFile);
											break;
										case FOLDER:
											directoryFlowPane.getChildren().clear();
											break;
										}
									});
								}

				}

			});

			this.setOnCloseRequest(event -> {
				getDesktopPane().addInternalWindow(new EdenFileBrowserWindow(INTERNAL_COUNT++, edenApp));
			});

			this.setOnShown(event -> addEdenFilesToBrowser(directoryFlowPane,
					EdenFileUtils.getUserDirectoryFiles(edenApp.getEden().getCurrentLoggedInUser())));

			fileBrowserVBox.getChildren().addAll(fileBrowserControlHBox, directoryScrollPane);
			contentPane.setCenter(fileBrowserVBox);
		}

		private void addEdenFilesToBrowser(FlowPane dirFlowPane, File[] userFiles) {
			if (userFiles != null) {

				for (File file : userFiles) {
					EdenFile edenFile = null;
					if (file.isDirectory())
						edenFile = new EdenFile(file, EdenFileType.FOLDER);
					else if (file.isFile())
						edenFile = new EdenFile(file, EdenFileType.FILE);

					dirFlowPane.getChildren().add(edenFile);
				}
			} else
				return;
		}

		private void checkFileType(EdenFile edenFile) {
			DesktopPane parent = getDesktopPane();

			String fileName = edenFile.getEdenFile().getName();
			if (fileName.contains(".")) {
				// Chances are we have an extension at the end.
				int fileNameLength = fileName.length();
				String fileExtension = fileName.substring(fileNameLength - 4, fileNameLength);
				if (fileExtension.equals(".txt"))
					parent.addInternalWindow(new EdenTextEditorWindow(INTERNAL_COUNT++, edenFile));
				else if (fileExtension.equals(".mp3"))
					parent.addInternalWindow(new EdenAudioPlayerWindow(INTERNAL_COUNT++, edenFile));
				else if (fileExtension.equals(".mp4"))
					parent.addInternalWindow(new EdenVideoPlayerWindow(INTERNAL_COUNT++, edenFile));
				else if (fileExtension.equals(".jpg") || fileExtension.equals(".png"))
					parent.addInternalWindow(new EdenImageViewerWindow(INTERNAL_COUNT++, edenFile));
			} else
				parent.addInternalWindow(new EdenTextEditorWindow(INTERNAL_COUNT++, edenFile));

		}

	}

	public class EdenTextEditorWindow extends InternalWindow {

		public EdenTextEditorWindow(int count, EdenFile textFile) {
			super("eden_textEditor_" + count, FontIcon.of(Material2MZ.PAGES),
					"Eden TextEditor - " + textFile.getEdenFile().getName(), new BorderPane());
			BorderPane contentPane = (BorderPane) getContent();
			contentPane.setPrefWidth(640);
			contentPane.setPrefHeight(480);

			String text = EdenFileUtils.getFileAsString(textFile.getEdenFile());

			TextArea textArea = new TextArea(text);
			textArea.setFont(new Font(14));

			this.setOnCloseRequest(e -> EdenFileUtils.saveTextFile(textArea.getText(), textFile.getEdenFile()));

			contentPane.setCenter(textArea);
		}

	}

	public class EdenImageViewerWindow extends InternalWindow {

		public EdenImageViewerWindow(int count, EdenFile image) {
			super("eden_imageViewer_" + count, FontIcon.of(Material2AL.IMAGE),
					"Eden ImageViewer - " + image.getEdenFile().getName(), new BorderPane());
			BorderPane contentArea = (BorderPane) getContent();
			contentArea.setPrefWidth(640);
			contentArea.setPrefHeight(480);

			ImageView imageViewer = new ImageView(new Image(image.getEdenFile().toURI().toString()));

			contentArea.setCenter(imageViewer);
		}

	}

	public class EdenAudioPlayerWindow extends InternalWindow {

		private Slider mediaTimeSlider;
		private Label mediaTimeLabel;
		private BorderPane contentPane;
		private MediaPlayer mediaPlayer;

		private Duration duration;

		public EdenAudioPlayerWindow(int count, EdenFile media) {
			super("eden_audioplayer_" + count, FontIcon.of(Material2MZ.PERSONAL_VIDEO),
					"Eden AudioPlayer - " + media.getEdenFile().getName(), new BorderPane());
			contentPane = (BorderPane) getContent();

			// Get the Media
			Media videoMedia = new Media(media.getEdenFile().toURI().toString());
			mediaPlayer = new MediaPlayer(videoMedia);

			mediaPlayer.setAutoPlay(true);

			// Create the Media HBox and Control GridPane
			HBox mediaHBox = new HBox();
			mediaHBox.setAlignment(Pos.CENTER);
			mediaHBox.setPadding(new Insets(10));
			mediaHBox.setSpacing(25);

			GridPane mediaControlGrid = new GridPane();
			mediaControlGrid.setHgap(5);
			mediaControlGrid.setVgap(5);

			// Controls for Media Player
			mediaTimeSlider = new Slider();
			Slider mediaVolumeSlider = new Slider(0, 1, 0.1);
			mediaTimeLabel = new Label("Time: ");
			Label mediaVolumeLabel = new Label("Volume: ");
			Button mediaPlayBtn = new Button("", FontIcon.of(Material2MZ.PLAY_CIRCLE_OUTLINE, 32));
			Button mediaPauseBtn = new Button("", FontIcon.of(Material2MZ.PAUSE_CIRCLE_OUTLINE, 32));

			mediaPlayBtn.setOnAction(event -> {
				mediaPlayer.play();
			});

			mediaPauseBtn.setOnAction(event -> {
				mediaPlayer.pause();
			});

			mediaPlayer.volumeProperty().bind(mediaVolumeSlider.valueProperty());
			mediaVolumeSlider.setValue(50);

			mediaPlayer.setOnReady(() -> {
				duration = mediaPlayer.getMedia().getDuration();
				updateValues();
			});

			mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() {

				@Override
				public void invalidated(Observable observable) {
					updateValues();
				}

			});

			mediaTimeSlider.valueProperty().addListener(new InvalidationListener() {

				@Override
				public void invalidated(Observable observable) {
					if (mediaTimeSlider.isPressed())
						mediaPlayer
								.seek(mediaPlayer.getMedia().getDuration().multiply(mediaTimeSlider.getValue() / 100));
				}

			});

			edenApp.getEden().getCurrentEdenUser().addListener((obs, oldValue, newValue) -> {

				if (newValue == null) {
					// Logout
					mediaPlayer.stop();
				}

			});

			// Add Media Controls to Grid
			mediaControlGrid.add(mediaPlayBtn, 0, 0);
			mediaControlGrid.add(mediaPauseBtn, 1, 0);

			mediaControlGrid.add(mediaVolumeLabel, 0, 1);
			mediaControlGrid.add(mediaVolumeSlider, 1, 1);

			mediaControlGrid.add(mediaTimeLabel, 0, 2);
			mediaControlGrid.add(mediaTimeSlider, 1, 2);

			this.setOnCloseRequest(event -> mediaPlayer.stop());

			// Add the Main Panes to the Content Pane
			mediaHBox.getChildren().addAll(
					new Label(media.getEdenFile().getName(), FontIcon.of(Material2AL.AUDIOTRACK, 32)),
					mediaControlGrid);
			contentPane.setCenter(mediaHBox);
		}

		protected void updateValues() {
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					Duration currentTime = mediaPlayer.getCurrentTime();
					mediaTimeLabel.setText("Time: " + EdenUtils.formatTime(currentTime, duration));
					mediaTimeSlider.setDisable(duration.isUnknown());

					if (!mediaTimeSlider.isDisabled() && duration.greaterThan(Duration.ZERO)
							&& !(mediaTimeSlider.isValueChanging())) {
						mediaTimeSlider.setValue(currentTime.divide(duration.toMillis()).toMillis() * 100.0);
					}

				}

			});
		}
	}

	public class EdenVideoPlayerWindow extends InternalWindow {

		public enum VIDEO_STATE {
			PLAY, PAUSE, STOP
		}

		private Slider mediaTimeSlider;
		private Label mediaTimeLabel;
		private BorderPane contentPane;
		private MediaPlayer mediaPlayer;

		private Duration duration;

		private final ObjectProperty<VIDEO_STATE> mediaVideoStateProperty = new SimpleObjectProperty<>(
				VIDEO_STATE.STOP);

		public EdenVideoPlayerWindow(int count, EdenFile media) {
			super("eden_videoplayer_" + count, FontIcon.of(Material2MZ.PERSONAL_VIDEO),
					"Eden VideoPlayer - " + media.getEdenFile().getName(), new BorderPane());
			contentPane = (BorderPane) getContent();

			// Get the Media
			Media videoMedia = new Media(media.getEdenFile().toURI().toString());
			mediaPlayer = new MediaPlayer(videoMedia);
			MediaView mediaViewer = new MediaView(mediaPlayer);

			mediaPlayer.setAutoPlay(true);
			mediaViewer.setFitWidth(640);
			mediaViewer.setFitHeight(480);

			// Create the Media HBox and Control GridPane
			VBox mediaVBox = new VBox();
			mediaVBox.setAlignment(Pos.CENTER);

			GridPane mediaControlGrid = new GridPane();
			mediaControlGrid.setHgap(5);
			mediaControlGrid.setVgap(5);

			// Controls for Media Player
			mediaTimeSlider = new Slider();
			Slider mediaVolumeSlider = new Slider(0, 1, 0.1);
			mediaTimeLabel = new Label("Time: ");
			Label mediaVolumeLabel = new Label("Volume: ");
			Button mediaPlayBtn = new Button("", FontIcon.of(Material2MZ.PLAY_CIRCLE_OUTLINE, 32));
			Button mediaPauseBtn = new Button("", FontIcon.of(Material2MZ.PAUSE_CIRCLE_OUTLINE, 32));
			Button mediaStopBtn = new Button("", FontIcon.of(Material2MZ.STOP_CIRCLE, 32));

			mediaPlayBtn.setOnAction(event -> {
				mediaVideoStateProperty.set(VIDEO_STATE.PLAY);
				mediaPlayer.play();
			});

			mediaPauseBtn.setOnAction(event -> {
				mediaVideoStateProperty.set(VIDEO_STATE.PAUSE);
				mediaPlayer.pause();
			});

			mediaStopBtn.setOnAction(event -> {
				mediaVideoStateProperty.set(VIDEO_STATE.STOP);
				mediaPlayer.stop();
			});

			mediaVideoStateProperty.addListener((obs, oldValue, newValue) -> {

				switch (newValue) {
				case PAUSE:
					mediaPlayBtn.setDisable(false);
					mediaPauseBtn.setDisable(true);
					break;
				case PLAY:
					mediaPlayBtn.setDisable(true);
					mediaPauseBtn.setDisable(false);
					break;
				case STOP:
					mediaPlayBtn.setDisable(false);
					mediaPauseBtn.setDisable(false);
					break;
				}

			});

			mediaPlayer.volumeProperty().bind(mediaVolumeSlider.valueProperty());
			mediaVolumeSlider.setValue(50);

			mediaPlayer.setOnReady(() -> {
				duration = mediaPlayer.getMedia().getDuration();
				updateValues();
			});

			mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() {

				@Override
				public void invalidated(Observable observable) {
					updateValues();
				}

			});

			mediaTimeSlider.valueProperty().addListener(new InvalidationListener() {

				@Override
				public void invalidated(Observable observable) {
					if (mediaTimeSlider.isPressed())
						mediaPlayer
								.seek(mediaPlayer.getMedia().getDuration().multiply(mediaTimeSlider.getValue() / 100));
				}

			});

			edenApp.getEden().getCurrentEdenUser().addListener((obs, oldValue, newValue) -> {

				if (newValue == null) {
					// Logout
					mediaPlayer.stop();
				}

			});

			// Add Media Controls to Grid
			mediaControlGrid.add(mediaPlayBtn, 0, 0);
			mediaControlGrid.add(mediaStopBtn, 1, 0);
			mediaControlGrid.add(mediaPauseBtn, 2, 0);

			mediaControlGrid.add(mediaVolumeLabel, 0, 1);
			mediaControlGrid.add(mediaVolumeSlider, 1, 1);

			mediaControlGrid.add(mediaTimeLabel, 0, 2);
			mediaControlGrid.add(mediaTimeSlider, 1, 2);

			this.setOnCloseRequest(event -> mediaPlayer.stop());

			// Add the Main Panes to the Content Pane
			mediaVBox.getChildren().addAll(mediaViewer, mediaControlGrid);
			contentPane.setCenter(mediaVBox);
		}

		protected void updateValues() {
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					Duration currentTime = mediaPlayer.getCurrentTime();
					mediaTimeLabel.setText("Time: " + EdenUtils.formatTime(currentTime, duration));
					mediaTimeSlider.setDisable(duration.isUnknown());

					if (!mediaTimeSlider.isDisabled() && duration.greaterThan(Duration.ZERO)
							&& !(mediaTimeSlider.isValueChanging())) {
						mediaTimeSlider.setValue(currentTime.divide(duration.toMillis()).toMillis() * 100.0);
					}

				}

			});
		}

	}

}
