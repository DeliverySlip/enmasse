import com.securemessaging.Message
import com.securemessaging.SecureMessenger
import com.securemessaging.sm.Credentials
import com.securemessaging.sm.enums.MessageBoxType
import com.securemessaging.sm.search.SearchMessagesFilter
import utils.ArgParser
import kotlin.concurrent.thread


fun main(params: Array<String>){

    val argParser = ArgParser(params)

    val serviceCode = argParser.getValue("-s")
    val username = argParser.getValue("-u")
    val password = argParser.getValue("-p")
    val query = argParser.getValue("-q")

    val matchingMessages = ArrayList<Message>()

    println("Starting Mass Search")

    val tokenMessenger = SecureMessenger.resolveViaServiceCode(serviceCode)
    val tokenGeneratinCredentals = Credentials(username, password)
    tokenMessenger.login(tokenGeneratinCredentals)

    val inboxToken = tokenMessenger.getAuthenticationToken(1)
    val sentToken = tokenMessenger.getAuthenticationToken(1)
    val draftToken = tokenMessenger.getAuthenticationToken(1)
    val trashToken = tokenMessenger.getAuthenticationToken(1)

    println("Spawning Thread For Searching Inbox")
    //spawn in new thread

    val inboxThread = thread {

        val messenger = SecureMessenger.resolveViaServiceCode(serviceCode)
        val credentals = Credentials(inboxToken)
        messenger.login(credentals)

        //fetch all inbox messages
        val inboxSearchFilter = SearchMessagesFilter()
        inboxSearchFilter.messageBoxType = MessageBoxType.INBOX
        val inboxSearchResults = messenger.searchMessages(inboxSearchFilter)

        val iterator = inboxSearchResults.iterator()
        while(iterator.hasNext()){
            val message = iterator.next()
            val retrievedMessage =  messenger.getMessage(message.messageGuid)

            if(retrievedMessage.body.contains(query!!) || retrievedMessage.subject.contains(query)){
                matchingMessages.add(retrievedMessage)
            }
        }
    }


    println("Spawning Thread For Searching Sent")
    //spawn in new thread
    val sentThread = thread {

        val messenger = SecureMessenger.resolveViaServiceCode(serviceCode)
        val credentals = Credentials(sentToken)
        messenger.login(credentals)

        //fetch all sent messages
        val sentSearchFilter = SearchMessagesFilter()
        sentSearchFilter.messageBoxType = MessageBoxType.SENT
        val sentSearchResults = messenger.searchMessages(sentSearchFilter)

        val iterator = sentSearchResults.iterator()
        while(iterator.hasNext()){
            val message = iterator.next()
            val retrievedMessage =  messenger.getMessage(message.messageGuid)

            if(retrievedMessage.body.contains(query!!) || retrievedMessage.subject.contains(query)){
                matchingMessages.add(retrievedMessage)
            }
        }
    }




    println("Spawning Thread For Searching Drafts")
    //spawn in new thread
    val draftThread = thread{

        val messenger = SecureMessenger.resolveViaServiceCode(serviceCode)
        val credentals = Credentials(draftToken)
        messenger.login(credentals)

        //fetch all draft messages
        val draftSearchFilter = SearchMessagesFilter()
        draftSearchFilter.messageBoxType = MessageBoxType.DRAFT
        val draftSearchResults = messenger.searchMessages(draftSearchFilter)

        val iterator = draftSearchResults.iterator()
        while(iterator.hasNext()){
            val message = iterator.next()
            val retrievedMessage =  messenger.getMessage(message.messageGuid)

            if(retrievedMessage.body.contains(query!!) || retrievedMessage.subject.contains(query)){
                matchingMessages.add(retrievedMessage)
            }
        }
    }




    println("Spawning Thread For Searching Trash")
    //spawn in new thread
    val trashThread = thread{

        val messenger = SecureMessenger.resolveViaServiceCode(serviceCode)
        val credentals = Credentials(trashToken)
        messenger.login(credentals)

        //fetch all trash messages
        val trashSearchFilter = SearchMessagesFilter()
        trashSearchFilter.messageBoxType = MessageBoxType.TRASH
        val trashSearchResults = messenger.searchMessages(trashSearchFilter)

        val iterator = trashSearchResults.iterator()
        while(iterator.hasNext()){
            val message = iterator.next()
            val retrievedMessage =  messenger.getMessage(message.messageGuid)

            if(retrievedMessage.body.contains(query!!) || retrievedMessage.subject.contains(query)){
                matchingMessages.add(retrievedMessage)
            }
        }
    }

    //wait for all threads to finish
    inboxThread.join()
    sentThread.join()
    draftThread.join()
    trashThread.join()

    tokenMessenger.deleteAuthenticationToken(inboxToken)
    tokenMessenger.deleteAuthenticationToken(sentToken)
    tokenMessenger.deleteAuthenticationToken(draftToken)
    tokenMessenger.deleteAuthenticationToken(trashToken)

    println("Search Completed. Printing Results")

    matchingMessages.forEach{
        println("${it.messageGuid} | Subject: ${it.subject} | Body: ${it.body}")
    }

}