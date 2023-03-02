package io.github.driveindex.feigh

import io.github.driveindex.dto.feign.AzureGraphDtoV2_Me
import io.github.driveindex.dto.feign.AzurePortalDtoV1_Token
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam


/**
 * @author sgpublic
 * @Date 2022/8/8 15:46
 */
interface AzurePortalClient {
    @PostMapping(value = ["/{tenant}/oauth2/v2.0/token"])
    fun getToken(
        @PathVariable("tenant") tenant: String,
        @RequestParam("code") code: String,
        @RequestParam("client_secret") clientSecret: String,
        @RequestParam("scope") scope: String = Scope.joinToString("%20"),
        @RequestParam("grant_type") redirectUri: String = "authorization_code"
    ): AzurePortalDtoV1_Token

    @PostMapping(value = ["/{tenant}/oauth2/v2.0/token"])
    fun refreshToken(
        @PathVariable("tenant") tenant: String,
        @RequestParam("client_id") clientId: String,
        @RequestParam("client_secret") clientSecret: String,
        @RequestParam("refresh_token") refreshToken: String,
        @RequestParam("grant_type") redirectUri: String = "refresh_token",
    ): AzurePortalDtoV1_Token

    companion object {
        @JvmStatic
        val Scope: Array<String> = arrayOf(
            "User.Read",
            "Files.ReadWrite",
            "Files.ReadWrite.All",
            "offline_access",
            "Sites.ReadWrite.All",
        )
    }
}

interface AzureGraphClient {
    @PostMapping(value = ["/v1.0/me"])
    fun Me(@RequestHeader("Authorization") token: String): AzureGraphDtoV2_Me
}