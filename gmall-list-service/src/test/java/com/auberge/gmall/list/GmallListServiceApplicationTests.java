package com.auberge.gmall.list;

import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallListServiceApplicationTests {
    @Autowired
    private JestClient jestClient;

//    @Test
//    void contextLoads() {
//    }
    //测试能否与es连通
    /*
        1.定义dsl语句
        2.定义执行动作
        3.执行动作
        4.获取执行后的结果集
    */
    @Test
    public void testEs() throws IOException {
        String query = "{\n" +
                "  \"query\": {\n" +
                "    \"match\": {\n" +
                "      \"actorList.name\": \"张译\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
        //查询get
        Search search = new Search.Builder(query).addIndex("movie_chn").addType("movie").build();
        //执行动作
        SearchResult searchResult = jestClient.execute(search);
        //获取数据
        List<SearchResult.Hit<Map, Void>> hits = searchResult.getHits(Map.class);
        //循环遍历集合
        for (SearchResult.Hit<Map, Void> hit : hits) {
            Map map = hit.source;
            System.out.println(map);
        }

    }
}
