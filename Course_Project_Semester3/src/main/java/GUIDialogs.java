import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class GUIDialogs {
    private Alert alert;
    private Image icon;

    public void setDialogIcon(Image icon) { this.icon = icon; }

    public void showError(String errorText) {
        alert = new Alert(Alert.AlertType.ERROR);
        show(errorText);
    }

    public void showWarning(String warningText) {
        alert = new Alert(Alert.AlertType.WARNING);
        show(warningText);
    }

    public void showInformation(String infoText) {
        alert = new Alert(Alert.AlertType.INFORMATION);
        show(infoText);
    }

    private void show(String text) {
        DialogPane dialogPane = alert.getDialogPane();
        if (icon != null)
            ((Stage) dialogPane.getScene().getWindow()).getIcons().add(icon);
        dialogPane.setStyle("-fx-font-size: 15px;");

        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }
}