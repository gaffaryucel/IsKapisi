package com.androiddevelopers.freelanceapp.util

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.androiddevelopers.freelanceapp.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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
                android:id="@+id/edittext"/>v
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
                    viewLayout.isErrorEnabled = false
                }
            }
        }

        override fun afterTextChanged(s: Editable?) {}

    })

    viewText.setOnFocusChangeListener { _, hasFocus ->
        run {
            if (!hasFocus && viewText.text.toString().isEmpty()) {
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
            if (s != null && s.length > 5) {
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
            if (!hasFocus && viewText.text.toString().isEmpty()) {
                viewLayout.error = viewLayout.context.getString(R.string.password_error)
            }
        }
    }
}

@BindingAdapter("setHasFixedSize")
fun setHasFixedSize(view: RecyclerView, value: Boolean) {
    view.setHasFixedSize(value)
}

//Bu üç metodu adaptör içinde databinding ile resimleri yüklemek için ekledik
@BindingAdapter("downloadImage")
fun downloadImage(view: ImageView, url: String?) {
    view.imageDownload(url, view.context)
}

fun ImageView.imageDownload(url: String?, context: Context) {
    val options =
        RequestOptions()
            .placeholder(createPlaceholder(context))
            .error(R.drawable.placeholder)

    Glide.with(context).setDefaultRequestOptions(options).load(url).into(this)
}

fun createPlaceholder(context: Context): CircularProgressDrawable {
    return CircularProgressDrawable(context).apply {
        strokeWidth = 8f
        centerRadius = 40f
        start()
    }
}

@BindingAdapter("setVisibility")
fun setVisibility(textView: TextView, text: String?) {
    text?.let {
        if (text.isNotEmpty()) {
            textView.visibility = View.VISIBLE
        } else {
            textView.visibility = View.GONE
        }
    } ?: run {
        textView.visibility = View.GONE
    }

}

@BindingAdapter("setVisibility")
fun setVisibility(textView: TextView, number: Double?) {
    number?.let {
        textView.visibility = View.VISIBLE
    } ?: run {
        textView.visibility = View.GONE
    }
}

@BindingAdapter("setVisibility")
fun setVisibility(textView: TextView, check: Boolean?) {
    check?.let {
        if (check) {
            textView.visibility = View.VISIBLE
        } else {
            textView.visibility = View.GONE
        }
    } ?: run {
        textView.visibility = View.GONE
    }

}

@BindingAdapter("setRatingImage")
fun setRatingImage(imageView: ImageView, rating: Double?) {
    rating?.let {
        when (rating) {
            5.0 -> {
                imageView.setImageResource(R.drawable.rating_star_full)
            }

            0.0 -> {
                imageView.setImageResource(R.drawable.rating_star_empty)
            }

            else -> {
                imageView.setImageResource(R.drawable.rating_star_half)
            }
        }
    } ?: run {
        imageView.setImageResource(R.drawable.rating_star_empty)
    }
}