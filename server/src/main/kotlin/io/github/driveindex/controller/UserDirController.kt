package io.github.driveindex.controller

import io.github.driveindex.dto.req.user.*
import io.github.driveindex.dto.resp.SampleResult
import io.github.driveindex.exception.FailedResult
import io.github.driveindex.h2.dao.*
import io.github.driveindex.h2.entity.FileEntity
import io.github.driveindex.module.Current
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.repository.findByIdOrNull
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
    private val current: Current,
) {
    @PostMapping("/api/user/file/dir")
    fun createDir(@RequestBody dto: CreateDirReqDto): SampleResult {
        val dir = fileDao.getLocalVirtualDir(dto.parent)

        val mkdir = dto.parent.append(dto.name)
        fileDao.save(FileEntity(
            createBy = current.User.id,
            accountId = null,

            name = dto.name,
            parentId = dir.id,
            path = mkdir,
            isDir = true,
        ))

        return SampleResult
    }

    @PostMapping("/api/user/file/link")
    fun createLink(@RequestBody dto: CreateLinkReqDto): SampleResult {
        val target = fileDao.findByIdOrNull(dto.target)
            ?: throw FailedResult.Dir.TargetNotFound
        val dir = fileDao.getLocalVirtualDir(dto.parent)

        val name = dto.name ?: target.name
        fileDao.save(FileEntity(
            createBy = current.User.id,
            accountId = null,

            name = name,
            parentId = dir.id,
            path = dir.path.append(name),
            isDir = target.isDir,
            linkTarget = target.id
        ))

        return SampleResult
    }

    @GetMapping("/api/user/file/dir")
    fun getDir(@RequestBody dto: GetDirReqDto): SampleResult {
        val topVirtual = fileDao.findVirtualDir(dto.path)
        fileDao.findByPath(topVirtual.path)
        return SampleResult
    }

    @DeleteMapping("/api/user/file/dir")
    fun deleteDir(@RequestBody dto: DeleteDirReqDto): SampleResult {
        val dir = fileDao.getLocalVirtualDir(dto.path)
        fileDao.deleteById(dir.id)
        return SampleResult
    }

    @PutMapping("/api/user/file/dir")
    fun renameDir(@RequestBody dto: RenameDirReqDto): SampleResult {
        val dir = fileDao.getLocalVirtualDir(dto.path)
        fileDao.rename(dir.id, dto.name)
        return SampleResult
    }
}