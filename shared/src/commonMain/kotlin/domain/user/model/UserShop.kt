package domain.user.model

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable


@Serializable
class UserShopUserDTO(
    var firebaseUid: String,
    val deviceId: String,
    var auth: List<UserStoreAuth> = listOf(UserStoreAuth.DataCenter, UserStoreAuth.Inventory),
    val activeCode: String?,
    val email: String? = "",
    val isOwner: Boolean = false,
    var invitedBy: String? = null,
    val hideInTeamList: Boolean = false,
    var inviteValidUntil: LocalDateTime?,
    val displayName: String?,
    val photoUrl: String?,
)


@Serializable
enum class UserStoreAuth {
    DataCenter,
    Inventory,
    Supplier,
    Admin,
    Subscription,
    Owner,
}

val selectableAuth = UserStoreAuth.entries.filterNot { it == UserStoreAuth.Owner }

@Serializable
data class UserStoreDetailsDTO(
    val deviceId: String,
    val storeName: String,
    @Contextual
    val salesToday: BigDecimal,
    val isOnline: Boolean,
    val ngrokOnline: Boolean,
    val lastUpdateTime: LocalDateTime,
    val isOwner: Boolean,
    val auth: List<UserStoreAuth>,
)

@Serializable
data class CreateInviteRequestDTO(
    val deviceId: String,
    val userId: String,
    val auth: List<UserStoreAuth>,
    val targetEmail: String
)

@Serializable
data class AcceptInviteRequestDTO(
    val deviceId: String,
    val activeCode: String,
    val firebaseUid: String
)

@Serializable
data class InviteInfoDTO(
    val deviceId: String,
    val activeCode: String,
)

@Serializable
data class ChangeAuthRequestDTO(
    val deviceId: String,
    val firebaseUid: String,
    val auth: List<UserStoreAuth>
)