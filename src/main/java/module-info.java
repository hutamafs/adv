module com.example.demo {
  requires javafx.controls;
  requires javafx.fxml;

  requires org.controlsfx.controls;
  requires org.kordamp.bootstrapfx.core;
  requires java.sql;
  requires java.desktop;

  exports view;
  exports controller;
  exports dao;
  exports model;
}