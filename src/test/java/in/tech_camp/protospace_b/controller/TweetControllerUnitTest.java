package in.tech_camp.protospace_b.controller;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import in.tech_camp.protospace_b.entity.PrototypeEntity;
import in.tech_camp.protospace_b.entity.UserEntity;
import in.tech_camp.protospace_b.repository.PrototypeRepository;
import in.tech_camp.protospace_b.repository.UserRepository;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class TweetControllerUnitTest {
  @Mock
  private PrototypeRepository prototypeRepository;

  @Mock 
  private UserRepository userRepository;

  @InjectMocks
  private PrototypeController prototypeController;

  private Model model;

  @BeforeEach
  public void setUp(){
    model = new ExtendedModelMap();
  }

  @Test
  public void 詳細機能にリクエストするとプロトタイプ詳細表示のビューファイルがレスポンスで返ってくる(){
    PrototypeEntity prototype = new PrototypeEntity();
    Integer prototypeId = 1;
    prototype.setId(prototypeId);


    
    when(prototypeRepository.findById(1)).thenReturn(prototype);
    String result = prototypeController.showTweetDetail(1, model);
    
    assertThat(result, is("prototypes/show"));
  }

  @Test
  public void 詳細機能にリクエストするとレスポンスにプロトタイプとユーザーが存在する(){
    PrototypeEntity prototype = new PrototypeEntity();
    Integer prototypeId = 1;
    prototype.setId(prototypeId);
    
    when(prototypeRepository.findById(1)).thenReturn(prototype);
    String result = prototypeController.showTweetDetail(1, model);
    
    assertThat(result, is("prototypes/show"));

    assertThat(model.getAttribute("prototype"), is(prototype));
  }
}
