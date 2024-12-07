package com.example.storyapp.presentation.auth.custom

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.example.storyapp.R

class ValidationEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {

    companion object {
        private const val NAME = 0
        private const val EMAIL = 1
        private const val PASSWORD = 2
    }

    private var validationType: Int = -1

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ValidationEditText,
            0, 0
        ).apply {
            try {
                validationType = getInt(R.styleable.ValidationEditText_validationType, -1)
            } finally {
                recycle()
            }
        }

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                validateInput(s.toString())
            }
        })
    }

    private fun validateInput(input: String) {
        when (validationType) {
            NAME -> {
                if (!input.matches("^[a-zA-Z\\s]*$".toRegex())) {
                    error = "Nama hanya boleh mengandung huruf"
                }
            }
            EMAIL -> {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                    error = "Format email tidak valid"
                }
            }
            PASSWORD -> {
                if (input.length < 8) {
                    error = "Password harus memiliki minimal 8 karakter"
                } else {
                    error = null
                }
            }
        }
    }
}
