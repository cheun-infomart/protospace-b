package in.tech_camp.protospace_b.form;

import org.hibernate.validator.constraints.Length;
import org.springframework.validation.BindingResult;

import in.tech_camp.protospace_b.validation.ValidationPriority1;
import in.tech_camp.protospace_b.validation.ValidationPriority2;
import jakarta.validation.constraints.Email;    // 追加
import jakarta.validation.constraints.NotBlank; // 追加
import lombok.Data;

@Data
public class UserForm {
    //メールアドレス
    @NotBlank(message = "メールアドレスは空白にできません", groups = ValidationPriority1.class)
    @Email(message = "メールアドレスは有効である必要があります", groups = ValidationPriority2.class)
    private String email;

    //パスワード
    @NotBlank(message = "パスワードは空白にできません", groups = ValidationPriority1.class)
    @Length(min = 6, max = 128, message = "パスワードは6文字以上必要です", groups = ValidationPriority2.class)
    private String password;

    private String passwordConfirmation;

    public void validatePasswordConfirmation(BindingResult result) {
        if (!password.equals(passwordConfirmation)) {
            result.rejectValue("passwordConfirmation", null, "パスワードの確認がパスワードと一致しません");
        }
    }

    //ユーザー名
    @NotBlank(message = "ユーザー名は空白にできません", groups = ValidationPriority1.class)
    private String name;

    //プロフィール
    @NotBlank(message = "プロフィールは空白にできません", groups = ValidationPriority1.class)
    private String profile;

    //所属
    @NotBlank(message = "所属は空白にできません", groups = ValidationPriority1.class)
    private String department;

    //役職
    @NotBlank(message = "役職は空白にできません", groups = ValidationPriority1.class)
    private String position;

}
