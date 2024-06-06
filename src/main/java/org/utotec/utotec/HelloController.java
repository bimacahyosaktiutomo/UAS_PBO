package org.utotec.utotec;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    @FXML private GridPane GPbarang;

    @FXML private VBox VboxMainContainer;

    @FXML private HBox HboxKasir;

    @FXML private VBox barangContainer;
    public VBox getBarangContainer() {
        return barangContainer;
    }

    @FXML private Button btnBayar;

    @FXML private CheckBox chkCASE;

    @FXML private CheckBox chkCPU;

    @FXML private CheckBox chkGPU;

    @FXML private CheckBox chkMOBO;

    @FXML private CheckBox chkPSU;

    @FXML private CheckBox chkRAM;

    @FXML private Pane pnExit;

    @FXML private Pane pnKasir;

    @FXML private Pane pnGudang;

    @FXML private Label txtKembali;

    @FXML private TextField txtPembayaran;

    @FXML private TextField txtSearchData;

    @FXML private Label txtTotal;

    Connection connection = DB.connectDB();

    ArrayList<Integer> resiList = new ArrayList<>();
    public ArrayList<Integer> getResiList() {
        return resiList;
    }

    private IntegerProperty HargaTotal = new SimpleIntegerProperty(0);
    public void addHargaTotal(int tambah){HargaTotal.set(HargaTotal.get() + tambah);}
    public void RemHargaTotal(int kurang){HargaTotal.set(HargaTotal.get() - kurang);}


    private IntegerProperty HargaKembalian = new SimpleIntegerProperty(0);
    public void calcHargaKembalian() {
        HargaKembalian.set(Integer.parseInt(txtPembayaran.getText()) - HargaTotal.get());
    }


    private static HelloController instance;
    public HelloController getInstance(){
        return instance;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        instance = this;
        pnKasir.setStyle("-fx-background-color: #4cbc27;");
        RefreshData();

        pnExit.setOnMouseClicked(mouseEvent -> Exit(mouseEvent));

        txtSearchData.textProperty().addListener((observable, oldValue, newValue) -> {Cari();});
        txtPembayaran.textProperty().addListener((observable, oldValue, newValue) -> {try {calcHargaKembalian();} catch (NumberFormatException ignored) {}});
        txtPembayaran.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    txtPembayaran.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        txtTotal.textProperty().bind(HargaTotal.asString());
        txtKembali.textProperty().bind(HargaKembalian.asString());
        txtTotal.textProperty().addListener((observable, oldValue, newValue) -> {try {calcHargaKembalian();} catch (NumberFormatException ignored) {}});

        pnGudang.setOnMouseClicked(mouseEvent -> openGudang());
        pnKasir.setOnMouseClicked(mouseEvent -> openKasir());

        CheckBox[] kategori = {chkCPU, chkGPU, chkRAM, chkMOBO, chkCASE, chkPSU};
        for (CheckBox checkBox : kategori){
            checkBox.setOnAction(actionEvent -> {
                if (checkBox.isSelected()) {
                    Cari();
                }else {
                    RefreshData();
                }
            });
        }
    }

    private void RefreshData() {
        GPbarang.getChildren().clear();
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
                    cardController.setDisplayMode("view");
                    cardController.setData(id, nama, harga, gambar);

                    if (column == 3){
                        column = 0;
                        ++row;
                    }

                    GPbarang.add(cardBox, column++, row);
                    GridPane.setMargin(cardBox, new Insets(8, 8, 8, 8));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void getRefreshData() {
        this.RefreshData();
    }

    private void openKasir() {
        pnGudang.setStyle("-fx-background-color: transparent;");
        pnKasir.setStyle("-fx-background-color: #4cbc27;");
        if (!VboxMainContainer.getChildren().equals(HboxKasir)){
            VboxMainContainer.getChildren().clear();
            VboxMainContainer.getChildren().add(HboxKasir);
        }
    }

    private void openGudang(){
        pnKasir.setStyle("-fx-background-color: transparent;");
        pnGudang.setStyle("-fx-background-color: #4cbc27;");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("gudang.fxml"));
        try {
           VboxMainContainer.getChildren().clear();
           VboxMainContainer.getChildren().add(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void Cari() {
        GPbarang.getChildren().clear();
        int column = 0;
        int row = 1;
        String cari = txtSearchData.getText();
        try {
            String extraQuery = checkBoxFilter();
            String query = "SELECT * FROM barang WHERE nama LIKE ?";
            if (!extraQuery.isEmpty()){
                query = "SELECT * FROM barang WHERE nama LIKE ? AND (" + extraQuery + ")";
            }
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, "%" + cari + "%");
            System.out.println(statement);
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
                    cardController.setDisplayMode("view");
                    cardController.setData(id, nama, harga, gambar);

                    if (column == 3){
                        column = 0;
                        ++row;
                    }

                    GPbarang.add(cardBox, column++, row);
                    GridPane.setMargin(cardBox, new Insets(8, 8, 8, 8));

                }catch (IOException e){
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void Exit(MouseEvent mouseEvent) {
            SesiUser.LoginSession = 0;
            SesiUser.izin = "";
            SesiUser.username = "";

        Node source = (Node) mouseEvent.getTarget();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();

        try {
            HelloApplication.HomeRefresh(stage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String checkBoxFilter() {
        StringBuilder query = new StringBuilder();
        CheckBox[] kategori = {chkCPU, chkGPU, chkRAM, chkMOBO, chkCASE, chkPSU};
        String[] preparedQuery = {"CPU", "GPU", "RAM", "Motherboard", "Casing", "PSU"};
        boolean pertama = true;
        for (int i = 0;i < kategori.length;i++){
            if (kategori[i].isSelected()){
                if (pertama){
                    query.append("kategori='").append(preparedQuery[i]).append("' ");
                    pertama = false;
                }else {
                    query.append("OR kategori='").append(preparedQuery[i]).append("' ");
                }
            }
        }
        return query.toString();
    }
}