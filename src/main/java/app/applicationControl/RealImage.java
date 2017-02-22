package app.applicationControl;

import javafx.scene.image.Image;

/**
 * This class holds the real image. It does not load the image
 * until the getValue function is called
 */
public class RealImage implements IProxyImage {

    private Image image = null;
    private String imageName;

    /**
     * Get the value of the proxy image. Load the image if it is not loaded
     * @return
     * @throws IllegalArgumentException - if the url is not valid
     */
    @Override
    public Image getValue() throws IllegalArgumentException {
        // if the image has not been loaded, load it
        if (image == null) {
            image = new Image(imageName);
            System.out.println("Loaded image : " + imageName);
        }
        return image;
    }

    /**
     * Construct a proxy image.
     * Does not load the image on startup
     * @param imageName
     */
    public RealImage (String imageName) {
        this.imageName = imageName;
    }
}

