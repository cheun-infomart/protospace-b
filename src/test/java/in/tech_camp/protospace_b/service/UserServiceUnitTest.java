package in.tech_camp.protospace_b.service;

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

import in.tech_camp.protospace_b.entity.UserEntity;
import in.tech_camp.protospace_b.factory.UserFactory;
import in.tech_camp.protospace_b.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserServiceUnitTest {
  @Mock
  private UserRepository userRepository;
  
  @InjectMocks
  private UserService userService;

  private UserEntity mockUser;

  @BeforeEach
  public void setUp() {
    mockUser = UserFactory.createMockUser();
  }

  @Test
  public void ユーザー詳細データを持ってこれてるか() {
    when(userRepository.findByIdWithProto(1)).thenReturn(mockUser);

    UserEntity result = userService.findUserDetail(1);

    assertThat(result, is(mockUser));
    assertThat(result.getName(), is("TestName"));
  }
}
