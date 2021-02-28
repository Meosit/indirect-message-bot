package by.mksn.indimebot.output

import kotlinx.serialization.Serializable

@Serializable
data class ApiRequest(
    val token: String,
    val message: String
)
