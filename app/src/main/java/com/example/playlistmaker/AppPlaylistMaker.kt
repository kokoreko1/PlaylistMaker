package com.example.playlistmaker

import android.app.Application

class AppPlaylistMaker: Application() {

    companion object {
        var globalVarSavedSearchText = ""
    }

    override fun onCreate() {
        super.onCreate()
        // initialization code here
    }

}