package io.azraein.eden.nodes.chat;

import io.azraein.eden.logic.EdenUser;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

public class EdenChatBubble extends Region {

	private final ObjectProperty<EdenUser> sendingEdenUserProperty = new SimpleObjectProperty<>();

	private HBox edenChatBubbleContainer;

	public EdenChatBubble(String message, EdenUser sendingEdenUser) {
		edenChatBubbleContainer = new HBox();
		sendingEdenUserProperty.set(sendingEdenUser);

		Label edenChatBubbleText = new Label(message);
		edenChatBubbleText.setWrapText(true);
		edenChatBubbleText.setPrefSize(125, 125);

		Label edenChatBubbleSenderLbl = new Label(sendingEdenUser.getUserName());

		edenChatBubbleContainer.getChildren().addAll(edenChatBubbleSenderLbl, edenChatBubbleText);
		getChildren().add(edenChatBubbleContainer);
	}

	public ObjectProperty<EdenUser> getSendingEdenUserProperty() {
		return sendingEdenUserProperty;
	}

}
