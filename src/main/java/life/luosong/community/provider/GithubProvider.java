package life.luosong.community.provider;

import com.alibaba.fastjson.JSON;
import life.luosong.community.dto.AccessTokenDTO;
import life.luosong.community.dto.GithubUser;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;


//具体的授权流程及内容可去github官网查看
//此处http插件为okhttp，详细百度
@Component
public class GithubProvider {

    //POST
    public String getAccessToken(AccessTokenDTO accessTokenDTO) {
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(accessTokenDTO));
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            //#DEBUG
            //System.out.println(string);
            String token = string.split("&")[0].split("=")[1];

            return token;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    //GET
    public GithubUser getUser(String accessToken){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/user?access_token=" + accessToken)
                .build();

        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            GithubUser githubUser = JSON.parseObject(string,GithubUser.class);
            return githubUser;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
