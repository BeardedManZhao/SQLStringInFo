package strInfo.parser;

import strInfo.manager.SQLParserManager;
import strInfo.result.InsertStatement;
import strInfo.result.InsertStatementBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Insert的sql语句解析器，其中包含针对insert语句进行解析的逻辑，解析之后会返回insert的解析结果对象。
 * <p>
 * The SQL statement parser of the Insert, which contains the logic for parsing the insert statement. After parsing, it will return the parsing result object of the insert.
 *
 * @author zhao
 */
public class InsertParser implements SQLParser<InsertStatement, InsertStatementBuilder> {

    /**
     * 其中第一个括号匹配的是表名称，第二个括号匹配的是表字段，第三个括号匹配的是插入模式value或values，第四个括号匹配的是插入的数据
     */
    protected final static Pattern INSERT_PATTERN = Pattern.compile("(?<=insert into )(.*?)\\(([\\s\\S]*?)\\)\\s*(value[s]*)\\s*\\(([\\s\\S]*)\\);", Pattern.CASE_INSENSITIVE);
    private final static String Name = "insert";

    /**
     * 获取到全局唯一的解析器对象，通过该函数，可以从管理者中获取到该解析器对象，如果管理者中不存在，该函数会在管理者中注册一个新组件。
     * <p>
     * Get the globally unique parser object. With this function, you can get the parser object from the manager. If the manager does not exist, this function will register a new component in the manager.
     *
     * @return 解析器对象
     */
    public static InsertParser getInstance() {
        SQLParser<InsertStatement, InsertStatementBuilder> sqlParserByName = SQLParserManager.getSQLParserByName(InsertParser.Name);
        if (sqlParserByName == null) {
            sqlParserByName = new InsertParser();
            SQLParserManager.register(sqlParserByName);
        }
        return (InsertParser) sqlParserByName;
    }

    /**
     * @return 获取到该解析器的名称，该名称还用于该组件在管理者中的注册，因此您也可以通过管理者获取到该组件对象
     * <p>
     * Get the name of the parser, which is also used for the registration of the component in the manager, so you can also get the component object through the manager
     */
    @Override
    public String getName() {
        return InsertParser.Name;
    }

    /**
     * 开始解析一个sql语句
     *
     * @param sql 需要被解析的sql语句
     * @return 解析之后的sql语句对象
     */
    @Override
    public InsertStatement parseSql(String sql) {
        return parseSqlByWord(InsertStatement.builder(), 0, sql, "insert");
    }

    /**
     * 解析一个sql语句中的指定sql解析词相关的子sql语句
     *
     * @param insertStatementBuilder 累计的sql语句结果的建造类
     * @param startIndex             解析词开始出现的索引位置
     * @param sql                    完整的sql语句
     * @param sqlWord                需要被解析的sql解析词
     * @return 解析之后的sql语句
     */
    @Override
    public InsertStatement parseSqlByWord(InsertStatementBuilder insertStatementBuilder, int startIndex, String sql, String sqlWord) {
        if (";".equals(sqlWord)) {
            return insertStatementBuilder.create();
        } else if ("insert".equalsIgnoreCase(sqlWord)) {
            Matcher matcher = INSERT_PATTERN.matcher(sql);
            if (matcher.find()) {
                String valueORvalues = matcher.group(3);
                char last = valueORvalues.charAt(valueORvalues.length() - 1);
                return parseSqlByWord(
                        insertStatementBuilder.setSQL(matcher.group())
                                .setTableName(matcher.group(1))
                                .setFieldStr(matcher.group(2))
                                .setMultiline(last == 's' || last == 'S')
                                .setDataStr(matcher.group(4)),
                        matcher.end(), sql, ";"
                );
            } else {
                throw new RuntimeException("无法解析sql语句：" + sql + ERROR + sqlWord);
            }
        }
        throw new RuntimeException("错误的解析词：" + sqlWord);
    }
}
