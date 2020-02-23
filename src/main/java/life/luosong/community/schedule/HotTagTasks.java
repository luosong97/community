package life.luosong.community.schedule;

import life.luosong.community.cache.HotTagCache;
import life.luosong.community.cache.TagCache;
import life.luosong.community.mapper.QuestionExtMapper;
import life.luosong.community.mapper.QuestionMapper;
import life.luosong.community.model.Question;
import life.luosong.community.model.QuestionExample;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class HotTagTasks {

    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private HotTagCache hotTagCache;

    //定时任务
    @Scheduled(fixedRate = 1000 * 60 * 60 * 3)
    //@Scheduled(cron = "0 0 1 * * *")
    public void hotTagScheduled(){

        int offset = 0;
        int limit  = 20;
        log.info("hotTagScheduled start {}",new Date());

        List<Question> list = new ArrayList<>();
        Map<String, Integer> priorities = new HashMap<>();

        while(offset == 0 || list.size() == limit){
            list = questionMapper.selectByExampleWithRowbounds(new QuestionExample(),new RowBounds(offset,limit));
            for (Question question : list) {
                String[] tags = StringUtils.split(question.getTag(), ",");
                for (String tag : tags) {
                    Integer priority = priorities.get(tag);
                    if(priority != null){
                        priorities.put(tag,priority + 5 + question.getCommentCount());
                    }else {
                        priorities.put(tag, 5 + question.getCommentCount());
                    }
                }
            }
            offset += limit;
        }

        hotTagCache.updateTags(priorities);
        log.info("hotTagScheduled stop {}",new Date());
    }

}
