package com.example.abyss.adapters

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("hideIfEmpty")
fun hideIfEmpty(textView: TextView, error: String?) {
    if (error != null) {
        if (error.isEmpty()) {
            textView.visibility = View.INVISIBLE
        } else {
            textView.visibility = View.VISIBLE
            textView.text = error
        }
    }
}

@BindingAdapter("hideIfFalse")
fun hideIfFalse(view: View, boolean: Boolean) {
    if (boolean) view.visibility = View.VISIBLE else view.visibility = View.GONE
}