package life.luosong.community.controller;

import life.luosong.community.dto.NotificationDTO;
import life.luosong.community.enums.NotificationTypeEnum;
import life.luosong.community.model.User;
import life.luosong.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notification/{id}")
    public String profile(@PathVariable(name = "id") Long id,HttpServletRequest request){

        User user = (User) request.getSession().getAttribute("user");

        if(user == null){
            return "redirect:/";
        }

        NotificationDTO notificationDTO = notificationService.read(id,user);

        if(NotificationTypeEnum.REPLY_COMMENT.getType() == notificationDTO.getType() || NotificationTypeEnum.REPLY_QUESTION.getType() == notificationDTO.getType()){
            return "redirect:/question/" + notificationDTO.getOuterid();
        }else{
            return "redirect:/";
        }
    }
}

