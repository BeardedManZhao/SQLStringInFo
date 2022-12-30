package strInfo.result;

/**
 * @author zhao
 */
public class AlterStatementBuilder implements Builder<AlterStatement> {

    private String alterStr;
    private String addStr;
    private String dropStr;
    private String renameStr;
    private String changeStr;
    private String mod;
    private String addINFO;
    private String indexName;
    private String sql;
    private String tableName;
    private String fieldStr;

    public AlterStatementBuilder setAlterStr(String alterStr) {
        this.alterStr = alterStr;
        return this;
    }

    public AlterStatementBuilder setAddStr(String addStr) {
        this.addStr = addStr;
        return this;
    }

    public AlterStatementBuilder setDropStr(String dropStr) {
        this.dropStr = dropStr;
        return this;
    }

    public AlterStatementBuilder setRenameStr(String renameStr) {
        this.renameStr = renameStr;
        return this;
    }

    public AlterStatementBuilder setChangeStr(String changeStr) {
        this.changeStr = changeStr;
        return this;
    }

    public AlterStatementBuilder setMod(String mod) {
        this.mod = mod;
        return this;
    }

    public AlterStatementBuilder setAddINFO(String addINFO) {
        this.addINFO = addINFO;
        return this;
    }

    public AlterStatementBuilder setIndexName(String indexName) {
        this.indexName = indexName;
        return this;
    }

    public AlterStatementBuilder setSql(String sql) {
        this.sql = sql;
        return this;
    }


    public AlterStatementBuilder setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public AlterStatementBuilder setFieldStr(String fieldStr) {
        this.fieldStr = fieldStr;
        return this;
    }

    /**
     * @return 建造者类所构造出来的数据封装对象，当调用该方法的时候意味着建造完成，将会正式的确定出被构造产品的结果数据
     */
    @Override
    public AlterStatement create() {
        return new AlterStatement(tableName, fieldStr, mod, alterStr, addStr, indexName, addINFO, dropStr, renameStr, changeStr, sql);
    }
}
