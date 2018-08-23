package application.views

import application.controllers.SearchController
import application.models.Message
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.paint.Color
import lib.Configuration
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


    private lateinit var tableView: javafx.scene.control.TableView<Message>

    override val root = vbox{
        form{
            fieldset{
                field("Service Code"){
                    textfield().bind(serviceCode)
                }
                field("Username"){
                    textfield().bind(username)
                }
                field("Password"){
                    passwordfield().bind(password)
                }
            }

            fieldset{
                field("Search Query"){
                    textfield().bind(searchQuery)
                }
            }

            fieldset{
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
                            searchResults.clear()
                            val configuration = Configuration()
                            configuration.password = password.get()
                            configuration.username = username.get()
                            configuration.serviceCode = serviceCode.get()
                            configuration.query = searchQuery.get()
                            configuration.useCache = useCache.get()
                            configuration.cacheResults = cacheResults.get()
                            controller.makeSearch(configuration)
                        }ui{ results:List<Message> ->
                            results.forEach{
                                searchResults.add(it)
                            }

                            tableView.requestResize()
                        }
                    }
                }
            }
        }

        tableview<Message> {
            columnResizePolicy = SmartResize.POLICY
            items = searchResults



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