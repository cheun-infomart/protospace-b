package in.tech_camp.protospace_b.service;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.argThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import in.tech_camp.protospace_b.ImageUrl;
import in.tech_camp.protospace_b.entity.UserEntity;
import in.tech_camp.protospace_b.factory.UserFactory;
import in.tech_camp.protospace_b.repository.UserRepository;



@ExtendWith(MockitoExtension.class) // Mockitoを使用するための設定
@ActiveProfiles("test")
public class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository; // 偽物のリポジトリを作成

    @Mock
    private PasswordEncoder passwordEncoder; // 偽物のエンコーダーを作成

    @Mock
    private ImageUrl imageUrl;

    @InjectMocks
    private UserService userService; // 上記のモックを注入したUserServiceを作成

    private UserEntity mockUser;

    @BeforeEach
    public void setUp() {
      mockUser = UserFactory.createMockUser();
    }

    @Test
    public void ユーザー登録時にパスワードが暗号化されて保存されること() throws IOException {
        // 1. 準備 (Given)
        UserEntity user = new UserEntity();
        user.setPassword("rawPassword"); // 暗号化前のパスワードをセット

        // テスト用の画像ファイルを生成
        MockMultipartFile imageFile = new MockMultipartFile(
            "image", "test.png", "image/png", "test data".getBytes()
        );
        
        // モックの挙動を設定
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(imageUrl.getImageUrl()).thenReturn("src/main/resources/static/uploads");

        // 2. 実行 (When)
        userService.createUserWithEncryptedPassword(user, imageFile);

        // 3. 検証 (Then)
        verify(passwordEncoder, times(1)).encode("rawPassword");
        // UserRepositoryのinsertメソッドが、パスワードが書き換わったUserオブジェクトで呼ばれたか確認
        // ここで user.getPassword() が "encodedPassword" になっているかを検証することと同じ意味になります
        verify(userRepository, times(1)).insert(argThat(u -> u.getPassword().equals("encodedPassword")));

        // 画像パスがセットされているか
        assertThat(user.getImage().startsWith("/uploads/"), is(true));
    }

    @Test
    public void ユーザー詳細データを持ってこれてるか() {
      when(userRepository.findByIdWithProto(1)).thenReturn(mockUser);

      UserEntity result = userService.findUserDetail(1);

      assertThat(result, is(mockUser));
      assertThat(result.getName(), is("TestName"));
    }
}
