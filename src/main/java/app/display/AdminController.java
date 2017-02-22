package app.display;

import app.applicationControl.ApplicationController;
import app.datastore.Map;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class AdminController extends DisplayController {
	@FXML DirectoryAdminController directoryAdminController;
	@FXML MapAdminController mapAdminController;
	@FXML AdminToolsController adminToolsController;

	public void init(Map map,
                     ApplicationController applicationController,
                     Stage stage) {
		super.init(map, applicationController, stage);

		directoryAdminController.init(map, applicationController, stage);
		mapAdminController.init(map, applicationController, stage);
		adminToolsController.init(map, applicationController, stage);
	}
}
