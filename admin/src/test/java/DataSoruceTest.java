import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;


@RestController
public class DataSoruceTest {


    @Autowired
    private DataSource dataSource;

        @Test
        void contextLoads() throws Exception {
            System.out.println("获取的数据库连接为:"+dataSource.getConnection());
        }


    }
