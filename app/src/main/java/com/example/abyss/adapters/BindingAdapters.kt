package com.example.abyss.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.abyss.R
import com.google.android.material.textfield.TextInputEditText
import ru.rhanza.constraintexpandablelayout.ExpandableLayout
import ru.rhanza.constraintexpandablelayout.State
import java.text.SimpleDateFormat


@BindingAdapter("hideIfEmpty")
fun hideIfEmpty(textView: TextView, text: String?) {
    if (text != null) {
        if (text.isEmpty()) {
            textView.visibility = View.INVISIBLE
        } else {
            textView.visibility = View.VISIBLE
            textView.text = text
        }
    }
}

@BindingAdapter("hideIfFalse")
fun hideIfFalse(view: View, boolean: Boolean) {
    if (boolean) view.visibility = View.VISIBLE else view.visibility = View.GONE
}

@BindingAdapter("SubIfTrue")
fun SubIfTrue(button: Button, boolean: Boolean) {
    if (boolean) {
        button.background = ContextCompat.getDrawable(button.context, R.drawable.bg_unsubscribe)
        button.text = "Отписаться"
    } else {
        button.background = ContextCompat.getDrawable(button.context, R.drawable.bg_com_btn)
        button.text = "Подписаться"
    }
}

@BindingAdapter("LikeIfTrue")
fun LikeIfTrue(imageView: ImageView, boolean: Boolean) {
    if (boolean) {
        imageView.setImageDrawable(
            ContextCompat.getDrawable(
                imageView.context,
                R.drawable.ic_like_true
            )
        )
    } else {
        imageView.setImageDrawable(
            ContextCompat.getDrawable(
                imageView.context,
                R.drawable.ic_like_false
            )
        )
    }
}

@BindingAdapter("savedIsTrue")
fun savedIsTrue(button: AppCompatButton, boolean: Boolean?) {
    if (boolean == null) {
        button.visibility = View.INVISIBLE
    } else if (boolean) {
        button.background = ContextCompat.getDrawable(button.context, R.drawable.bg_unsubscribe)
        button.text = "Убрать из избранного"
        button.visibility = View.VISIBLE
    } else {
        button.background = ContextCompat.getDrawable(button.context, R.drawable.bg_com_btn)
        button.text = "Сохранить"
        button.visibility = View.VISIBLE
    }
}

@BindingAdapter("savesPost")
fun savesPost(textView: TextView, numbers: Int?) {
    if (numbers != null) {
        textView.visibility = View.VISIBLE
        if (numbers == 0) {
            textView.text = " Нет сохранений"
        } else if (numbers < 1000 && (numbers % 100) > 10 && (numbers % 100) < 15) {
            textView.text = " $numbers сохранений"
        } else if (numbers < 1000 && (numbers % 10) > 0 && (numbers % 10) < 5) {
            when (numbers % 10) {
                1 -> textView.text = " $numbers сохранение"
                2, 3, 4 -> textView.text = " $numbers сохранения"
            }
        } else if (numbers in 10000..999999) {
            textView.text = " ${numbers / 1000} Тыс. сохранений"
        } else if (numbers >= 1000000) {
            textView.text = " ${numbers / 1000000} Млн. сохранений"
        } else {
            textView.text = " $numbers сохранений"
        }
    }else{
        textView.visibility = View.GONE
    }
}

@BindingAdapter("loadImage")
fun loadImage(imageView: ImageView, url: String?) {
    if (!url.isNullOrEmpty()) {
        Glide.with(imageView).load(url).into(imageView)
    } else {
        imageView.setImageResource(R.drawable.ic_user_image_profile)
    }
}


fun ImageView.loadImageStatusTracking(url: String?, onLoadingFinished: () -> Unit = {}) {

    val listener = object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {
            onLoadingFinished()
            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            onLoadingFinished()
            return false
        }
    }

    Glide.with(this)
        .load(url)
        .listener(listener)
        .into(this)
}

fun ImageView.loadImage2(url: String?) {
    Glide.with(this).load(url).into(this)
}

@SuppressLint("SimpleDateFormat")
@BindingAdapter("DateConverter")
fun DateConverter(textView: TextView, date: java.util.Date?) {

    date?.let {
        var format = SimpleDateFormat("dd/MM/yyy")
        val today = format.format(java.util.Date(System.currentTimeMillis()))
        val datePost = format.format(date)

        if (today == datePost) {
            format = SimpleDateFormat("HH:mm:ss")
            textView.text = format.format(date)
        } else {
            textView.text = datePost
        }
    }
}


@SuppressLint("SetTextI18n")
@BindingAdapter("textForNumberViews")
fun textForNumberViews(textView: TextView, numbers: Int) {

    if (numbers == 0) {
        textView.text = " Нет просмотров"
    } else if (numbers < 1000 && (numbers % 100) > 10 && (numbers % 100) < 15) {
        textView.text = " $numbers просмотров"
    } else if (numbers < 1000 && (numbers % 10) > 0 && (numbers % 10) < 5) {
        when (numbers % 10) {
            1 -> textView.text = " $numbers просмотр"
            2, 3, 4 -> textView.text = " $numbers просмотра"
        }
    } else if (numbers in 10000..999999) {
        textView.text = " ${numbers / 1000} Тыс. просмотров"
    } else if (numbers >= 1000000) {
        textView.text = " ${numbers / 1000000} Млн. просмотров"
    } else {
        textView.text = " $numbers просмотров"
    }
}


@SuppressLint("SetTextI18n")
@BindingAdapter("textForNumberLikes")
fun textForNumberLikes(textView: TextView, numbers: Int) {
    if (numbers == 0) {
        textView.text = ""
    } else if (numbers < 10000) {
        textView.text = "$numbers"
    } else if (numbers in 10000..999999) {
        textView.text = "${numbers / 1000} Тыс."
    } else {
        textView.text = "${numbers / 1000000} Млн."
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("textForNumberSubscribers")
fun textForNumberSubscribers(textView: TextView, numbers: Int?) {

    if (numbers == 0 || numbers == null) {
        textView.text = "Нет подписчиков"
    } else if (numbers < 1000 && (numbers % 100) > 10 && (numbers % 100) < 15) {
        textView.text = " $numbers подписчиков"
    } else if (numbers < 1000 && (numbers % 10) > 0 && (numbers % 10) < 5) {
        when (numbers % 10) {
            1 -> textView.text = " $numbers подписчик"
            2, 3, 4 -> textView.text = " $numbers подписчика"
        }
    } else if (numbers in 10000..999999) {
        textView.text = " ${numbers / 1000} Тыс. подписчиков"
    } else if (numbers >= 1000000) {
        textView.text = " ${numbers / 1000000} Млн. подписчиков"
    } else {
        textView.text = " $numbers подписчиков"
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("textForNumberComments")
fun textForNumberComments(textView: TextView, numbers: Int?) {

    if (numbers == 0 || numbers == null) {
        textView.text = "Пока нет комментариев"
    } else if (numbers < 1000 && (numbers % 100) > 10 && (numbers % 100) < 15) {
        textView.text = " $numbers комментариев"
    } else if (numbers < 1000 && (numbers % 10) > 0 && (numbers % 10) < 5) {
        when (numbers % 10) {
            1 -> textView.text = " $numbers комментарий"
            2, 3, 4 -> textView.text = " $numbers комментария"
        }
    } else if (numbers in 10000..999999) {
        textView.text = " ${numbers / 1000} Тыс. комментариев"
    } else if (numbers >= 1000000) {
        textView.text = " ${numbers / 1000000} Млн. комментариев"
    } else {
        textView.text = " $numbers комментариев"
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("textForNumberSubscriptions")
fun textForNumberSubscriptions(textView: TextView, numbers: Int?) {

    if (numbers == 0 || numbers == null) {
        textView.text = "Нет подписок"
    } else if (numbers < 1000 && (numbers % 100) > 10 && (numbers % 100) < 15) {
        textView.text = " $numbers подписок"
    } else if (numbers < 1000 && (numbers % 10) > 0 && (numbers % 10) < 5) {
        when (numbers % 10) {
            1 -> textView.text = " $numbers подписка"
            2, 3, 4 -> textView.text = " $numbers подписки"
        }
    } else if (numbers in 10000..999999) {
        textView.text = " ${numbers / 1000} Тыс. подписок"
    } else if (numbers >= 1000000) {
        textView.text = " ${numbers / 1000000} Млн. подписок"
    } else {
        textView.text = " $numbers подписок"
    }
}



@SuppressLint("SetTextI18n")
fun TextView.textForNotification(
    action: String,
    name: String,
    comment: String?
) {
    when (action) {
        "like" -> {
            this.text = "$name нравится ваша публикация."
        }
        "subscribe" -> {
            this.text = "$name подписался(-ась) на ваш аккаунт."
        }
        "unsubscribe" -> {
            this.text = "$name отписался(-ась) от вашего аккаунта."
        }
        "comment" -> {
            this.text = "$name оставил(а) комментарий под публикацией: $comment"
        }
    }
}

@BindingAdapter("loadImageOrGone")
fun loadImageOrGone(imageView: ImageView, url: String?) {
    if (!url.isNullOrEmpty()) {
        Glide.with(imageView).load(url).into(imageView)
        imageView.visibility = View.VISIBLE
    } else {
        imageView.visibility = View.GONE
    }
}

@BindingAdapter("displayTextForExpandable")
fun displayTextForExpandable(exp: ExpandableLayout, text: String?) {
    if (!text.isNullOrEmpty()) {
        exp.invalidateState(State.Collapsed)
        exp.visibility = View.VISIBLE
    } else {
        exp.visibility = View.GONE
    }
}

@BindingAdapter("getSearchQuery")
fun getSearchQuery(searchView: androidx.appcompat.widget.SearchView, text: String?) {
    if (text != null && text != searchView.query) {
        searchView.setQuery(text, true)
    }
}

@BindingAdapter("addedIsTrue")
fun addedIsTrue(button: Button, boolean: Boolean?) {
    if (boolean == true) {
        button.background = ContextCompat.getDrawable(button.context, R.drawable.bg_unsubscribe)
        button.text = "Убрать"
    } else {
        button.background = ContextCompat.getDrawable(button.context, R.drawable.bg_com_btn)
        button.text = "Добавить"
    }
}

@BindingAdapter("notEnabledIfEmpty")
fun notEnabledIfEmpty(button: AppCompatImageButton, text: String?) {
    button.isEnabled = !text.isNullOrEmpty()
}











