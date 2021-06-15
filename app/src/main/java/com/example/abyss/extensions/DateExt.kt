package com.example.abyss.extensions

import java.util.*

/**
 * Возвращает Timestamp секундного формата
 * */
val Date.secondTimestamp get() = time / 1000f

/**
 * Альтернативный конструктор для получения создания даты из секундного TimeStamp типа float
 * */
fun Date(n: Float): Date = Date((n * 1000).toLong())