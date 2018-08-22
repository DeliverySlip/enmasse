package application.models

data class Message(val messageGuid:String,
                   val subject:String,
                   val body:String,
                   val to:String,
                   val cc:String,
                   val bcc:String,
                   val from:String) {
}