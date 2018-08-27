package lib

class Configuration {

    var username:String = ""
    var password:String = ""
    var serviceCode:String = ""
    var query:String = ""

    var useCache:Boolean = false
    var cacheResults:Boolean = false

    var searchSubject = true
    var searchBody = true
    var searchRecipients = true
    var absoluteSearch = false
    var caseSensitive = false
}