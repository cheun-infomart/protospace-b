package in.tech_camp.protospace_b.form;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import in.tech_camp.protospace_b.factory.PrototypeFormFactory;
import in.tech_camp.protospace_b.validation.ValidationOrder;
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
  class プロトタイプが作成できる場合 {
    @Test
    public void すべての項目が入力されていれば投稿できる() {
      Set<ConstraintViolation<PrototypeForm>> violations = validator.validate(prototypeForm,
          ValidationOrder.Name1.class,
          ValidationOrder.catchCopy1.class,
          ValidationOrder.concept1.class);
      assertEquals(0, violations.size());
    }
  }

  @Nested
  class プロトタイプが作成できない場合 {
    @Test
    public void プロトタイプ名称が空ではバリデーションエラーが発生する() {
      prototypeForm.setName("");
      Set<ConstraintViolation<PrototypeForm>> violations = validator.validate(prototypeForm,
          ValidationOrder.Name1.class);
      assertEquals(1, violations.size());
      assertEquals("プロトタイプの名称は入力必須です", violations.iterator().next().getMessage());
    }

    @Test
    public void キャッチコピーが空ではバリデーションエラーが発生する() {
      prototypeForm.setCatchCopy("");
      Set<ConstraintViolation<PrototypeForm>> violations = validator.validate(prototypeForm,
          ValidationOrder.catchCopy1.class);
      assertEquals(1, violations.size());
      assertEquals("キャッチコピーは入力必須です", violations.iterator().next().getMessage());
    }

    @Test
    public void コンセプトが空ではバリデーションエラーが発生する() {
      prototypeForm.setConcept("");
      Set<ConstraintViolation<PrototypeForm>> violations = validator.validate(prototypeForm,
          ValidationOrder.concept1.class);
      assertEquals(1, violations.size());
      assertEquals("コンセプトは入力必須です", violations.iterator().next().getMessage());
    }
  }
}
