package org.utotec.utotec;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CardController {

    @FXML
    private HBox HboxBTN;

    @FXML
    private VBox VboxAddItem;

    @FXML
    private Button btnEdit;

    @FXML
    private Button btnHapus;

    @FXML
    private CheckBox chkBox;

    @FXML
    private ImageView imgFoto;

    @FXML
    private Label txtNama;

    @FXML
    private Label txtHarga;

    Connection connection = DB.connectDB();
    HelloController helloController = new HelloController().getInstance();

    private String displayMode = "edit";
    public void setDisplayMode(String displayMode) {
        this.displayMode = displayMode;
    }

    public void setData(int id,String nama, int harga, String gambar) {
        if (!SesiUser.izin.equals("admin")) {
            HboxBTN.getChildren().clear();
        }

        if (!gambar.trim().isEmpty()){
            File file = new File(gambar);
            Image image = new Image(file.toURI().toString());
            imgFoto.setImage(image);
        }

        txtNama.setText(nama);
        txtHarga.setText(String.valueOf(harga));

        Platform.runLater(() -> {
            if (!displayMode.equals("edit")){
                HboxBTN.getChildren().clear();
                VboxAddItem.setOnMouseClicked(mouseEvent->addToCArt(id));
            }
        });

        chkBox.setOnAction(actionEvent -> ChkBoxCeker(id));
        btnHapus.setOnAction(actionEvent -> Hapus(id));
        btnEdit.setOnAction(actionEvent -> openEditPopup(id));
    }

    private void openEditPopup(int idr) {
        Popup popup = new Popup();
        Stage stage = new Stage();
        popup.setAutoHide(true);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("formEdit.fxml"));
        try {
            if (!popup.isShowing()){
                Scene scene = new Scene(fxmlLoader.load());
                FormEditController formEditController = fxmlLoader.getController();
                formEditController.setForm(idr);
                File file = new File("src/main/resources/img/backspaceaqua.png");
                stage.getIcons().add(new Image(file.toURI().toString()));
                stage.setScene(scene);
                stage.setMinWidth(782);
                stage.setMinHeight(433);
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        private void addToCArt(int id) {
            helloController.resiList.add(id);
            helloController.addHargaTotal(Integer.parseInt(txtHarga.getText()));

            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("cartCard.fxml"));
                helloController.getBarangContainer().getChildren().addLast(fxmlLoader.load());

                cartController cartController = fxmlLoader.getController();
                cartController.setData(id);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    private void Hapus(int id) {
        Connection connection = DB.connectDB();
        if (Tools.AlertNotifCONFIRM("Notifikasi", "Hapus data", "Apakah anda yakin ingin mengapus laptop berikut : " + txtNama.getText() + " ?" )){
                try {

                    ResultSet resultSet = connection.createStatement().executeQuery("SELECT gambar FROM laptop WHERE id=" + id);
                    if (resultSet.next() && !resultSet.getString("gambar").isEmpty()){
                        Path path = Path.of(resultSet.getString("gambar")).toAbsolutePath();
                        Files.deleteIfExists(path);
                    }
                    PreparedStatement statement = statement = connection.prepareStatement("DELETE FROM laptop WHERE id=?");
                    statement.setInt(1, id);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        helloController.getRefreshData();
    }

    private void ChkBoxCeker(int id){
        GudangController gudangController= new GudangController();
        ArrayList<Integer> list = gudangController.getInstance().getList();
        if (chkBox.isSelected()){
            if (!list.contains(id)){
                list.add(id);
            }
        }else if (!chkBox.isSelected()){
            list.remove(Integer.valueOf(id));
        }
    }
}
