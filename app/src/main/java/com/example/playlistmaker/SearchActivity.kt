package com.example.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {

    private val iTunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesAPIService: ITunesAPI = retrofit.create(ITunesAPI::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val imageNothingWasFound = findViewById<ImageView>(R.id.image_nothing_was_found)
        val textNothingWasFound = findViewById<TextView>(R.id.text_nothing_was_found)

        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        searchEditText.setText((application as AppPlaylistMaker).globalVarSavedSearchText)

        // кнопка Обновить при проблемах с соединением
        val buttonConnectionProblems = findViewById<Button>(R.id.button_update)
        buttonConnectionProblems.setOnClickListener {
            searchTracks(searchEditText.text.toString())
        }

        // кнопка Возврат
        val imageBack = findViewById<ImageView>(R.id.image_back)
        imageBack.setOnClickListener {
            finish()
        }

        // RecycleView
        val recyclerViewTracks = findViewById<RecyclerView>(R.id.recycler_view_tracks)
        recyclerViewTracks.layoutManager = LinearLayoutManager(this)

        // кнопка Очистки поля inputEditText
        val clearButton = findViewById<ImageView>(R.id.clearIcon)

        clearButton.setOnClickListener {

            searchEditText.setText("")
            hideSoftKeyboard(it)

            imageNothingWasFound.visibility = View.GONE
            textNothingWasFound.visibility = View.GONE

            recyclerViewTracks.adapter = TracksAdapter(mutableListOf())
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

        searchEditText.addTextChangedListener(simpleTextWatcher)


        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTracks(searchEditText.text.toString())
                true
            }
            false
        }

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
        val manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun saveSearchText(s: Editable?) {
        (application as AppPlaylistMaker).globalVarSavedSearchText = s.toString()
    }

    private fun searchTracks(wordToSearch: String){

        val imageNothingWasFound = findViewById<ImageView>(R.id.image_nothing_was_found)
        val textNothingWasFound = findViewById<TextView>(R.id.text_nothing_was_found)

        val imageConnectionProblems = findViewById<ImageView>(R.id.image_connection_problems)
        val textConnectionProblems = findViewById<TextView>(R.id.text_connection_problems)
        val buttonConnectionProblems = findViewById<Button>(R.id.button_update)

        // RecycleView
        val recyclerViewTracks = findViewById<RecyclerView>(R.id.recycler_view_tracks)


        iTunesAPIService.search(wordToSearch).enqueue(object : Callback<TracksResponse>{
            override fun onResponse(call: Call<TracksResponse>, response: Response<TracksResponse>) {

                if (response.isSuccessful) {

                    if (response.body()?.results?.size ?: 0 == 0){

                        recyclerViewTracks.visibility = View.GONE

                        imageNothingWasFound.visibility = View.VISIBLE
                        textNothingWasFound.visibility = View.VISIBLE

                    } else {

                        recyclerViewTracks.visibility = View.VISIBLE

                        imageNothingWasFound.visibility = View.GONE
                        textNothingWasFound.visibility = View.GONE

                        imageConnectionProblems.visibility = View.GONE
                        textConnectionProblems.visibility = View.GONE
                        buttonConnectionProblems.visibility = View.GONE

                        val tracksAdapter = TracksAdapter(response.body()?.results)
                        recyclerViewTracks.adapter = tracksAdapter

                    }

                } else {

                    recyclerViewTracks.visibility = View.GONE

                    imageConnectionProblems.visibility = View.VISIBLE
                    textConnectionProblems.visibility = View.VISIBLE
                    buttonConnectionProblems.visibility = View.VISIBLE

                }
            }

            override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })

    }

}