package com.gojol.notto.network

import com.google.gson.annotations.SerializedName

class GithubResponse : ArrayList<Contributor>()

data class Contributor(
    @SerializedName("avatar_url") val avatarUrl: String,
    @SerializedName("html_url") val htmlUrl: String,
    @SerializedName("login") val login: String
)
