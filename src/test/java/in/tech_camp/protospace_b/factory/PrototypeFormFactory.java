package in.tech_camp.protospace_b.factory;

import org.springframework.mock.web.MockMultipartFile;

import com.github.javafaker.Faker;

import in.tech_camp.protospace_b.form.PrototypeForm;

public class PrototypeFormFactory {
  private static final Faker faker = new Faker();

  public static PrototypeForm createPrototype() {
    PrototypeForm prototypeForm = new PrototypeForm();
    prototypeForm.setName(faker.book().title());
    prototypeForm.setCatchCopy(faker.lorem().sentence(20));
    prototypeForm.setConcept(faker.lorem().paragraph());

    // 画像ファイルの生成
    MockMultipartFile mockImage = new MockMultipartFile(
    "image", 
    "test_image.png", 
    "image/png", 
    "test data".getBytes()
    );

    prototypeForm.setImage(mockImage);
    return prototypeForm;
  }
}
