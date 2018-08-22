package lib

import com.securemessaging.Message

data class SearchResults(val matchingMessages:List<Message>,
                         val inboxList:List<Message>,
                         val draftList:List<Message>,
                         val sentList:List<Message>,
                         val trashList:List<Message>) {
}