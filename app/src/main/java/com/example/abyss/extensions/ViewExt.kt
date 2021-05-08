package com.example.abyss.extensions

import android.view.View


// Слушатели для клика, не позволяющие быстро дважды кликнуть на одну и ту же кнопку
fun View.onClick(listener: View.OnClickListener) = setOnClickListener(OnClick(listener))

fun View.onClick(listener: (View) -> Unit) = setOnClickListener(OnClick(listener))