package com.example.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.random.Random

class SearchActivity : AppCompatActivity() {

    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesAPIService: ITunesAPI = retrofit.create(ITunesAPI::class.java)

    private val gson = Gson()
    private lateinit var sharedPrefs: SharedPreferences

    private val tracksAdapter = TracksAdapter(mutableListOf())

    private var tracksHistory: MutableList<Track> = mutableListOf()
    private val tracksAdapterHistory = TracksAdapterHistory(tracksHistory)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search)

        val ivBack = findViewById<ImageView>(R.id.image_back)
        val etSearch = findViewById<EditText>(R.id.searchEditText)
        val btClear = findViewById<ImageView>(R.id.clearIcon)

        val rvTracks = findViewById<RecyclerView>(R.id.recycler_view_tracks)

        val ivNothingWasFound = findViewById<ImageView>(R.id.image_nothing_was_found)
        val tvNothingWasFound = findViewById<TextView>(R.id.text_nothing_was_found)
        val btConnectionProblems = findViewById<Button>(R.id.button_update)

        val vgHistory = findViewById<ViewGroup>(R.id.view_group_history)
        val rvTracksHistory = findViewById<RecyclerView>(R.id.recycler_view_tracks_history)

        // создание экземпляра Shared Preferences
        sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)

        // заполнение поля сохраненной переменной globalVarSavedSearchText
        etSearch.setText((application as AppPlaylistMaker).globalVarSavedSearchText)

        rvTracks.layoutManager = LinearLayoutManager(this)

        rvTracksHistory.layoutManager = LinearLayoutManager(this)
        rvTracksHistory.adapter = tracksAdapterHistory

        // нажатие на кнопку возврат
        ivBack.setOnClickListener {
            finish()
        }

        // нажатие на кнопку "Обновить при проблемах с соединением"
        btConnectionProblems.setOnClickListener {
            // получение трэков с сервера
            searchTracks(etSearch.text.toString())
        }

        // Если фокус устанавливается на поле поиска и текст пустой,
        // тогда отражается группа элементов Поиск
        etSearch.setOnFocusChangeListener { view, hasFocus ->
            vgHistory.visibility =
                if (hasFocus && etSearch.text.isEmpty()) View.VISIBLE else View.GONE
        }

        // Нажатие на кнопку очистки поля поиска
        btClear.setOnClickListener {

            // очищение поля поиска
            etSearch.setText("")

            // скрытие клавиатуры
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)

            // скрытие картинки и текста "Ничего не найдено"
            ivNothingWasFound.visibility = View.GONE
            tvNothingWasFound.visibility = View.GONE

            rvTracks.adapter = tracksAdapter
        }

        // TextWatcher
        val simpleTextWatcher = object : SimpleTextWatcher {

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                // кнопка очистить видна только, если в поле поиска введены символы
                btClear.isVisible = !s.isNullOrEmpty()

                // группа истории выбранных трэков видна только, если фокус установлен
                // на поле поиска и поле поиска пустое
                vgHistory.visibility =
                    if (etSearch.hasFocus() && s?.isEmpty() == true) View.VISIBLE else View.GONE
            }

            // после введения символов сохраним их в переменной
            override fun afterTextChanged(s: Editable?) {
                (application as AppPlaylistMaker).globalVarSavedSearchText = s.toString()
            }

        }

        // слушатель изменения текста в поле поиска
        etSearch.addTextChangedListener(simpleTextWatcher)



        // Нажатие на кнопку Done на клавиатуре появившейся при вводе в поле поиск
        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // получить трэки с сервера
                searchTracks(etSearch.text.toString())
                true
            }
            false
        }

        // Обработка нажатия на картинку трэка в списке rvTracks
        tracksAdapter.onTrackClickListener =
            OnTrackClickListener { track ->
                addTrackInHistoryList(track, tracksHistory)
            }


    }

    // При закрытии активити сохраним Историю выбранных трэков
    override fun onStop() {
        super.onStop()
        saveTracksListHistory(tracksHistory)
    }

    // при смене ориентации экрана сохраняем последнюю строку поиска
    override fun onSaveInstanceState(outState: Bundle) {

        super.onSaveInstanceState(outState)

        val etSearch = findViewById<EditText>(R.id.searchEditText)
        outState.putString(SEARCH_EDIT_TEXT, etSearch.getText().toString())

    }

    // при смене ориентации экрана восстанавливаем последнюю строку поиска
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {

        super.onRestoreInstanceState(savedInstanceState)

        val etSearch = findViewById<EditText>(R.id.searchEditText)
        etSearch.setText(savedInstanceState.getString(SEARCH_EDIT_TEXT,""))

    }


    // Функция получения трэков с сервера и отражения их в списке
    private fun searchTracks(wordToSearch: String) {

        val ivNothingWasFound = findViewById<ImageView>(R.id.image_nothing_was_found)
        val tvNothingWasFound = findViewById<TextView>(R.id.text_nothing_was_found)

        val ivConnectionProblems = findViewById<ImageView>(R.id.image_connection_problems)
        val tvConnectionProblems = findViewById<TextView>(R.id.text_connection_problems)
        val btConnectionProblems = findViewById<Button>(R.id.button_update)

        val rvTracks = findViewById<RecyclerView>(R.id.recycler_view_tracks)

        iTunesAPIService.search(wordToSearch).enqueue(object : Callback<TracksResponse> {

            override fun onResponse(
                call: Call<TracksResponse>,
                response: Response<TracksResponse>
            ) {
                if (response.isSuccessful) {
                    if (response.body()?.results?.size ?: 0 == 0) {
                        // если ничего на сервере не найдено,
                        // показываем сообщение Ничего не найдено
                        showMessage(TYPE_MESSAGE_NOTHING_WAS_FOUND)
                    } else {
                        // если с сервера получены данные,
                        // тогда список трэков делаем видимым,
                        // а вспомогательные сообщения невидимыми
                        rvTracks.isVisible = true
                        val tracksAdapter = TracksAdapter(response.body()?.results)
                        rvTracks.adapter = tracksAdapter

                        ivNothingWasFound.isVisible = false
                        tvNothingWasFound.isVisible = false
                        ivConnectionProblems.isVisible = false
                        tvConnectionProblems.isVisible = false
                        btConnectionProblems.isVisible = false

                    }
                } else {
                    // при ошибках отражаем сообщение Проблемы с соединением
                    showMessage(TYPE_MESSAGE_CONNECTION_PROBLEMS)
                }
            }

            override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                // при сбое отражаем сообщение Проблемы с соединением
                showMessage(TYPE_MESSAGE_CONNECTION_PROBLEMS)
            }

            fun showMessage(tpMessage: Int) {
                when (tpMessage) {
                    TYPE_MESSAGE_CONNECTION_PROBLEMS -> {
                        rvTracks.isVisible = false
                        ivConnectionProblems.isVisible = true
                        tvConnectionProblems.isVisible = true
                        btConnectionProblems.isVisible = true
                    }
                    TYPE_MESSAGE_NOTHING_WAS_FOUND -> {
                        rvTracks.isVisible = false
                        ivNothingWasFound.isVisible = true
                        tvNothingWasFound.isVisible = true
                    }
                }
            }
        })

    }

/////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////

    //region ** funs history list
    private fun addTrackInHistoryList(track: Track, tracksList: MutableList<Track>) {
        tracksList.add(track)
    }

    private fun saveTracksListHistory(tracksList: MutableList<Track>) {
        val json: String = gson.toJson(tracksList)
        sharedPrefs
            .edit()
            .putString(TRACKS_HISTORY, json)
            .apply()
    }

    private fun getLocalTracksHistory(): MutableList<Track>? {

        val json: String? = sharedPrefs.getString(TRACKS_HISTORY, null)

        return if (json != null) {
            val tracksList: MutableList<Track> =
                gson.fromJson(json, object : TypeToken<MutableList<Track>>() {}.type)
            tracksList
        } else {
            null
        }
    }

    private fun getTracksFromServer(): MutableList<Track> {

        val tracksHistory: MutableList<Track> = mutableListOf()

        tracksHistory.add(
            Track(
                "Smells Like Teen Spirit", "Nirvana", 282547,
                "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
            )
        )

        tracksHistory.add(
            Track(
                "Billie Jean", "Michael Jackson", 382547,
                "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
            )
        )

        tracksHistory.add(
            Track(
                "Stayin' Alive", "Bee Gees", 482547,
                "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
            )
        )

        tracksHistory.add(
            Track(
                "Whole Lotta Love", "Led Zeppelin", 582547,
                "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
            )
        )

        tracksHistory.add(
            Track(
                "Sweet Child O'Mine", "Guns N' Roses", 682547,
                "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"
            )
        )

        return tracksHistory
    }

/////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////

    companion object {

        const val SEARCH_EDIT_TEXT = "search_edit_text"

        const val TYPE_MESSAGE_NOTHING_WAS_FOUND = 1
        const val TYPE_MESSAGE_CONNECTION_PROBLEMS = 2

        const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
        const val TRACKS_HISTORY = "tracks_history"

    }

}