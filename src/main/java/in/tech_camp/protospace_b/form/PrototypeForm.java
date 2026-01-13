package in.tech_camp.protospace_b.form;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PrototypeForm {
  @NotBlank(message = "プロトタイプの名称は入力必須です")
  private String name;

  @NotBlank(message = "キャッチコピーは入力必須です")
  private String catchCopy;

  @NotBlank(message = "コンセプトは入力必須です")
  private String concept;

  private MultipartFile image; 
}
