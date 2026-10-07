package main.ui;

import java.awt.Image;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class AssetManager {
    private static AssetManager instance;
    private Map<String, Image> imageCache;

    private AssetManager() {
        imageCache = new HashMap<>();
        loadAssets();
    }

    public static AssetManager getInstance() {
        if (instance == null) {
            instance = new AssetManager();
        }
        return instance;
    }

    private void loadAssets() {
        String[] files = {
            "background_start.png",
            "background_level_select.png",
            "background_level1.png",
            "background_level2.png",
            "background_level3.jpg",
            "result_complete.png",
            "result_complete_level2.png",
            "result_complete_level3.png",
            "result_lose.png",
            "result_lose_level2.png",
            "result_lose_level3.png"
        };
        
        File assetDir = new File("assets");
        if (!assetDir.exists() && new File("../assets").exists()) {
            assetDir = new File("../assets");
        }

        for (String file : files) {
            try {
                Image img = ImageIO.read(new File(assetDir, file));
                imageCache.put(file, img);
            } catch (Exception e) {
                System.err.println("Could not load asset: " + file);
                e.printStackTrace();
            }
        }
    }

    public Image getImage(String name) {
        return imageCache.get(name);
    }
}
