import com.securemessaging.Message

data class LocalStorage(val inbox:ArrayList<Message>, val sent:ArrayList<Message>, val draft:ArrayList<Message>,
                        val trash:ArrayList<Message>) {
}