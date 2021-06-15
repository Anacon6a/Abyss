package com.example.abyss.utils

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class ChartValueFormatter : IndexAxisValueFormatter() {
    override fun getFormattedValue(value: Float): String {

        return value.toString().run { substring(0, lastIndex - 1) }
    }
}