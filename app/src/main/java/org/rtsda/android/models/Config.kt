package org.rtsda.android.models

data class Config(
    val id: String,
    val appName: String,
    val churchName: String,
    val churchAddress: String,
    val churchPhone: String,
    val churchEmail: String,
    val churchWebsite: String,
    val privacyPolicy: String,
    val termsOfService: String,
    val jellyfinUrl: String,
    val owncastUrl: String,
    val api_key: ApiKey,
    val created: String,
    val updated: String
)

data class ApiKey(
    val jellyfin_api_key: String
) 