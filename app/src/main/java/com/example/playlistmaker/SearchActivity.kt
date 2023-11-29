package com.example.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import kotlin.random.Random



class SearchActivity : AppCompatActivity() {

    private val tracks: MutableList<Track> = mutableListOf()
    lateinit var adapter: TracksAdapter

    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val itunesService: ItunesAPI = retrofit.create(ItunesAPI::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        searchEditText.setText((application as AppPlaylistMaker).globalVarSavedSearchText)

        // кнопка Возврат
        val imageBack = findViewById<ImageView>(R.id.image_back)
        imageBack.setOnClickListener {
            finish()
        }

        // кнопка Очистки поля inputEditText
        val clearButton = findViewById<ImageView>(R.id.clearIcon)

        clearButton.setOnClickListener {
            searchEditText.setText("")
            hideSoftKeyboard(it)
        }

        // TextWatcher
        val simpleTextWatcher = object : SimpleTextWatcher {

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
            }

            override fun afterTextChanged(s: Editable?) {
                saveSearchText(s)
            }
        }

        //29.11.2023
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // ВЫПОЛНЯЙТЕ ПОИСКОВЫЙ ЗАПРОС ЗДЕСЬ
                true
            }
            false
        }

        searchEditText.addTextChangedListener(simpleTextWatcher)

        // RecycleView
        val recyclerViewTracks = findViewById<RecyclerView>(R.id.recycler_view_tracks)
        recyclerViewTracks.layoutManager = LinearLayoutManager(this)

        //29.11.2023
//        val tracksList: MutableList<Track> = mutableListOf()
//
//        tracksList.add(Track("Smells Like Teen Spirit", "Nirvana", "5:01",
//            "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"))
//
//        tracksList.add(Track("Billie Jean", "Michael Jackson", "4:35",
//            "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"))
//
//        tracksList.add(Track("Stayin' Alive", "Bee Gees", "4:10",
//            "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"))
//
//        tracksList.add(Track("Whole Lotta Love", "Led Zeppelin", "5:33",
//            "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"))
//
//        tracksList.add(Track("Sweet Child O'Mine", "Guns N' Roses", "5:03",
//            "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"))


        val tracksAdapter = TracksAdapter(tracksList)

        recyclerViewTracks.adapter = tracksAdapter

    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        outState.putString("searchEditText", searchEditText.getText().toString())
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        searchEditText.setText(savedInstanceState.getString("searchEditText"))
    }


    private fun hideSoftKeyboard(view: View) {
        val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun saveSearchText(s: Editable?) {
        (application as AppPlaylistMaker).globalVarSavedSearchText = s.toString()
    }
}