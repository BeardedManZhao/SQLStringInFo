package strInfo.parser;

import strInfo.result.Builder;

/**
 * SQL解析器的接口，解析器的统一父类，其中提供了针对sql语句与sql解析词的解析函数，由不同的解析器实现。
 * <p>
 * The interface of SQL parser, the unified parent class of the parser, which provides parsing functions for SQL statements and SQL parsing words, and is implemented by different parsers.
 *
 * @param <StatementType>        解析器所返回的sql语句解析结果对象类型
 * @param <StatementBuilderType> 解析器需要的建造者类，建造者类所建造出来的是sql语句的解析结果对象，在解析器中，使用建造者类进行不断的建造，最终返回一个结果数据对象
 * @author zhao
 */
public interface SQLParser<StatementType, StatementBuilderType extends Builder<?>> {


    /**
     * @return 获取到该解析器的名称，该名称还用于该组件在管理者中的注册，因此您也可以通过管理者获取到该组件对象
     * <p>
     * Get the name of the parser, which is also used for the registration of the component in the manager, so you can also get the component object through the manager
     */
    String getName();

    /**
     * 开始解析一个sql语句
     *
     * @param sql 需要被解析的sql语句
     * @return 解析之后的sql语句对象
     */
    StatementType parseSql(String sql);

    /**
     * 解析一个sql语句中的指定sql解析词相关的子sql语句
     *
     * @param StatementBuilder 累计的sql语句结果的建造类
     * @param startIndex       解析词开始出现的索引位置
     * @param sql              完整的sql语句
     * @param sqlWord          需要被解析的sql解析词
     * @return 解析之后的sql语句
     */
    StatementType parseSqlByWord(StatementBuilderType StatementBuilder, int startIndex, String sql, String sqlWord);
}
