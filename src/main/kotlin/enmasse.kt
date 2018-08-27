import application.EnMasseApplication
import application.models.Message
import com.google.gson.Gson
import com.securemessaging.sm.auth.ServiceCodeResolver
import lib.*
import lib.enum.OutputType
import utils.ArgParser
import java.io.*
import tornadofx.launch

fun printHelp(){
    println("=========================================================================================================")
    println("                                               EnMasse                                                   ")
    println("                                               v1.0.0                                                    ")
    println("=========================================================================================================")
    println("Usage:")
    println("\t java -jar ./enmasse-1.0.0.jar --NOGUI -s <servicecode> -u <username> -p <password> -q <query> \n" +
            "\t -o <CONSOLE|JSON|CSV>")

    println("Parameters:")
    println("\t-s\t[REQUIRED]\t\tSpecify the service code to search\n")
    println("\t-u\t[REQUIRED]\t\tSpecify the username to login for the search")
    println("\t-p\t[REQUIRED]\t\tSpecify the password to login for the search")
    println("\t-q\t[REQUIRED]\t\tSpecify the search query string")
    println("\t-o\t[OPTIONAL]\t\tSpecify the output format of the data. Default is CONSOLE which prints to console. \n" +
            "\t  \t\t\t\t\tOtherwise JSON will print to a .json file and CSV will output to a .csv")

    println("Flags:")
    println("\t--CACHERESULT\t\tLocally cache the searched messages to speed up future searches")
    println("\t--USECACHE\t\tUse cache data instead of the API to make search")
    println("\t--NOGUI\t\tLoad EnMasse in terminal mode. Without this flag the GUI will load")
    println("\t--HELP\t\tPrint this help information")
}


fun main(params: Array<String>){

    ServiceCodeResolver.setResolverUrl("https://devlab-api.betasabrina.com/api")

    val argParser = ArgParser(params)

    if(!argParser.keyExists("--NOGUI")){
        launch<EnMasseApplication>(params)
    }else{

        if(argParser.keyExists("--HELP") || argParser.keyExists("--help")
                || argParser.keyExists("-h") || argParser.keyExists("-H")){
            printHelp()
            return
        }

        val serviceCode = argParser.getValue("-s")
        val username = argParser.getValue("-u")
        val password = argParser.getValue("-p")
        val query = argParser.getValue("-q")

        if(serviceCode == null || username == null || password == null || query == null){
            println("Required Parameters: ServiceCode, Username, Password and Query Are Required. Pass" +
                    "--HELP flag for help")
            return
        }

        val outputType = if (argParser.getValue("-o") != null) argParser.getValue("-o")!! else "CONSOLE"

        val cacheResults = argParser.keyExists("--CACHERESULT")
        val useCache = argParser.keyExists("--USECACHE")

        val configuration = Configuration()
        configuration.serviceCode = serviceCode
        configuration.username = username
        configuration.password = password
        configuration.query = query
        configuration.useCache = useCache
        configuration.cacheResults = cacheResults

        configuration.caseSensitive = argParser.keyExists("--CASESENSITIVE")
        configuration.absoluteSearch = argParser.keyExists("--ABSOLUTESEARCH")
        configuration.searchRecipients = argParser.keyExists("--SEARCHRECIPIENTS")
        configuration.searchBody = argParser.keyExists("--SEARCHBODY")
        configuration.searchSubject = argParser.keyExists("--SEARCHSUBJECT")

        val searchResults = Executor.execute(configuration)

        println("Search Completed. Outputting Results")
        println("There Are ${searchResults.matchingMessages.size} Matching Results")

        when(OutputType.valueOf(outputType.toUpperCase())){
            OutputType.CONSOLE ->{

                val messages = ArrayList<application.models.Message>()
                searchResults.matchingMessages.forEach {
                    messages.add(Message(it.messageGuid, it.subject, it.body,
                            it.to.joinToString(", "), it.cc.joinToString(", "),
                            it.bcc.joinToString(", "), it.from.joinToString(", ")))
                }

                Exporter.exportToConsole(messages)
            }
            OutputType.CSV ->{
                val messages = ArrayList<application.models.Message>()
                searchResults.matchingMessages.forEach {
                    messages.add(Message(it.messageGuid, it.subject, it.body,
                            it.to.joinToString(", "), it.cc.joinToString(", "),
                            it.bcc.joinToString(", "), it.from.joinToString(", ")))
                }

                Exporter.exportToCSV(messages)
            }
            OutputType.JSON ->{
                val messages = ArrayList<application.models.Message>()
                searchResults.matchingMessages.forEach {
                    messages.add(Message(it.messageGuid, it.subject, it.body,
                            it.to.joinToString(", "), it.cc.joinToString(", "),
                            it.bcc.joinToString(", "), it.from.joinToString(", ")))
                }

                Exporter.exportToJSON(messages)
            }
        }


        println("Terminating")
    }
}