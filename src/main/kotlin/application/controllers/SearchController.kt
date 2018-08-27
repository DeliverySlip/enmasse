package application.controllers


import application.models.Message
import lib.*
import lib.enum.OutputType
import tornadofx.Controller

class SearchController: Controller() {

    fun makeSearch(configuration: Configuration):List<Message>{

        val messages = ArrayList<Message>()

        val searchResults = Executor.execute(configuration)

        searchResults.matchingMessages.forEach{
            messages.add(Message(it.messageGuid ?: "", it.subject ?: "", it.body ?: "",
                    it.to.joinToString(", "),it.cc.joinToString(", "), it.bcc.joinToString(", "),
                    it.from.joinToString(", ")))
        }

        return messages

    }

    fun exportData(outputType: OutputType, data:List<Message>){

        when(outputType){
            OutputType.CSV ->{
                Exporter.exportToCSV(data)
            }
            OutputType.JSON ->{
                Exporter.exportToJSON(data)
            }
            else ->{
                println("ERROR - Export Type Has Not Been Coded For")
            }
        }

    }

}