package com.example.demo01

data class Data(
    val chart : String,
    val bpm : Double,
    val str : String
){
    constructor(): this("",0.0,"")
}