package strInfo.parser;

import strInfo.manager.SQLParserManager;
import strInfo.result.AlterStatement;
import strInfo.result.AlterStatementBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据库 alter 语句的sql解析器，会将有关该语句的信息解析出来，并封装到一个对象中，提供函数对外界传递数据。
 * <p>
 * The SQL parser of the database "alter" statement will parse the information about the statement, encapsulate it into an object, and provide functions to transfer data to the outside world.
 *
 * @author zhao
 */
public class AlterParser implements SQLParser<AlterStatement, AlterStatementBuilder> {

    private final static String Name = "alter";

    /**
     * alter 数据提取，其中第1个括号中存储的是修改的表名，第2个括号是修改模式(解析词)
     */
    private final static Pattern ALTER_PATTERN = Pattern.compile("alter\\s+?table\\s*?(\\S*)\\s+?(add|drop|rename|change)", Pattern.CASE_INSENSITIVE);

    /**
     * add 修改模式，第一个括号是添加模式，同时也作为，下一个解析词
     */
    private final static Pattern ADD_PATTERN = Pattern.compile("add\\s+?(\\S+)\\s?", Pattern.CASE_INSENSITIVE);

    /**
     * add 列模式，第一个括号是字段数据，下一个解析词是;
     */
    private final static Pattern ADD_COLUMN_PATTERN = Pattern.compile("add\\s*?([\\s\\S]*?);", Pattern.CASE_INSENSITIVE);

    /**
     * add xxx 模式，第一个括号是索引名称/key/unique ，第二个括号是列名。
     */
    private final static Pattern ADD_INFO_PATTERN = Pattern.compile("add\\s*?\\S+?\\s*?(\\S+)\\s+?\\(([\\s\\S]+?)\\);", Pattern.CASE_INSENSITIVE);

    /**
     * drop 删除模式，第一个括号是需要删除的列，下一个解析词为;
     */
    private final static Pattern DROP_PATTERN = Pattern.compile("drop\\s+?([\\S]*?)\\s*?;", Pattern.CASE_INSENSITIVE);

    /**
     * rename 重命名模式，第一个括号是需要对表重命名的新名字，下一个解析词为;
     */
    private final static Pattern RENAME_PATTERN = Pattern.compile("rename\\s+?([\\S]*?)\\s*?;", Pattern.CASE_INSENSITIVE);

    /**
     * change 列名，第一个括号是需要被重命名的列名，第二个括号为对列重命名之后的新名字，下一个解析词为;
     */
    private final static Pattern CHANGE_PATTERN = Pattern.compile("change\\s+?(\\S+)\\s+?([\\s\\S]+?)\\s*?;", Pattern.CASE_INSENSITIVE);

    /**
     * 获取到全局唯一的解析器对象，通过该函数，可以从管理者中获取到该解析器对象，如果管理者中不存在，该函数会在管理者中注册一个新组件。
     * <p>
     * Get the globally unique parser object. With this function, you can get the parser object from the manager. If the manager does not exist, this function will register a new component in the manager.
     *
     * @return 解析器对象
     */
    public static AlterParser getInstance() {
        SQLParser<AlterStatement, AlterStatementBuilder> sqlParserByName = SQLParserManager.getSQLParserByName(AlterParser.Name);
        if (sqlParserByName == null) {
            sqlParserByName = new AlterParser();
            SQLParserManager.register(sqlParserByName);
        }
        return (AlterParser) sqlParserByName;
    }


    /**
     * @return 获取到该解析器的名称，该名称还用于该组件在管理者中的注册，因此您也可以通过管理者获取到该组件对象
     * <p>
     * Get the name of the parser, which is also used for the registration of the component in the manager, so you can also get the component object through the manager
     */
    @Override
    public String getName() {
        return AlterParser.Name;
    }

    /**
     * 开始解析一个sql语句
     *
     * @param sql 需要被解析的sql语句
     * @return 解析之后的sql语句对象
     */
    @Override
    public AlterStatement parseSql(String sql) {
        return parseSqlByWord(AlterStatement.builder(), 0, sql, "alter");
    }

    /**
     * 解析一个sql语句中的指定sql解析词相关的子sql语句
     *
     * @param StatementBuilder 累计的sql语句结果的建造类
     * @param startIndex       解析词开始出现的索引位置
     * @param sql              完整的sql语句
     * @param sqlWord          需要被解析的sql解析词
     * @return 解析之后的sql语句
     */
    @Override
    public AlterStatement parseSqlByWord(AlterStatementBuilder StatementBuilder, int startIndex, String sql, String sqlWord) {
        if (";".equals(sqlWord)) {
            return StatementBuilder.create();
        }
        int wordNum = AlterStatement.getWordNum(sqlWord, AlterStatement.hashNum);
        if (wordNum == AlterStatement.ALTER_WORD) {
            Matcher matcher = ALTER_PATTERN.matcher(sql);
            if (matcher.find(startIndex)) {
                String mod = matcher.group(2);
                return parseSqlByWord(
                        StatementBuilder.setAlterStr(matcher.group().substring(5)).setSql(sql).setMod(mod).setTableName(matcher.group(1)),
                        5, sql, mod
                );
            }
            throw new RuntimeException("无法解析sql语句：" + sql);
        } else if (wordNum == AlterStatement.ADD_WORD) {
            Matcher matcher = ADD_PATTERN.matcher(sql);
            if (matcher.find(startIndex)) {
                String addInFo = matcher.group(1);
                // 获取到add的模式
                int addWordNum = AlterStatement.getWordNum(addInFo, AlterStatement.hashNum);
                if (addWordNum == AlterStatement.INDEX_WORD) {
                    // 这种情况下代表添加的是索引
                    Matcher matcher1 = ADD_INFO_PATTERN.matcher(sql);
                    if (matcher1.find(startIndex)) {
                        // 匹配到了正确的add语法
                        return parseSqlByWord(
                                StatementBuilder
                                        .setAddStr(matcher.group() + ' ' + matcher1.group())
                                        .setAddINFO(addInFo).setIndexName(matcher1.group(1)).setFieldStr(matcher1.group(2)),
                                matcher1.end(), sql, ";"
                        );
                    }
                    throw new RuntimeException("无法解析sql语句：" + sql);
                } else if (addWordNum == AlterStatement.UNIQUE_WORD || addWordNum == AlterStatement.PRIMARY_WORD) {
                    // 这种情况下代表添加的是约束，只需要取第二括号的内容
                    Matcher matcher1 = ADD_INFO_PATTERN.matcher(sql);
                    if (matcher1.find(startIndex)) {
                        return parseSqlByWord(
                                StatementBuilder
                                        .setAddStr(matcher.group() + ' ' + matcher1.group())
                                        .setAddINFO(addInFo).setFieldStr(matcher1.group(2)),
                                matcher1.end(), sql, ";"
                        );
                    } else {
                        throw new RuntimeException("add 约束子句解析错误。\nError parsing add constraint clause.\nERROR SQL => " + sql.substring(startIndex));
                    }
                } else {
                    // 这种情况代表的是添加的列
                    Matcher matcher1 = ADD_COLUMN_PATTERN.matcher(sql);
                    if (matcher1.find(startIndex)) {
                        return parseSqlByWord(
                                StatementBuilder
                                        .setAddStr(matcher1.group())
                                        .setAddINFO("field").setFieldStr(matcher1.group(1)),
                                matcher1.end(), sql, ";"
                        );
                    } else {
                        throw new RuntimeException("add 字段子句解析错误。\nParsing error of add field clause.\nERROR SQL => " + sql.substring(startIndex));
                    }
                }
            } else {
                throw new RuntimeException("add 子句的语法发生了错误!!!\nError occurred in syntax of add clause!!!\nERROR SQL => " + sql.substring(startIndex));
            }
        } else if (wordNum == AlterStatement.RENAME_WORD) {
            Matcher matcher = RENAME_PATTERN.matcher(sql);
            if (matcher.find(startIndex)) {
                return parseSqlByWord(
                        StatementBuilder.setRenameStr(matcher.group(1)), matcher.end(), sql, ";"
                );
            }
            throw new RuntimeException("无法解析sql语句：" + sql);
        } else if (wordNum == AlterStatement.DROP_WORD) {
            Matcher matcher = DROP_PATTERN.matcher(sql);
            if (matcher.find(startIndex)) {
                String dropCol = matcher.group(1);
                return parseSqlByWord(
                        StatementBuilder.setDropStr(dropCol).setFieldStr(dropCol), matcher.end(), sql, ";"
                );
            }
            throw new RuntimeException("无法解析sql语句：" + sql);
        } else if (wordNum == AlterStatement.CHANGE_WORD) {
            Matcher matcher = CHANGE_PATTERN.matcher(sql);
            if (matcher.find(startIndex)) {
                return parseSqlByWord(
                        StatementBuilder.setChangeStr(matcher.group(2)).setFieldStr(matcher.group(1)), matcher.end(), sql, ";"
                );
            }
            throw new RuntimeException("无法解析sql语句：" + sql);
        } else {
            throw new RuntimeException("错误的解析词：" + sqlWord);
        }
    }
}
