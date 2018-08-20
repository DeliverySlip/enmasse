import com.google.gson.Gson
import com.securemessaging.Message
import com.securemessaging.SecureMessenger
import com.securemessaging.sm.Credentials
import com.securemessaging.sm.auth.ServiceCodeResolver
import com.securemessaging.sm.enums.MessageBoxType
import com.securemessaging.sm.search.SearchMessagesFilter
import enum.OutputType
import org.springframework.cglib.core.Local
import utils.ArgParser
import java.io.*
import java.util.*
import java.util.concurrent.Executors
import kotlin.concurrent.thread

/**
 * --CACHERESULT - store search results into the cache, updating if it already exists
 * --USECACHE - make search against cached data
 */

fun main(params: Array<String>){


    ServiceCodeResolver.setResolverUrl("https://devlab-api.betasabrina.com/api")

    val argParser = ArgParser(params)

    val serviceCode = argParser.getValue("-s")
    val username = argParser.getValue("-u")
    val password = argParser.getValue("-p")
    val query = argParser.getValue("-q")
    val outputType = if (argParser.getValue("-o") != null) argParser.getValue("-o")!! else "CONSOLE"

    val cacheResults = argParser.keyExists("--CACHERESULT")
    val useCache = argParser.keyExists("--USECACHE")

    val cacheFilePath = "./enmasse.cache.dat"
    val jsonFilePath = "./output.json"
    val csvFilePath = "./output.csv"

    val inboxList = ArrayList<Message>()
    val sentList = ArrayList<Message>()
    val draftList = ArrayList<Message>()
    val trashList = ArrayList<Message>()

    val matchingMessages = Collections.synchronizedList(ArrayList<Message>())

    println("Starting Mass Search")

    if(useCache){
        println("Use Cache Parameter Detected. Searching Against Cached Data")
        val fileInputStream = FileInputStream(File(cacheFilePath))
        val objectInputStream = ObjectInputStream(fileInputStream)
        val deserializedObject = objectInputStream.readObject()
        when(deserializedObject){
            is LocalStorage -> {

                inboxList.clear()
                inboxList.addAll(deserializedObject.inbox)
                sentList.clear()
                sentList.addAll(deserializedObject.sent)
                draftList.clear()
                draftList.addAll(deserializedObject.draft)
                trashList.clear()
                trashList.addAll(deserializedObject.trash)

            }
        }
    }

    val tokenMessenger = SecureMessenger.resolveViaServiceCode(serviceCode)
    val tokenGeneratinCredentials = Credentials(username, password)
    tokenMessenger.login(tokenGeneratinCredentials)

    val inboxToken = tokenMessenger.getAuthenticationToken(1)
    val sentToken = tokenMessenger.getAuthenticationToken(1)
    val draftToken = tokenMessenger.getAuthenticationToken(1)
    val trashToken = tokenMessenger.getAuthenticationToken(1)

    val executor = Executors.newFixedThreadPool(20)

    println("Spawning Thread For Searching Inbox")
    //spawn in new thread

    val inboxThread = thread{

        if(useCache){
            inboxList.forEach{
                if(it.body.contains(query!!) || it.subject.contains(query, ignoreCase = true)){
                    matchingMessages.add(it)
                }
            }
        }else{
            val messenger = SecureMessenger.resolveViaServiceCode(serviceCode)
            val credentals = Credentials(inboxToken)
            messenger.login(credentals)

            //fetch all inbox messages
            val inboxSearchFilter = SearchMessagesFilter()
            inboxSearchFilter.messageBoxType = MessageBoxType.INBOX
            val inboxSearchResults = messenger.searchMessages(inboxSearchFilter)

            println("Spawning Inbox Executor")
            //val executor = Executors.newFixedThreadPool(5)

            val iterator = inboxSearchResults.iterator()
            while(iterator.hasNext()){
                val message = iterator.next()

                executor.execute{
                    val retrievedMessage =  messenger.getMessage(message.messageGuid)

                    if(cacheResults){
                        inboxList.add(retrievedMessage)
                    }

                    if(retrievedMessage.body != null){
                        if(retrievedMessage.body.contains(query!!, ignoreCase = true)){
                            matchingMessages.add(retrievedMessage)
                        }
                    }
                    if(retrievedMessage.subject != null){
                        if(retrievedMessage.subject.contains(query!!, ignoreCase = true)){
                            matchingMessages.add(retrievedMessage)
                        }
                    }
                    return@execute
                }
            }
        }
        println("Inbox Thread Complete")
        return@thread
    }

    println("Spawning Thread For Searching Sent")
    //spawn in new thread
    val sentThread = thread{

        if(useCache){
            sentList.forEach{
                if(it.body.contains(query!!) || it.subject.contains(query, ignoreCase = true)){
                    matchingMessages.add(it)
                }
            }
        }else{
            val messenger = SecureMessenger.resolveViaServiceCode(serviceCode)
            val credentals = Credentials(sentToken)
            messenger.login(credentals)

            //fetch all sent messages
            val sentSearchFilter = SearchMessagesFilter()
            sentSearchFilter.messageBoxType = MessageBoxType.SENT
            val sentSearchResults = messenger.searchMessages(sentSearchFilter)

            println("Spawning Sent Executor")
            //val executor = Executors.newFixedThreadPool(5)

            val iterator = sentSearchResults.iterator()
            while(iterator.hasNext()){
                val message = iterator.next()

                executor.execute{
                    val retrievedMessage =  messenger.getMessage(message.messageGuid)

                    if(cacheResults){
                        sentList.add(retrievedMessage)
                    }

                    if(retrievedMessage.body != null){
                        if(retrievedMessage.body.contains(query!!, ignoreCase = true)){
                            matchingMessages.add(retrievedMessage)
                        }
                    }
                    if(retrievedMessage.subject != null){
                        if(retrievedMessage.subject.contains(query!!, ignoreCase = true)){
                            matchingMessages.add(retrievedMessage)
                        }
                    }
                    return@execute
                }
            }
        }

        println("Sent Thread Complete")
        return@thread
    }

    println("Spawning Thread For Searching Drafts")
    //spawn in new thread
    val draftThread = thread{

        if(useCache){
            draftList.forEach{
                if(it.body.contains(query!!) || it.subject.contains(query, ignoreCase = true)){
                    matchingMessages.add(it)
                }
            }
        }else{
            val messenger = SecureMessenger.resolveViaServiceCode(serviceCode)
            val credentals = Credentials(draftToken)
            messenger.login(credentals)

            //fetch all draft messages
            val draftSearchFilter = SearchMessagesFilter()
            draftSearchFilter.messageBoxType = MessageBoxType.DRAFT
            val draftSearchResults = messenger.searchMessages(draftSearchFilter)

            println("Spawning Draft Executor")

            val iterator = draftSearchResults.iterator()
            while(iterator.hasNext()){
                val message = iterator.next()

                executor.execute{
                    val retrievedMessage =  messenger.getMessage(message.messageGuid)

                    if(cacheResults){
                        draftList.add(retrievedMessage)
                    }

                    if(retrievedMessage.body != null){
                        if(retrievedMessage.body.contains(query!!, ignoreCase = true)){
                            matchingMessages.add(retrievedMessage)
                        }
                    }
                    if(retrievedMessage.subject != null){
                        if(retrievedMessage.subject.contains(query!!, ignoreCase = true)){
                            matchingMessages.add(retrievedMessage)
                        }
                    }
                    return@execute
                }
            }
        }
        println("Draft Thread Complete")
        return@thread
    }


    println("Spawning Thread For Searching Trash")
    //spawn in new thread
    val trashThread = thread{

        if(useCache){
            trashList.forEach{
                if(it.body.contains(query!!) || it.subject.contains(query, ignoreCase = true)){
                    matchingMessages.add(it)
                }
            }
        }else{
            val messenger = SecureMessenger.resolveViaServiceCode(serviceCode)
            val credentals = Credentials(trashToken)
            messenger.login(credentals)

            //fetch all trash messages
            val trashSearchFilter = SearchMessagesFilter()
            trashSearchFilter.messageBoxType = MessageBoxType.TRASH
            val trashSearchResults = messenger.searchMessages(trashSearchFilter)

            println("Spawning Trash Executor")

            val iterator = trashSearchResults.iterator()
            while(iterator.hasNext()){
                val message = iterator.next()

                executor.execute{
                    val retrievedMessage =  messenger.getMessage(message.messageGuid)

                    if(cacheResults){
                        trashList.add(retrievedMessage)
                    }

                    if(retrievedMessage.body != null){
                        if(retrievedMessage.body.contains(query!!, ignoreCase = true)){
                            matchingMessages.add(retrievedMessage)
                        }
                    }
                    if(retrievedMessage.subject != null){
                        if(retrievedMessage.subject.contains(query!!, ignoreCase = true)){
                            matchingMessages.add(retrievedMessage)
                        }
                    }
                    return@execute
                }
            }
        }

        println("Trash Thread Complete")
        return@thread
    }

    println("Waiting For Thread Termination")
    //wait for all threads to finish
    inboxThread.join()
    sentThread.join()
    draftThread.join()
    trashThread.join()

    //once all these threads have terminated, we know there are no more items being added to the executor
    println("Thread Termination Complete. Waiting On Executor Shutdown")
    executor.shutdown()
    while(!executor.isShutdown && !executor.isTerminated){
        //wait for stuff to finish
    }
    executor.shutdownNow()

    println("Executor Shutdown Complete. Closing API Session")
    tokenMessenger.deleteAuthenticationToken(inboxToken)
    tokenMessenger.deleteAuthenticationToken(sentToken)
    tokenMessenger.deleteAuthenticationToken(draftToken)
    tokenMessenger.deleteAuthenticationToken(trashToken)

    if(cacheResults){
        println("Cache Results Detected. Exporting Results")
        val localStorage = LocalStorage(inboxList, sentList, draftList, trashList)

        val fileOutputStream = FileOutputStream(File(cacheFilePath))
        val objectOutputStream = ObjectOutputStream(fileOutputStream)
        objectOutputStream.writeObject(localStorage)

        objectOutputStream.flush()
        objectOutputStream.close()
    }


    println("Search Completed. Printing Results")
    println("There Are ${matchingMessages.size} Matching Results")

    when(OutputType.valueOf(outputType.toUpperCase())){
        OutputType.CONSOLE ->{
            matchingMessages.forEach{
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

            matchingMessages.forEach{
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
            val json = gson.toJson(matchingMessages)

            val fileOutputStream = FileOutputStream(File(jsonFilePath))
            fileOutputStream.write(json.toByteArray())
            fileOutputStream.flush()
            fileOutputStream.close()
        }
    }


    println("Terminating")

}