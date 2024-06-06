package org.utotec.utotec;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.UUID;

public class FormEditController implements Initializable {

    @FXML
    private Button btnTambah;

    @FXML
    private Button btnUbahGambar;

    @FXML
    private ComboBox<String> cbKategori;

    @FXML
    private ComboBox<String> cbWarna;

    @FXML
    private ImageView imgPP;

    @FXML
    private TextField txtHarga;

    @FXML
    private TextField txtMerek;

    @FXML
    private TextField txtNama;

    @FXML
    private Label txtWarning;


    Connection connection = DB.connectDB();

    File currentFile = null;

    private String gambarLama;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtWarning.setVisible(false);

        cbKategori.getItems().addAll("CPU", "GPU", "RAM",
                "MOTHERBOARD", "PSU", "CASING");

        cbWarna.getItems().addAll("NONE", "HITAM", "PERAK", "ABU-ABU", "PUTIH", "BIRU", "MERAH", "EMAS",
                "HIJAU", "MERAH MUDA", "UNGU", "ORANYE");

        txtHarga.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    txtHarga.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }

    public void setForm(int id) {
        try {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM barang WHERE id = " + id);
            if (resultSet.next()) {
                txtNama.setText(resultSet.getString("nama"));
                cbKategori.setValue(resultSet.getString("kategori"));
                txtMerek.setText(resultSet.getString("merek"));
                cbWarna.setValue(resultSet.getString("warna"));
                txtHarga.setText(String.valueOf(resultSet.getInt("harga")));

                File file = new File(resultSet.getString("gambar"));
                if (!resultSet.getString("gambar").isEmpty()) {
                    gambarLama = resultSet.getString("gambar");
                    Image image = new Image(file.toURI().toString());
                    currentFile = file;
                    imgPP.setImage(image);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        btnTambah.setOnAction(actionEvent ->  Edit(actionEvent,id));
        btnUbahGambar.setOnAction(actionEvent -> getImage());
    }

    private void getImage() {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("(*.JPG *.PNG *.jpg *.png)", "*.JPG", "jpg files (*.jpg)", "*.jpg", "PNG files (*.PNG)", "*.PNG", "png files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            Image image = new Image(file.toURI().toString());
            imgPP.setImage(image);
            currentFile = file;
        }
    }

    private void Edit(ActionEvent actionEvent, int id) {
        try {
            if (!txtNama.getText().trim().isEmpty() && !txtMerek.getText().trim().isEmpty()
                    && !txtHarga.getText().trim().isEmpty() && currentFile != null) {
                PreparedStatement statement = connection.prepareStatement("UPDATE barang SET nama=?, kategori=?, merek=?, warna=?, harga=?, gambar=? WHERE id=" + id);
                statement.setString(1, txtNama.getText());
                statement.setString(2, cbKategori.getValue());
                statement.setString(3, txtMerek.getText());
                statement.setString(4, cbWarna.getValue());
                statement.setInt(5, Integer.parseInt(txtHarga.getText()));

                String fileName = txtNama.getText() + "_" + UUID.randomUUID() + "_" + SesiUser.uid + "." + FilenameUtils.getExtension(currentFile.getName());
                Path destinationDir = Path.of("src/main/resources/img/barang");
                Path destinationFile = destinationDir.toAbsolutePath().resolve(fileName);

                statement.setString(6, destinationDir.resolve(fileName).toString());

                try {
                    Files.copy(currentFile.toPath(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                statement.executeUpdate();
                Files.deleteIfExists(Path.of(gambarLama));

                Tools.AlertNotifINFORMATION("Informasi", "Tambah Laptop", "Berhasil Menambah Laptop");
                Tools.CloseMeStage(actionEvent);

                GudangController gudangController = new GudangController().getInstance();
                gudangController.getRefreshData();

                HelloController helloController = new HelloController().getInstance();
                helloController.getRefreshData();
            }else {
                Tools.AlertNotif("Warning", "Data", "Data Belum Lengkap");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}