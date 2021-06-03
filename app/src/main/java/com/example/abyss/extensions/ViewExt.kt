package com.example.abyss.extensions

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment


// Слушатели для клика, не позволяющие быстро дважды кликнуть на одну и ту же кнопку
fun View.onClick(listener: View.OnClickListener) = setOnClickListener(OnClick(listener))
fun View.onClick(listener: (View) -> Unit) = setOnClickListener(OnClick(listener))

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}
fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}
fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

