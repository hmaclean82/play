package hmac.play.models

import com.google.gson.annotations.SerializedName

data class Post(
    @SerializedName("userId") val userId: Long?,
    @SerializedName("id") val id: Long?,
    @SerializedName("title") val title: String?,
    @SerializedName("body") val body: String?
)