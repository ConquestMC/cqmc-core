package com.conquestmc.core.config;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;

public class ConfigManager<T> {

    private String fileName;
    private Class clazz;
    private Gson gson;
    private String directory;

    @Getter
    private T config;

    public ConfigManager(String directory, String fileName, Class clazz) {
        this.directory = directory;
        this.fileName = fileName;
        this.clazz = clazz;
        this.gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).setPrettyPrinting().create();
    }

    public void init() {
        File f = new File("plugins/" + directory + "Core");
        if (!f.exists()) {
            f.mkdir();
        }
        File file = new File("plugins/" + directory + "/" + fileName);
        if (!file.exists()) {
            URL url = getClass().getResource("/" + fileName);
            try {
                System.out.println("[CQMC-CORE] COPYING DEFAULT CONFIG: " + fileName);
                FileUtils.copyURLToFile(url, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        loadConfig();
    }

    public void loadConfig() {
        FileReader reader = null;
        try {
            reader = new FileReader(new File("plugins/" + directory + "/" + fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        this.config = (T) gson.fromJson(reader, clazz);

    }

    public void saveConfig() {
        FileWriter fw = null;
        try {
            fw = new FileWriter("plugins/" + directory +"/" + fileName);
            gson.toJson(config, fw);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}