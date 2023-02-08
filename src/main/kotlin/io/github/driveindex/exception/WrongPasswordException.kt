package io.github.driveindex.exception

import org.springframework.security.authentication.BadCredentialsException

/**
 * @author sgpublic
 * @Date 2023/2/7 15:58
 */
class WrongPasswordException: BadCredentialsException("密码错误")