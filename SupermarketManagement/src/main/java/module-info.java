module com.ou.controllers {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.ou.controllers to javafx.fxml;
    exports com.ou.controllers;
    opens com.ou.pojos to javafx.fxml;
    exports com.ou.pojos;
    opens com.ou.repositories to javafx.fxml;
    exports com.ou.repositories;
    opens com.ou.services to javafx.fxml;
    exports com.ou.services;
    opens com.ou.utils to javafx.fxml;
    exports com.ou.utils;

}
