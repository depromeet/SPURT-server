package com.ssak3.timeattack.utils

import io.kotest.data.Row1
import io.kotest.data.row

fun <T> listToRowArray(list: List<T>) : Array<Row1<T>> {
    return list.map { row(it) }.toTypedArray()
}
