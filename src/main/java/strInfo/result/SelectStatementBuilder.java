package strInfo.result;

import java.util.ArrayList;

/**
 * 针对 SelectStatement 类的建造者对象，是针对 SelectStatement 进行建造的临时对象类，在其中有着很多的设置函数，通过这些参数就可以实现针对某些数据的设置，通过create函数可以将 SelectStatement 类构建出来。
 * <p>
 * The builder object for the SelectStatement class is a temporary object class built for the SelectStatement class. There are many setting functions in it. These parameters can be used to set certain data. The create function can be used to build the SelectStatement class.
 *
 * @author zhao
 */
public class SelectStatementBuilder implements Builder<SelectStatement> {
    private final ArrayList<SelectStatement> SubQueryList = new ArrayList<>(8);
    private String SQL;
    private StringBuilder tableName;
    private String selectStr;
    private String whereStr;
    private String groupStr;
    private String orderStr;
    private boolean asc;
    private String limitStr;
    private boolean isSub;

    public SelectStatementBuilder addTableName(String tableName) {
        this.tableName.append(tableName);
        return this;
    }

    public SelectStatementBuilder setSQL(String SQL) {
        this.SQL = SQL;
        return this;
    }

    public SelectStatementBuilder setTableName(String tableName) {
        this.tableName = new StringBuilder(tableName);
        return this;
    }

    public SelectStatementBuilder setSelectStr(String selectStr) {
        this.selectStr = selectStr;
        return this;
    }

    public SelectStatementBuilder setWhereStr(String whereStr) {
        this.whereStr = whereStr;
        return this;
    }

    public SelectStatementBuilder setGroupStr(String groupStr) {
        this.groupStr = groupStr;
        return this;
    }

    public SelectStatementBuilder setOrderStr(String orderStr) {
        this.orderStr = orderStr;
        return this;
    }

    public SelectStatementBuilder setAsc(boolean isAsc) {
        this.asc = isAsc;
        return this;
    }

    public SelectStatementBuilder setLimitStr(String limitStr) {
        this.limitStr = limitStr;
        return this;
    }

    /**
     * 将子查询的sql解析结果提供给对象列表
     *
     * @param selectStatement 子查询结果对象
     * @return 建造者模式的链式建造
     */
    public SelectStatementBuilder addSubSelect(SelectStatement selectStatement) {
        this.SubQueryList.add(selectStatement);
        return this;
    }

    public boolean isSub() {
        return isSub;
    }

    public SelectStatementBuilder setSub(boolean sub) {
        isSub = sub;
        return this;
    }

    /**
     * @return 建造者类所构造出来的数据封装对象，当调用该方法的时候意味着建造完成，将会正式的确定出被构造产品的结果数据
     */
    @Override
    public SelectStatement create() {
        if (this.SQL == null) {
            return new SelectStatement(tableName.toString(), selectStr, whereStr, groupStr, orderStr, asc, limitStr, SubQueryList);
        }
        return new SelectStatement(tableName.toString(), this.SQL, selectStr, whereStr, groupStr, orderStr, asc, limitStr, SubQueryList);
    }
}
