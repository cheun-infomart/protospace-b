package in.tech_camp.protospace_b.form;

import org.springframework.web.multipart.MultipartFile;

import in.tech_camp.protospace_b.validation.ValidationOrder;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PrototypeForm {
  @NotBlank(message = "プロトタイプの名称は入力必須です", groups = ValidationOrder.Name1.class)
  private String name;

  @NotBlank(message = "キャッチコピーは入力必須です", groups = ValidationOrder.catchCopy1.class)
  private String catchCopy;

  @NotBlank(message = "コンセプトは入力必須です", groups = ValidationOrder.concept1.class)
  private String concept;

  private MultipartFile image; 
}
