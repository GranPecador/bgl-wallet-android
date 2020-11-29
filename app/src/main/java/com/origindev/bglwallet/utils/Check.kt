package com.origindev.bglwallet.utils

class Check {
    companion object {
        fun isPhraseCorrect(phrase: String): Boolean {
            val regex =
                Regex("[a-z]+ [a-z]+ [a-z]+ [a-z]+ [a-z]+ [a-z]+ [a-z]+ [a-z]+ [a-z]+ [a-z]+ [a-z]+ [a-z]+ [a-z]+ [a-z]+ [a-z]+ [a-z]+ [a-z]+ [a-z]+ [a-z]+ [a-z]+ [a-z]+ [a-z]+ [a-z]+ [a-z]+")
            return regex matches phrase
        }
    }
}