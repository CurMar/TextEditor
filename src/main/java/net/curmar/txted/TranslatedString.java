package net.curmar.txted;

import java.io.File;
import java.nio.file.Files;
import java.util.Map;
import java.util.HashMap;

public class TranslatedString {

    private static final String PATH = TranslatedString.class.getClassLoader().getResource("lang").getPath();
    private static final Map<String, Map<String, String>> LANGS = new HashMap<>();
    private String lang;

    static {
        try {
            File langDir = new File(PATH);
            File[] locFiles = langDir.listFiles((dir, name) -> name.endsWith(".locale"));
            for (File f : locFiles) {
                Map<String, String> pairs = new HashMap<>();
                for (String s : Files.readAllLines(f.toPath())) {
                    s= s.replace("\\n", "\n");
                    if (!s.isEmpty()) pairs.put(s.split("=")[0], s.split("=")[1]);
                }
                LANGS.put(f.getName().split("\\.")[0], pairs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TranslatedString() {
        this("en_EN");
    }

    public TranslatedString(String lang) {
        this.lang = lang;
    }

    public String getTranslatedText(String s) {
        return LANGS.get(lang).getOrDefault(s, s);
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
