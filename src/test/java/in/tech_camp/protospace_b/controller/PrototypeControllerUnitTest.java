package in.tech_camp.protospace_b.controller;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Nested;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import in.tech_camp.protospace_b.ImageUrl;
import in.tech_camp.protospace_b.factory.PrototypeFormFactory;
import in.tech_camp.protospace_b.form.PrototypeForm;
import in.tech_camp.protospace_b.repository.PrototypeRepository;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class PrototypeControllerUnitTest {
  @Mock
  private PrototypeRepository prototypeRepository;

  @Mock
  private BindingResult bindingResult;

  @Mock
  private MultipartFile multipartFile;

  @Mock
  private ImageUrl imageUrl;

  @InjectMocks
  private PrototypeController prototypeController;

  @Nested
  class プロトコル新規投稿ページ表示機能 {
    @Test
    public void 新規投稿フォーム表示機能にリクエストすると投稿フォームのビューファイルがレスポンスで返ってくる() {
      Model model = new ExtendedModelMap();

      String result = prototypeController.showPrototypeNew(model);

      MatcherAssert.assertThat(result, Matchers.is("prototypes/new"));
    }
  }

  @Nested
  class 新規投稿保存機能 {
    @Test
    public void すべての項目が入力されて保存ボタンが押された場合insertメソッドが呼ばれてトップページにリダイレクトする() {
      PrototypeForm form = PrototypeFormFactory.createPrototype();
      
      // 画像が選択され、エラーも起きていない状態を生成
      Mockito.when(imageUrl.getImageUrl()).thenReturn("src/main/resources/static/uploads");
      Mockito.when(bindingResult.hasErrors()).thenReturn(false);

      Model model = new ExtendedModelMap();
      String result = prototypeController.createPrototype(form, bindingResult, model);

      assertThat(result, is("redirect:/"));
      Mockito.verify(prototypeRepository).insert(Mockito.any());
    }

    @Nested
    class 保存に失敗する場合 {
      @Test
      public void データベースの保存処理でエラーが発生した場合新規投稿ページを返す() throws Exception {
        PrototypeForm form = PrototypeFormFactory.createPrototype();
        form.setImage(multipartFile);

        Mockito.when(multipartFile.getInputStream()).thenReturn(new java.io.ByteArrayInputStream(new byte[0]));
        Mockito.when(multipartFile.getOriginalFilename()).thenReturn("test.png");

        Mockito.when(multipartFile.isEmpty()).thenReturn(false);
        Mockito.when(imageUrl.getImageUrl()).thenReturn("src/main/resources/static/uploads");
        Mockito.when(bindingResult.hasErrors()).thenReturn(false);

        // insertメソッドが呼ばれたら、例外を投げるように設定
        Mockito.doThrow(new RuntimeException("DBエラーが発生しました"))
              .when(prototypeRepository).insert(Mockito.any());

        Model model = new ExtendedModelMap();

        String result = prototypeController.createPrototype(form, bindingResult, model);
        // 例外をcatchした場合、新規投稿画面が返されることを確認
        assertThat(result, is("prototypes/new"));
        
        // insertメソッドが少なくとも1回は呼ばれたことを確認
        Mockito.verify(prototypeRepository).insert(Mockito.any());
      }

      @Test
      public void 画像が空の場合にバリデーションエラーが発生しプロトコル新規投稿画面を返す() {
        PrototypeForm form = PrototypeFormFactory.createPrototype();

        // 画像は空にする
        form.setImage(multipartFile);
        Mockito.when(multipartFile.isEmpty()).thenReturn(true);
        
        // BindingResultにエラーがある状態を生成（hasErrorsがtrueを返すように）
        Mockito.when(bindingResult.hasErrors()).thenReturn(true);

        Model model = new ExtendedModelMap();

        String result = prototypeController.createPrototype(form, bindingResult, model);

        assertThat(result, is("prototypes/new"));
        Mockito.verify(bindingResult).rejectValue(eq("image"), eq("error.image"), anyString());
      }
    }
  }
}
