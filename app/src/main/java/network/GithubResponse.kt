package network

import com.google.gson.annotations.SerializedName

class GithubResponse : ArrayList<Contributors>()

data class Contributors(
    @SerializedName("avatar_url") val avatarUrl: String,
    @SerializedName("html_url") val htmlUrl: String,
    @SerializedName("login") val login: String
)
