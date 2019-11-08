import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class Controller {
    final MenuBar menuBar = new MenuBar();

    final Menu menuSignsFinder = new Menu("Signs Finder");
    final MenuItem about = new MenuItem("About");
    final MenuItem github = new MenuItem("View Source On Github");
    final SeparatorMenuItem separator = new SeparatorMenuItem();
    final MenuItem quit = new MenuItem("Quit");

    final Menu menuFile = new Menu("File");
    final MenuItem open = new MenuItem("Open...");
    final MenuItem save = new MenuItem("Save Result As...");

    final Menu menuProcess = new Menu("Process");
    final MenuItem start = new MenuItem("Start");
    final MenuItem cancel = new MenuItem("Cancel");

    final GridPane gridPane = new GridPane();
    final Scene scene = new Scene(gridPane);
    final Label infoLabel = new Label("\"File -> Open...\" / Drag&Drop / Type here to set the input string");
    final TextField inputField = new TextField();
    final Button buttonStart = new Button();
    final ProgressIndicator progressIndicator = new ProgressIndicator();
    final TextField outputField = new TextField();

    private SignsFinder model = new SignsFinder();
    private Image appIcon;
    private final GUIDialogs dialogs = new GUIDialogs();

    public void setAppIcon(Image icon) {
        appIcon = icon;
        dialogs.setDialogIcon(icon);
    }

    public Image getAppIcon() { return appIcon; }

    public void showInfo() {
        dialogs.showInformation("Developer: Andrew Jeus\n" +
                "Course 2, Group №3530901/80003");
    }

    public void openGitHub() {
        try {
            Desktop.getDesktop().browse(new URI("https://github.com/MickeyMouseMouse/Course_Project_Semester3"));
        } catch (URISyntaxException | IOException e) {
            dialogs.showError("Error 1: Can't open the link");
        }
    }

    public void closeApp() { System.exit(0); }

    public void openFile() {
        File file = showOpenFileChooser();
        if (file != null) getInputStringFromFile(file);
    }

    public void saveFile() {
        File file = showSaveFileChooser();

        if (file == null) return;

        try (FileWriter writer = new FileWriter(file.getAbsolutePath())) {
            writer.write(outputField.getText());
        } catch (IOException e) {
            dialogs.showError("Error 3: File save failed");
        }
    }

    private File showOpenFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Input File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text file", "*.txt"));
        return fileChooser.showOpenDialog(new Stage());
    }

    private File showSaveFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Output File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text file", "*.txt"));
        return fileChooser.showSaveDialog(new Stage());
    }

    public void getDragAndDropFile(List<File> file) {
        if (file.size() != 1) {
            dialogs.showError("Error 4: Only ONE file can be selected");
            return;
        }

        if (file.get(0).getName().matches("(.*)\\.(txt)"))
            getInputStringFromFile(file.get(0));
        else
            dialogs.showError("Error 5: Only *.txt file can be selected");
    }

    private void getInputStringFromFile(File inputFile) {
        String inputString = "";

        try (Scanner scanner = new Scanner(inputFile)) {
            if (scanner.hasNext()) inputString = scanner.nextLine();
        } catch (FileNotFoundException e) {
            dialogs.showError("Error 2: File not found");
        }

        inputField.setText(inputString);
        outputField.setText("");
    }

    public void start() {
        if (process.isRunning()) return;

        if (inputField.getText().length() == 0) {
            outputField.setText("");
            dialogs.showWarning("Warning 2: The input string is empty");
            return;
        }

        if (!model.setInputString(inputField.getText())) {
            dialogs.showInformation("This is already done. Look at the output field.");
            return;
        }

        outputField.setText("");
        if (!model.isValid()) {
            dialogs.showWarning("Warning 1: The input string isn't valid\n" +
                    "Example: 7 ( 5 3 ) 4 = 15 -> 7+( 5-3 )*4 = 15");
        } else {
            setGUISettings(false);
            process.reset();
            process.start();

            process.setOnSucceeded((e) -> {
                setGUISettings(true);

                if (model.isSucceeded()) {
                    outputField.setText((String) process.getValue());
                } else {
                    dialogs.showError("Error 6: " + process.getValue() +
                            " (Integer type is used, 4 bytes)");
                }
            });

            process.setOnCancelled((e) -> setGUISettings(true));
        }
    }

    // The Service class is used to prevent the GUI from freezing
    // if it takes a long time to find a solution.
    private Service process = new Service() {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected String call() {
                    return model.solve();
                }
            };
        }
    };

    public void cancel() {
        model.cancel();
        process.cancel();
    }

    private void setGUISettings(boolean mode) {
        open.setDisable(!mode);
        save.setDisable(!mode);

        start.setDisable(!mode);
        cancel.setDisable(mode);

        inputField.setEditable(mode);
        buttonStart.setVisible(mode);
        progressIndicator.setVisible(!mode);
    }
}