import application.EnMasseApplication
import com.google.gson.Gson
import com.securemessaging.sm.auth.ServiceCodeResolver
import lib.*
import lib.enum.OutputType
import utils.ArgParser
import java.io.*
import tornadofx.launch

/**
 * --CACHERESULT - store search results into the cache, updating if it already exists
 * --USECACHE - make search against cached data
 */

fun main(params: Array<String>){


    ServiceCodeResolver.setResolverUrl("https://devlab-api.betasabrina.com/api")

    val argParser = ArgParser(params)

    if(!argParser.keyExists("--NOGUI")){
        launch<EnMasseApplication>(params)
    }else{
        val serviceCode = argParser.getValue("-s")
        val username = argParser.getValue("-u")
        val password = argParser.getValue("-p")
        val query = argParser.getValue("-q")

        if(serviceCode == null || username == null || password == null || query == null){
            println("Required Parameters: ServiceCode, Username, Password and Query Are Needed")
            return
        }

        val outputType = if (argParser.getValue("-o") != null) argParser.getValue("-o")!! else "CONSOLE"

        val cacheResults = argParser.keyExists("--CACHERESULT")
        val useCache = argParser.keyExists("--USECACHE")

        val jsonFilePath = "./output.json"
        val csvFilePath = "./output.csv"

        val configuration = Configuration()
        configuration.serviceCode = serviceCode!!
        configuration.username = username!!
        configuration.password = password!!
        configuration.query = query!!
        configuration.useCache = useCache
        configuration.cacheResults = cacheResults

        val searchResults = Executor.execute(configuration)

        println("Search Completed. Printing Results")
        println("There Are ${searchResults.matchingMessages.size} Matching Results")

        when(OutputType.valueOf(outputType.toUpperCase())){
            OutputType.CONSOLE ->{
                searchResults.matchingMessages.forEach{
                    println("${it.messageGuid} | Subject: ${it.subject} | Body: ${it.body}")
                }
            }
            OutputType.CSV ->{
                val headerData = "MessageGuid, Subject, Body"
                val lineSeperator = "\n"
                val delimiter = ","

                val fileWriter = FileWriter(File(csvFilePath))
                fileWriter.append(headerData)
                fileWriter.append(lineSeperator)

                searchResults.matchingMessages.forEach{
                    fileWriter.append(it.messageGuid)
                    fileWriter.append(delimiter)
                    fileWriter.append(it.subject)
                    fileWriter.append(delimiter)
                    fileWriter.append(it.body)
                    fileWriter.append(lineSeperator)
                }

                fileWriter.flush()
                fileWriter.close()
            }
            OutputType.JSON ->{
                val gson = Gson()
                val json = gson.toJson(searchResults.matchingMessages)

                val fileOutputStream = FileOutputStream(File(jsonFilePath))
                fileOutputStream.write(json.toByteArray())
                fileOutputStream.flush()
                fileOutputStream.close()
            }
        }


        println("Terminating")
    }





}