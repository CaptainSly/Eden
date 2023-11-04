package io.azraein.eden.logic;

import java.io.File;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;

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

		FontIcon fontIcon = null;
		switch (this.edenFileType) {
		case FILE:
			fontIcon = FontIcon.of(Material2OutlinedAL.FILE_COPY);
			break;
		case FOLDER:
			fontIcon = FontIcon.of(Material2OutlinedAL.FOLDER);
			break;
		}
		fontIcon.setIconSize(32);
		fontIcon.setTextAlignment(TextAlignment.CENTER);

		Label fileName = new Label(edenFile.getName());
		fileName.setAlignment(Pos.CENTER);

		VBox box = new VBox();
		box.setAlignment(Pos.CENTER);
		box.setPadding(new Insets(5));
		box.getChildren().addAll(fontIcon, fileName);

		getChildren().add(box);
	}

	public File getEdenFile() {
		return edenFile;
	}

	public EdenFileType getEdenFileType() {
		return edenFileType;
	}

}
