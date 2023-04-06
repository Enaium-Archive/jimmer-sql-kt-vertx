package cn.enaium.vertx.model.input

import cn.enaium.vertx.model.Author
import cn.enaium.vertx.model.Gender
import org.babyfish.jimmer.Input
import org.mapstruct.BeanMapping
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy
import org.mapstruct.factory.Mappers

data class AuthorInput(
  val id: Long?,
  val firstName: String,
  val lastName: String,
  val gender: Gender
) : Input<Author> {

  override fun toEntity(): Author =
    CONVERTER.toAuthor(this)

  @Mapper
  internal interface Converter {

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    fun toAuthor(input: AuthorInput): Author
  }

  companion object {
    @JvmStatic
    private val CONVERTER = Mappers.getMapper(Converter::class.java)
  }
}
