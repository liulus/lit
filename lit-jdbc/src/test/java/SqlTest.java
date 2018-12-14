import org.junit.Test;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;

/**
 * @author liulu
 * @version v1.0
 * date 2018-12-10 13:29
 */
public class SqlTest {


    @Test
    public void test1() {

        String sql = "select * from user where username = :username";

        ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(sql);

        System.out.println(parsedSql.toString());


    }



}
