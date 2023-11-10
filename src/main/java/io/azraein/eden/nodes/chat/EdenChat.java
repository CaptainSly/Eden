package io.azraein.eden.nodes.chat;

import io.azraein.eden.EdenApp;
import io.azraein.eden.logic.EdenUser;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class EdenChat extends Region {

	// EdenChat Properties
	private final ObjectProperty<EdenUser> receivingEdenUser = new SimpleObjectProperty<>();

	// EdenChat Panes and Nodes
	private VBox edenChatRootContainerBox;
	private VBox edenChatMessagesBox;
	private HBox edenChatHeaderBox;
	private HBox edenChatFooterBox;
	private ScrollPane edenChatScrollContainer;

	// EdenChat Observable List
	private final ObservableList<EdenChatBubble> edenChatBubbles = FXCollections.observableArrayList();

	public EdenChat(EdenApp edenApp, EdenUser edenUser) {
		receivingEdenUser.set(edenUser);

		edenChatRootContainerBox = new VBox();
		edenChatMessagesBox = new VBox();
		edenChatHeaderBox = new HBox();
		edenChatFooterBox = new HBox();
		edenChatScrollContainer = new ScrollPane();

		// Setup Header
		edenChatHeaderBox.setAlignment(Pos.CENTER);
		edenChatHeaderBox.setSpacing(10);
		Label edenChatUserLbl = new Label(edenUser.getUserName());

		edenChatHeaderBox.getChildren().add(edenChatUserLbl);

		// Setup Content Pane
		edenChatScrollContainer.setContent(edenChatMessagesBox);
		edenChatScrollContainer.setFitToWidth(true);
		edenChatScrollContainer.setFitToHeight(true);

		edenChatBubbles.addListener(new ListChangeListener<EdenChatBubble>() {

			@Override
			public void onChanged(Change<? extends EdenChatBubble> c) {

				while (c.next()) {
					if (c.wasAdded()) {
						c.getAddedSubList().forEach(edenChatBubble -> {
							edenChatMessagesBox.getChildren().add(edenChatBubble);
						});
					}
				}

			}

		});

		// Setup Footer
		edenChatFooterBox.setAlignment(Pos.CENTER);
		edenChatFooterBox.setSpacing(5);

		TextField edenChatMessageArea = new TextField();
		edenChatMessageArea.setPromptText("Send Message...");

		Button edenChatSendBtn = new Button("Send");
		edenChatSendBtn.setOnAction(event -> {
			EdenChatBubble newChatBubble = new EdenChatBubble(edenChatMessageArea.getText(),
					edenApp.getEden().getCurrentLoggedInUser());

			addMessage(newChatBubble);
		});

		edenChatFooterBox.getChildren().addAll(edenChatMessageArea, edenChatSendBtn);

		edenChatRootContainerBox.getChildren().addAll(edenChatHeaderBox, new Separator(Orientation.HORIZONTAL),
				edenChatScrollContainer, new Separator(Orientation.HORIZONTAL), edenChatFooterBox);
		getChildren().add(edenChatRootContainerBox);
	}

	public void addMessage(EdenChatBubble edenChatBubble) {
		edenChatBubbles.add(edenChatBubble);
	}

	public ObjectProperty<EdenUser> receivingEdenUserProperty() {
		return receivingEdenUser;
	}

	public ObservableList<EdenChatBubble> getEdenChatBubbles() {
		return edenChatBubbles;
	}

}
