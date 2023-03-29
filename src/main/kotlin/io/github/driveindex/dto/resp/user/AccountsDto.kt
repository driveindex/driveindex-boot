package io.github.driveindex.dto.resp.user

import java.util.*

data class AccountsDto<T: Any>(
    val id: UUID,
    val displayName: String,
    val userPrincipalName: String,
    val createAt: Long,
    val modifyAt: Long?,
    val detail: T,
)

data class OneDriveAccountDetail(
    val azureUserId: String,
)