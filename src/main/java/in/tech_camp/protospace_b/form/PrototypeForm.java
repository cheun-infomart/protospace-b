package in.tech_camp.protospace_b.form;

import org.springframework.web.multipart.MultipartFile;

import in.tech_camp.protospace_b.validation.ValidationPriority1;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PrototypeForm {
  @NotBlank(message = "プロトタイプの名称は入力必須です", groups = ValidationPriority1.class)
  private String name;

  @NotBlank(message = "キャッチコピーは入力必須です", groups = ValidationPriority1.class)
  private String catchCopy;

  @NotBlank(message = "コンセプトは入力必須です", groups = ValidationPriority1.class)
  private String concept;

  private MultipartFile image; 
}
