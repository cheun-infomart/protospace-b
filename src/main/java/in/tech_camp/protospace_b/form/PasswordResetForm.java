package in.tech_camp.protospace_b.form;

import org.hibernate.validator.constraints.Length;
import org.springframework.validation.BindingResult;

import in.tech_camp.protospace_b.validation.ValidationOrder;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordResetForm {
  @NotBlank(message = "パスワードは空白にできません", groups = ValidationOrder.Password1.class)
  @Length(min = 6, max = 128, message = "パスワードは6文字以上必要です",
      groups = ValidationOrder.Password2.class)
  private String password;

  private String passwordConfirmation;

  public void validatePasswordConfirmation(BindingResult result) {
    if (!password.equals(passwordConfirmation)) {
      result.rejectValue("passwordConfirmation", null, "パスワードの確認がパスワードと一致しません");
    }
  }
}
