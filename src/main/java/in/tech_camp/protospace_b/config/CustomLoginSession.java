package in.tech_camp.protospace_b.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomLoginSession extends SimpleUrlAuthenticationSuccessHandler {
  private final RequestCache requestCache = new HttpSessionRequestCache();

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    // ログインが必要な画面に飛んだ時、requestを記憶する
    SavedRequest savedRequest = requestCache.getRequest(request, response);
    if (savedRequest != null) {
      getRedirectStrategy().sendRedirect(request, response, savedRequest.getRedirectUrl());
      return;
    }

    // 直接ログインボタンを押した場合
    String prevPage = (String) request.getSession().getAttribute("prevPage");
    if (prevPage != null) {
      request.getSession().removeAttribute("prevPage");
      getRedirectStrategy().sendRedirect(request, response, prevPage);
    } else {
      // エラーの場合indexに移動
      super.onAuthenticationSuccess(request, response, authentication);
    }
  }
}
