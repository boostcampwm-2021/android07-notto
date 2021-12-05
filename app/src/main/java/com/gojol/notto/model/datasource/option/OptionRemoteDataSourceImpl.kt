package com.gojol.notto.model.datasource.option

import com.gojol.notto.network.GithubResponse
import com.gojol.notto.network.GithubService
import java.lang.NullPointerException
import javax.inject.Inject

class OptionRemoteDataSourceImpl @Inject constructor(
    private val githubService: GithubService
) : OptionRemoteDataSource {

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
