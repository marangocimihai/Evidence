package view;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.controlsfx.control.Rating;

import controller.Constants;
import controller.Controller;
import controller.Entity;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Class that implements the entire GUI.
 * 
 * @author marangocimihai
 * 
 */
public class View extends Application {
	private Scene scene;
	private BorderPane root;
	private static BorderPane pane5;
	private ComboBox<String> category, status;
	private Label ratingLabelEast, endDateLabelEast;
	private Rating rating;
	private TextField ratingTextField, currentSeasonTextField, currentEpisodeTextField, nameTextFieldEast;
	private Button previousSeason, nextSeason, previousEpisode, nextEpisode;
	private static ListView<String> entities;

	/**
	 * Starts the GUI.
	 * 
	 * @param primaryStage
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			entities = new ListView<String>();
			primaryStage.setTitle("Evidence");
			root = new BorderPane();
			scene = new Scene(root, 750, 500);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			BorderPane pane = new BorderPane();
			pane.setId("pane");

			StackPane  pane2 = new StackPane();
			GridPane   pane3 = new GridPane();
			BorderPane pane4 = new BorderPane();
			
			pane5 = new BorderPane();
			pane5.setId("pane5");

			category = new ComboBox<String>();

			category.getItems().addAll(Controller.getCategories());

			category.valueProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> ov, String oldValue, String newValue) {
					if (entities.getItems().size() == 0) {
						pane.getChildren().clear();
					}

					pane4.setLeft(upperMenu());

					List<String> categoryContent = new ArrayList<String>();
					categoryContent = Controller.getCategoryContent(newValue);
					Collections.sort(categoryContent, new Comparator<String>() {
						@Override
						public int compare(String s1, String s2) {
							return s1.compareToIgnoreCase(s2);
						}
					});
					ObservableList<String> items = FXCollections.observableArrayList(categoryContent);
					entities.setItems(items);
					entities.setId("entities-of-entities");

					pane2.getChildren().remove(entities);
					pane2.getChildren().add(entities); 

					entities.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
						@Override
						public void changed(ObservableValue<? extends String> observable, String oldValue,
								String newValue) {
							if (newValue != null) // have to find a workaround asap
							{
								Entity entity = Controller.getDetails(newValue);
								addToCenterPane(entity, pane);
							}
						}
					});

					if (entities.getItems().size() >= 0) {
						entities.getSelectionModel().selectFirst();
					}

				}
			});

			// always autoselect first the first category and the first item in
			// it
			if (category.getItems().size() > 0 && entities.getItems().size() >= 0) {
				category.getSelectionModel().selectFirst();
				entities.getSelectionModel().selectFirst();
			}

			BorderPane top = new BorderPane();
			top.setRight(category);

			HBox addRemoveCategoryPanel = new HBox();
			addRemoveCategoryPanel.setId("add-remove-category-panel");
			addRemoveCategoryPanel.getChildren().addAll(addCategoryButton(), deleteCategoryButton());
			top.setLeft(addRemoveCategoryPanel);

			HBox topHbox = new HBox();
			topHbox.getChildren().add(top);
			topHbox.setId("top-hbox");

			pane4.setRight(topHbox);
			pane2.setId("vbox1");
			pane4.setId("vbox");
			pane5.setLeft(search());
			
			root.setCenter(pane);
			root.setLeft(pane2);
			root.setRight(pane3);
			root.setTop(pane4);

			root.setBottom(pane5); //asta e

			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates the component for adding and removing entities by buttons.
	 * 
	 * @return - addRemoveHBox
	 */
	public HBox upperMenu() {
		FlowPane addRemoveButtons = new FlowPane();
		Button add = new Button("Add");
		Button remove = new Button("Remove");

		add.setId("add");
		remove.setId("remove");
		addRemoveButtons.getChildren().addAll(add, remove);
		addRemoveButtons.setId("add-remove-buttons-pane");
		addRemoveButtons.setHgap(150);

		add.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				addEntity();
			}
		});

		remove.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Confirmation Dialog");
				alert.setContentText("Are you sure ?");
				
				if (alert.showAndWait().get() == ButtonType.OK){	
					Controller.removeEntity(entities.getSelectionModel().getSelectedItem(), category.getValue());
					entities.getItems().remove(entities.getSelectionModel().getSelectedItem());
				}
			}
		});

		BorderPane addRemoveButtonsPane = new BorderPane();
		addRemoveButtonsPane.setLeft(addRemoveButtons);

		HBox addRemoveHBox = new HBox();
		addRemoveHBox.getChildren().add(addRemoveButtons);
		addRemoveHBox.setId("vbox");

		return addRemoveHBox;
	}

	/**
	 * Adds new components to the center pane of the application. This pane
	 * contains information about the current entity.
	 * 
	 * @param entity
	 *            the entity alongside its details
	 * @param pane
	 *            the center pane of the app
	 * @return pane
	 */
	public BorderPane addToCenterPane(Entity entity, BorderPane pane) {
		VBox vbox = new VBox();
		vbox.setId("vbox2");

		BorderPane paneTable = new BorderPane();
		paneTable.getChildren().removeAll();
		VBox centerHboxWest = new VBox();
		VBox centerHboxEast = new VBox();

		BorderPane paneTableCenter = new BorderPane();
		paneTableCenter.setId("pane-table-center");

		nameTextFieldEast = new TextField(entity.name);
		nameTextFieldEast.setTooltip(new Tooltip(Constants.PRESS_ENTER_TO_SAVE));
		nameTextFieldEast.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent keyEvent) {
				if (keyEvent.getCode().equals(KeyCode.ENTER)) {
					if (Controller.changeName(entity, nameTextFieldEast.getText())) {
						entities.getItems().set(entities.getSelectionModel().getSelectedIndex(), nameTextFieldEast.getText());
						entities.getSelectionModel().select(nameTextFieldEast.getText());
						nameTextFieldEast.requestFocus();
						nameTextFieldEast.positionCaret(nameTextFieldEast.getText().length());
						nameTextFieldEast.setId("green-background");
					} else {
						nameTextFieldEast.setId("red-background");
					}
				}	
			}
		});
		ratingLabelEast = new Label(String.valueOf(entity.rating) + " / 10"); 
		Label name = new Label("Name");
		name.setId("name");
		centerHboxWest.getChildren().add(name);
		centerHboxWest.getChildren().add(new Separator());
		centerHboxEast.getChildren().add(nameTextFieldEast);
		centerHboxEast.getChildren().add(new Separator());
		
		Label statusLabelEast = new Label("Status");
		statusLabelEast.setId("status-label");
		status = new ComboBox<String>();
		status.getItems().addAll(Constants.ON_AIR, Constants.HIATUS, Constants.ENDED);
		status.getSelectionModel().select(entity.status);
		centerHboxWest.getChildren().add(statusLabelEast);
		centerHboxWest.getChildren().add(new Separator());
		centerHboxEast.getChildren().add(status);
		centerHboxEast.getChildren().add(new Separator());
		
		status.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> ov, String oldStatus, String newStatus) {
				Controller.changeStatus(entity, newStatus);
			}
		});
		
		centerHboxWest.getChildren().add(new Label("Rating"));
		centerHboxWest.getChildren().add(new Separator());
		centerHboxEast.getChildren().add(ratingLabelEast);
		centerHboxEast.getChildren().add(new Separator());

		paneTableCenter.setLeft(centerHboxWest);
		paneTableCenter.setRight(centerHboxEast);

		// start date
		long miliseconds = Long.parseLong(entity.startDate);
		Calendar calendar = Calendar.getInstance();
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy (HH:mm)");
		calendar.setTimeInMillis(miliseconds);

		Label startDateLabelEast = new Label(formatter.format(calendar.getTime()));
		centerHboxWest.getChildren().add(new Label("Start date"));
		centerHboxWest.getChildren().add(new Separator());
		centerHboxEast.getChildren().add(startDateLabelEast);
		centerHboxEast.getChildren().add(new Separator());

		if (entity.endDate.equals(Constants.CURRENTLY_WATCHING)) {
			centerHboxWest.getChildren().add(new Label("End date"));
			centerHboxEast.getChildren().add(new Label(Constants.CURRENTLY_WATCHING));
		} else {
			endDateLabelEast = new Label(Controller.getCurrentDate(entity.endDate));
			centerHboxWest.getChildren().add(new Label("End date"));
			centerHboxEast.getChildren().add(endDateLabelEast);
		}
		
		centerHboxWest.getChildren().add(new Separator());
		centerHboxEast.getChildren().add(new Separator());

		String currentCategory = category.getValue();
		Label seasonLabel = new Label(Constants.SEASON); 
		seasonLabel.setId("season-and-episode-label");
		centerHboxWest.getChildren().add(seasonLabel);
		centerHboxWest.getChildren().add(new Separator());
		centerHboxEast.getChildren().add(currentSeasonLabel(entity, currentCategory));
		centerHboxEast.getChildren().add(new Separator());
		
		Label currentEpisodeLabel = new Label(Constants.CURRENT_EPISODE);
		currentEpisodeLabel.setId("season-and-episode-label");
		centerHboxWest.getChildren().add(currentEpisodeLabel);
		centerHboxWest.getChildren().add(new Separator());
		centerHboxEast.getChildren().add(currentEpisodeLabel(entity, currentCategory));
		centerHboxEast.getChildren().add(new Separator());

		paneTable.setBottom(ratingAndMarkAsFinished(entity));
		paneTable.setCenter(paneTableCenter);
		paneTable.setId("panetable");
		pane.setTop(vbox);
		pane.setCenter(paneTable);
		
		if (!entity.endDate.equals(Constants.CURRENTLY_WATCHING)) {
			disable(nameTextFieldEast, status, previousSeason, nextSeason, previousEpisode, nextEpisode, rating, ratingTextField, currentSeasonTextField, currentEpisodeTextField);
		}
		
		return pane;
	}

	/**
	 * Creates and combines the rating and marking an entity as finished into
	 * one single component.
	 * 
	 * @param entity
	 *            the entity alongside its details
	 * @return the components which includes the rating and markAsFinished
	 *         button
	 */
	public VBox ratingAndMarkAsFinished(Entity entity) {
		Button finished = new Button("Mark as finished");

		finished.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (finished.getText().equals("Mark as finished")) {
					if (Controller.markAsFinished(entity, category.getValue())) {
						long miliseconds1 = Long.parseLong(Controller.getCurrentDate());
						Calendar calendar = Calendar.getInstance();
						DateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy (HH:mm)");
						calendar.setTimeInMillis(miliseconds1);
						endDateLabelEast = new Label();
						endDateLabelEast.setText(formatter1.format(calendar.getTime()));
						finished.setText("Mark as unfinished");
						disable(nameTextFieldEast, status, previousSeason, nextSeason, previousEpisode, nextEpisode, rating, ratingTextField, currentSeasonTextField, currentEpisodeTextField);
					}
				} else {
					Controller.changeEndDate(entity);
					finished.setText("Mark as finished");
					enable(nameTextFieldEast, status, previousSeason, nextSeason, previousEpisode, nextEpisode, rating, ratingTextField, currentSeasonTextField, currentEpisodeTextField);
				}
			}
		});

		VBox ratingAndFinished = new VBox();

		ratingAndFinished.setId("rating-and-finished-vbox");
		ratingAndFinished.getChildren().addAll(finished, rating(entity));

		// add 'Mark as finished' button to pane only if the current entity has
		// ENDDATE = Currently watching
		if (!entity.endDate.equals(Constants.CURRENTLY_WATCHING)) {
			finished.setText("Mark as unfinished");
		}

		return ratingAndFinished;
	}

	/**
	 * Generates a new dialog window in order to add a new entity. The only
	 * required information is the name of the new entity, rest of them
	 * (starting date, ending date, rating and current episode) are
	 * automatically generated.
	 */
	public void addEntity() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setHeaderText(Constants.INFO_REQUIRED);
		dialog.setContentText(Constants.NEW_ENTITY_NAME);
		Optional<String> result = dialog.showAndWait();
		
		if (result.isPresent()) {
			String entityName = result.get();
			String categoryName = category.getValue();
			Entity entity = new Entity(entityName, Constants.ON_AIR, 0, 1, 0, Controller.getCurrentDate(), Constants.CURRENTLY_WATCHING,
					Controller.getCategoryID(categoryName));

			if (Controller.entityExists(entity)) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setContentText(Constants.ENTITY_EXISTS);
				alert.showAndWait();
				addEntity();
			} else {
				Controller.addEntity(entity, categoryName);
				entities.getItems().add(entityName);
			}
		}
	}

	/**
	 * Generates a new modal window in order to add a new category specifying
	 * its name.
	 */
	public void addCategoryWindow() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setHeaderText(Constants.INFO_REQUIRED);
		dialog.setContentText(Constants.NEW_CATEGORY_NAME);
		Optional<String> result = dialog.showAndWait();

		if (result.isPresent()) {
			String categoryName = result.get();

			if (Controller.categoryExists(categoryName)) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setContentText(Constants.CATEGORY_EXISTS);
				alert.showAndWait();
				addCategoryWindow();
			} else {
				category.getItems().add(category.getItems().size(), categoryName);
				Controller.addCategory(categoryName);
				category.getSelectionModel().select(categoryName);
			}
		}
	}

	/**
	 * Generates the button that facilitates deleting a category.
	 * 
	 * @return delete category button
	 */
	public Button deleteCategoryButton() {
		Button deleteCategory = new Button("Delete");

		deleteCategory.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {	
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Confirmation Dialog");
				alert.setContentText("Are you sure ?");
				
				if (alert.showAndWait().get() == ButtonType.OK){	
					String temp = category.getValue();
					Controller.removeCategoryContent(temp);
					Controller.removeCategory(temp);
					category.getSelectionModel().select(category.getItems().size() - 2);
					category.getItems().remove(temp);
				} 
			}
		});

		return deleteCategory;
	}

	/**
	 * Generates the button that facilitates adding a category.
	 * 
	 * @return add category button
	 */
	public Button addCategoryButton() {
		Button addCategory = new Button("Add new category");

		addCategory.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				addCategoryWindow();
			}
		});

		return addCategory;
	}
	
	/**
	 * Generates the component which includes the number of the current season
	 * and also its increasing according to the last season
	 * watched. Decreasing this number is also possible.
	 * 
	 * @param entity
	 *            the entity alongside its details
	 * @param category
	 *            the category the new entity should be linked with
	 * @return HBox
	 */
	public HBox currentSeasonLabel(Entity entity, String category) {
		currentSeasonTextField = new TextField(String.valueOf(entity.season));
		currentSeasonTextField.setTooltip(new Tooltip(Constants.PRESS_ENTER_TO_SAVE));
		currentSeasonTextField.setId("current-episode-label");
		previousSeason = new Button("<");
		previousSeason.setId("current-episode-button");
		nextSeason = new Button(">");
		nextSeason.setId("current-episode-button");

		currentSeasonTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent keyEvent) {
				if (keyEvent.getCode().equals(KeyCode.ENTER) && currentSeasonTextField.getText().matches("^[1-9][0-9]*$") &&
																Integer.parseInt(currentSeasonTextField.getText()) > 0) {
					Controller.changeSeason(entity, Integer.parseInt(currentSeasonTextField.getText()));
				}
				
			}
		});

		previousSeason.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (Integer.parseInt(currentSeasonTextField.getText()) > 1) {
					Controller.changeSeason(entity, Constants.MINUS);
					Entity newEntity = new Entity();
					newEntity = Controller.getDetails(entity.name);
					currentSeasonTextField.setText(String.valueOf(newEntity.season));
				}
			}
		});

		nextSeason.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Controller.changeSeason(entity, Constants.PLUS);
				Entity newEntity = new Entity();
				newEntity = Controller.getDetails(entity.name);
				currentSeasonTextField.setText(String.valueOf(newEntity.season));
			}
		});

		HBox hbox = new HBox();
		hbox.setId("hbox");
		hbox.getChildren().addAll(previousSeason, currentSeasonTextField, nextSeason);

		return hbox;
	}

	/**
	 * Generates the component which includes the number of the current episode
	 * and also its increasing according to the last episode
	 * watched. Decreasing this number is also possible.
	 * 
	 * @param entity
	 *            the entity alongside its details
	 * @param category
	 *            the category the new entity should be linked with
	 * @return - HBox
	 */
	public HBox currentEpisodeLabel(Entity entity, String category) {
		currentEpisodeTextField = new TextField(String.valueOf(entity.currentEpisode));
		currentEpisodeTextField.setTooltip(new Tooltip(Constants.PRESS_ENTER_TO_SAVE));
		currentEpisodeTextField.setId("current-episode-label");
		previousEpisode = new Button("<");
		previousEpisode.setId("current-episode-button");
		nextEpisode = new Button(">");
		nextEpisode.setId("current-episode-button");

		currentEpisodeTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent keyEvent) {
				if (keyEvent.getCode().equals(KeyCode.ENTER) && currentEpisodeTextField.getText().matches("^[1-9][0-9]*$")) {
					Controller.changeCurrentEpisode(entity, Integer.parseInt(currentEpisodeTextField.getText()));		
				}	
			}
		});

		previousEpisode.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (Integer.parseInt(currentEpisodeTextField.getText()) > 0) {
					Controller.changeCurrentEpisode(entity, Constants.MINUS);
					Entity newEntity = new Entity();
					newEntity = Controller.getDetails(entity.name/* , category */);
					currentEpisodeTextField.setText(String.valueOf(newEntity.currentEpisode));
				}
			}
		});

		nextEpisode.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Controller.changeCurrentEpisode(entity, Constants.PLUS);
				Entity newEntity = new Entity();
				newEntity = Controller.getDetails(entity.name);
				currentEpisodeTextField.setText(String.valueOf(newEntity.currentEpisode));
			}
		});

		HBox hbox = new HBox();
		hbox.setId("hbox");
		hbox.getChildren().addAll(previousEpisode, currentEpisodeTextField, nextEpisode);

		return hbox;
	}

	/**
	 * Creates the pane which includes the possibility to give a rating to the
	 * current entity.
	 * 
	 * @param entity
	 * @return ratingPane
	 */
	public VBox rating(Entity entity) {
		rating = new Rating();
		rating.setPartialRating(true);
		rating.setRating(entity.rating);
		ratingTextField = new TextField();
		ratingTextField.setText(String.valueOf(entity.rating));
		ratingTextField.setId("rating-text-field");
		VBox ratingPane = new VBox();
		ratingPane.getChildren().addAll(new Separator(), rating, ratingTextField);
		ratingPane.setId("rating-pane");
		
		rating.ratingProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (newValue != null && !newValue.equals("") && newValue.toString().matches("^([0-4](\\.)[0-9]*)|((5)(\\.)(0))$")) {
					ratingLabelEast.setText(String.valueOf(Double.parseDouble(String.valueOf(newValue).substring(0, 3)) * 2));
					ratingTextField.setText(String.valueOf(Double.parseDouble((newValue.toString().substring(0, 3))) * 2));

					Controller.addRating(entity, Double.parseDouble(newValue.toString().substring(0, 3)) * 2, category.getValue());
				}
			}
		});

		ratingTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null && !newValue.equals("")
								 && newValue.toString().matches("^([0-9])|([0-9](\\.)[0-9]*)|(10)$")
								 && Double.parseDouble(newValue) <= 10) {
				double newRating = 0;

				if (newValue.toString().length() == 1) {
					newRating = Double.parseDouble(newValue.toString());
					ratingLabelEast.setText(String.valueOf(Double.parseDouble(String.valueOf(newValue).substring(0, 1))));
				} else if (newValue.toString().length() == 2) {
					newRating = Double.parseDouble(newValue.toString().substring(0, 2));
					ratingLabelEast.setText(String.valueOf(Double.parseDouble(String.valueOf(newValue).substring(0, 2))));
				} else {
					newRating = Double.parseDouble(newValue.toString().substring(0, 3));
					ratingLabelEast.setText(String.valueOf(Double.parseDouble(String.valueOf(newValue).substring(0, 3))));
				}

				Controller.addRating(entity, newRating, category.getValue());
			}
		});

		return ratingPane;
	}

	/**
	 * Create the search facility.
	 * 
	 * @return the search texfield
	 */
	public static TextField search() {
		TextField search = new TextField();
		search.setPromptText("Search");
		search.setId("search-text-field");

		search.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent keyEvent) {
				String searchEntity = search.getText();
				if (keyEvent.getCode() == KeyCode.ENTER) {
					if (pane5.getChildren().size() > 1) {
						pane5.getChildren().remove(1);
					}
					
					if (Controller.entityExists(new Entity(searchEntity))) {
						entities.getSelectionModel().select(searchEntity);
						entities.scrollTo(entities.getSelectionModel().getSelectedIndex());
					} else {
						ComboBox<String> results = searchHint(searchEntity);
						
						results.valueProperty().addListener(new ChangeListener<String>() {
							@Override
							public void changed(ObservableValue<? extends String> ov, String oldHint, String newHint) {
								entities.getSelectionModel().select(newHint);
								entities.scrollTo(entities.getSelectionModel().getSelectedIndex());
							}
						});
						
						if (results.getItems().size() == 0) {
							Alert alert = new Alert(AlertType.ERROR);
							alert.setContentText(Constants.FAILED_SEARCH);
							alert.showAndWait();
						} else if (results.getItems().size() == 1) {
							entities.getSelectionModel().select(results.getItems().get(0));
							entities.scrollTo(entities.getSelectionModel().getSelectedIndex());
						} else {
							FlowPane hintsPane = new FlowPane();
							hintsPane.getChildren().addAll(new Label(Constants.NO_RESULTS_FOUND_AND_HINTS), results);
							pane5.setRight(hintsPane);
						}
					}
				}
			}
		});

		return search;
	}
	
	/**
	 * The combo box with the hints after the search is done and returns to exact entity.
	 * 
	 * @param searchEntity
	 * @return
	 */
	public static ComboBox<String> searchHint(String searchEntity) {
		ComboBox<String> hints = new ComboBox<String>();
		
		for (int i = 0; i < entities.getItems().size(); i ++) {
			if (entities.getItems().get(i).contains(searchEntity) |
				entities.getItems().get(i).toUpperCase().contains(searchEntity) |
				entities.getItems().get(i).toLowerCase().contains(searchEntity)) {
				hints.getItems().add(entities.getItems().get(i));
			}
		}
		
		return hints;
	}
	/**
	 * Enables the specified components.
	 * 
	 * @param status
	 * @param previousSeason
	 * @param nextSeason
	 * @param previousEpisode
	 * @param nextEpisode
	 * @param rating
	 * @param ratingTextField
	 * @param currentSeasonTextField
	 * @param currentEpisodeTextField
	 */
	public static void enable(TextField nameTextFieldEast, ComboBox<String> status, Button previousSeason, Button nextSeason, Button previousEpisode, 
			   				  Button nextEpisode, Rating rating, TextField ratingTextField, TextField currentSeasonTextField, TextField currentEpisodeTextField) {
		nameTextFieldEast.setDisable(false);
		status.setDisable(false);
		previousSeason.setDisable(false);
		nextSeason.setDisable(false);
		previousEpisode.setDisable(false);
		nextEpisode.setDisable(false);
		rating.setDisable(false);
		ratingTextField.setDisable(false);
		currentSeasonTextField.setDisable(false);
		currentEpisodeTextField.setDisable(false);
	}
	
	/**
	 * Disables the specified components.
	 * 
	 * @param status
	 * @param previousSeason
	 * @param nextSeason
	 * @param previousEpisode
	 * @param nextEpisode
	 * @param rating
	 * @param ratingTextField
	 * @param currentSeasonTextField
	 * @param currentEpisodeTextField
	 */
	public static void disable(TextField nameTextFieldEast, ComboBox<String> status, Button previousSeason, Button nextSeason, Button previousEpisode, 
							   Button nextEpisode, Rating rating, TextField ratingTextField, TextField currentSeasonTextField, TextField currentEpisodeTextField) {
		nameTextFieldEast.setDisable(true);
		status.setDisable(true);
		previousSeason.setDisable(true);
		nextSeason.setDisable(true);
		previousEpisode.setDisable(true);
		nextEpisode.setDisable(true);
		rating.setDisable(true);
		ratingTextField.setDisable(true);
		currentSeasonTextField.setDisable(true);
		currentEpisodeTextField.setDisable(true);
	}

	/**
	 * Launches the GUI in execution.
	 */
	public static void go() {
		Controller.createDatabaseFile();
		Controller.createDatabaseTables();
		launch();
	}
}
