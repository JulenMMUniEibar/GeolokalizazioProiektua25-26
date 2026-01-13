module geo_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;
    requires jdk.jsobject;
    requires java.net.http;
    requires org.json;

    opens geo_project.controller to javafx.fxml;
    exports geo_project;
}
