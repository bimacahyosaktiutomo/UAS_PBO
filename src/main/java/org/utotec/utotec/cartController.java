package org.utotec.utotec;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class cartController {

    @FXML private HBox HboxDataContainer;

    @FXML private VBox VboxCartMain;

    @FXML private Button btnDecre;

    @FXML private Button btnIncre;

    @FXML private ImageView imgBarang;

    @FXML private Pane pnBuang;

    @FXML private TextField txtJumlah;

    @FXML private Label txtNama;

    @FXML private Label txtHarga;

    Connection connection = DB.connectDB();
    private static cartController instance;
    HelloController helloController = new HelloController().getInstance();
    private int hargaNum = 1;

    public void setData(int id) {
        instance = this;
        try {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM barang WHERE id=" + id);
            if (resultSet.next()){
                txtJumlah.setText(String.valueOf(hargaNum));
                txtHarga.setText(String.valueOf(hargaNum*resultSet.getInt("harga")));
                txtNama.setText(resultSet.getString("nama"));

                if (!resultSet.getString("gambar").trim().isEmpty()){
                    File file = new File(resultSet.getString("gambar"));
                    Image image = new Image(file.toURI().toString());
                    imgBarang.setImage(image);
                }

                btnIncre.setOnAction(actionEvent -> {
                    hargaNum++;

                    try {
                        helloController.addHargaTotal(resultSet.getInt("harga"));
                        txtHarga.setText(String.valueOf(hargaNum*resultSet.getInt("harga")));
                        txtJumlah.setText(String.valueOf(hargaNum));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
                btnDecre.setOnAction(actionEvent -> {
                    if (hargaNum > 1){
                        hargaNum--;
                        try {
                            helloController.RemHargaTotal(resultSet.getInt("harga"));
                            txtHarga.setText(String.valueOf(hargaNum*resultSet.getInt("harga")));
                            txtJumlah.setText(String.valueOf(hargaNum));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        pnBuang.setOnMouseClicked(mouseEvent->Buang());
    }

    private void Buang(){
//        Yang di comment itu alternatif , pas lagi buntu, ternyata container yang paling atas tinggal dikasih id biar simpel

//        ((VBox) parent.getParent()).getChildren().remove(parent);

//        Parent parent = pnBuang.getParent().getParent();
//        helloController.getBarangContainer().getChildren().remove(parent);

//        Parent parent = HboxDataContainer.getParent();
//        helloController.getBarangContainer().getChildren().remove(parent);


        helloController.getBarangContainer().getChildren().remove(VboxCartMain);
        helloController.RemHargaTotal(Integer.parseInt(txtHarga.getText()));
    }
}
