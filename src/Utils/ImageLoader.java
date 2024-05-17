package Utils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.InputStream;

public class ImageLoader {
    public static ImageView loadImage(String fileName) {
        Image image = null;

        // Try loading the image from the filesystem
        try (FileInputStream fileInputStream = new FileInputStream("resources/images/" + fileName + ".png")) {
            image = new Image(fileInputStream);

        } catch (Exception e) {
            // If FileInputStream fails, try loading the image from the classpath
            try (InputStream classPathStream = ImageLoader.class.getResourceAsStream("/images/" + fileName + ".png")) {
                if (classPathStream == null) {
                    throw new RuntimeException("Image not found: " + fileName);
                }

                image = new Image(classPathStream);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return new ImageView(image);
    }
}
