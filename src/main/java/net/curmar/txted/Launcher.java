package net.curmar.txted;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;

public class Launcher extends Application {

    public static void go(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Editor editor = new Editor(stage);
        Parameters p = getParameters();
        if (!p.getRaw().isEmpty())
            editor.openFile(p.getRaw().get(0));
        else
            editor.openFile((File) null);
    }
}