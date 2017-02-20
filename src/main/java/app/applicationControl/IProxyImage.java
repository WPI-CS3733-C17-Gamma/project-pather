package app.applicationControl;

import javafx.scene.image.Image;

/**
 * app.applicationControl.IProxyImage lets the app.applicationControl.ProxyImage behave as the app.applicationControl.RealImage
 */
public interface IProxyImage {
    /**
     * Get the value stored in the proxy image
     * @return
     */
    public Image getValue();
}
