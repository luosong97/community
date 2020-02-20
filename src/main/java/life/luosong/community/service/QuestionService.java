package life.luosong.community.service;

import life.luosong.community.dto.PagenationDTO;
import life.luosong.community.dto.QuestionDTO;
import life.luosong.community.dto.QuestionQueryDTO;
import life.luosong.community.exception.CustomizeErrorCode;
import life.luosong.community.exception.CustomzieException;
import life.luosong.community.mapper.QuestionExtMapper;
import life.luosong.community.mapper.QuestionMapper;
import life.luosong.community.mapper.UserMapper;
import life.luosong.community.model.Question;
import life.luosong.community.model.QuestionExample;
import life.luosong.community.model.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    public PagenationDTO list(String search, Integer page, Integer size) {
        if(StringUtils.isNoneBlank(search)){
            String tags[] = StringUtils.split(search," ");
            String regexTag =  Arrays.stream(tags).collect(Collectors.joining("|"));
            search = Arrays.stream(tags).collect(Collectors.joining("|"));
        }

        Integer totalPage;
        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        questionQueryDTO.setSearch(search);
        Integer totalCount =  questionExtMapper.countBySearch(questionQueryDTO);

        if(totalCount % size == 0) {
            totalPage = totalCount / size;
        }else {
            totalPage = totalCount / size  + 1;
        }

        if(page < 1){
            page = 1;
        }
        if(page > totalPage){
            page = totalPage;
        }

        //数据条数偏移量
        Integer offset = size * (page - 1);
        if(offset < 0){
            offset = 0;
        }

        PagenationDTO pagenationDTO = new PagenationDTO();

        pagenationDTO.setPagenation(totalPage,page);

        if(page < 1){
            page = 1;
        }
        if(page > pagenationDTO.getTotalPage()){
            page = pagenationDTO.getTotalPage();
        }

        QuestionExample example = new QuestionExample();

        QuestionExample questionExample = new QuestionExample();
        questionExample.setOrderByClause("gmt_create desc");

        questionQueryDTO.setPage(offset);
        questionQueryDTO.setSize(size);
        List<Question> questions = questionExtMapper.selectBySearch(questionQueryDTO);
        List<QuestionDTO> questionDTOList = new ArrayList<>();


        for(Question question:questions){
           //通过id去获取user
          User user =  userMapper.selectByPrimaryKey(question.getCreator());
          QuestionDTO questionDTO = new QuestionDTO();
          BeanUtils.copyProperties(question,questionDTO);
          questionDTO.setUser(user);
          questionDTOList.add(questionDTO);
        }
        pagenationDTO.setData(questionDTOList);

        return pagenationDTO;
    }

    public PagenationDTO list( Long userId, Integer page, Integer size) {

        Integer totalPage;

        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria().andCreatorEqualTo(userId);

        Integer totalCount = (int)questionMapper.countByExample(questionExample);


        if(totalCount % size == 0) {
            totalPage = totalCount / size;
        }else {
            totalPage = totalCount / size  + 1;
        }

        if(page < 1){
            page = 1;
        }
        if(page > totalPage){
            page = totalPage;
        }

        Integer offset = size * (page - 1);
        if(offset < 0){
            offset = 0;
        }

        PagenationDTO pagenationDTO = new PagenationDTO();


        pagenationDTO.setPagenation(totalPage,page);

        QuestionExample example = new QuestionExample();
        example.createCriteria().andCreatorEqualTo(userId);
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(example,new RowBounds(offset,size) );
        List<QuestionDTO> questionDTOList = new ArrayList<>();

        for(Question question:questions){
            //通过id去获取user
            User user =  userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        pagenationDTO.setData(questionDTOList);

        return pagenationDTO;
    }

    public QuestionDTO getById(Long id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if(question == null){
            throw new CustomzieException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);
        User user =  userMapper.selectByPrimaryKey(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }

    public void craeteOrUpdate(Question question) {
        if(question.getId() == null){
            //insert
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());

            question.setViewCount(0);
            question.setLikeCount(0);
            question.setCommentCount(0);

            questionMapper.insert(question);
        }else{
            //update
            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setTag(question.getTag());
            updateQuestion.setDescription(question.getDescription());
            QuestionExample example = new QuestionExample();
            example.createCriteria().andIdEqualTo(question.getId());
            int updated = questionMapper.updateByExampleSelective(updateQuestion, example);
            if(updated != 1){
                throw new CustomzieException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }

    public void incView(Long id) {

        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incView(question);
    }

    public List<QuestionDTO> selectRelated(QuestionDTO queryDTO) {
        if(StringUtils.isBlank(queryDTO.getTag())){
            return new ArrayList<>();
        }
        String tags[] = StringUtils.split(queryDTO.getTag(),",");
        String regexTag =  Arrays.stream(tags).collect(Collectors.joining("|"));
        Question question = new Question();
        question.setId(queryDTO.getId());
        question.setTag(regexTag);

        List<Question> questions = questionExtMapper.selectRelated(question);
        List<QuestionDTO> questionDTOS = questions.stream().map(q -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q,questionDTO);
            return questionDTO;
        }).collect(Collectors.toList());

        return questionDTOS;
    }
}
