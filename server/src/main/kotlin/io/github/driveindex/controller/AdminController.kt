package io.github.driveindex.controller

import io.github.driveindex.h2.dao.UserDao
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RestController
@Tag(name = "管理页接口")
class AdminController(
    private val userDao: UserDao
) {
    @PostMapping("/api/admin/user/create")
    fun createUser() {

    }

    @GetMapping("/api/admin/user")
    fun getUsers() {

    }

    @PostMapping("/api/admin/user/edit")
    fun editUser() {

    }

    @PostMapping("/api/admin/user/delete")
    fun deleteUsers() {

    }
}