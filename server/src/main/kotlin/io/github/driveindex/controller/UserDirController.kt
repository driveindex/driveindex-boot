package io.github.driveindex.controller

import io.github.driveindex.dto.req.user.CreateDirReqDto
import io.github.driveindex.dto.req.user.DeleteDirReqDto
import io.github.driveindex.dto.req.user.GetDirReqDto
import io.github.driveindex.dto.req.user.RenameDirReqDto
import io.github.driveindex.dto.resp.SampleResult
import io.github.driveindex.exception.FailedResult
import io.github.driveindex.h2.dao.*
import io.github.driveindex.h2.entity.FileEntity
import io.github.driveindex.module.Current
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * @author sgpublic
 * @Date 2023/3/29 上午10:50
 */
@RestController
@Tag(name = "目录接口")
class UserDirController(
    private val fileDao: FileDao,
    private val accountsDao: AccountsDao,
    private val current: Current,
) {
    @PostMapping("/api/user/file/dir")
    fun createDir(@RequestBody dto: CreateDirReqDto): SampleResult {
        fileDao.findMyLocalDirByIdAssert(dto.parent, current.User.id)

        fileDao.save(FileEntity(
            createBy = current.User.id,
            accountId = null,

            name = dto.name,
            mimeType = FileEntity.TYPE_LOCAL_DIR,
            parentId = dto.parent,
        ))

        return SampleResult
    }

    @GetMapping("/api/user/file/dir")
    fun getDir(@RequestBody dto: GetDirReqDto): SampleResult {
        fileDao.findMyLocalDirByIdAssert(dto.id, current.User.id)
        fileDao.deleteById(dto.id)
        return SampleResult
    }

    @DeleteMapping("/api/user/file/dir")
    fun deleteDir(@RequestBody dto: DeleteDirReqDto): SampleResult {
        fileDao.findMyLocalDirByIdAssert(dto.id, current.User.id)
        fileDao.deleteById(dto.id)
        return SampleResult
    }

    @PutMapping("/api/user/file/dir")
    fun renameDir(@RequestBody dto: RenameDirReqDto): SampleResult {
        fileDao.findMyLocalDirByIdAssert(dto.id, current.User.id)
        fileDao.rename(dto.id, dto.name)
        return SampleResult
    }
}