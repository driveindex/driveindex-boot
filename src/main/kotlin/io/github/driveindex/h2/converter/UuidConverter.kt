package io.github.driveindex.h2.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.util.UUID

@Converter(autoApply = true)
class UuidConverter: AttributeConverter<UUID, String> {
    override fun convertToDatabaseColumn(attribute: UUID): String = attribute.toString()

    override fun convertToEntityAttribute(dbData: String): UUID = UUID.fromString(dbData)
}