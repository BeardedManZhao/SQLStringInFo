package strInfo.result;

import java.util.ArrayList;

/**
 * sql语句对象，其中包含的就是有关sql中的Select语句的参数信息。
 * <p>
 * SQL statement object, which contains parameter information about the Select statement in SQL.
 *
 * @author zhao
 */
public class SelectStatement extends Statement {
    public final static short hashNum = 4;
    public final static int SELECT_WORD = getWordNum("select", hashNum);
    public final static int FROM_WORD = getWordNum("from", hashNum);
    public final static int WHERE_WORD = getWordNum("where", hashNum);
    public final static int GROUP_WORD = getWordNum("group", hashNum);
    public final static int ORDER_WORD = getWordNum("order", hashNum);
    public final static int LIMIT_WORD = getWordNum("limit", hashNum);

    private final ArrayList<SelectStatement> SubQueryList;
    private final String SQL;
    private final String selectStr;
    private final String whereStr;
    private final String groupStr;
    private final boolean asc;
    private final String orderStr;
    private final String limitStr;

    protected SelectStatement(String tableName, String sql, String selectStr, String whereStr, String groupStr, String orderStr, boolean asc, String limitStr, ArrayList<SelectStatement> SubQueryList) {
        super(tableName, COMMA_PATTERN.split(selectStr));
        this.SQL = sql;
        this.selectStr = selectStr;
        this.whereStr = whereStr;
        this.groupStr = groupStr;
        this.asc = asc;
        this.orderStr = orderStr;
        this.limitStr = limitStr;
        this.SubQueryList = SubQueryList;
    }

    protected SelectStatement(String tableName, String selectStr, String whereStr, String groupStr, String orderStr, boolean asc, String limitStr, ArrayList<SelectStatement> SubQueryList) {
        super(tableName, COMMA_PATTERN.split(selectStr));
        this.selectStr = selectStr;
        this.whereStr = whereStr;
        this.groupStr = groupStr;
        this.orderStr = orderStr;
        this.asc = asc;
        this.limitStr = limitStr;
        StringBuilder stringBuilder = new StringBuilder(0b1000000);
        stringBuilder
                .append("select ").append(selectStr)
                .append(" from ").append(tableName);
        if (whereStr != null) stringBuilder.append(" where ").append(whereStr);
        if (groupStr != null) stringBuilder.append(" group  by ").append(groupStr);
        if (orderStr != null) stringBuilder.append(" order by ").append(orderStr);
        if (limitStr != null) stringBuilder.append(" limit ").append(limitStr);
        this.SQL = stringBuilder + ";";
        this.SubQueryList = SubQueryList;
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
    public static SelectStatementBuilder builder() {
        return new SelectStatementBuilder();
    }

    /**
     * 获取到本次查询涉及到的所有表名组成的数组数据。
     * <p>
     * Get the array data composed of all table names involved in this query.
     *
     * @return 将tableName数据按照一定的格式进行分组，获取到包含所有表名的一个新数组。
     * <p>
     * Group tableName data in a certain format to obtain a new array containing all table names.
     */
    public String[] getTables() {
        ArrayList<String> arrayList = new ArrayList<>();
        String tableName = this.tableName;
        StringBuilder backStr = new StringBuilder();
        for (int i = 0, length = tableName.length(); i < length; i++) {
            char c = tableName.charAt(i);
            if (c != ' ' && c != ',') {
                backStr.append(c);
            } else {
                int length1 = backStr.length();
                if (length1 == 5) {
                    String s1 = backStr.toString();
                    if (s1.equalsIgnoreCase("right") || s1.equalsIgnoreCase("inner") || s1.equalsIgnoreCase("cross")) {
                        backStr.delete(0, 5);
                    } else {
                        arrayList.add(s1);
                    }
                } else if (length1 == 4) {
                    String s1 = backStr.toString();
                    if ("join".equalsIgnoreCase(s1) || "left".equalsIgnoreCase(s1) || "full".equalsIgnoreCase(s1)) {
                        backStr.delete(0, 4);
                    } else {
                        arrayList.add(s1);
                    }
                } else {
                    int length2 = backStr.length();
                    if (length2 != 0) {
                        arrayList.add(backStr.toString());
                        backStr.delete(0, length2);
                    }
                }
            }
        }
        if (backStr.length() != 0) {
            arrayList.add(backStr.toString());
        }
        return arrayList.toArray(new String[0]);
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
     * @return 如果返回1，代表是正序排序子句，如果返回-1，是倒序排序子句，如果返回0，代表不进行排序操作
     * <p>
     * If 1 is returned, it represents a positive ordering clause; if - 1 is returned, it represents a reverse ordering clause; if 0 is returned, it represents no sorting operation
     */
    public int isAsc() {
        return orderStr == null ? 0 : asc ? 1 : -1;
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
        if (wordNum == SELECT_WORD) {
            return this.selectStr;
        } else if (wordNum == FROM_WORD) {
            return this.tableName;
        } else if (wordNum == WHERE_WORD) {
            return this.whereStr;
        } else if (wordNum == GROUP_WORD) {
            return this.groupStr;
        } else if (wordNum == ORDER_WORD) {
            return this.orderStr;
        } else if (wordNum == LIMIT_WORD) {
            return this.limitStr;
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
        if (wordNum == SELECT_WORD) {
            return this.fieldNames;
        } else if (wordNum == FROM_WORD) {
            return this.tableName == null ? new String[0] : getTables();
        } else if (wordNum == WHERE_WORD) {
            return this.whereStr == null ? new String[0] : getWhereArray();
        } else if (wordNum == GROUP_WORD) {
            return this.groupStr == null ? new String[0] : COMMA_PATTERN.split(this.groupStr);
        } else if (wordNum == ORDER_WORD) {
            return this.orderStr == null ? new String[0] : COMMA_PATTERN.split(this.orderStr);
        } else if (wordNum == LIMIT_WORD) {
            return this.limitStr == null ? new String[0] : COMMA_PATTERN.split(this.limitStr);
        }
        throw new IllegalStateException("Unexpected value: " + wordNum);
    }

    /**
     * 返回本次解析到的 where 子句中的条件数组，其中偶数位是子句的条件表达式。
     *
     * @return where子句的条件数组。
     */
    public String[] getWhereArray() {
        ArrayList<String> arrayList = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder backStr = new StringBuilder();
        for (int i = 0, length = this.whereStr.length(); i < length; i++) {
            char c = this.whereStr.charAt(i);
            if (c != ' ') {
                stringBuilder.append(c);
            } else {
                String s = stringBuilder.toString();
                int length1 = s.length();
                if ("or".equalsIgnoreCase(s) || "and".equalsIgnoreCase(s)) {
                    arrayList.add(backStr.toString());
                    arrayList.add(s);
                } else {
                    backStr.append(c).append(stringBuilder);
                }
                stringBuilder.delete(0, length1);
            }
        }
        if (backStr.length() != 0) {
            arrayList.add(backStr.toString());
        }
        return arrayList.toArray(new String[0]);
    }

    /**
     * 获取到本次解析到的所有子查询语句
     *
     * @return 每一个子查询语句的sql解析结果对象组成的列表，值得注意的是，该列表是源数据，在外界的修改会影响到本类中的数据。
     * <p>
     * The list of SQL parsing result objects of each sub query statement. It is worth noting that this list is source data, and external modifications will affect the data in this class.
     */
    public ArrayList<SelectStatement> getSubQueryList() {
        return SubQueryList;
    }

    /**
     * 以当前类为中心，将其它sql语句类中的数据合并到当前类中，并返回当前类的新副本，不会影响当前类的源数据
     *
     * @param selectStatement 其它的sql语句类，也是另一方的数据来源
     * @return 一个以当前语句对象为中心，与其它类合并之后的新语句对象
     */
    public SelectStatement merge(SelectStatement selectStatement) {
        if (selectStatement == null) return this;
        String selectStr = null;
        String whereStr = null;
        String groupStr = null;
        String orderStr = null;
        boolean asc = this.asc;
        String limitStr = null;
        if (this.selectStr == null && selectStatement.selectStr != null) {
            selectStr = selectStatement.selectStr;
        }
        if (this.whereStr == null && selectStatement.whereStr != null) {
            whereStr = selectStatement.whereStr;
        }
        if (this.groupStr == null && selectStatement.groupStr != null) {
            groupStr = selectStatement.groupStr;
        }
        if (this.orderStr == null && selectStatement.orderStr != null) {
            orderStr = selectStatement.orderStr;
            asc = selectStatement.asc;
        }
        if (this.limitStr == null && selectStatement.limitStr != null) {
            limitStr = selectStatement.limitStr;
        }
        ArrayList<SelectStatement> arrayList = new ArrayList<>(this.SubQueryList.size() + selectStatement.SubQueryList.size() + 16);
        arrayList.addAll(this.SubQueryList);
        arrayList.addAll(selectStatement.SubQueryList);
        return new SelectStatement(this.tableName, selectStr, whereStr, groupStr, orderStr, this.asc == selectStatement.asc && asc, limitStr, arrayList);
    }

    @Override
    public String toString() {
        return this.getSqlStr();
    }
}
