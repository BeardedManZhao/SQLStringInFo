package strInfo.result;

/**
 * sql语句对象，其中包含的就是有关sql中create语句的参数信息。
 * <p>
 * SQL statement object, which contains parameter information about the "create" statement in SQL.
 *
 * @author zhao
 */
public class AlterStatement extends Statement {

    public final static short hashNum = 8;
    public final static int ALTER_WORD = getWordNum("alter", hashNum);
    public final static int ALTER_MOD_WORD = getWordNum("mod", hashNum);
    public final static int ADD_INFO_WORD = getWordNum("addINFO", hashNum);
    public final static int TABLE_WORD = getWordNum("table", hashNum);
    public final static int ADD_WORD = getWordNum("add", hashNum);
    public final static int DROP_WORD = getWordNum("drop", hashNum);
    public final static int RENAME_WORD = getWordNum("rename", hashNum);
    public final static int CHANGE_WORD = getWordNum("change", hashNum);
    public final static int INDEX_WORD = getWordNum("index", hashNum);
    public final static int PRIMARY_WORD = getWordNum("primary", hashNum);
    public final static int UNIQUE_WORD = getWordNum("unique", hashNum);
    public final static int FIELD_WORD = getWordNum("field", hashNum);

    private final String alterStr;
    private final String addStr;
    private final String dropStr;
    private final String renameStr;
    private final String changeStr;
    private final String mod;
    private final String addINFO;
    private final String indexName;
    private final String sql;
    private final String fieldStr;

    public AlterStatement(String tableName, String fieldStr, String mod, String alterStr, String addStr, String indexName, String addINFO, String dropStr, String renameStr, String changeStr, String sql) {
        super(tableName, fieldStr == null ? new String[0] : COMMA_PATTERN.split(fieldStr));
        this.alterStr = alterStr;
        int wordNum = getWordNum(mod, hashNum);
        if (wordNum == ADD_WORD) {
            this.addStr = addStr;
            this.dropStr = "null";
            this.renameStr = "null";
            this.changeStr = "null";
            int wordNum1 = getWordNum(addINFO, hashNum);
            if (wordNum1 == INDEX_WORD) {
                this.indexName = indexName;
            } else {
                this.indexName = "null";
            }
            this.addINFO = addINFO;
        } else if (wordNum == DROP_WORD) {
            this.dropStr = dropStr;
            this.addStr = "null";
            this.renameStr = "null";
            this.changeStr = "null";
            this.indexName = "null";
            this.addINFO = "null";
        } else if (wordNum == RENAME_WORD) {
            this.renameStr = renameStr;
            this.dropStr = "null";
            this.addStr = "null";
            this.changeStr = "null";
            this.indexName = "null";
            this.addINFO = "null";
        } else if (wordNum == CHANGE_WORD) {
            this.changeStr = changeStr;
            this.dropStr = "null";
            this.renameStr = "null";
            this.addStr = "null";
            this.indexName = "null";
            this.addINFO = "null";
        } else {
            throw new RuntimeException("无法解析alter语句，语句中解析词对应的句子格式错误。\n" +
                    "The alter statement cannot be parsed. The format of the sentence corresponding to the parsed word in the statement is incorrect.\n" +
                    "ERROR => " + mod);
        }
        this.fieldStr = fieldStr;
        this.mod = mod;
        this.sql = sql;
    }

    /**
     * 开始构建语句类对象，调用该方法也就意味着开始该类的建造，会返回对应的建造者对象。
     * <p>
     * Start building statement class objects. Calling this method means starting the construction of this class, and the corresponding constructor object will be returned.
     *
     * @return 需要被构建的语句类对象，一般来说，如果您不进行解析器的拓展，该方法由解析器调用，并将建造结果返回给您。
     * <p>
     * Generally speaking, if you do not extend the parser, the method will be called by the parser and the construction result will be returned to you.
     */
    public static AlterStatementBuilder builder() {
        return new AlterStatementBuilder();
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
        return this.sql;
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
        if (wordNum == ALTER_WORD) {
            return this.alterStr;
        } else if (wordNum == TABLE_WORD) {
            return this.tableName;
        } else if (wordNum == ADD_WORD) {
            return this.addStr;
        } else if (wordNum == DROP_WORD) {
            return this.dropStr;
        } else if (wordNum == RENAME_WORD) {
            return this.renameStr;
        } else if (wordNum == CHANGE_WORD) {
            return this.changeStr;
        } else if (wordNum == INDEX_WORD) {
            return this.indexName;
        } else if (wordNum == FIELD_WORD) {
            return this.fieldStr;
        } else if (wordNum == ALTER_MOD_WORD) {
            return this.mod;
        } else if (wordNum == ADD_INFO_WORD) {
            return this.addINFO;
        }
        throw new IllegalStateException("Unexpected value: " + wordNum);
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
        if (wordNum == ALTER_WORD) {
            return new String[]{this.alterStr};
        } else if (wordNum == TABLE_WORD) {
            return new String[]{this.tableName};
        } else if (wordNum == ADD_WORD) {
            return new String[]{this.addStr};
        } else if (wordNum == DROP_WORD) {
            return new String[]{this.dropStr};
        } else if (wordNum == RENAME_WORD) {
            return new String[]{this.renameStr};
        } else if (wordNum == CHANGE_WORD) {
            return new String[]{this.changeStr};
        } else if (wordNum == INDEX_WORD) {
            return new String[]{this.indexName};
        } else if (wordNum == FIELD_WORD) {
            return this.fieldNames;
        } else if (wordNum == ALTER_MOD_WORD) {
            return new String[]{this.mod};
        } else if (wordNum == ADD_INFO_WORD) {
            return new String[]{this.addINFO};
        }
        throw new IllegalStateException("Unexpected value: " + wordNum);
    }
}
