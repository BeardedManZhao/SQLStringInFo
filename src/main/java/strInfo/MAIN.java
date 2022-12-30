package strInfo;

import strInfo.parser.AlterParser;
import strInfo.parser.CreateParser;
import strInfo.parser.InsertParser;
import strInfo.parser.SelectParser;
import strInfo.result.AlterStatement;
import strInfo.result.CreateStatement;
import strInfo.result.InsertStatement;
import strInfo.result.SelectStatement;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 测试用例类
 *
 * @author zhao
 */
public class MAIN {
    public static void main(String[] args) throws InterruptedException {
        String sql = "select name, avg(achievement) as a from student join achievement join (select * from sub_zhao;) join (select * from sub_zhao123;) WHERE student.id = achievement.id and true group by name order by a desc limit 1;";
        SelectStatement selectStatement = SelectParser.getInstance().parseSql(sql);
        System.out.println("查询的表\t：" + selectStatement.getTableName());
        System.out.println("查询的字段\t：" + Arrays.toString(selectStatement.getFieldNames()));
        System.out.println("select 子句\t：" + selectStatement.getStatementStrByWord("select"));
        System.out.println("group 子句\t：" + selectStatement.getStatementStrByWord("group"));
        System.out.println("order 子句\t：" + selectStatement.getStatementStrByWord("order"));
        System.out.println("是否以正序排序\t：" + selectStatement.isAsc());
        System.out.println("查询的表(数组)\t：" + Arrays.toString(selectStatement.getStatementArrayByWord("table")));
        System.out.println("where中的条件(数组)\t：" + Arrays.toString(selectStatement.getStatementArrayByWord("where")));
        System.out.println("被group的字段(数组)\t：" + Arrays.toString(selectStatement.getStatementArrayByWord("group")));
        System.out.println("被order的字段(数组)\t：" + Arrays.toString(selectStatement.getStatementArrayByWord("order")));
        System.out.println("被解析的整个sql语句\t：" + selectStatement.getSqlStr());
        // 获取到所有的子查询语句
        ArrayList<SelectStatement> subQueryList = selectStatement.getSubQueryList();
        for (SelectStatement statement : subQueryList) {
            System.out.println("子查询表：" + statement.getTableName() + "\t子查询语句：" + statement.getSqlStr());
        }

        System.out.println("======================================");
        Thread.sleep(124);

        String createSQL = "" +
                "create temporary table zhao (\n" +
                "    name varchar(20),\n" +
                "    age int\n" +
                ") ENGINE=MyISAM DEFAULT CHARSET=utf8;";

        CreateStatement createStatement = CreateParser.getInstance().parseSql(createSQL);
        System.out.println("创建的表\t：" + createStatement.getTableName());
        System.out.println("是否是临时表\t：" + createStatement.isTemporary());
        System.out.println("表中的字段\t：" + Arrays.toString(createStatement.getFieldNames()));
        System.out.println("创建表时的like表\t：" + createStatement.getStatementStrByWord("create"));
        System.out.println("创建表时的表名称\t：" + createStatement.getStatementStrByWord("table"));
        System.out.println("创建表时指定的字段\t：" + createStatement.getStatementStrByWord("field"));
        System.out.println("创建表时指定的选项\t：" + createStatement.getStatementStrByWord("option"));
        System.out.println("创建表时指定的选项(数组)\t：" + Arrays.toString(createStatement.getStatementArrayByWord("option")));
        System.out.println("创建表时的like表\t：" + createStatement.getStatementStrByWord("like"));
        System.out.println("sql语句数据：" + createStatement.getSqlStr());

        System.out.println("======================================");
        Thread.sleep(124);
        String insertSql = "insert into zhao (name, age) values (\"zhao\", 18), (\"tangyuan\", 19);";
        InsertStatement insertStatement = InsertParser.getInstance().parseSql(insertSql);
        System.out.println("插入数据的目标数据表\t：" + insertStatement.getTableName());
        System.out.println("插入数据的目标字段\t：" + Arrays.toString(insertStatement.getFieldNames()));
        System.out.println("插入的所有字段数据\t：" + insertStatement.getStatementStrByWord("value"));
        System.out.println("插入的所有目标字段(数组)\t：" + Arrays.toString(insertStatement.getStatementArrayByWord("insert")));
        System.out.println("插入的所有字段数据(数组)\t：" + Arrays.toString(insertStatement.getStatementArrayByWord("values")));


        System.out.println("======================================");
        Thread.sleep(124);

//        String sql1 = "alter table zhao1 add test varchar(20);";
//        String sql2 = "alter table zhao1 drop test;";
//        String sql3 = " alter table zhao1 change age age1 int;";
//        String sql4 = "alter table zhao1 rename zhao;";
        String sql5 = "alter table zhao add index indexName (name, age1);";
//        String sql6 = "alter table zhao add primary key (name);";
//        String sql7 = "alter table zhao add unique (name);";
        AlterParser instance = AlterParser.getInstance();
        AlterStatement alterStatement1 = instance.parseSql(sql5);
        System.out.println("alter的语句子句:" + alterStatement1.getStatementStrByWord("alter"));
        System.out.println("alter作用的表名 :" + alterStatement1.getStatementStrByWord("table"));
        System.out.println("alter增加的子句 :" + alterStatement1.getStatementStrByWord("add"));
        System.out.println("alter删除的表名 :" + alterStatement1.getStatementStrByWord("drop"));
        System.out.println("alter增加的索引 :" + alterStatement1.getStatementStrByWord("index"));
        System.out.println("发生变化的列名称 :" + alterStatement1.getStatementStrByWord("field"));
        System.out.println("alter更新后的表 :" + alterStatement1.getStatementStrByWord("rename"));
        System.out.println("alter更新后的列 :" + alterStatement1.getStatementStrByWord("change"));
        System.out.println("alter的修改模式 :" + alterStatement1.getStatementStrByWord("mod"));
        System.out.println("alter的增加模式 :" + alterStatement1.getStatementStrByWord("addINFO"));
        System.out.println(alterStatement1.getSqlStr());
        // 也可以获取到数组形式的数据
        System.out.println("发生变化的列名称 :" + Arrays.toString(alterStatement1.getStatementArrayByWord("field")));
    }
}
