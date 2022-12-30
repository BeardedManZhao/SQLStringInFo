package strInfo.result;

/**
 * Insert的sql语句类对象的建造者，解析器会通过该类建造出来语句结果对象。
 * <p>
 * The constructor of the SQL statement class object of Insert. The parser will build the statement result object through this class.
 *
 * @author zhao
 */
public class InsertStatementBuilder implements Builder<InsertStatement> {

    private boolean Multiline;
    private String SQL;
    private String tableName;
    private String fieldStr;
    private String dataStr;

    public InsertStatementBuilder setSQL(String SQL) {
        this.SQL = SQL;
        return this;
    }

    public InsertStatementBuilder setMultiline(boolean multiline) {
        Multiline = multiline;
        return this;
    }

    public InsertStatementBuilder setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public InsertStatementBuilder setFieldStr(String fieldStr) {
        this.fieldStr = fieldStr;
        return this;
    }

    public InsertStatementBuilder setDataStr(String dataStr) {
        this.dataStr = dataStr;
        return this;
    }

    /**
     * @return 建造者类所构造出来的数据封装对象，当调用该方法的时候意味着建造完成，将会正式的确定出被构造产品的结果数据
     */
    @Override
    public InsertStatement create() {
        return new InsertStatement(tableName, Multiline, this.SQL, fieldStr, dataStr);
    }
}
