package cdsre

import javafx.fxml.FXMLLoader
import javafx.scene.layout.AnchorPane

class MultiController(var masterLoader: FXMLLoader, var viewLoader: FXMLLoader, var detailsLoader: FXMLLoader)
{
    var master: AnchorPane = masterLoader.load<AnchorPane>()
    var view: AnchorPane = viewLoader.load<AnchorPane>()
    var details: AnchorPane = detailsLoader.load<AnchorPane>()
}