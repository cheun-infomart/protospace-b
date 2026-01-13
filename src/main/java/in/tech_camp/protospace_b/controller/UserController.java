package in.tech_camp.protospace_b.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class UserController {
  @GetMapping("/user/{id}")
    public String showUserDetailsTest(@PathVariable("id") Long id, Model model) {
        Map<String, Object> user = new HashMap<>();
        user.put("id", id);
        user.put("name", "千世明");
        user.put("profile", "こんにちは！");
        user.put("department", "DevOp");
        user.put("position", "プロント");
        
        List<Map<String, Object>> prototypes = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            Map<String, Object> p = new HashMap<>();
            p.put("name", "タイトル " + i);
            p.put("catchCopy", i + "番テスト");
            prototypes.add(p);
        }


        model.addAttribute("user", user);
        model.addAttribute("prototypes", prototypes);
        return "users/show";
    }
}
