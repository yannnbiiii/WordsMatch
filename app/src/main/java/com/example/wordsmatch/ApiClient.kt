package com.example.wordsmatch

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object ApiClient {

    // Emulator: 10.0.2.2 = "localhost" laptop kamu
    // HP asli: ganti dengan IP laptop (cek lewat `ipconfig`), contoh "http://192.168.1.5/wordsmatch/"
    const val BASE_URL = "http://localhost/wordsmatch/"


    suspend fun post(endpoint: String, params: Map<String, String> = emptyMap()): JSONObject =
        withContext(Dispatchers.IO) {
            try {
                val body = params.entries.joinToString("&") {
                    "${URLEncoder.encode(it.key, "UTF-8")}=${URLEncoder.encode(it.value, "UTF-8")}"
                }
                val conn = URL(BASE_URL + endpoint).openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.connectTimeout = 8000
                conn.readTimeout = 8000
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                conn.outputStream.use { it.write(body.toByteArray()) }

                val text = conn.inputStream.bufferedReader().use { it.readText() }
                conn.disconnect()
                JSONObject(text)
            } catch (e: Exception) {
                android.util.Log.e("ApiClient", "Gagal ke $BASE_URL$endpoint: $e", e)
                JSONObject().put("success", false).put("message", "Error: $e")
            }
        }

    // Dikirim tanpa menunggu hasil, aman walaupun Activity langsung ditutup
    fun kirimSkor(username: String, skor: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            post("update_score.php", mapOf("username" to username, "score" to skor.toString()))
        }
    }
}