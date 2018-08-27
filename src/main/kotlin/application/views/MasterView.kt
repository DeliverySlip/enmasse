package application.views

import application.controllers.SearchController
import application.models.Message
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableBooleanValue
import javafx.geometry.Orientation
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import lib.Configuration
import lib.enum.OutputType
import tornadofx.*
import javax.swing.text.TableView
import kotlin.concurrent.thread

class MasterView: View("EnMasse - Bulk Secure Messaging Search") {

    val controller: SearchController by inject()

    val username = SimpleStringProperty()
    val serviceCode = SimpleStringProperty()
    val password = SimpleStringProperty()
    val searchQuery = SimpleStringProperty()
    val searchResults = ArrayList<Message>().observable()

    val cacheResults = SimpleBooleanProperty(true)
    val useCache = SimpleBooleanProperty(false)

    val absoluteMatch = SimpleBooleanProperty(false)
    val caseSensitive = SimpleBooleanProperty(false)
    val searchSubject = SimpleBooleanProperty(true)
    val searchBody = SimpleBooleanProperty(true)
    val searchRecipients = SimpleBooleanProperty(true)

    var searchIsRunning = SimpleBooleanProperty(true)


    private lateinit var tableView: javafx.scene.control.TableView<Message>

    override val root = vbox{
        form{
            fieldset{
                field("Service Code"){
                    textfield(){
                        bind(serviceCode)
                    }
                }
                field("Username"){
                    textfield(){
                        bind(username)
                    }
                }
                field("Password"){
                    passwordfield(){
                        bind(password)
                    }
                }
            }

            fieldset("Search"){
                field("Search Query"){
                    textfield().bind(searchQuery)
                }
                hbox{
                    spacing=10.0
                    paddingLeft = 125
                    checkbox().bind(absoluteMatch)
                    label("Absolute Match")
                    checkbox().bind(caseSensitive)
                    label("Case Sensitive")
                    checkbox().bind(searchSubject)
                    label("Search Subject")
                    checkbox().bind(searchBody)
                    label("Search Body")
                    checkbox().bind(searchRecipients)
                    label("Search Recipients")
                }
            }

            fieldset("Cache Settings"){
                field("Cache Search Results"){
                    checkbox().bind(cacheResults)
                }
                field("Use Cached Data"){
                    checkbox().bind(useCache)
                }
            }
        }

        borderpane{
            style{
                paddingBottom = 10
            }
            center{
                button("Search"){
                    action{
                        runAsyncWithProgress{
                            searchIsRunning.set(true)
                            searchResults.clear()
                            val configuration = Configuration()
                            configuration.password = password.get()
                            configuration.username = username.get()
                            configuration.serviceCode = serviceCode.get()
                            configuration.query = searchQuery.get()
                            configuration.useCache = useCache.get()
                            configuration.cacheResults = cacheResults.get()

                            configuration.absoluteSearch = absoluteMatch.get()
                            configuration.searchBody = searchBody.get()
                            configuration.searchSubject = searchSubject.get()
                            configuration.searchRecipients = searchRecipients.get()
                            configuration.caseSensitive = caseSensitive.get()


                            controller.makeSearch(configuration)
                        }ui{ results:List<Message> ->
                            results.forEach{
                                searchResults.add(it)
                            }

                            tableView.requestResize()
                            searchIsRunning.set(false)
                        }
                    }
                }
            }
            right{
                style{
                    paddingRight = 10
                }
                hbox{
                    spacing = 20.0
                    button("Export CSV"){
                        disableWhen(searchIsRunning)
                        action{
                            runAsyncWithProgress {
                                controller.exportData(OutputType.CSV, searchResults)
                            }
                        }
                    }
                    button("Export JSON"){
                        disableWhen(searchIsRunning)
                        action{
                            runAsyncWithProgress {
                                controller.exportData(OutputType.JSON, searchResults)
                            }
                        }
                    }
                }
            }
        }

        tableview<Message> {
            columnResizePolicy = SmartResize.POLICY
            items = searchResults

            vgrow = Priority.ALWAYS

            readonlyColumn("Message Guid", Message::messageGuid)
            //readonlyColumn("Box", Message::box)
            readonlyColumn("From", Message::from)
            readonlyColumn("To", Message::to)
            readonlyColumn("Cc", Message::cc)
            readonlyColumn("Bcc", Message::bcc)
            readonlyColumn("Subject", Message::subject)
            readonlyColumn("Body", Message::body)

            tableView = this
        }
    }
}