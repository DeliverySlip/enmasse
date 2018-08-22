package application

import application.styles.MasterStyle
import application.views.MasterView
import javafx.stage.Stage
import tornadofx.App
import tornadofx.importStylesheet

class EnMasseApplication: App() {
    override val primaryView = MasterView::class

    init{
        importStylesheet(MasterStyle::class)
    }

    override fun start(stage: Stage) {
        stage.isResizable = false
        super.start(stage)
    }
}