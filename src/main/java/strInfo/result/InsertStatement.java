package strInfo.result;

import java.util.regex.Pattern;

/**
 * sql语句对象，其中包含的就是有关sql中的 insert 语句的参数信息。
 * <p>
 * SQL statement object, which contains parameter information about the Insert statement in SQL.
 *
 * @author zhao
 */
public class InsertStatement extends Statement {

    public final static short hashNum = 4;
    public final static int INSERT_WORD = getWordNum("insert", hashNum);
    public final static int TABLE_WORD = getWordNum("table", hashNum);
    public final static int VALUE_WORD = getWordNum("value", hashNum);
    public final static int VALUES_WORD = getWordNum("values", hashNum);
    protected final static Pattern VALUE_SPLIT_PATTERN = Pattern.compile("\\)\\s*?,\\s*?\\(", Pattern.CASE_INSENSITIVE);

    private final boolean Multiline;
    private final String SQL;
    private final String fieldStr;
    private final String dataStr;

    protected InsertStatement(String tableName, boolean multiline, String sql, String fieldStr, String dataStr) {
        super(tableName, fieldStr == null ? new String[]{"null"} : COMMA_PATTERN.split(fieldStr));
        Multiline = multiline;
        SQL = sql;
        this.fieldStr = fieldStr;
        this.dataStr = dataStr;
    }

    protected InsertStatement(String tableName, boolean multiline, String fieldStr, String dataStr) {
        super(tableName, fieldStr == null ? new String[]{"null"} : COMMA_PATTERN.split(fieldStr));
        Multiline = multiline;
        this.fieldStr = fieldStr;
        this.dataStr = dataStr;
        this.SQL = "insert into " + tableName + '(' + fieldStr + ')' + (multiline ? " values " : " value ") + dataStr + ';';
    }

    /**
     * 开始构建一个insert语句对象，通过此方法会返回一个建造者模式的对象。
     * <p>
     * Start to build an insert statement object. This method will return an object in builder mode.
     *
     * @return insert语句的建造者对象
     * <p>
     * The constructor object of the insert statement
     */
    public static InsertStatementBuilder builder() {
        return new InsertStatementBuilder();
    }

    /**
     * 获取到解析词在sql语句中的编号数值，用于将解析词按照hash与阈值一起映射成为数值后，再进行解析词的匹配，这样的匹配性能会优秀很多，其中的编号数值阈值默认为当前语句对象的hashNum。
     * <p>
     * Get the number value of the parsed word in the sql statement, which is used to map the parsed word into a number by hash and threshold value, and then match the parsed word. The matching performance will be much better. The number value threshold value is the hashNum of the current statement object by default.
     *
     * @param sqlWord 需要被转换的sql词
     * @return sql词对应的编号
     */
    @Override
    protected int getWordNum(String sqlWord) {
        return getWordNum(sqlWord, hashNum);
    }

    /**
     * @return 获取到SQL字符串的原语句
     */
    @Override
    public String getSqlStr() {
        return this.SQL;
    }

    /**
     * 按照SQL解析词获取到目标解析词下的sql语句
     *
     * @param sqlWord 需要获取的sql语句所属的解析词
     * @return sqlWord对应的的sql语句
     */
    @Override
    public String getStatementStrByWord(String sqlWord) {
        int wordNum = getWordNum(sqlWord, hashNum);
        if (wordNum == INSERT_WORD) {
            return this.fieldStr;
        } else if (wordNum == TABLE_WORD) {
            return this.tableName;
        } else if (wordNum == VALUE_WORD || wordNum == VALUES_WORD) {
            return '(' + this.dataStr + ')';
        }
        throw new IllegalStateException("Unexpected value: " + wordNum);
    }

    /**
     * 以当前类为中心，将其它sql语句类中的数据合并到当前类中，并返回当前类的新副本，不会影响当前类的源数据
     *
     * @param selectStatement 其它的sql语句类，也是另一方的数据来源
     * @return 一个以当前语句对象为中心，与其它类合并之后的新语句对象
     */
    public InsertStatement merge(InsertStatement selectStatement) {
        String dataStr = null;
        String fieldStr = null;
        if (this.dataStr == null && selectStatement.dataStr != null) {
            dataStr = selectStatement.dataStr;
        }
        if (this.fieldStr == null && selectStatement.fieldStr != null) {
            fieldStr = selectStatement.fieldStr;
        }
        return new InsertStatement(
                this.tableName,
                this.Multiline == selectStatement.Multiline && this.Multiline,
                fieldStr, dataStr
        );
    }

    /**
     * 按照SQL解析词获取到目标解析词下的sql语句
     *
     * @param sqlWord 需要获取的sql语句所属的解析词
     * @return sqlWord对应的的sql语句
     */
    @Override
    public String[] getStatementArrayByWord(String sqlWord) {
        int wordNum = getWordNum(sqlWord, hashNum);
        if (wordNum == INSERT_WORD) {
            return this.fieldNames;
        } else if (wordNum == TABLE_WORD) {
            return new String[]{this.tableName};
        } else if (wordNum == VALUE_WORD || wordNum == VALUES_WORD) {
            return this.dataStr != null ? VALUE_SPLIT_PATTERN.split(this.dataStr) : new String[0];
        }
        throw new IllegalStateException("Unexpected value: " + wordNum);
    }
}
