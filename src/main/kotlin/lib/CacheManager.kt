package lib

import com.securemessaging.Message
import java.io.*

class CacheManager {

    val cacheFilePath = "./enmasse.cache.dat"

    fun cacheData(localStorage:LocalStorage, serviceCode:String){

        val hashMap = HashMap<String, LocalStorage>()
        hashMap.put(serviceCode, localStorage)

        val fileOutputStream = FileOutputStream(File(cacheFilePath))
        val objectOutputStream = ObjectOutputStream(fileOutputStream)
        objectOutputStream.writeObject(hashMap)

        objectOutputStream.flush()
        objectOutputStream.close()

    }

    fun getCachedData(serviceCode:String):LocalStorage?{

        try{
            val fileInputStream = FileInputStream(File(cacheFilePath))
            val objectInputStream = ObjectInputStream(fileInputStream)
            val deserializedObject = objectInputStream.readObject()

            val hashMap = deserializedObject as? HashMap<String, LocalStorage>
            if(hashMap != null){

                if(hashMap.containsKey(serviceCode)){
                    return hashMap[serviceCode]
                }
            }

            return null

        }catch(e:Exception){
            e.printStackTrace()
            println("Failed To Fetch Caching Data. Returning Null")
            return null
        }
    }
}