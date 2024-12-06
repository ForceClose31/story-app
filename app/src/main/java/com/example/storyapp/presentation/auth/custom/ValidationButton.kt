package com.example.storyapp.presentation.auth.custom

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import com.example.storyapp.R

class ValidationButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.buttonStyle
) : AppCompatButton(context, attrs, defStyleAttr) {

    fun monitorFields(vararg fields: ValidationEditText) {
        updateButtonState(fields)
        isEnabled = false

        fields.forEach { field ->
            field.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    updateButtonState(fields)
                }
            })
        }
    }

    private fun updateButtonState(fields: Array<out ValidationEditText>) {
        val allValid = fields.all { it.error == null && (it.text?.isNotBlank() ?: false) }
        isEnabled = allValid

        val backgroundResource = if (allValid) {
            R.drawable.button_background
        } else {
            R.drawable.button_disabled_background
        }
        setBackgroundResource(backgroundResource)
    }
}
