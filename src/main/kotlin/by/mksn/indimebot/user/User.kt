package by.mksn.indimebot.user

data class User(
    val id: Long,
    val name: String,
    val tokenHash: String,
    val username: String? = null,
    val passphraseHash: String? = null
)