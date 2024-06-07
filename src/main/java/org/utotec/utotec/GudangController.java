package org.utotec.utotec;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class GudangController implements Initializable {
    @FXML private GridPane GPlaptop;

    @FXML private Button btnHapus;

    @FXML private HBox btnHolder;

    @FXML private Button btnTambah;

    @FXML private TextField txtSearchData;

    @FXML private Label txtUser;

    Connection connection = DB.connectDB();

    private static GudangController instance;
    public GudangController getInstance(){
        return instance;
    }

    ArrayList<Integer> list = new ArrayList<>();
    public ArrayList<Integer> getList() {
        return list;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        instance = this;
        RefreshData();
        if (SesiUser.uid == 0 || !SesiUser.izin.equals("admin")){
            btnHolder.getChildren().clear();
        }

        txtUser.setText(SesiUser.username);

        btnTambah.setOnAction(actionEvent -> openTambahPopup());
        btnHapus.setOnAction(actionEvent -> HapusList());
        txtSearchData.textProperty().addListener((observable, oldValue, newValue) -> {Cari();});
    }

    private void RefreshData() {
        GPlaptop.getChildren().clear();
        int column = 0;
        int row = 1;
        try {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM barang");
            while (resultSet.next()) {
                try {
                    int id = resultSet.getInt("id");
                    String nama = resultSet.getString("nama");
                    int harga = resultSet.getInt("harga");
                    String gambar = resultSet.getString("gambar");

                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("laptopCard.fxml"));
                    VBox cardBox =  fxmlLoader.load();
                    CardController cardController = fxmlLoader.getController();
                    cardController.setData(id, nama, harga, gambar);

                    if (column == 5){
                        column = 0;
                        ++row;
                    }

                    GPlaptop.add(cardBox, column++, row);
                    GridPane.setMargin(cardBox, new Insets(5));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void getRefreshData(){
        this.RefreshData();
    }

    private void Cari() {
        GPlaptop.getChildren().clear();
        int column = 0;
        int row = 1;
        String cari = txtSearchData.getText();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM barang WHERE nama LIKE ?");
            statement.setString(1, "%" + cari + "%");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                try {
                    int id = resultSet.getInt("id");
                    String nama = resultSet.getString("nama");
                    int harga = resultSet.getInt("harga");
                    String gambar = resultSet.getString("gambar");

                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("laptopCard.fxml"));
                    VBox cardBox =  fxmlLoader.load();
                    CardController cardController = fxmlLoader.getController();
                    cardController.setData(id, nama, harga, gambar);

                    if (column == 5){
                        column = 0;
                        ++row;
                    }

                    GPlaptop.add(cardBox, column++, row);
                    GridPane.setMargin(cardBox, new Insets(5));

                }catch (IOException e){
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void openTambahPopup() {
        Popup popup = new Popup();
        Stage stage = new Stage();
        popup.setAutoHide(true);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("formTambah.fxml"));
        try {
            if (!popup.isShowing()){
                Scene scene = new Scene(fxmlLoader.load());
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

    private void HapusList() {
        if (Tools.AlertNotifCONFIRM("Notifikasi", "Hapus data", "Apakah anda yakin ingin mengapus barang berikut :  " + list)){
            for (int id : list){
                try {
                    ResultSet resultSet = connection.createStatement().executeQuery("SELECT gambar FROM barang WHERE id=" + id);
                    if (resultSet.next() && !resultSet.getString("gambar").isEmpty()){
                        Path path = Path.of(resultSet.getString("gambar")).toAbsolutePath();
                        Files.deleteIfExists(path);
                    }
                    PreparedStatement statement = connection.prepareStatement("DELETE FROM barang WHERE id=?");
                    statement.setInt(1, id);
                    statement.executeUpdate();

                    GudangController gudangController = new GudangController().getInstance();
                    gudangController.getRefreshData();

                    HelloController helloController = new HelloController().getInstance();
                    helloController.getRefreshData();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            RefreshData();
        }
    }
}
