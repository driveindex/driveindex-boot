package io.github.driveindex.h2.converter

import io.github.driveindex.core.util.CanonicalPath
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class CanonicalPathConverter: AttributeConverter<CanonicalPath, String> {
    override fun convertToDatabaseColumn(attribute: CanonicalPath): String = attribute.getPath()

    override fun convertToEntityAttribute(dbData: String): CanonicalPath = CanonicalPath.of(dbData)
}