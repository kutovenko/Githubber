package com.blogspot.alexeykutovenko.githubber.network

import com.google.gson.annotations.SerializedName

class RepoItem {
    @SerializedName("name")
    var name: String = ""

    @SerializedName("html_url")
    var html_url: String = ""

    @SerializedName("avatar_url")
    var avatarUrl: String = ""

    @SerializedName("description")
    var description: String = ""
}
