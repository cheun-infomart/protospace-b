package in.tech_camp.protospace_b.form;

import in.tech_camp.protospace_b.validation.ValidationPriority1;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentForm {
  @NotBlank(message = "コメント入力は必須です", groups = ValidationPriority1.class)
  private String text;
}
