package com.example.abyss.extensions

import android.view.View
import androidx.databinding.BindingAdapter


// Слушатели для клика, не позволяющие быстро дважды кликнуть на одну и ту же кнопку
fun View.onClick(listener: View.OnClickListener) = setOnClickListener(OnClick(listener))

fun View.onClick(listener: (View) -> Unit) = setOnClickListener(OnClick(listener))
//@BindingAdapter("onClick2")
//fun onClick2(view: View, listener: View.OnClickListener) = view.setOnClickListener(OnClick(listener))
//@BindingAdapter("onClick2")
//fun onClick2(view: View, listener: (View) -> Unit) = view.setOnClickListener(OnClick(listener))