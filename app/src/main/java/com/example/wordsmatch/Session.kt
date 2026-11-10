package com.example.wordsmatch

import android.content.Context

object Session {
    private fun prefs(ctx: Context) =
        ctx.getSharedPreferences("wordsmatch", Context.MODE_PRIVATE) // file yang sama dengan "rekor"

    fun username(ctx: Context): String? = prefs(ctx).getString("username", null)

    fun isLogin(ctx: Context) = username(ctx) != null

    // Saat login, rekor lokal diganti dengan best score milik akun tersebut
    fun login(ctx: Context, username: String, bestScore: Int) {
        prefs(ctx).edit().putString("username", username).putInt("rekor", bestScore).apply()
    }

    fun logout(ctx: Context) {
        prefs(ctx).edit().remove("username").putInt("rekor", 0).apply()
    }
}