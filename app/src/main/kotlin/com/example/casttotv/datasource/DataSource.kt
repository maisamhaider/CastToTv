package com.example.casttotv.datasource

import android.content.Context
import com.example.casttotv.R
import com.example.casttotv.dataclasses.QuestionAnswer
import com.example.casttotv.dataclasses.SearchEngine

class DataSource {

    val engines = mapOf(
        "google" to SearchEngine("Google",
            "https://www.google.com/search?q=",
            R.drawable.ic_google),
        "bing" to SearchEngine("Bing", "www.bing.com/search?q=", R.drawable.ic_bing_logo),
        "duckduckgo" to SearchEngine("Duckduckgo",
            "duckduckgo.com/?q=",
            R.drawable.ic_duck_duck_go),
        "searx" to SearchEngine("Searx",
            "searx.github.io/searx/search.html?q=",
            R.drawable.search_one),
        "baidu" to SearchEngine("Baidu", "www.baidu.com/s?wd=", R.drawable.ic_baidu),
        "qwant" to SearchEngine("Qwant", "www.qwant.com/?q=", R.drawable.ic_qwant),
        "ecosia" to SearchEngine("Ecosia", "www.ecosia.org/search?q=", R.drawable.ic_ecosia),
    )
    val enginesList = listOf(
        SearchEngine("Google",
            "https://www.google.com/search?q=",
            R.drawable.ic_google),
        SearchEngine("Bing", "www.bing.com/search?q=", R.drawable.ic_bing_logo),
        SearchEngine("Duckduckgo",
            "duckduckgo.com/?q=",
            R.drawable.ic_duck_duck_go),
        SearchEngine("Searx",
            "searx.github.io/searx/search.html?q=",
            R.drawable.search_one),
        SearchEngine("Baidu", "www.baidu.com/s?wd=", R.drawable.ic_baidu),
        SearchEngine("Qwant", "www.qwant.com/?q=", R.drawable.ic_qwant),
        SearchEngine("Ecosia", "www.ecosia.org/search?q=", R.drawable.ic_ecosia))

    fun listOfQA(context: Context): List<QuestionAnswer> {
        return listOf(QuestionAnswer(context.getString(R.string.question_1),
            listOf(
                context.getString(R.string.q_answer_1_1),
                context.getString(R.string.q_answer_1_2),
                context.getString(R.string.q_answer_1_3)
            ), R.color.cr_cinnabar),
            QuestionAnswer(context.getString(R.string.question_2),
                listOf(
                    context.getString(R.string.q_answer_2_1),
                    context.getString(R.string.q_answer_2_2)
                ), R.color.cr_pumpkin),
            QuestionAnswer(context.getString(R.string.question_3),
                listOf(
                    context.getString(R.string.q_answer_3_1),
                    context.getString(R.string.q_answer_3_2)
                ), R.color.cr_medium_purple),
            QuestionAnswer(context.getString(R.string.question_4),
                listOf(
                    context.getString(R.string.q_answer_4_1)
                ), R.color.cr_bright_sun),
            QuestionAnswer(context.getString(R.string.question_5),
                listOf(
                    context.getString(R.string.q_answer_5_1),
                    context.getString(R.string.q_answer_5_2)
                ), R.color.cr_amethyst),
            QuestionAnswer(context.getString(R.string.question_6),
                listOf(
                    context.getString(R.string.q_answer_6_1),
                    context.getString(R.string.q_answer_6_2)
                ), R.color.cr_mountain_meadow),
            QuestionAnswer(context.getString(R.string.question_7),
                listOf(
                    context.getString(R.string.q_answer_7_1),
                    context.getString(R.string.q_answer_7_2)
                ), R.color.cr_mona_lisa))
    }

}