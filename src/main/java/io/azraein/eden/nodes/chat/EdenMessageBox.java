package io.azraein.eden.nodes.chat;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.tinylog.Logger;

import io.azraein.eden.EdenApp;
import io.azraein.eden.logic.EdenUser;
import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class EdenMessageBox extends Region {

	// EdenMessageBox Properties
	private final ListProperty<EdenUser> lastConvoProperty = new SimpleListProperty<>();
	private final MapProperty<EdenUser, EdenChat> edenChatsProperty = new SimpleMapProperty<>();

	private VBox edenMessageBoxContainer;
	private HBox edenMessageBoxLastConvos;
	private BorderPane edenMessageBoxRootContainer;
	private ListView<EdenUser> edenUserListView;

	public EdenMessageBox(EdenApp edenApp) {
		edenMessageBoxContainer = new VBox();
		edenMessageBoxLastConvos = new HBox();
		edenMessageBoxRootContainer = new BorderPane();

		lastConvoProperty.set(FXCollections.observableArrayList());
		edenChatsProperty.set(FXCollections.observableHashMap());

		edenUserListView = new ListView<>();
		edenUserListView.setCellFactory(new Callback<ListView<EdenUser>, ListCell<EdenUser>>() {

			@Override
			public ListCell<EdenUser> call(ListView<EdenUser> param) {
				return new ListCell<EdenUser>() {

					private HBox employeeListItemContainer;
					private VBox employeeLabelVbox;
					private Label employeeImageLbl;
					private Label employeeNameLbl;
					private Label lastActiveLbl;

					@Override
					protected void updateItem(EdenUser item, boolean empty) {
						super.updateItem(item, empty);

						if (item != null) {
							employeeListItemContainer = new HBox();
							employeeLabelVbox = new VBox();

							employeeListItemContainer.setPadding(new Insets(5, 10, 5, 10));
							employeeListItemContainer.setSpacing(25);
							employeeLabelVbox.setPadding(new Insets(5, 10, 5, 10));
							employeeLabelVbox.setSpacing(25);

							// Setup Employee Cell
							employeeImageLbl = new Label("", FontIcon.of(Material2AL.ACCOUNT_CIRCLE, 32));
							employeeNameLbl = new Label(item.getUserName());
							lastActiveLbl = new Label("Last Active: 10 minutes ago");

							employeeLabelVbox.getChildren().addAll(employeeNameLbl, lastActiveLbl);
							employeeListItemContainer.getChildren().addAll(employeeImageLbl, employeeLabelVbox);

							setText(null);
							setGraphic(employeeListItemContainer);
						} else {
							setText(null);
							setGraphic(null);
						}
					}

				};
			}

		});

		edenUserListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {

			if (newValue != null) {
				// First check to see if the user is already added to a convobubble
				for (Node node : edenMessageBoxLastConvos.getChildren()) {
					if (node instanceof EdenConvoBubble) {
						// We want only convoBubbles
						EdenConvoBubble convoBubble = (EdenConvoBubble) node;
						if (newValue.equals(convoBubble.edenUserProperty().get()))
							return;

						Logger.debug("Didn't add duplicate");
					}
				}

				if (lastConvoProperty.getSize() > 3) {
					lastConvoProperty.removeFirst();
				}

				lastConvoProperty.add(newValue);

				EdenChat edenChat = null;

				if (edenChatsProperty.get().containsKey(newValue)) {
					edenChat = edenChatsProperty.get(newValue);
				}

				if (edenChat == null)
					edenChat = new EdenChat(edenApp, newValue);

				edenMessageBoxRootContainer.setCenter(edenChat);

			}

		});

		lastConvoProperty.addListener(new ListChangeListener<EdenUser>() {

			@Override
			public void onChanged(Change<? extends EdenUser> c) {

				while (c.next()) {
					if (c.wasAdded()) {

						c.getAddedSubList().forEach(edenUser -> {
							// Loop through every user that's been added. We already pruned duplicates

							EdenConvoBubble convoBubble = new EdenConvoBubble(edenUser);
							convoBubble.getConvoBubbleButton().setOnAction(event -> {
								// Check to see if the there is a EdenChat associated to edenUser, if so bring
								// it front and center, else create one add it and bring it front and center
								EdenChat edenChat = null;

								if (edenChatsProperty.get().containsKey(edenUser)) {
									edenChat = edenChatsProperty.get(edenUser);
								}

								if (edenChat == null) {
									edenChat = new EdenChat(edenApp, edenUser);
									edenChatsProperty.put(edenUser, edenChat);
								}

								edenMessageBoxRootContainer.setCenter(edenChat);
							});

							edenMessageBoxLastConvos.getChildren().add(convoBubble);
						});

					} else if (c.wasRemoved()) {
						c.getRemoved().forEach(edenUser -> {
							// Loop through the nodes of the lastConvoBox

							var lastConvosChildren = edenMessageBoxLastConvos.getChildren();
							for (Node node : lastConvosChildren) {
								if (node instanceof EdenConvoBubble) {
									EdenConvoBubble convoBubble = (EdenConvoBubble) node;
									lastConvosChildren.remove(convoBubble);
								}
							}

							edenMessageBoxLastConvos.getChildren().clear();
							edenMessageBoxLastConvos.getChildren().addAll(lastConvosChildren);
						});
					}
				}

			}

		});

		edenApp.getEden().getCurrentEdenUser().addListener((obs, oldValue, newValue) -> {

			if (newValue != null) {
				edenUserListView.getItems().clear();

				for (EdenUser user : edenApp.getEden().getEdenUsers().values()) {
					if (!user.getUserName().equals(newValue.getUserName())) {
						Logger.debug("Adding user: " + user.getUserName());
						edenUserListView.getItems().add(user);
					}
				}

			}

		});

		edenMessageBoxContainer.getChildren().addAll(edenMessageBoxLastConvos, edenUserListView);
		edenMessageBoxRootContainer.setLeft(edenMessageBoxContainer);
		getChildren().add(edenMessageBoxRootContainer);
	}

}
