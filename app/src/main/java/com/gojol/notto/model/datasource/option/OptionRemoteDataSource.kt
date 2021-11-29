package com.gojol.notto.model.datasource.option

import com.gojol.notto.common.ERROR_MSG
import network.GithubResponse
import network.GithubService
import java.lang.NullPointerException
import javax.inject.Inject

class OptionRemoteDataSource @Inject constructor(
    private val githubService: GithubService
) : OptionDataSource {

    override fun isPushNotificationChecked(): Boolean {
        throw Exception(ERROR_MSG)
    }

    override fun saveIsPushNotificationChecked(isPushChecked: Boolean) {
        throw Exception(ERROR_MSG)
    }

    override suspend fun getGitContributors(owner: String, repo: String): Result<GithubResponse> {
        return try {
            val result = githubService.getContributors(owner, repo)
            val body = result.body() ?: return Result.failure(Throwable(NullPointerException()))
            Result.success(body)
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }
}
