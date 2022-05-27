package trashissue.rebage.domain.model

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val photo: String?,
    val token: String
)
