package life.luosong.community.controller;

import life.luosong.community.dto.PagenationDTO;
import life.luosong.community.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
public class IndexController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/")
//    @ResponseBody
    public String index(Model model,
                        @RequestParam(name = "page",defaultValue = "1") Integer page,
                        @RequestParam(name = "size",defaultValue = "5") Integer size,
                        @RequestParam(name = "search",required = false) String search) {

        PagenationDTO pagenation = questionService.list(search,page,size) ;
        model.addAttribute("pagenation",pagenation);
        model.addAttribute("search",search);

        return "index";
    }

}
