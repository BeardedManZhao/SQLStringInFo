package strInfo.result;

import java.util.regex.Pattern;

public abstract class Statement {
    protected final static Pattern COMMA_PATTERN = Pattern.compile("\\s*,\\s*");
    protected final static Pattern SPACE_PATTERN = Pattern.compile(" +");
    protected final String tableName;
    protected final String[] fieldNames;

    protected Statement(String tableName, String[] fieldNames) {
        this.tableName = tableName;
        this.fieldNames = fieldNames;
    }

    /**
     * 获取到词对应的解析词hash编号
     *
     * @param sqlWord 需要被计算为hash编号的解析词
     * @param hashNum hash编号阈值，阈值越大，越能避免hash冲突
     * @return 借此词对应的hash编号数值
     */
    public static int getWordNum(String sqlWord, int hashNum) {
        if (sqlWord == null) return -1;
        int i = sqlWord.hashCode();
        return Math.abs(i - (i >> hashNum << hashNum));
    }


    /**
     * @return 获取到SQL字符串的原语句
     */
    public abstract String getSqlStr();

    /**
     * 按照SQL解析词获取到目标解析词下的sql语句
     *
     * @param sqlWord 需要获取的sql语句所属的解析词
     * @return sqlWord对应的的sql语句
     */
    public abstract String getStatementStrByWord(String sqlWord);

    /**
     * @return 语句作用的table表名
     */
    public final String getTableName() {
        return this.tableName;
    }

    /**
     * @return 语句作用的所有字段组成的数组，每一个数组都是一个字段
     */
    public final String[] getFieldNames() {
        return this.fieldNames;
    }

    /**
     * 获取到解析词在sql语句中的编号数值，用于将解析词按照hash与阈值一起映射成为数值后，再进行解析词的匹配，这样的匹配性能会优秀很多，其中的编号数值阈值默认为4，
     *
     * @param sqlWord 需要被转换的sql词
     * @return sql词对应的编号
     */
    protected int getWordNum(String sqlWord) {
        return getWordNum(sqlWord, 4);
    }

    /**
     * 按照SQL解析词获取到目标解析词下的sql语句
     *
     * @param sqlWord 需要获取的sql语句所属的解析词
     * @return sqlWord对应的的sql语句
     */
    public abstract String[] getStatementArrayByWord(String sqlWord);
}
