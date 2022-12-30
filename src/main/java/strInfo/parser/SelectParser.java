package strInfo.parser;

import strInfo.manager.SQLParserManager;
import strInfo.result.SelectStatement;
import strInfo.result.SelectStatementBuilder;
import strInfo.result.Statement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据库Select语句解析器，该组件能够提供针对Select语句的解析，并将解析及如果封装起来提供给外界。
 * <p>
 * Database Select statement parser. This component can provide the resolution of the Select statement, and provide the resolution and if to the outside world.
 *
 * @author zhao
 */
public class SelectParser implements SQLParser<SelectStatement, SelectStatementBuilder> {

    private final static String Name = "select";

    /**
     * select 字段与表解析 第一个括号为所有字段，第二个括号为所有表，第三个括号为下一个解析词
     */
    private final static Pattern FIELD_AND_TABLE_RESOLUTION = Pattern.compile("select\\s*([()a-zA-Z, 0-9+\\-*/%\\r\\n]+?)\\s*?from\\s*?([a-z_A-Z0-9\\s]*?)(?=(\\(|where|group|order|limit|;))", Pattern.CASE_INSENSITIVE);

    /**
     * 对所有的子查询语句进行替换的静态正则
     */
    private final static Pattern SUB_QUERY_REPLACEMENT_PATTERN = Pattern.compile("\\(select[\\s\\S]*?;\\)", Pattern.CASE_INSENSITIVE);

    /**
     * where 子句解析，其中的第一个括号为子句的where表达式，第二个括号为下一个解析词
     */
    private final static Pattern WHERE_CLAUSE_PARSING = Pattern.compile("(?:on|where)\\s*([\\s\\S]+?)\\s*?(?=(group|order|limit|;))", Pattern.CASE_INSENSITIVE);

    /**
     * group 子句解析，其中第一个括号为子句的group字段，第二个括号为下一个解析词
     */
    private final static Pattern GROUP_CLAUSE_PARSING = Pattern.compile("group *?by\\s*([\\s\\S]+?)\\s*?(?=(order|limit|;))", Pattern.CASE_INSENSITIVE);

    /**
     * 排序 子句解析，其中第一个括号为所有需要排序的字段，第二个括号为排序方式/下一个解析词，第三个括号为下一个解析词
     */
    private final static Pattern ORDER_CLAUSE_PARSING = Pattern.compile("order *?by\\s*([\\s\\S]+?)(desc|asc)\\s*?(?=(limit|;))", Pattern.CASE_INSENSITIVE);

    /**
     * 分页 子句解析，其中第一个括号为所有需要排序的字段，第二个括号为排序方式/下一个解析词，第三个括号为下一个解析词
     */
    private final static Pattern LIMIT_CLAUSE_PARSING = Pattern.compile("limit\\s*([\\s\\S]+?)\\s*?(?=(;))", Pattern.CASE_INSENSITIVE);

    protected SelectParser() {
    }

    /**
     * 获取到全局唯一的解析器对象，通过该函数，可以从管理者中获取到该解析器对象，如果管理者中不存在，该函数会在管理者中注册一个新组件。
     * <p>
     * Get the globally unique parser object. With this function, you can get the parser object from the manager. If the manager does not exist, this function will register a new component in the manager.
     *
     * @return 解析器对象
     */
    public static SelectParser getInstance() {
        SQLParser<SelectStatement, SelectStatementBuilder> sqlParserByName = SQLParserManager.getSQLParserByName(SelectParser.Name);
        if (sqlParserByName == null) {
            sqlParserByName = new SelectParser();
            SQLParserManager.register(sqlParserByName);
        }
        return (SelectParser) sqlParserByName;
    }

    @Override
    public String getName() {
        return SelectParser.Name;
    }

    /**
     * 开始解析一个sql语句
     *
     * @param sql 需要被解析的sql语句
     * @return 解析之后的sql语句对象
     */
    @Override
    public SelectStatement parseSql(String sql) {
        return parseSqlByWord(null, 0, sql, "select");
    }

    /**
     * 解析一个sql语句中的指定sql解析词相关的子sql语句
     *
     * @param selectStatementBuilder 正在被构建的sql语句对象建造者类
     * @param startIndex             解析的sql命令的起始索引位置，该数值会作为解析的起始位置
     * @param sql                    完整的sql语句
     * @param sqlWord                需要被解析的sql解析词
     * @return 解析之后的sql语句
     */
    @Override
    public SelectStatement parseSqlByWord(SelectStatementBuilder selectStatementBuilder, int startIndex, String sql, String sqlWord) {
        if ("SUB_QUERY".equals(sql) || ";".equals(sqlWord)) {
            return selectStatementBuilder.create();
        }
        int wordNum = Statement.getWordNum(sqlWord, SelectStatement.hashNum);
        if (wordNum == SelectStatement.SELECT_WORD) {
            Matcher matcher = FIELD_AND_TABLE_RESOLUTION.matcher(sql);
            if (matcher.find(startIndex)) {
                // 所有字段与表
                String fields = matcher.group(1);
                String tableName = matcher.group(2);
                // 下一个解析词
                String word = matcher.group(3);
                return parseSqlByWord(SelectStatement.builder().setTableName(tableName).setSQL(sql).setSelectStr(fields), matcher.end(), sql, word);
            } else {
                throw new RuntimeException("无法解析sql语句：" + sql);
            }
        } else if ("(".equals(sqlWord)) {
            // 这种情况代表当前要解析的是子查询了
            Matcher matcher1 = SUB_QUERY_REPLACEMENT_PATTERN.matcher(sql);
            if (matcher1.find(startIndex)) {
                {
                    SelectStatement subSelect = parseSqlByWord(null, 1, matcher1.group(), "select");
                    selectStatementBuilder
                            .addSubSelect(subSelect)
                            .addTableName(",")
                            .addTableName(subSelect.getTableName());
                }
                while (matcher1.find()) {
                    SelectStatement subSelect = parseSqlByWord(null, 1, matcher1.group(), "select");
                    selectStatementBuilder
                            .addSubSelect(subSelect)
                            .addTableName(subSelect.getTableName());
                }
            } else {
                throw new RuntimeException("无法解析子查询sql语句：" + sql);
            }
            // 继续解析之前的句子，这里使用的是子查询语句删除
            return parseSqlByWord(
                    selectStatementBuilder, startIndex, matcher1.replaceFirst("SUB_QUERY"), "where"
            );
        } else if (wordNum == SelectStatement.WHERE_WORD) {
            Matcher matcher = WHERE_CLAUSE_PARSING.matcher(sql);
            if (matcher.find(startIndex)) {
                return parseSqlByWord(selectStatementBuilder.setWhereStr(matcher.group(1)), matcher.end(), sql, matcher.group(2));
            } else {
                throw new RuntimeException("无法解析sql语句：" + sql);
            }
        } else if (wordNum == SelectStatement.GROUP_WORD) {
            Matcher matcher = GROUP_CLAUSE_PARSING.matcher(sql);
            if (matcher.find(startIndex)) {
                return parseSqlByWord(selectStatementBuilder.setGroupStr(matcher.group(1)), matcher.end(), sql, matcher.group(2));
            } else {
                throw new RuntimeException("无法解析sql语句：" + sql);
            }
        } else if (wordNum == SelectStatement.ORDER_WORD) {
            Matcher matcher = ORDER_CLAUSE_PARSING.matcher(sql);
            if (matcher.find(startIndex)) {
                return parseSqlByWord(
                        selectStatementBuilder
                                .setOrderStr(matcher.group(1))
                                .setAsc(matcher.group(2).equalsIgnoreCase("asc"))
                        , matcher.end(), sql, matcher.group(3)
                );
            } else {
                throw new RuntimeException("无法解析sql语句：" + sql);
            }
        } else if (wordNum == SelectStatement.LIMIT_WORD) {
            Matcher matcher = LIMIT_CLAUSE_PARSING.matcher(sql);
            if (matcher.find(startIndex)) {
                return parseSqlByWord(selectStatementBuilder.setLimitStr(matcher.group(1)), matcher.end(), sql, matcher.group(2));
            } else {
                throw new RuntimeException("无法解析sql语句：" + sql);
            }
        } else {
            throw new RuntimeException("错误的解析词：" + sqlWord);
        }
    }
}
