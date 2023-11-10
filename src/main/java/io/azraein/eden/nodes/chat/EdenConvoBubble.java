package io.azraein.eden.nodes.chat;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

import io.azraein.eden.logic.EdenUser;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class EdenConvoBubble extends Region {

	private Button edenConvoBubbleBtn;
	private Label edenConvoBubbleLbl;
	private VBox edenContainerBox;

	private final ObjectProperty<EdenUser> edenUserProperty = new SimpleObjectProperty<>();

	public EdenConvoBubble(EdenUser edenUser) {
		edenUserProperty.set(edenUser);
		edenConvoBubbleBtn = new Button("", FontIcon.of(Material2AL.ACCOUNT_CIRCLE, 32));

		edenConvoBubbleLbl = new Label(edenUser.getUserName());
		edenConvoBubbleLbl.setTextAlignment(TextAlignment.CENTER);

		edenContainerBox = new VBox();
		edenContainerBox.setAlignment(Pos.CENTER);
		edenContainerBox.setSpacing(15);
		edenContainerBox.setPadding(new Insets(5, 10, 5, 10));

		edenContainerBox.getChildren().addAll(edenConvoBubbleBtn, edenConvoBubbleLbl);
		getChildren().add(edenContainerBox);
	}
	
	public Button getConvoBubbleButton() {
		return edenConvoBubbleBtn;
	}

	public ObjectProperty<EdenUser> edenUserProperty() {
		return edenUserProperty;
	}

}
