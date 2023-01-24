package strInfo.parser;

import strInfo.manager.SQLParserManager;
import strInfo.result.CreateStatement;
import strInfo.result.CreateStatementBuilder;
import strInfo.result.Statement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据库 create 语句的sql解析器，会将有关该语句的信息解析出来，并封装到一个对象中，提供函数对外界传递数据。
 * <p>
 * The SQL parser of the database create statement will parse the information about the statement, encapsulate it into an object, and provide functions to transfer data to the outside world.
 *
 * @author zhao
 */
public class CreateParser implements SQLParser<CreateStatement, CreateStatementBuilder> {

    private final static String Name = "create";

    /**
     * create 表解析，其中第一个括号是表类型，第二个括号是表名，第三个括号是下一个解析词
     */
    private final static Pattern TABLE_PATTERN = Pattern.compile("(?<=create)\\s*?(temporary\\s*?table|table)\\s*([\\S]+)\\s*?(?=(\\(|as|like))", Pattern.CASE_INSENSITIVE);

    /**
     * 字段解析，其中第0个组是字段数据，第1个括号是下一个解析词
     */
    private final static Pattern FIELD_PATTERN = Pattern.compile("(?<=\\()[\\s\\S]*(?=\\))", Pattern.CASE_INSENSITIVE);

    /**
     * 建表更多信息解析项，其中的第0个组就是建表信息，第1个括号是下一个解析词
     */
    private final static Pattern OPTIONS_PATTERN = Pattern.compile("(?<=\\)[^,])[\\s\\S]*?(?=(;))", Pattern.CASE_INSENSITIVE);

    /**
     * like表解析，其中第0个组是被like的表名，第1个括号是下一个解析词
     */
    private final static Pattern LIKE_PATTERN = Pattern.compile("(?<=like)[\\s\\S]*(?=(;))", Pattern.CASE_INSENSITIVE);

    /**
     * as 子查询语句解析，其中第0组是as的语句，第1个括号是子查询的语句，从select 到结尾的分号，下一个解析词为;
     */
    private final static Pattern AS_PATTERN = Pattern.compile("as\\s*?(select[\\s\\S]*)", Pattern.CASE_INSENSITIVE);

    /**
     * 获取到全局唯一的解析器对象，通过该函数，可以从管理者中获取到该解析器对象，如果管理者中不存在，该函数会在管理者中注册一个新组件。
     * <p>
     * Get the globally unique parser object. With this function, you can get the parser object from the manager. If the manager does not exist, this function will register a new component in the manager.
     *
     * @return 解析器对象
     */
    public static CreateParser getInstance() {
        SQLParser<CreateStatement, CreateStatementBuilder> sqlParserByName = SQLParserManager.getSQLParserByName(CreateParser.Name);
        if (sqlParserByName == null) {
            sqlParserByName = new CreateParser();
            SQLParserManager.register(sqlParserByName);
        }
        return (CreateParser) sqlParserByName;
    }

    /**
     * @return 获取到该解析器的名称，该名称还用于该组件在管理者中的注册，因此您也可以通过管理者获取到该组件对象
     * <p>
     * Get the name of the parser, which is also used for the registration of the component in the manager, so you can also get the component object through the manager
     */
    @Override
    public String getName() {
        return CreateParser.Name;
    }

    /**
     * 开始解析一个sql语句
     *
     * @param sql 需要被解析的sql语句
     * @return 解析之后的sql语句对象
     */
    @Override
    public CreateStatement parseSql(String sql) {
        return parseSqlByWord(CreateStatement.builder(), 0, sql, "create");
    }


    /**
     * 解析一个sql语句中的指定sql解析词相关的子sql语句
     *
     * @param createStatementBuilder 累计的sql语句结果
     * @param startIndex             解析词开始出现的索引位置
     * @param sql                    完整的sql语句
     * @param sqlWord                需要被解析的sql解析词
     * @return 解析之后的sql语句
     */
    @Override
    public CreateStatement parseSqlByWord(CreateStatementBuilder createStatementBuilder, int startIndex, String sql, String sqlWord) {
        if (";".equals(sqlWord)) {
            return createStatementBuilder.create();
        }
        int wordNum = Statement.getWordNum(sqlWord.toLowerCase(), CreateStatement.hashNum);
        if (wordNum == CreateStatement.CREATE_WORD) {
            Matcher matcher = TABLE_PATTERN.matcher(sql);
            if (matcher.find(startIndex)) {
                return parseSqlByWord(
                        createStatementBuilder
                                .setSQL(sql)
                                .setCreateStr(matcher.group())
                                .setTemporary(!matcher.group(1).equalsIgnoreCase("table"))
                                .setTableName(matcher.group(2)),
                        matcher.end(), sql, matcher.group(3)
                );
            } else {
                throw new RuntimeException("无法解析sql语句：" + sql + ERROR + sqlWord);
            }
        } else if (wordNum == CreateStatement.LEFT_BRACKET_WORD) {
            Matcher matcher = FIELD_PATTERN.matcher(sql);
            if (matcher.find(startIndex)) {
                return parseSqlByWord(createStatementBuilder.setFieldStr(matcher.group(0)), matcher.end(), sql, ")");
            } else {
                throw new RuntimeException("无法解析sql语句：" + sql + ERROR + sqlWord);
            }
        } else if (wordNum == CreateStatement.LIKE_WORD) {
            Matcher matcher = LIKE_PATTERN.matcher(sql);
            if (matcher.find(startIndex)) {
                return parseSqlByWord(createStatementBuilder.setLikeStr(matcher.group(0)), matcher.end(), sql, matcher.group(1));
            } else {
                throw new RuntimeException("无法解析sql语句：" + sql + ERROR + sqlWord);
            }
        } else if (wordNum == CreateStatement.RIGHT_BRACKET_WORD) {
            if (startIndex < sql.length()) {
                if (";".equals(sql.substring(startIndex + 1).trim())) {
                    return parseSqlByWord(createStatementBuilder, startIndex, sql, ";");
                } else {
                    Matcher matcher = OPTIONS_PATTERN.matcher(sql);
                    if (matcher.find(startIndex)) {
                        return parseSqlByWord(createStatementBuilder.setOptionsStr(matcher.group(0)), matcher.end(), sql, matcher.group(1));
                    }
                }
                throw new RuntimeException("无法解析sql语句：" + sql + ERROR + sqlWord);
            }
        } else if (wordNum == CreateStatement.AS_WORD) {
            Matcher matcher = AS_PATTERN.matcher(sql);
            if (matcher.find(startIndex)) {
                return parseSqlByWord(
                        createStatementBuilder.setSelectStatement(SelectParser.getInstance().parseSql(matcher.group(1))),
                        matcher.end(), sql, ";"
                );
            }
        }
        throw new RuntimeException("错误的解析词：" + sqlWord);
    }
}
