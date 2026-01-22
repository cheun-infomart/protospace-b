package in.tech_camp.protospace_b.form;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import in.tech_camp.protospace_b.factory.CommentFormFactory;
import in.tech_camp.protospace_b.validation.ValidationOrder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ActiveProfiles("test")
public class CommentFormUnitTest {
  private CommentForm commentForm;
  private Validator validator;

  @BeforeEach
  public void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
    commentForm = CommentFormFactory.createComment();
  }

  @Nested
  class コメント作成ができる場合 {
    @Test
    public void textが存在する場合コメント投稿できる() {
      Set<ConstraintViolation<CommentForm>> violations = validator.validate(commentForm,
          ValidationOrder.concept1.class);
      assertEquals(0, violations.size());
    }
  }

  @Nested
  class コメント作成ができない場合 {
    @Test
    public void textが空の場合バリデーションエラーが発生する() {
      commentForm.setText("");
      Set<ConstraintViolation<CommentForm>> violations = validator.validate(commentForm, ValidationOrder.text1.class);
      assertEquals(1, violations.size());
      assertEquals("コメント入力は必須です", violations.iterator().next().getMessage());
    }
  }

}
