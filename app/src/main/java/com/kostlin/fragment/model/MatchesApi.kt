package com.kostlin.fragment.model

import retrofit2.http.GET

interface MatchesApi {

    @GET("/v4/soccer/scores/json/Competitions?key=7863411794c14ab4b6f5c885a009e656")
    suspend fun getMatches(): List<Match>
}