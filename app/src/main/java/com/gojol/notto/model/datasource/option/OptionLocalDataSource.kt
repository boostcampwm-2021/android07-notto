package com.gojol.notto.model.datasource.option

import com.gojol.notto.common.DAY_PUSH_NOTIFICATION_KEY
import com.gojol.notto.common.ERROR_MSG
import com.gojol.notto.model.SharedPrefManager
import com.gojol.notto.network.GithubResponse
import javax.inject.Inject

class OptionLocalDataSource @Inject constructor(
    private val sharedPreferences: SharedPrefManager
) : OptionDataSource {

    override fun isPushNotificationChecked(): Boolean {
        return sharedPreferences.getBoolean(DAY_PUSH_NOTIFICATION_KEY)
    }

    override fun saveIsPushNotificationChecked(isPushChecked: Boolean) {
        sharedPreferences.setBoolean(DAY_PUSH_NOTIFICATION_KEY, isPushChecked)
    }

    override suspend fun getGitContributors(owner: String, repo: String): Result<GithubResponse> {
        throw Exception(ERROR_MSG)
    }
}
