import com.securemessaging.Message

object Searcher {

    fun stringContainsKey(searchString:String?, searchKey:String, ignoreCase:Boolean = true):Boolean{
        return searchString?.contains(searchKey, ignoreCase = ignoreCase) ?: false

    }

    fun arrayContainsKey(searchArray:Array<String>?, searchKey:String, ignoreCase:Boolean = true):Boolean{

        if(searchArray != null){
            searchArray.forEach{
                if(it.contains(searchKey, ignoreCase=ignoreCase)){
                    return true
                }
            }
        }


        return false
    }


    fun stringPreciselyContainsKey(searchString:String, searchKey:String, ignoreCase:Boolean = true):Boolean{
        return searchString.equals(searchKey, ignoreCase=ignoreCase)
    }

    fun messageCointainsKey(message: Message, searchKey:String, ignoreCase:Boolean = true):Boolean{

        return stringContainsKey(message.body, searchKey) || stringContainsKey(message.subject, searchKey) ||
                arrayContainsKey(message.to, searchKey) || arrayContainsKey(message.from, searchKey) ||
                arrayContainsKey(message.cc, searchKey) || arrayContainsKey(message.bcc, searchKey)

    }
}