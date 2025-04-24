module ru.vl7sha.demo1 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens ru.vl7sha.demo1 to javafx.fxml;
    exports ru.vl7sha.demo1;
}