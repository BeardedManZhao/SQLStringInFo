package strInfo.manager;

import strInfo.parser.SQLParser;
import strInfo.result.Builder;

import java.util.HashMap;

/**
 * SQL解析库的管理者类，其中存储的都是全局唯一的数据解析组件，针对使用过的解析器，在这里都会有存储。
 * <p>
 * The manager class of the SQL parsing library stores the globally unique data parsing components. All used parsers will be stored here.
 *
 * @author zhao
 */
public final class SQLParserManager {

    private final static float VERSION = 1.0f;

    private final static HashMap<String, SQLParser<?, ?>> hashMap = new HashMap<>(8);

    /**
     * 将一个解析组件注册到管理者中，注册之后可以通过 getSQLParserByName 函数获取到对应名称的解析器对象。
     * <p>
     * Register a resolution component to the manager. After registration, you can obtain the parser object with the corresponding name through the getSQLParserByName function.
     *
     * @param sqlParser 需要被注册的解析器对象
     */
    public static void register(SQLParser<?, ?> sqlParser) {
        hashMap.put(sqlParser.getName(), sqlParser);
    }

    /**
     * 通过组件名称，将一个解析组件对象从管理者中获取到。
     * <p>
     * Get a resolution component object from the manager through the component name.
     *
     * @param sqlParserName                解析组件对象名称
     * @param <StatementType>              解析器返回的的结果对象类型
     * @param <CreateStatementBuilderType> 解析器建造结果对象使用的建造者类
     * @return 对应名称的解析器对象
     */
    @SuppressWarnings("unchecked")
    public static <StatementType, CreateStatementBuilderType extends Builder<?>> SQLParser<StatementType, CreateStatementBuilderType> getSQLParserByName(String sqlParserName) {
        return (SQLParser<StatementType, CreateStatementBuilderType>) hashMap.get(sqlParserName);
    }

    /**
     * 将一个解析器组件从管理中注销，同时将注销成功的组件返回出来。
     * <p>
     * Logs a parser component out of the management, and returns the components that were successfully logged out.
     *
     * @param sqlParserName 需要被注销的组件名称
     * @return 被注销的组件对象
     */
    public static SQLParser<?, ?> unRegisterByName(String sqlParserName) {
        return hashMap.remove(sqlParserName);
    }
}
