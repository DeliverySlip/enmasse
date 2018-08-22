package lib

import com.securemessaging.Message
import java.io.*

class CacheManager {

    val cacheFilePath = "./enmasse.cache.dat"

    fun cacheData(localStorage:LocalStorage){

        val fileOutputStream = FileOutputStream(File(cacheFilePath))
        val objectOutputStream = ObjectOutputStream(fileOutputStream)
        objectOutputStream.writeObject(localStorage)

        objectOutputStream.flush()
        objectOutputStream.close()

    }

    fun getCachedData():LocalStorage?{

        try{
            val fileInputStream = FileInputStream(File(cacheFilePath))
            val objectInputStream = ObjectInputStream(fileInputStream)
            val deserializedObject = objectInputStream.readObject()
            return deserializedObject as? LocalStorage
        }catch(e:Exception){
            return null
        }
    }
}