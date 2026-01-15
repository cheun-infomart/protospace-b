package in.tech_camp.protospace_b.controller;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import in.tech_camp.protospace_b.factory.PrototypeFormFactory;
import in.tech_camp.protospace_b.validation.ValidationPriority1;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ActiveProfiles("test")
public class PrototypeFormUnitTest {
  private Validator validator;
  private PrototypeForm prototypeForm;  

  @BeforeEach
    public void setUp() {
      ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
      validator = factory.getValidator();
      prototypeForm = PrototypeFormFactory.createPrototype();
  }

  @Nested
  class プロトタイプが編集できる場合 {
    @Test
    public void すべての項目が入力されていれば編集できる() {
      Set<ConstraintViolation<PrototypeForm>> violations = validator.validate(prototypeForm, ValidationPriority1.class);
      assertEquals(0, violations.size());
    }
    @Test
    public void 各項目が編集前後同じでも編集できる() {
      prototypeForm.setName("新しいプロトタイプ名");
      prototypeForm.setCatchCopy("新しいキャッチコピー");

      Set<ConstraintViolation<PrototypeForm>> violations = validator.validate(prototypeForm, ValidationPriority1.class);
      assertEquals(0, violations.size());
    }
  }

  @Nested
  class プロトタイプが編集できない場合 {
    @Test
    public void プロトタイプ名称が空ではバリデーションエラーが発生する() {
      prototypeForm.setName("");
      Set<ConstraintViolation<PrototypeForm>> violations = validator.validate(prototypeForm, ValidationPriority1.class);
      assertEquals(1, violations.size());
      assertEquals("プロトタイプの名称は入力必須です", violations.iterator().next().getMessage());
    }

    @Test
    public void キャッチコピーが空ではバリデーションエラーが発生する() {
      prototypeForm.setCatchCopy("");
      Set<ConstraintViolation<PrototypeForm>> violations = validator.validate(prototypeForm, ValidationPriority1.class);
      assertEquals(1, violations.size());
      assertEquals("キャッチコピーは入力必須です", violations.iterator().next().getMessage());
    }

    @Test
    public void コンセプトが空ではバリデーションエラーが発生する() {
      prototypeForm.setConcept("");
      Set<ConstraintViolation<PrototypeForm>> violations = validator.validate(prototypeForm, ValidationPriority1.class);
      assertEquals(1, violations.size());
      assertEquals("コンセプトは入力必須です", violations.iterator().next().getMessage());
    }
    @Test
    public void 画像がないとバリデーションエラーが発生する() {
      prototypeForm.setImage(null);
      Set<ConstraintViolation<PrototypeForm>> violations = validator.validate(prototypeForm, ValidationPriority1.class);
      assertEquals(1, violations.size());
      assertEquals("画像を選択してください", violations.iterator().next().getMessage());
}
  }
}
