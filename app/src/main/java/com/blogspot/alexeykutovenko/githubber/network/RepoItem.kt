package com.blogspot.alexeykutovenko.githubber.network

import com.google.gson.annotations.SerializedName

class RepoItem {
    @SerializedName("name")
    var name: String = ""

    @SerializedName("html_url")
    var html_url: String = ""

    @SerializedName("language")
    var language: String = ""

    @SerializedName("topics")
    var topics: List<String>? = ArrayList()

    @SerializedName("stargazers_count")
    var stargazers_count: Int = 0

}

class Topics {

}
