package in.tech_camp.protospace_b.form;

import in.tech_camp.protospace_b.validation.ValidationOrder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordFindForm {

  @NotBlank(message = "メールアドレスは空白にできません", groups = ValidationOrder.Email1.class)
  @Email(message = "メールアドレスは有効である必要があります", groups = ValidationOrder.Email2.class)
  private String email;

  @NotBlank(message = "質問を選んでください")
  private String securityQuestion;

  @NotBlank(message = "答えを入力してください")
  private String securityAnswer;

}
