import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class GUI extends Application {
    public static void main(String[] args) { Application.launch(args); }

    private final Controller cont = new Controller();

    @Override
    public void start(Stage mainStage) {
        cont.setAppIcon(new Image("icon.png"));

        mainStage.getIcons().add(cont.getAppIcon());
        mainStage.setTitle("Signs Finder");
        mainStage.centerOnScreen();
        mainStage.setScene(cont.scene);

        ColumnConstraints column = new ColumnConstraints(600,600, Double.MAX_VALUE);
        column.setHgrow(Priority.ALWAYS);
        cont.gridPane.getColumnConstraints().add(column);

        cont.gridPane.getRowConstraints().add(new RowConstraints(25)); // row for menu bar
        cont.gridPane.getRowConstraints().add(new RowConstraints(30)); // row for info label
        cont.gridPane.getRowConstraints().add(new RowConstraints(60)); // row for input field
        cont.gridPane.getRowConstraints().add(new RowConstraints(25)); // row for button start
        cont.gridPane.getRowConstraints().add(new RowConstraints(60)); // row for output field

        GridPane.setRowIndex(cont.menuBar, 0); // 0 row = menu bar
        GridPane.setRowIndex(cont.infoLabel, 1); // 1 row = info label
        GridPane.setRowIndex(cont.inputField, 2); // 2 row = input field
        GridPane.setMargin(cont.inputField, new Insets(10));
        GridPane.setRowIndex(cont.buttonStart, 3); // 3 row = button start
        GridPane.setRowIndex(cont.progressIndicator, 3);
        GridPane.setRowIndex(cont.outputField, 4); // 4 row = output field
        GridPane.setMargin(cont.outputField, new Insets(10));
        cont.gridPane.getChildren().addAll(cont.menuBar, cont.infoLabel, cont.inputField,
                cont.buttonStart, cont.progressIndicator, cont.outputField);
        Platform.runLater(cont.inputField::requestFocus); // set focus on inputField
        GridPane.setHalignment(cont.infoLabel, HPos.CENTER);
        GridPane.setHalignment(cont.buttonStart, HPos.CENTER);
        GridPane.setHalignment(cont.progressIndicator, HPos.CENTER);

        mainStage.show();
        mainStage.setMinWidth(mainStage.getWidth());
        mainStage.setMinHeight(mainStage.getHeight());
        mainStage.setMaxHeight(mainStage.getHeight());

        cont.menuBar.getMenus().addAll(cont.menuSignsFinder,
                cont.menuFile, cont.menuProcess);
        cont.menuSignsFinder.getItems().addAll(cont.about, cont.github,
                cont.separator, cont.quit);
        cont.menuFile.getItems().addAll(cont.open, cont.save);
        cont.menuProcess.getItems().addAll(cont.start, cont.cancel);

        cont.about.setOnAction((e) -> cont.showInfo());
        cont.github.setOnAction((e) -> cont.openGitHub());
        cont.quit.setOnAction((e) -> cont.closeApp());

        cont.open.setOnAction((e) -> cont.openFile());
        cont.open.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN));
        cont.save.setOnAction((e) -> cont.saveFile());
        cont.save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));

        cont.cancel.setDisable(true);

        cont.infoLabel.setFont(Font.font(null, 19));

        cont.inputField.setFont(Font.font(null, 20));
        cont.inputField.setPromptText("Input");

        cont.buttonStart.graphicProperty()
                .setValue(new ImageView(new Image("start.png")));
        cont.buttonStart.setStyle("-fx-padding: 0 0 0 0; -fx-background-radius: 90;");
        cont.buttonStart.setTooltip(new Tooltip("Start solving"));

        cont.progressIndicator.setVisible(false);

        cont.outputField.setFont(Font.font(null, 20));
        cont.outputField.setPromptText("Output");
        cont.outputField.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);

        // start Drag&Drop
        cont.scene.setOnDragOver((e) -> {
            if (e.getDragboard().hasFiles())
                e.acceptTransferModes(TransferMode.ANY);
        });

        // event on drop new file
        cont.scene.setOnDragDropped((e) ->
            cont.getDragAndDropFile(e.getDragboard().getFiles())
        );

        cont.start.setOnAction((e) -> cont.start());

        cont.cancel.setOnAction((e) -> cont.cancel());

        cont.inputField.setOnKeyPressed((e) -> {
            if (e.getCode() == KeyCode.ENTER) cont.start();
        });

        cont.buttonStart.setOnMouseClicked((e) -> cont.start());

        // prevent editing of the output field
        cont.outputField.setOnKeyPressed((e) -> {
            if (e.getCode() == KeyCode.BACK_SPACE) e.consume();
        });

        // prevent editing of the output field
        cont.outputField.setOnKeyTyped((e) -> {
            if (e.getCode() != KeyCode.LEFT && e.getCode() != KeyCode.RIGHT) e.consume();
        });
    }
}