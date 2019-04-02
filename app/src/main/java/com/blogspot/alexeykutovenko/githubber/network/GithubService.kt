package com.blogspot.alexeykutovenko.githubber.network

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubService {
    @GET("/users/{user}/repos?per_page=100")
    @Headers("Accept: application/vnd.github.mercy-preview+json")
    fun getReposAsync(@Path("user") user: String, @Query("page") page: String): Deferred<Response<List<RepoItem>>>
}