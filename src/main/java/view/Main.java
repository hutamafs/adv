package view;

import controller.TableController;

public class Main {
    public static void main(String[] args) {
        TableController.createUserTable();
        LoginView.main(args);
    }
}
