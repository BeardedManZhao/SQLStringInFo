package strInfo.result;

/**
 * sql语句对象，其中包含的就是有关sql中create语句的参数信息。
 * <p>
 * SQL statement object, which contains parameter information about the create statement in SQL.
 *
 * @author zhao
 */
public class CreateStatement extends Statement {
    public final static short hashNum = 4;
    public final static int CREATE_WORD = getWordNum("create", hashNum);
    public final static int TABLE_WORD = getWordNum("table", hashNum);
    public final static int FIELD_WORD = getWordNum("field", hashNum);
    public final static int OPTION_WORD = getWordNum("option", hashNum);
    public final static int LEFT_BRACKET_WORD = getWordNum("(", hashNum);
    public final static int RIGHT_BRACKET_WORD = getWordNum(")", hashNum);
    public final static int LIKE_WORD = getWordNum("like", hashNum);

    private final String SQL;
    private final String createStr;
    private final String fieldStr;
    private final String optionsStr;
    private final boolean temporary;
    private final String likeStr;

    protected CreateStatement(String tableName, String sql, String createStr, String fieldStr, String optionsStr, boolean temporary, String likeStr) {
        super(tableName, fieldStr != null ? COMMA_PATTERN.split(fieldStr.trim()) : new String[0]);
        this.SQL = sql;
        this.createStr = createStr;
        this.fieldStr = fieldStr;
        this.optionsStr = optionsStr;
        this.temporary = temporary;
        this.likeStr = likeStr;
    }


    protected CreateStatement(String tableName, String createStr, String fieldStr, String optionsStr, boolean temporary, String likeStr) {
        super(tableName, fieldStr == null ? new String[0] : COMMA_PATTERN.split(fieldStr.trim()));
        this.createStr = createStr;
        this.fieldStr = fieldStr;
        this.optionsStr = optionsStr;
        this.temporary = temporary;
        this.likeStr = likeStr;
        StringBuilder stringBuilder = new StringBuilder(0b1000000);
        stringBuilder
                .append("create ").append(createStr);
        if (fieldStr != null) stringBuilder.append(" (").append(fieldStr).append(") ");
        else if (likeStr != null) stringBuilder.append(" like ").append(likeStr);
        if (optionsStr != null) stringBuilder.append(optionsStr);
        this.SQL = stringBuilder + ";";
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
    public static CreateStatementBuilder builder() {
        return new CreateStatementBuilder();
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
     * @return 如果返回true，代表是临时表的创建，如果返回false，代表是普通表创建。
     * <p>
     * If true is returned, it represents the creation of a temporary table. If false is returned, it represents the creation of a common table.
     */
    public boolean isTemporary() {
        return temporary;
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
        if (wordNum == CREATE_WORD) {
            return this.createStr;
        } else if (wordNum == TABLE_WORD) {
            return this.tableName;
        } else if (wordNum == FIELD_WORD) {
            return this.fieldStr;
        } else if (wordNum == OPTION_WORD) {
            return this.optionsStr;
        } else if (wordNum == LIKE_WORD) {
            return this.likeStr;
        }
        throw new IllegalStateException("Unexpected value: " + wordNum);
    }

    /**
     * 按照SQL解析词获取到目标解析词下的sql语句的数组形式
     *
     * @param sqlWord 需要获取的sql语句所属的解析词
     * @return sqlWord对应的的sql语句的数组对象
     */
    @Override
    public String[] getStatementArrayByWord(String sqlWord) {
        int wordNum = getWordNum(sqlWord, hashNum);
        if (wordNum == CREATE_WORD) {
            return this.createStr == null ? new String[0] : SPACE_PATTERN.split(this.createStr);
        } else if (wordNum == TABLE_WORD) {
            return new String[]{this.tableName};
        } else if (wordNum == FIELD_WORD) {
            return this.fieldStr == null ? new String[0] : COMMA_PATTERN.split(this.fieldStr);
        } else if (wordNum == OPTION_WORD) {
            return this.optionsStr == null ? new String[0] : SPACE_PATTERN.split(this.optionsStr);
        } else if (wordNum == LIKE_WORD) {
            return new String[]{this.likeStr};
        }
        throw new IllegalStateException("Unexpected value: " + wordNum);
    }

    /**
     * 以当前类为中心，将其它sql语句类中的数据合并到当前类中，并返回当前类的新副本，不会影响当前类的源数据
     *
     * @param selectStatement 其它的sql语句类，也是另一方的数据来源
     * @return 一个以当前语句对象为中心，与其它类合并之后的新语句对象
     */
    public CreateStatement merge(CreateStatement selectStatement) {
        String createStr = null;
        String fieldStr = null;
        String optionStr = null;
        String limitStr = null;
        if (this.createStr == null && selectStatement.createStr != null) {
            createStr = selectStatement.createStr;
        }
        if (this.fieldStr == null && selectStatement.fieldStr != null) {
            fieldStr = selectStatement.fieldStr;
        }
        if (this.optionsStr == null && selectStatement.optionsStr != null) {
            optionStr = selectStatement.optionsStr;
        }
        if (this.likeStr == null && selectStatement.likeStr != null) {
            limitStr = selectStatement.likeStr;
        }
        return new CreateStatement(this.tableName, createStr, fieldStr == null ? "----" : fieldStr, optionStr,
                this.temporary == selectStatement.temporary && this.temporary, limitStr);
    }

    @Override
    public String toString() {
        return this.getSqlStr();
    }
}
