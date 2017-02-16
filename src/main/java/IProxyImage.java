import javafx.scene.image.Image;

/**
 * IProxyImage lets the ProxyImage behave as the RealImage
 */
public interface IProxyImage {
    /**
     * Get the value stored in the proxy image
     * @return
     */
    public Image getValue();
}
