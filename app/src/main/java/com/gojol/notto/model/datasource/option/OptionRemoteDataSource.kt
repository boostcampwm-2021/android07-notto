package com.gojol.notto.model.datasource.option

import com.gojol.notto.network.GithubResponse

interface OptionRemoteDataSource {

    suspend fun getGitContributors(owner: String, repo: String): Result<GithubResponse>
}
