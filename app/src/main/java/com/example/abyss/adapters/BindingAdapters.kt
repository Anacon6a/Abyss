package com.example.abyss.adapters

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.abyss.R
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
//
//@BindingAdapter("loadImage")
//fun loadImage(imageView: ImageView, url: String?) {
//    Picasso.get().load(url).into(imageView)
//}
//
//
//fun ImageView.loadImageStatusTracking(url: String?, onLoadingFinished: () -> Unit = {}) {
//
//    val listener = object : RequestListener<Drawable> {
//        override fun onLoadFailed(
//            e: Picasso?,
//            model: Any?,
//            target: Target<Drawable>?,
//            isFirstResource: Boolean
//        ): Boolean {
//            return false
//        }
//
//        override fun onResourceReady(
//            resource: Drawable?,
//            model: Any?,
//            target: Target<Drawable>?,
//            dataSource: DataSource?,
//            isFirstResource: Boolean
//        ): Boolean {
//            onLoadingFinished()
//            return false
//        }
//    }
//
//    Picasso.get().load(url).noFade().into(this)
//        .into(imageView, new Callback() {
//            @Override
//            public void onSuccess() {
//                supportStartPostponedEnterTransition();
//            }
//
//            @Override
//            public void onError() {
//                supportStartPostponedEnterTransition();
//            }
//        });
//}

@BindingAdapter("loadImage")
fun loadImage(imageView: ImageView, url: String?) {
    Glide.with(imageView).load(url).into(imageView)
}


fun ImageView.loadImageStatusTracking(url: String?, onLoadingFinished: () -> Unit = {}) {

    val listener = object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {
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
        .onlyRetrieveFromCache(true)
//        .dontTransform()
        .listener(listener)
        .into(this)
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
    } else if ( numbers < 1000 && (numbers % 100) > 10 && (numbers % 100) < 15 ) {
        textView.text = " $numbers просмотров"
    } else if (numbers < 1000 && (numbers % 10) > 0 && (numbers % 10) < 5) {
        when (numbers % 10) {
            1 -> textView.text = " $numbers просмотр"
            2, 3, 4 -> textView.text = " $numbers просмотра"
        }
    }else if (numbers in 10000..999999) {
       textView.text = " ${numbers / 1000} Тыс. просмотров"
    }else if (numbers >= 1000000) {
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
    } else if ( numbers < 10000 ) {
        textView.text = "$numbers"
    } else if (numbers in 10000..999999) {
        textView.text = "${numbers / 1000} Тыс."
    } else {
        textView.text = "${numbers / 1000000} Млн."
    }
}