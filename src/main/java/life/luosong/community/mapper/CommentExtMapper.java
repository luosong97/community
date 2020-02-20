package life.luosong.community.mapper;

import life.luosong.community.model.Comment;
import life.luosong.community.model.CommentExample;
import life.luosong.community.model.Question;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface CommentExtMapper {
    int incCommentCount(Comment comment);
}