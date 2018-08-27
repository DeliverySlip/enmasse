package lib

import application.models.Message
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter

object Exporter {

    val csvFilePath = "./output.csv"
    val jsonFilePath = "./output.json"


    fun exportToJSON(messages:List<Message>){
        val gson = GsonBuilder().setPrettyPrinting().create()
        val json = gson.toJson(messages)

        val fileOutputStream = FileOutputStream(File(jsonFilePath))
        fileOutputStream.write(json.toByteArray())
        fileOutputStream.flush()
        fileOutputStream.close()
    }

    fun exportToCSV(messages:List<Message>){

        val headerData = "MessageGuid, Subject, Body, To, Cc, Bcc, From"
        val lineSeperator = "\n"
        val delimiter = ","

        val fileWriter = FileWriter(File(csvFilePath))
        fileWriter.append(headerData)
        fileWriter.append(lineSeperator)

        messages.forEach{
            fileWriter.append(it.messageGuid)
            fileWriter.append(delimiter)
            fileWriter.append(it.subject)
            fileWriter.append(delimiter)
            fileWriter.append(it.body)
            fileWriter.append(delimiter)
            fileWriter.append(it.to)
            fileWriter.append(delimiter)
            fileWriter.append(it.cc)
            fileWriter.append(delimiter)
            fileWriter.append(it.bcc)
            fileWriter.append(delimiter)
            fileWriter.append(it.from)
            fileWriter.append(lineSeperator)
        }

        fileWriter.flush()
        fileWriter.close()
    }

    fun exportToConsole(messages:List<Message>){
        messages.forEach{
            println("${it.messageGuid} | Subject: ${it.subject} | Body: ${it.body}")
        }
    }
}