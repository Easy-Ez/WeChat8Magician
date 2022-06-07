package cc.wecando.harmoniousfamily.backend.storage

import android.content.*
import android.net.Uri
import android.util.Log
import cc.wecando.harmoniousfamily.Global.ACTION_UPDATE_PREF
import cc.wecando.harmoniousfamily.Global.FOLDER_SHARED_PREFS
import cc.wecando.harmoniousfamily.Global.MAGICIAN_BASE_DIR
import cc.wecando.harmoniousfamily.Global.PREFERENCE_PROVIDER_AUTHORITY
import cc.wecando.harmoniousfamily.Global.PREFERENCE_STRING_LIST_KEYS
import com.gh0u1l5.wechatmagician.spellbook.base.WaitChannel
import com.gh0u1l5.wechatmagician.spellbook.util.BasicUtil.tryAsynchronously
import com.gh0u1l5.wechatmagician.spellbook.util.BasicUtil.tryVerbosely
import de.robv.android.xposed.XSharedPreferences
import java.io.File
import java.lang.Exception
import java.util.concurrent.ConcurrentHashMap

class Preferences(private val preferencesName: String) : SharedPreferences {
    private val tag = "yaocai-sp-${preferencesName}"

    // loadChannel resumes all the threads waiting for the preference loading.
    private val loadChannel = WaitChannel()

    // listCache caches the string lists in memory to speed up getStringList()
    private val listCache: MutableMap<String, List<String>> = ConcurrentHashMap()

    // legacy is prepared for the fallback logic if ContentProvider is not working.
    private var legacy: XSharedPreferences? = null

    // content is the preferences generated by the frond end of Wechat Magician.
    private val content: MutableMap<String, Any?> = ConcurrentHashMap()

    // load reads the shared preferences or reloads the existing preferences
    fun load(context: Context) {
        tryAsynchronously {
            try {
                // Load the shared preferences using ContentProvider.
                val uri = Uri.parse("content://$PREFERENCE_PROVIDER_AUTHORITY/$preferencesName")
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    while (it.moveToNext()) {
                        val key = it.getString(0)
                        val type = it.getString(2)
                        content[key] = when (type) {
                            "Int" -> it.getInt(1)
                            "Long" -> it.getLong(1)
                            "Float" -> it.getFloat(1)
                            "Boolean" -> (it.getString(1) == "true")
                            "String" -> it.getString(1)
                            else -> null
                        }
                    }
                }
                for ((key, value) in content) {
                    Log.d(tag, "content-key:${key},value:${value}")
                }
            } catch (_: Exception) {
                // Failed to use the ContentProvider pattern, fallback to XSharedPreferences.
                if (loadChannel.isDone() && legacy != null) {
                    legacy?.reload()
                    return@tryAsynchronously
                }
                val preferencesDir = "$MAGICIAN_BASE_DIR/$FOLDER_SHARED_PREFS/"
                legacy = XSharedPreferences(File(preferencesDir, "$preferencesName.xml"))
                legacy?.all?.let {
                    for ((key, value) in it) {
                        Log.d(tag, "legacy-key:${key},value:${value}")
                    }
                }

            } finally {
                loadChannel.done()
                cacheStringList()
            }
        }
    }

    // listen registers the updateReceiver to listen the update events from the frontend.
    private val updateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            loadChannel.wait(2000)
            // If we are using the legacy logic, then just stay with it.
            if (legacy != null) {
                legacy?.reload()
                cacheStringList()
                return
            }
            // Otherwise we completely follow the new ContentProvider pattern.
            if (intent != null) {
                val key = intent.getStringExtra("key")
                key?.let {
                    content[it] = intent.extras?.get("value")
                }

            }
        }
    }

    fun listen(context: Context) {
        tryVerbosely {
            context.registerReceiver(updateReceiver, IntentFilter(ACTION_UPDATE_PREF))
        }
    }

    private fun cacheStringList() {
        PREFERENCE_STRING_LIST_KEYS.forEach { key ->
            getString(key, "")?.split(" ", "|")?.filter { it.isNotEmpty() }?.let {
                listCache[key] = it
            }

        }
    }

    override fun contains(key: String): Boolean =
        content.contains(key) || legacy?.contains(key) == true

    override fun getAll(): MutableMap<String, *>? {
        return if (legacy != null) {
            Log.d(tag, "from legacy")
            legacy!!.all
        } else {
            Log.d(tag, "from content")
            content
        }
    }

    private fun getValue(key: String?): Any? {
        loadChannel.wait(100)
        val all = all
        val ret = all?.get(key)
        Log.d(tag, "getValue-key:${key};value:${ret}")
        return ret
    }

    private inline fun <reified T> getValue(key: String?, defValue: T) =
        getValue(key) as? T ?: defValue

    override fun getInt(key: String, defValue: Int): Int = getValue(key, defValue)

    override fun getLong(key: String, defValue: Long): Long = getValue(key, defValue)

    override fun getFloat(key: String, defValue: Float): Float = getValue(key, defValue)

    override fun getBoolean(key: String, defValue: Boolean): Boolean = getValue(key, defValue)

    override fun getString(key: String?, defValue: String?): String? = getValue(key, defValue)

    override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String>? =
        getValue(key, defValues)

    fun getStringList(key: String, defValue: List<String>): List<String> {
        loadChannel.wait(100)
        return listCache[key] ?: defValue
    }

    override fun edit(): SharedPreferences.Editor {
        throw UnsupportedOperationException()
    }

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        throw UnsupportedOperationException()
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        throw UnsupportedOperationException()
    }
}
