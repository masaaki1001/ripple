package models.util

import org.specs2.mutable.Specification

class UniqueValidatorSpec extends Specification {
  "validate" should {
    "データがすでに存在する場合はエラー" >> {
      new UniqueValidator(Some(1)).validate must beLeft(List(UniqueValidator.KEY))
    }
    "データが存在しない場合は成功" >> {
      new UniqueValidator(None).validate must beRight
    }
  }
}
