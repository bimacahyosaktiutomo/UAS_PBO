package org.utotec.utotec;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.UUID;

import javafx.stage.FileChooser;
import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Text;

public class FormTambahController implements Initializable {

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtWarning.setVisible(false);

        cbKategori.setValue("CPU");
        cbKategori.getItems().addAll("CPU", "GPU", "RAM",
                "MOTHERBOARD", "PSU", "CASING");
        cbWarna.setValue("HITAM");
        cbWarna.getItems().addAll("NONE", "HITAM", "PERAK", "ABU-ABU", "PUTIH", "BIRU", "MERAH", "EMAS",
                "HIJAU", "MERAH MUDA", "UNGU", "ORANYE");

        btnTambah.setOnAction(this::Tambah);
        btnUbahGambar.setOnAction(actionEvent -> getImage());

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

    private void Tambah(ActionEvent actionEvent) {
        try {
            if (!txtNama.getText().trim().isEmpty() && !txtMerek.getText().trim().isEmpty()
                    && !txtHarga.getText().trim().isEmpty() && currentFile != null) {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO barang VALUES (NULL, ?, ?, ?, ?, ?, ?)");
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

                Tools.AlertNotifINFORMATION("Informasi", "Tambah barang", "Berhasil Menambah barang");
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
        }
    }
}