package ch.pentagrid.burpexts.responseoverview

import burp.HttpRequestResponse
import burp.HttpService
import java.io.*
import java.util.*

open class Persist {

    companion object {

        //Add a toBase64 functionality to ByteArrays
        private fun ByteArray.toBase64(): String = String(Base64.getEncoder().encode(this))
        private fun String.fromBase64(): ByteArray = Base64.getDecoder().decode(this)

        private const val host = "pentagrid-ag-response_overview.local"
        private const val port = 1337
        private const val protocol = "https"

        fun println(s: String){
            BurpExtender.stdout.println(s)
        }

        internal fun saveExtensionSettings(serializableThing: Serializable, name: String){
            //println("Serializing $serializableThing")
            val byteOut = ByteArrayOutputStream()
            val objectOut = ObjectOutputStream(byteOut)
            objectOut.writeObject(serializableThing)
            objectOut.close()
            val bytes = byteOut.toByteArray()
            byteOut.close()
            BurpExtender.c.saveExtensionSetting(name, bytes.toBase64())
        }

        internal fun loadExtensionSettings(name: String): Any? {
            val value = BurpExtender.c.loadExtensionSetting(name)
            return if(value == null){
                null
            }else {
                val serializedThing = value.fromBase64()
                //println("Deserialized $serializedThing")
                val byteIn = ByteArrayInputStream(serializedThing)
                ObjectInputStream(byteIn).readObject()
            }
        }

        internal fun saveProjectSettings(serializableThing: Serializable, name: String){
            val byteOut = ByteArrayOutputStream()
            val objectOut = ObjectOutputStream(byteOut)
            objectOut.writeObject(serializableThing)
            objectOut.close()
            val bytes = byteOut.toByteArray()
            byteOut.close()
            val request = """GET /$name HTTP/0.9
                |X-Header: You can ignore this item in the site map. It was created by the ResponseOverview extension.
                |X-Header: The Burp extender API does not support project-level settings, so every extension author
                |X-Header: has to abuse this SiteMap storage.
            """.trimMargin().toByteArray()
            val rr = HttpRequestResponse(request, bytes, null, null, HttpService(host, port, protocol))
            BurpExtender.c.addToSiteMap(rr)
        }

        internal fun loadProjectSettings(name: String): Any? {
            val rr = BurpExtender.c.getSiteMap("$protocol://$host:$port/$name")
            if(rr.isEmpty()){
                return null
            }
            val serializedThing = rr[0].response
            val byteIn = ByteArrayInputStream(serializedThing)
            return ObjectInputStream(byteIn).readObject()
        }


    }
}