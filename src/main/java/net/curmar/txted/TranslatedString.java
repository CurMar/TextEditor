package net.curmar.txted;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.*;

public class TranslatedString {

    private static final Map<String, Map<String, String>> LANGS = new HashMap<>();
    private String lang;

    static {
        try {
            URI uri = TranslatedString.class.getResource("/lang").toURI();
            Path path;
            List<Path> files;
            if (uri.getScheme().equals("jar")) {
                try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
                    path = fileSystem.getPath("/lang");
                    files = listFiles(path);
                    loadLangs(files, fileSystem);
                }
            } else {
                path = Paths.get(uri);
                files = listFiles(path);
                loadLangs(files, null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Path> listFiles(Path path) throws IOException {
        List<Path> result = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path entry : stream)
                if (Files.isRegularFile(entry))
                    result.add(entry);
        }
        return result;
    }

    private static void loadLangs(List<Path> files, FileSystem fileSystem) {
        for (Path p : files) {
            Map<String, String> pairs = new HashMap<>();
            try {
                List<String> lines = fileSystem != null ? Files.readAllLines(fileSystem.getPath(p.toString())) : Files.readAllLines(p);
                for (String s : lines)
                    if (!s.isEmpty()) {
                        s = s.replace("\\n", "\n");
                        String[] pair = s.split("=");
                        pairs.put(pair[0], pair[1]);
                    }

                LANGS.put(p.getFileName().toString().split("\\.")[0], pairs);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public TranslatedString() {
        this("en_EN");
    }

    public TranslatedString(String lang) {
        this.lang = lang;
    }

    public String getTranslatedText(String s) {
        return LANGS.getOrDefault(lang, new HashMap<>()).getOrDefault(s, s);
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

}