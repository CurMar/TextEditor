package net.curmar.txted;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.stage.FileChooser;


public class Editor {

    private final TranslatedString translator;
    private final Scene scene;
    private final Stage stage;
    private final VBox pane;
    private final MenuBar menubar;
    private final TextArea text;
    private Optional<File> file;
    private boolean isChanged;

    public static final String NEW_FILE_NAME = "New file.txt";

    public Editor(Stage stage) {
        translator = new TranslatedString("ru_RU");
        menubar = new MenuBar(new Editor.FileMenu().MENU, new AboutMenu().MENU, new SettingsMenu().MENU);
        text = new TextArea();
        text.positionCaret(0);
        text.textProperty().addListener(x -> {
            isChanged = true;
            updateTitle();
        });
        pane = new VBox(menubar, text);
        scene = new Scene(pane);
        text.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(text, Priority.ALWAYS);
        this.stage = stage;
        this.stage.setHeight(800);
        this.stage.setWidth(1000);
        this.stage.setScene(scene);
        this.stage.setOnCloseRequest(e -> {
            safeExit();
            if (isChanged) e.consume();
        });


        this.stage.show();
    }

    public void selectFile() {
        FileChooser fc = new FileChooser();
        fc.setTitle(translator.getTranslatedText("gui.menu_file.open"));
        openFile(fc.showOpenDialog(stage));
    }

    public void openFile(File file) {
        this.file = Optional.ofNullable(file);
        if (this.file.isPresent()) {
            try {
                List<String> lines = Files.readAllLines(file.toPath());
                StringBuilder sb = new StringBuilder();
                for (String line : lines)
                    sb.append(line).append('\n');

                text.setText(sb.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        updateTitle();
    }

    public void openFile(String path) {
        openFile(new File(path));
    }

    public void saveFile() {
        file.ifPresentOrElse(f -> {
            try {
                Files.writeString(f.toPath(), text.getText());
                isChanged = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, this::saveFileAs);
        updateTitle();
    }

    public void saveFileAs() {
        FileChooser fc = new FileChooser();
        fc.setTitle(translator.getTranslatedText("gui.menu_file.save"));
        fc.setInitialFileName(getFileName());
        file = Optional.ofNullable(fc.showSaveDialog(stage));
        if (file.isPresent()) saveFile();
    }

    public String getFileName() {
        return file.isPresent() ? file.get().getName() : NEW_FILE_NAME;
    }

    public void updateTitle() {
        stage.setTitle(getFileName());
        if (isChanged) stage.setTitle(stage.getTitle() + "*");
    }

    public void safeExit() {
        if (!isChanged) {
            stage.close();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.NONE, "Save the changes?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.setTitle("Confirmation");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                saveFile();
            } else if (response == ButtonType.NO)
                stage.close();
        });
    }

    class FileMenu {
        private final Menu MENU = new Menu(translator.getTranslatedText("gui.menu_file"));
        private final MenuItem OPEN = new MenuItem(translator.getTranslatedText("gui.menu_file.open"));
        private final MenuItem SAVE = new MenuItem(translator.getTranslatedText("gui.menu_file.save"));
        private final MenuItem SAVE_AS = new MenuItem(translator.getTranslatedText("gui.menu_file.saveas"));
        private final MenuItem EXIT = new MenuItem(translator.getTranslatedText("gui.menu_file.exit"));

        public FileMenu() {
            MENU.getItems().addAll(OPEN, SAVE, SAVE_AS, EXIT);

            OPEN.setOnAction(e -> selectFile());
            SAVE.setOnAction(e -> saveFile());
            SAVE_AS.setOnAction(e -> saveFileAs());
            EXIT.setOnAction(e -> safeExit());
        }
    }

    class SettingsMenu {
        private final Menu MENU = new Menu(translator.getTranslatedText("gui.menu_settings"));
        private final MenuItem LANG = new MenuItem(translator.getTranslatedText("gui.menu_settings.language"));

        public SettingsMenu() {
            MENU.getItems().add(LANG);
            LANG.setOnAction(x -> {
            });
        }
    }

    class AboutMenu {
        private final Menu MENU = new Menu(translator.getTranslatedText("gui.menu_app"));
        private final MenuItem ABOUT = new MenuItem(translator.getTranslatedText("gui.menu_about"));

        public AboutMenu() {
            MENU.getItems().add(ABOUT);
            ABOUT.setOnAction(x -> {
                Alert message = new Alert(Alert.AlertType.INFORMATION, "CurMar text editor\nDeveloper: CurMar\nVersion: 0.1");
                message.setHeaderText("Information about program");
                message.showAndWait();
            });

        }
    }
}
