package com.androiddevelopers.freelanceapp.util

import android.text.Editable
import android.text.TextWatcher
import androidx.databinding.BindingAdapter
import com.androiddevelopers.freelanceapp.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/*
Bu binding adapter TextInputLayout tipindeki edittext 'lere uygulanabilir
EditText içeriğinin boş olamayacağı konusunda kullanıcıyı bilgilendirmek için kullanılabilir.
TextInputLayout içinde isEmptyCheck="@{bu alana TextInputLayout altında bulunan TextInputEditText 'in ID si girilecek}"

Örnek kullanım;
        <com.google.android.material.textfield.TextInputLayout
            android:id="@+id/edittextLayout"
            isEmptyCheck="@{edittext}">

            <com.google.android.material.textfield.TextInputEditText
                android:id="@+id/edittext"/>
        </com.google.android.material.textfield.TextInputLayout>
*/
@BindingAdapter("isEmptyCheck")
fun isEmptyCheck(viewLayout: TextInputLayout, viewText: TextInputEditText) {
    viewText.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s != null) {
                if (viewLayout.error != null) {
                    viewLayout.error = null
                }
            } else {
                viewLayout.error = viewLayout.context.getString(R.string.text_empty_error)
            }
        }

        override fun afterTextChanged(s: Editable?) {}

    })

    viewText.setOnFocusChangeListener { v, hasFocus ->
        run {
            if (hasFocus && viewText.text.toString().isEmpty()) {
                viewLayout.error = viewLayout.context.getString(R.string.text_empty_error)
            }
        }
    }
}



/*
Bu binding adapter TextInputLayout tipindeki edittext 'lere uygulanabilir
EditText içeriğinin boş ve 6 karakterden küçük olamayacağı konusunda kullanıcıyı bilgilendirmek için kullanılabilir.
TextInputLayout içinde isEmptyCheck="@{bu alana TextInputLayout altında bulunan TextInputEditText 'in ID si girilecek}"

Örnek kullanım;
        <com.google.android.material.textfield.TextInputLayout
            android:id="@+id/edittextLayout"
            passwordCheck="@{edittext}">

            <com.google.android.material.textfield.TextInputEditText
                android:id="@+id/edittext"/>
        </com.google.android.material.textfield.TextInputLayout>
*/
@BindingAdapter("passwordCheck")
fun passwordCheck(viewLayout: TextInputLayout, viewText: TextInputEditText) {
    viewText.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s != null && s.length > 6) {
                if (viewLayout.error != null) {
                    viewLayout.error = null
                }
            } else {
                viewLayout.error = viewLayout.context.getString(R.string.password_error)
            }
        }

        override fun afterTextChanged(s: Editable?) {}

    })

    viewText.setOnFocusChangeListener { _, hasFocus ->
        run {
            if (hasFocus && viewText.text.toString().isEmpty()) {
                viewLayout.error = viewLayout.context.getString(R.string.password_error)
            }
        }
    }
}