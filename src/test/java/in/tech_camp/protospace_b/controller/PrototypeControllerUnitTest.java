package in.tech_camp.protospace_b.controller;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import in.tech_camp.protospace_b.entity.PrototypeEntity;
import in.tech_camp.protospace_b.repository.PrototypeRepository;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class PrototypeControllerUnitTest {
  @Mock
  private PrototypeRepository prototypeRepository;

  @InjectMocks
  private PrototypeController prototypeController;

  @Test
  public void プロトタイプ一覧にリクエストするとプロトタイプ一覧のビューファイルがレスポンスで返ってくる(){
    Model model = new ExtendedModelMap();

    String result = prototypeController.showPrototypes(model);

    assertThat(result,is("index"));
  }

  @Test
  public void プロトタイプ一覧機能にリクエストするとレスポンスに投稿済みのツイートがすべて含まれること(){
    PrototypeEntity prototype1 = new PrototypeEntity();
    prototype1.setId(1);
    prototype1.setName("プロトタイプ名1");
    prototype1.setCatchCopy("キャチコピー1");
    prototype1.setConcept("コンセプト1");
    prototype1.setImage("sample1.png");

    PrototypeEntity prototype2 = new PrototypeEntity();
    prototype2.setId(2);
    prototype2.setName("プロトタイプ名2");
    prototype2.setCatchCopy("キャチコピー2");
    prototype2.setConcept("コンセプト2");
    prototype2.setImage("sample2.png");

    // テストデータをexpectedPrototypeListに格納
    List<PrototypeEntity> expectedPrototypeList = Arrays.asList(prototype1, prototype2);

    // findAllメソッド呼び出し時expectedPrototypeListを返す
    when(prototypeRepository.findAll()).thenReturn(expectedPrototypeList);

    // テスト用のモデルオブジェクトを生成し、showPrototypesに渡す
    Model model = new ExtendedModelMap();
    prototypeController.showPrototypes(model);

    // 実測値prototypesが期待値expectedPrototypeListと一致するか確認
    assertThat(model.getAttribute("prototypes"), is(expectedPrototypeList));
  }
}
