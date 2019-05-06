package hmac.play.models.raw

import com.google.gson.annotations.SerializedName

data class Comment (
    @SerializedName("postId") val postId: Long?,
    @SerializedName("id") val id: Long?,
    @SerializedName("name") val name: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("body") val body: String?
)