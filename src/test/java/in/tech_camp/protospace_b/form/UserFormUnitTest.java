package in.tech_camp.protospace_b.form;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.BindingResult;

import in.tech_camp.protospace_b.validation.ValidationPriority1;
import in.tech_camp.protospace_b.validation.ValidationPriority2;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ActiveProfiles("test")
public class UserFormUnitTest {
    private UserForm userForm;
    private Validator validator;
    private BindingResult bindingResult;

    @BeforeEach
    public void setUp(){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        // テスト用に値をセット
        userForm = new UserForm();
        userForm.setName("テストユーザー");
        userForm.setEmail("test@example.com");
        userForm.setPassword("password123");
        userForm.setPasswordConfirmation("password123");
        userForm.setProfile("プロフィールです");
        userForm.setDepartment("開発部");
        userForm.setPosition("マネージャー");

        bindingResult = mock(BindingResult.class);
    }

    @Nested
    class 新規登録ができる場合 {
        @Test
        public void emailとpasswordとpasswordConfirmationとnameとprofileとdepartmentとpositionが存在すれば登録できる() {
            Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationPriority1.class);
            assertEquals(0, violations.size());
        }
    }

    @Nested
    class 新規登録ができない場合 {
        //フォーム空の確認
        @Test
        public void emailが空の場合バリデーションエラーが発生する() {
            userForm.setEmail("");
            Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationPriority1.class);
            assertEquals(1, violations.size());
            assertEquals("メールアドレスは空白にできません", violations.iterator().next().getMessage());
        }
        
        @Test
        public void passwordが空の場合バリデーションエラーが発生する() {
            userForm.setPassword("");
            Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationPriority1.class);
            assertEquals(1, violations.size());
            assertEquals("パスワードは空白にできません", violations.iterator().next().getMessage());
        }

        @Test
        public void nameが空の場合バリデーションエラーが発生する() {
            userForm.setName("");
            Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationPriority1.class);
            assertEquals(1, violations.size());
            assertEquals("ユーザー名は空白にできません", violations.iterator().next().getMessage());
        }

        @Test
        public void profileが空の場合バリデーションエラーが発生する() {
            userForm.setProfile("");
            Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationPriority1.class);
            assertEquals(1, violations.size());
            assertEquals("プロフィールは空白にできません", violations.iterator().next().getMessage());
        }

        @Test
        public void departmentが空の場合バリデーションエラーが発生する() {
            userForm.setDepartment("");
            Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationPriority1.class);
            assertEquals(1, violations.size());
            assertEquals("所属は空白にできません", violations.iterator().next().getMessage());
        }

        @Test
        public void positionが空の場合バリデーションエラーが発生する() {
            userForm.setPosition("");
            Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationPriority1.class);
            assertEquals(1, violations.size());
            assertEquals("役職は空白にできません", violations.iterator().next().getMessage());
        }

        //個別確認
        //パスワードが6文字以上の入力が必要
        @Test
        public void passwordが5文字以下ではバリデーションエラーが発生する() {
            String password = "a".repeat(5);
            userForm.setPassword(password); // 短すぎるパスワード
            Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationPriority2.class);
            violations.forEach(violation -> System.out.println(violation.getMessage()));
            assertEquals(1, violations.size());
            assertEquals("パスワードは6文字以上必要です", violations.iterator().next().getMessage());
        }

        //確認用パスワードの一致
        @Test
        public void passwordとpasswordConfirmationが不一致ではバリデーションエラーが発生する() {
            //  異なるパスワードを設定
            userForm.setPasswordConfirmation("differentPassword");

            //  バリデーションメソッドを呼び出す
            userForm.validatePasswordConfirmation(bindingResult);

            //  結果とエラーメッセージの一致を確認
            verify(bindingResult).rejectValue("passwordConfirmation", null, "パスワードの確認がパスワードと一致しません");
        }

        //パスワードは確認用を含めて2回入力が必要
        @Test
        public void パスワードは確認用を含めて2回入力しない場合エラーが発生する() {
            // パスワードだけ入力し、確認用は空にする
            userForm.setPassword("password123");
            userForm.setPasswordConfirmation("");

            // バリデーションメソッドを実行
            userForm.validatePasswordConfirmation(bindingResult);

            // 一致しない（空である）ためにエラーが発生することを確認
            verify(bindingResult).rejectValue("passwordConfirmation", null, "パスワードの確認がパスワードと一致しません");
        }

        //メールアドレスは、@を含む必要がある
        @Test
        public void emailはアットマークを含まないとバリデーションエラーが発生する() {
            userForm.setEmail("invalidEmail"); // 無効なメール
            Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationPriority2.class);
            assertEquals(1, violations.size());
            assertEquals("メールアドレスは有効である必要があります", violations.iterator().next().getMessage());
        }

    }
    
}
