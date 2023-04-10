package io.github.driveindex.controller

import io.github.driveindex.client.ClientType
import io.github.driveindex.dto.req.user.*
import io.github.driveindex.dto.resp.RespResult
import io.github.driveindex.dto.resp.SampleResult
import io.github.driveindex.dto.resp.resp
import io.github.driveindex.dto.resp.user.FileListRespDto
import io.github.driveindex.exception.FailedResult
import io.github.driveindex.h2.dao.*
import io.github.driveindex.h2.dao.onedrive.OneDriveFileDao
import io.github.driveindex.h2.entity.FileEntity
import io.github.driveindex.module.Current
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.*

/**
 * @author sgpublic
 * @Date 2023/3/29 上午10:50
 */
@RestController
@Tag(name = "目录接口")
class UserDirController(
    private val fileDao: FileDao,
    private val current: Current,

    private val onedriveFileDao: OneDriveFileDao,
) {
    @PostMapping("/api/user/file/dir")
    fun createDir(@RequestBody dto: CreateDirReqDto): SampleResult {
        val dir = fileDao.getLocalVirtualDir(dto.parent, current.User.id)

        val mkdir = dto.parent.append(dto.name)
        fileDao.save(FileEntity(
            createBy = current.User.id,
            accountId = null,

            name = dto.name,
            parentId = dir.id,
            path = mkdir,
            isDir = true,
            clientType = null,
        ))

        return SampleResult
    }

    @PostMapping("/api/user/file/link")
    fun createLink(@RequestBody dto: CreateLinkReqDto): SampleResult {
        val target = fileDao.findByIdOrNull(dto.target)
            ?: throw FailedResult.Dir.TargetNotFound
        val dir = fileDao.getLocalVirtualDir(dto.parent, current.User.id)

        val name = dto.name ?: target.name
        fileDao.save(FileEntity(
            createBy = current.User.id,
            accountId = null,

            name = name,
            parentId = dir.id,
            path = dir.path.append(name),
            isDir = target.isDir,
            linkTarget = target.id,
            clientType = target.clientType,
        ))

        return SampleResult
    }

    @GetMapping("/api/user/file")
    fun getFile(@RequestBody dto: GetFileReqDto): RespResult<FileListRespDto.Item<*>> {
        val findVirtualByPath = fileDao.findVirtualByPath(dto.path, current.User.id)
        val findById: FileEntity = if (findVirtualByPath != null) {
            val linkTarget = fileDao.findByIdOrNull(findVirtualByPath.linkTarget)
                ?: throw FailedResult.Dir.TargetNotFound
            if (linkTarget.isDir) {
                throw FailedResult.Dir.NotAFile
            }
            // ^ findById
            linkTarget
        } else {
            val top = fileDao.findTopVirtualDir(dto.path, current.User.id)
            val linkTarget = fileDao.findByIdOrNull(top.linkTarget)
                ?: throw FailedResult.Dir.TargetNotFound
            if (!linkTarget.isDir) {
                throw FailedResult.Dir.NotADir
            }
            val targetPath = linkTarget.path.append(dto.path.subPath(top.path.length))
            // ^ findById
            fileDao.findLinkedByPath(targetPath, linkTarget.accountId!!)
                ?: throw FailedResult.Dir.TargetNotFound
        }
        return FileListRespDto.Item(
            name = findById.name,
            createAt = findById.createAt,
            modifyAt = findById.modifyAt,
            isDir = findById.isDir,
            isLink = findById.linkTarget != null,
            type = findById.clientType,
            detail = when (findById.clientType!!) {
                ClientType.OneDrive ->
                    onedriveFileDao.getReferenceById(findById.id).let onedrive@{ byId ->
                        return@onedrive FileListRespDto.Item.OneDrive(
                            webUrl = byId.webUrl,
                            mimeType = byId.mimeType,
                            quickXorHash = byId.quickXorHash,
                            sha1Hash = byId.sha1Hash,
                            sha256Hash = byId.sha256Hash,
                        )
                    }
            }
        ).resp()
    }

    @GetMapping("/api/user/file/list")
    fun getDir(@RequestBody dto: GetDirReqDto): RespResult<FileListRespDto> {
        val findVirtualByPath = fileDao.findVirtualByPath(dto.path, current.User.id)
        val findByParent: List<FileEntity> = if (findVirtualByPath != null) {
            if (findVirtualByPath.linkTarget != null) {
                val linkTarget = fileDao.findByIdOrNull(findVirtualByPath.linkTarget)
                    ?: throw FailedResult.Dir.TargetNotFound
                if (!linkTarget.isDir) {
                    throw FailedResult.Dir.NotADir
                }
                // ^ findByParent
                fileDao.findByParent(findVirtualByPath.linkTarget)
            } else {
                // ^ findByParent
                fileDao.findByParent(findVirtualByPath.id)
            }
        } else {
            val top = fileDao.findTopVirtualDir(dto.path, current.User.id)
            val linkTarget = fileDao.findByIdOrNull(top.linkTarget)
                ?: throw FailedResult.Dir.TargetNotFound
            if (!linkTarget.isDir) {
                throw FailedResult.Dir.NotADir
            }
            val targetPath = linkTarget.path.append(dto.path.subPath(top.path.length))
            val inlinkTarget = fileDao.findLinkedByPath(targetPath, linkTarget.accountId!!)
                ?: throw FailedResult.Dir.TargetNotFound
            // ^ findByParent
            fileDao.findByParent(inlinkTarget.id)
        }

        return FileListRespDto(
            contentSize = findByParent.size,
            content = findByParent.map {
                return@map FileListRespDto.Item(
                    name = it.name,
                    createAt = it.createAt,
                    modifyAt = it.modifyAt,
                    isDir = it.isDir,
                    isLink = it.linkTarget != null,
                    type = it.clientType,
                    detail = when (it.clientType!!) {
                        ClientType.OneDrive ->
                            onedriveFileDao.getReferenceById(it.id).let onedrive@{ byId ->
                                return@onedrive FileListRespDto.Item.OneDrive(
                                    webUrl = byId.webUrl,
                                    mimeType = byId.mimeType,
                                )
                            }
                    }
                )
            }
        ).resp()
    }

    @PostMapping("/api/user/file/delete")
    fun deleteItem(@RequestBody dto: DeleteDirReqDto): SampleResult {
        val dir = fileDao.getLocalVirtualDir(dto.path, current.User.id)
        fileDao.deleteById(dir.id)
        return SampleResult
    }

    @PostMapping("/api/user/file/rename")
    fun renameItem(@RequestBody dto: RenameDirReqDto): SampleResult {
        val dir = fileDao.getLocalVirtualDir(dto.path, current.User.id)
        fileDao.rename(dir.id, dto.name)
        return SampleResult
    }
}