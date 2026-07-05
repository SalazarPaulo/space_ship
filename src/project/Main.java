package src.project;

import java.awt.EventQueue;

import src.io.GameSettings;

public final class Main {
    private Main() { }

    public static void main(String[] args) {
        GameSettings.load();
        EventQueue.invokeLater(() -> new Window().start());
    }
}
