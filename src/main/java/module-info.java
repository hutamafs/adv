module com.example.demo {
  requires javafx.controls;
  requires javafx.fxml;

  requires org.controlsfx.controls;
  requires org.kordamp.bootstrapfx.core;
  requires java.sql;
  requires java.desktop;

  opens com.example.demo to javafx.fxml;
  exports com.example.demo;
  exports view;
  exports controller;
  exports dao;
  exports model;
}