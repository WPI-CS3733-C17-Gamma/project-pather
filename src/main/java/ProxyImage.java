import javafx.scene.image.Image;

/**
 * ProxyImage serves as a proxy class for the real image
 */
public class ProxyImage implements IProxyImage {

    private RealImage realImage;

    /**
     * Get the value of the real image.
     * Delegates this function to the real image
     * @return
     */
    @Override
    public Image getValue() {
        return realImage.getValue();
    }

    /**
     * Construct the proxy image to load the given string
     * @param imageName - name of image this proxyimage is responsible for
     */
    public ProxyImage (String imageName) {
        realImage = new RealImage(imageName);
    }
}
