package cdsre

import javafx.fxml.FXMLLoader
import javafx.scene.layout.AnchorPane

class MultiController(masterLoader: FXMLLoader, viewLoader: FXMLLoader, detailsLoader: FXMLLoader)
{
    var master: AnchorPane = masterLoader.load<AnchorPane>()
    var view: AnchorPane = viewLoader.load<AnchorPane>()
    var details: AnchorPane = detailsLoader.load<AnchorPane>()
}