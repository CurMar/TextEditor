package net.curmar.txted;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class TranslatedString {
    private final Map<String, Map<String, String>> langs = new HashMap<>();
    private String lang;
    private String path = getClass().getClassLoader().getResource("lang").getPath();

    public TranslatedString() {
        this("en_EN");
    }

    public TranslatedString(String lang) {
        this.lang = lang;
        try {
            File langDir = new File(path);
            File[] locFiles = langDir.listFiles((dir, name) -> name.endsWith(".locale"));
            for (File f : locFiles) {
                Map<String, String> pairs = new HashMap<>();
                List<String> lines = Files.readAllLines(f.toPath());
                for (String s : lines)
                    pairs.put(s.split("=")[0], s.split("=")[1]);
                langs.put(f.getName().split("\\.")[0], pairs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getTranslatedText(String s) {
        return langs.get(lang).get(s);
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
