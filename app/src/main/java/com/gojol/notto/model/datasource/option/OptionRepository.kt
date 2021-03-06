package com.gojol.notto.model.datasource.option

import com.gojol.notto.network.GithubResponse

interface OptionRepository {

    fun isPushNotificationChecked(): Boolean

    fun saveIsPushNotificationChecked(isPushChecked: Boolean)

    suspend fun getGitContributors(owner: String, repo: String): Result<GithubResponse>
}
