module org.utotec.utotec {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires org.apache.commons.io;

    opens org.utotec.utotec to javafx.fxml;
    exports org.utotec.utotec;
}