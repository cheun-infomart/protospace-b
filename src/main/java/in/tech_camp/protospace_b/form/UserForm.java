package in.tech_camp.protospace_b.form;

import org.hibernate.validator.constraints.Length;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import in.tech_camp.protospace_b.validation.ValidationOrder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;    // 追加
import lombok.Data; // 追加

@Data
public class UserForm {
    //メールアドレス
    @NotBlank(message = "メールアドレスは空白にできません", groups = ValidationOrder.Email1.class)
    @Email(message = "メールアドレスは有効である必要があります", groups = ValidationOrder.Email2.class)
    private String email;

    //パスワード
    @NotBlank(message = "パスワードは空白にできません", groups = ValidationOrder.Password1.class)
    @Length(min = 6, max = 128, message = "パスワードは6文字以上必要です", groups = ValidationOrder.Password2.class)
    private String password;

    private String passwordConfirmation;

    public void validatePasswordConfirmation(BindingResult result) {
        if (!password.equals(passwordConfirmation)) {
            result.rejectValue("passwordConfirmation", null, "パスワードの確認がパスワードと一致しません");
        }
    }

    //ユーザー名
    @NotBlank(message = "ユーザー名は空白にできません", groups = ValidationOrder.Name1.class)
    private String name;

    //プロフィール
    @NotBlank(message = "プロフィールは空白にできません", groups = ValidationOrder.Profile1.class)
    private String profile;

    //所属
    @NotBlank(message = "所属は空白にできません", groups = ValidationOrder.Department1.class)
    private String department;

    //役職
    @NotBlank(message = "役職は空白にできません", groups = ValidationOrder.Position1.class)
    private String position;

    //アイコン画像
    private MultipartFile image;
}
