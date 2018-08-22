package application.styles

import tornadofx.Stylesheet
import tornadofx.px

class MasterStyle: Stylesheet() {
    init{
        root {
            prefHeight = 600.px
            prefWidth = 800.px
        }
    }
}