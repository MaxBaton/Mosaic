package com.example.mosaic

fun main() {
    val mutableList = mutableListOf(1,2,3,4,5,6,7,8,9,10)
    var newMutableList = mutableList.shuffled()
    println("old list = $mutableList\nnew list = $newMutableList")
    if (newMutableList == mutableList) {
        println("equals")
    }else {
        println("not equals")
    }
    Thread.sleep(1000)
    newMutableList = mutableList
    println("old list = $mutableList\nnew list = $newMutableList")
    if (newMutableList == mutableList) {
        println("equals")
    }else {
        println("not equals")
    }
}