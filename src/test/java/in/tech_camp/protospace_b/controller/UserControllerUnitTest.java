package in.tech_camp.protospace_b.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import in.tech_camp.protospace_b.entity.UserEntity;
import in.tech_camp.protospace_b.factory.UserFactory;
import in.tech_camp.protospace_b.repository.UserRepository;
import in.tech_camp.protospace_b.service.UserService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserControllerUnitTest {
  @Mock
  private UserService userService;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserController userController;

  private UserEntity mockUser;

  @BeforeEach
    public void setUp() {
        mockUser = UserFactory.createMockUser();
    }

  @Test
  public void ログイン機能にリクエストするとログイン画面のビューファイルがレスポンスで返ってくる() {
      String result = userController.showLogin();

      assertThat(result,is("users/login"));
  }

  @Test
  public void ログインに失敗したユーザーに対してエラーメッセージを表示させる(){
    // テスト用の空のModelオブジェクトを作成
    Model model = new ExtendedModelMap();
    // URLパラメータ ?error= の値を想定したダミー文字列
    String errorParam = "true";

    // メソッドを呼び出す
    String result = userController.showLoginWithError(errorParam, model);

    // 返り値のビュー名が正しいか確認
    assertThat(result, is("users/login"));

    // Modelの中に "loginError" という名前でメッセージが格納されているか確認
    assertThat(model.getAttribute("loginError"), is("メールアドレスまたはパスワードが無効です。"));
  }

  @Test
  public void 詳細ページに移動したら適切なView画面が表示される() {
    //userService実行時、mockUserオブジェクト収得
    when(userService.findUserDetail(mockUser.getId())).thenReturn(mockUser);
    //model定義
    Model model = new ExtendedModelMap();
    //Controller実行とresultにその結果を入れる
    String result = userController.showUserDetail(mockUser.getId(), model);
    // 結果View画面が詳細ページなのか確認
    assertThat(result, is("users/show"));
    // ページにmodelがちゃんと渡されてるか確認
    assertThat(model.getAttribute("user"), is(mockUser));
    assertThat(model.getAttribute("prototypes"), is(mockUser.getPrototypes()));
  }

}
