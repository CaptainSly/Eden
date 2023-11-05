package io.azraein.eden.logic;

import java.io.File;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.kordamp.ikonli.material2.Material2RoundAL;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class EdenFile extends Region {

	public enum EdenFileType {
		FOLDER, FILE
	}

	private File edenFile;
	private EdenFileType edenFileType;

	public EdenFile(File edenFile, EdenFileType edenFileType) {
		this.edenFile = edenFile;
		this.edenFileType = edenFileType;

		String fileName = edenFile.getName();
		String fileExtension = "";
		if (fileName.contains("."))
			fileExtension = fileName.substring(fileName.length() - 4, fileName.length());

		FontIcon fontIcon = null;
		switch (this.edenFileType) {
		case FILE:
			// TODO: Choose the icon based on the extension
			
			if (fileExtension.equals(".txt"))
				fontIcon = FontIcon.of(Material2RoundAL.FILE_COPY);
			else if (fileExtension.equals(".png") || fileExtension.equals(".jpg"))
				fontIcon = FontIcon.of(Material2OutlinedAL.IMAGE);
			else if (fileExtension.equals(".mp3"))
				fontIcon = FontIcon.of(Material2OutlinedAL.AUDIOTRACK);
			else if (fileExtension.equals(".mp4"))
				fontIcon = FontIcon.of(Material2OutlinedAL.FEATURED_VIDEO);
			else 
				fontIcon = FontIcon.of(Material2OutlinedAL.FILE_COPY);
			
			break;
		case FOLDER:
			fontIcon = FontIcon.of(Material2OutlinedAL.FOLDER);
			break;
		}
		fontIcon.setIconSize(32);
		fontIcon.setTextAlignment(TextAlignment.CENTER);

		if (fileName.length() > 10)
			fileName = fileName.substring(0, 10) + fileExtension;

		Label fileNameLbl = new Label(fileName);
		fileNameLbl.setAlignment(Pos.CENTER);

		VBox box = new VBox();
		box.setAlignment(Pos.CENTER);
		box.setPadding(new Insets(5));
		box.getChildren().addAll(fontIcon, fileNameLbl);

		getChildren().add(box);
	}

	public File getEdenFile() {
		return edenFile;
	}

	public EdenFileType getEdenFileType() {
		return edenFileType;
	}

}
