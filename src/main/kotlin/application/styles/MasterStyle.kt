package application.styles

import tornadofx.Stylesheet
import tornadofx.px

class MasterStyle: Stylesheet() {
    init{
        root {
            prefHeight = 800.px
            prefWidth = 800.px
        }
    }
}