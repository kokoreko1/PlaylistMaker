package com.example.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.core.view.isVisible

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search)

        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        searchEditText.setText((application as AppPlaylistMaker).globalVarSavedSearchText)

        //////////////////////////////////////////////////////
        // кнопка Возврат
        val imageBack = findViewById<ImageView>(R.id.image_back)
        imageBack.setOnClickListener {
            finish()
        }

        //////////////////////////////////////////////////////
        // кнопка Очистки поля inputEditText
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        clearButton.setOnClickListener {
            searchEditText.setText("")
            hideSoftKeyboard(it)
        }

        //////////////////////////////////////////////////////
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