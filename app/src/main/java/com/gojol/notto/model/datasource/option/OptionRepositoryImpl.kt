package com.gojol.notto.model.datasource.option

import com.gojol.notto.network.GithubResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OptionRepositoryImpl @Inject constructor(
    private val optionLocalDataSource: OptionLocalDataSource,
    private val optionRemoteDataSource: OptionRemoteDataSource
) : OptionRepository {

    override fun isPushNotificationChecked(): Boolean {
        return optionLocalDataSource.isPushNotificationChecked()
    }

    override fun saveIsPushNotificationChecked(isPushChecked: Boolean) {
        optionLocalDataSource.saveIsPushNotificationChecked(isPushChecked)
    }

    override suspend fun getGitContributors(owner: String, repo: String): Result<GithubResponse> {
        return optionRemoteDataSource.getGitContributors(owner, repo)
    }
}
