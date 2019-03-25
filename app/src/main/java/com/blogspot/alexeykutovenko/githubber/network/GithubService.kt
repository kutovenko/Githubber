package com.blogspot.alexeykutovenko.githubber.network

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubService {
    @GET("/posts")
    fun getPostsAsync(): Deferred<Response<List<RepoItem>>>


    @GET("/users/{user}/repos")
    fun getReposAsync(@Path("user") user: String): Deferred<Response<List<RepoItem>>>
}