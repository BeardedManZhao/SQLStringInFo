package strInfo;

import strInfo.manager.SQLParserManager;
import strInfo.parser.SelectParser;
import strInfo.result.SelectStatement;

import java.util.Arrays;

/**
 * 测试用例类
 *
 * @author zhao
 */
public class MAIN {
    public static void main(String[] args) {
        System.out.println("version = " + SQLParserManager.VERSION);
        String sql1 = "select sc.CONSUMER_NAME , sb.BRAND_CODE " +
                "from sc_consumer as sc join sc_brand as sb" +
                "where sc.CONSUMER_ID = sb.PRODUCT_LINE and b = 1 or a = a order;";
        SelectStatement selectStatement1 = SelectParser.getInstance().parseSql(sql1);
        System.out.println(Arrays.toString(selectStatement1.getFieldNames()));
        System.out.println(Arrays.toString(selectStatement1.getWhereArray()));
        // 获取到所有表的名称 TODO 将忽略所有别名配置，直接尽可能的找到每一个表名称
        System.out.println(Arrays.toString(selectStatement1.getTables()));
        // 判断是否使用到了别名
        if (selectStatement1.isUseAlias()) {
            // 获取到所有表的别名 TODO 如果没有使用别名将返回空数组
            System.out.println(Arrays.toString(selectStatement1.getTablesByAlias()));
            // 获取到所有表的别名 TODO 如果没有使用别名将返回空数组
            System.out.println(Arrays.toString(selectStatement1.getTablesByRealName()));
        } else {
            System.out.println("NO Alias");
        }
    }
}