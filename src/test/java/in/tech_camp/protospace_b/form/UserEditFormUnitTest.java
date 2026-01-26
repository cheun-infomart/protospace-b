package in.tech_camp.protospace_b.form;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import in.tech_camp.protospace_b.factory.UserFactory;
import in.tech_camp.protospace_b.validation.ValidationOrder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ActiveProfiles("test")
public class UserEditFormUnitTest {
  private Validator validator;
  private UserForm userForm;

  @BeforeEach
  public void setUp(){
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
    userForm = UserFactory.createUser();
  }

  @Nested
  class ユーザー編集が可能な場合{
    @Test
    public void すべての項目が入力されていれば投稿できる(){
      Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, 
        ValidationOrder.Name1.class,
        ValidationOrder.Profile1.class,
        ValidationOrder.Department1.class,
        ValidationOrder.Position1.class);
      assertEquals(0, violations.size());
    }
  }

  @Nested
  public class ユーザー編集ができない場合 {
    @Test
    public void ユーザー名が空ではバリデーションエラーが発生する(){
      userForm.setName("");
      Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationOrder.Name1.class);
      assertEquals(1, violations.size());
    }

    @Test
    public void プロフィールが空ではバリデーションエラーが発生する(){
      userForm.setProfile("");
      Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationOrder.Profile1.class);
      assertEquals(1, violations.size());
    }

    @Test
    public void 部署が空ではバリデーションエラーが発生する(){
      userForm.setDepartment("");
      Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationOrder.Department1.class);
      assertEquals(1, violations.size());
    }

    @Test
    public void 役職が空ではバリデーションエラーが発生する(){
      userForm.setPosition("");
      Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationOrder.Position1.class);
      assertEquals(1, violations.size());
    }
  }
}
