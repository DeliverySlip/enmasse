package lib

import com.securemessaging.Message
import java.io.Serializable


data class LocalStorage(val inbox:List<Message>, val sent:List<Message>, val draft:List<Message>,
                        val trash:List<Message>) : Serializable{
}