package in.tech_camp.protospace_b.form;

import in.tech_camp.protospace_b.validation.ValidationOrder;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentForm {
  @NotBlank(message = "コメント入力は必須です", groups = ValidationOrder.text1.class)
  private String text;
}
