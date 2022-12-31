package strInfo.result;

/**
 * create 语句对象的建造者类，通过该类可以将一个create语句建造出来。
 *
 * @author zhao
 */
public class CreateStatementBuilder implements Builder<CreateStatement> {

    private String SQL;
    private String tableName;
    private String createStr;
    private String fieldStr;
    private String optionsStr;
    private boolean temporary;
    private String likeStr;
    private SelectStatement selectStatement;

    /**
     * 设置SQL的整体语句，是sql语句的全部字符串对象
     * <p>
     * Set the whole SQL statement, which is all string objects of the SQL statement
     *
     * @param SQL 当前对象的sql语句字符串对象
     *            <p>
     *            SQL statement string object of the current object
     * @return 建造者模式的链式构造
     * <p>
     * Chain structure of builder pattern
     */
    public CreateStatementBuilder setSQL(String SQL) {
        this.SQL = SQL;
        return this;
    }

    /**
     * @param tableName 当前语句作用于哪个表
     * @return 建造者模式的链式构造
     * <p>
     * Chain structure of builder pattern
     */
    public CreateStatementBuilder setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public CreateStatementBuilder setCreateStr(String createStr) {
        this.createStr = createStr;
        return this;
    }

    public CreateStatementBuilder setFieldStr(String fieldStr) {
        this.fieldStr = fieldStr;
        return this;
    }

    public CreateStatementBuilder setOptionsStr(String optionsStr) {
        this.optionsStr = optionsStr;
        return this;
    }

    public CreateStatementBuilder setTemporary(boolean temporary) {
        this.temporary = temporary;
        return this;
    }

    public CreateStatementBuilder setLikeStr(String likeStr) {
        this.likeStr = likeStr;
        return this;
    }

    public CreateStatementBuilder setSelectStatement(SelectStatement selectStatement) {
        this.selectStatement = selectStatement;
        return this;
    }

    /**
     * @return 建造者类所构造出来的数据封装对象，当调用该方法的时候意味着建造完成，将会正式的确定出被构造产品的结果数据
     */
    @Override
    public CreateStatement create() {
        if (this.SQL == null) {
            return new CreateStatement(tableName, createStr, fieldStr, optionsStr, temporary, likeStr, selectStatement);
        }
        return new CreateStatement(tableName, SQL, createStr, fieldStr, optionsStr, temporary, likeStr, selectStatement);
    }
}
