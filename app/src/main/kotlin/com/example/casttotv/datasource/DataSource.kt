package com.example.casttotv.datasource

import android.content.Context
import com.example.casttotv.R
import com.example.casttotv.models.QuestionAnswer

class DataSource {

    val engines = mapOf(
        "google" to "https://www.google.com/search?q=",
        "bing" to "www.bing.com/search?q=",
        "duckduckgo" to "duckduckgo.com/?q=",
        "searx" to "searx.github.io/searx/search.html?q=",
        "baidu" to "www.baidu.com/s?wd=",
        "qwant" to "www.qwant.com/?q=",
        "ecosia" to "www.ecosia.org/search?q=",
    )


    fun listOfQA(context: Context): List<QuestionAnswer> {
        return listOf(QuestionAnswer(context.getString(R.string.question_1),
            listOf(
                context.getString(R.string.q_answer_1_1),
                context.getString(R.string.q_answer_1_2),
                context.getString(R.string.q_answer_1_3)
            ), R.color.cinnabar),
            QuestionAnswer(context.getString(R.string.question_2),
                listOf(
                    context.getString(R.string.q_answer_1_1),
                    context.getString(R.string.q_answer_1_2),
                    context.getString(R.string.q_answer_1_3)
                ), R.color.pumpkin),
            QuestionAnswer(context.getString(R.string.question_3),
                listOf(
                    context.getString(R.string.q_answer_1_1),
                    context.getString(R.string.q_answer_1_2),
                    context.getString(R.string.q_answer_1_3)
                ), R.color.medium_purple),
            QuestionAnswer(context.getString(R.string.question_4),
                listOf(
                    context.getString(R.string.q_answer_1_1),
                    context.getString(R.string.q_answer_1_2),
                    context.getString(R.string.q_answer_1_3)
                ), R.color.bright_sun),
            QuestionAnswer(context.getString(R.string.question_5),
                listOf(
                    context.getString(R.string.q_answer_1_1),
                    context.getString(R.string.q_answer_1_2),
                    context.getString(R.string.q_answer_1_3)
                ), R.color.amethyst),
            QuestionAnswer(context.getString(R.string.question_6),
                listOf(
                    context.getString(R.string.q_answer_1_1),
                    context.getString(R.string.q_answer_1_2),
                    context.getString(R.string.q_answer_1_3)
                ), R.color.mountain_meadow),
            QuestionAnswer(context.getString(R.string.question_7),
                listOf(
                    context.getString(R.string.q_answer_1_1),
                    context.getString(R.string.q_answer_1_2),
                    context.getString(R.string.q_answer_1_3)
                ), R.color.mona_lisa),
            QuestionAnswer(context.getString(R.string.question_8),
                listOf(
                    context.getString(R.string.q_answer_1_1),
                    context.getString(R.string.q_answer_1_2),
                    context.getString(R.string.q_answer_1_3)
                ), R.color.turquoise),
            QuestionAnswer(context.getString(R.string.question_9),
                listOf(
                    context.getString(R.string.q_answer_1_1),
                    context.getString(R.string.q_answer_1_2),
                    context.getString(R.string.q_answer_1_3)
                ), R.color.malachite))
    }

}