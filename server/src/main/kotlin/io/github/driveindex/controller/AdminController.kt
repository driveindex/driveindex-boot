package io.github.driveindex.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RestController
@Tag(name = "管理页接口")
class AdminController {
    @PostMapping("/api/admin/user")
    fun createUser() {

    }

    @GetMapping("/api/admin/user")
    fun getUsers() {

    }

    @PutMapping("/api/admin/user")
    fun editUser() {

    }

    @DeleteMapping("/api/admin/user")
    fun deleteUsers() {

    }
}