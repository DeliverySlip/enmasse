package lib

import com.securemessaging.Message

object Searcher {

    object SearchSettings{
        var absoluteSearch = false
        var searchSubject = true
        var searchBody = true
        var searchRecipients = true
        var caseSensitive = false
    }

    fun stringContainsKey(searchString:String?, searchKey:String):Boolean{

        return searchString?.contains(searchKey, ignoreCase = !SearchSettings.caseSensitive) ?: false

    }

    fun arrayContainsKey(searchArray:Array<String>?, searchKey:String):Boolean{

        if(searchArray != null){
            searchArray.forEach{
                if(it.contains(searchKey, ignoreCase= !SearchSettings.caseSensitive)){
                    return true
                }
            }
        }


        return false
    }

    fun arrayPreciselyContainsKey(searchArray:Array<String>?, searchKey:String):Boolean{
        if(searchArray != null){
            searchArray.forEach{
                if(it.equals(searchKey, ignoreCase= !SearchSettings.caseSensitive)){
                    return true
                }
            }
        }


        return false
    }



    fun stringPreciselyContainsKey(searchString:String, searchKey:String):Boolean{
        return searchString.equals(searchKey, ignoreCase= !SearchSettings.caseSensitive)
    }

    fun messageCointainsKey(message: Message, searchKey:String):Boolean{

        if(SearchSettings.absoluteSearch){

            if(SearchSettings.searchBody){
                if(stringPreciselyContainsKey(message.body, searchKey)){
                    return true
                }
            }

            if(SearchSettings.searchSubject){
                if(stringPreciselyContainsKey(message.subject, searchKey)){
                    return true
                }
            }

            if(SearchSettings.searchRecipients){
                if(arrayPreciselyContainsKey(message.to, searchKey) || arrayPreciselyContainsKey(message.cc, searchKey)
                    || arrayPreciselyContainsKey(message.bcc, searchKey)){
                    return true
                }
            }

            return false

        }else{

            if(SearchSettings.searchBody){
                if(stringContainsKey(message.body, searchKey)){
                    return true
                }
            }

            if(SearchSettings.searchSubject){
                if(stringContainsKey(message.subject, searchKey)){
                    return true
                }
            }

            if(SearchSettings.searchRecipients){
                if(arrayContainsKey(message.to, searchKey) || arrayContainsKey(message.cc, searchKey)
                        || arrayContainsKey(message.bcc, searchKey)){
                    return true
                }
            }

            return false


        }

    }
}