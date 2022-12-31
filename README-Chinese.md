# SQLStringInFo

## 介绍

SQLStringInFo是一个专注于sql命令语句解析的sql命令解析库，在库中提供了有关SQL命令语法的解析器，通过该库，可以实现快速准确的SQL语句分析处理。

### 框架中的解析器

解析器是针对SQL语句数据处理的实现，其中存储的有关SQL解析的具体逻辑，在库中，所有的解析器都实现了strInfo.parser.SQLParser接口，接口中有最基本的获取数据的函数，下面是有关库中当前所有解析器的信息。

| 解析器类型                       | 解析器名称  | 加入版本 | 解析器作用          |
|-----------------------------|--------|------|----------------|
| strInfo.parser.SQLParser    | null   | v1.0 | 统一所有解析器的接口     |
| strInfo.parser.SelectParser | select | v1.0 | 针对Select语句进行解析 |
| strInfo.parser.InsertParser | insert | v1.0 | 针对insert语句进行解析 |
| strInfo.parser.CreateParser | create | v1.0 | 针对create语句进行解析 |
| strInfo.parser.AlterParser  | alter  | v1.0 | 针对alter语句进行解析  |

### 框架中解析器的返回值

返回值是解析器在解析一个SQL语句之后的结果存储对象，所有的结果语句对象实现于"strInfo.result.Statement"抽象类，在结果对象可以通过解析词获取到所有借此词对应的数据，解析器本身也会提供一些函数供人使用。

| 语句结果类型                         | 加入版本 | 支持的解析词                                                                    | 功能  |
|--------------------------------|------|---------------------------------------------------------------------------|-----|
| strInfo.result.Statement       | v1.0 | null                                                                      |     |
| strInfo.result.SelectStatement | v1.0 | select from where group order limit                                       |     |
| strInfo.result.InsertStatement | v1.0 | insert table value values                                                 |     |
| strInfo.result.CreateStatement | v1.0 | create table field option like as                                         |     |
| strInfo.result.AlterStatement  | v1.0 | alter mod addINFO table add drop rename change index primary unique field |     |

#### strInfo.result.SelectStatement 的解析词

该类是针对 select 语句的解析结果进行存储的类，在类中有诸多解析词可以使用，下面就是有关该类解析词的所有解释与说明。

| 解析词    | 加入版本 | 作用                  |
|--------|------|---------------------|
| select | v1.0 | 用于获取到select子句的字符串   |
| from   | v1.0 | 用于获取到查询的表名称         |
| where  | v1.0 | 用于获取到where或者on子句的数据 |
| group  | v1.0 | 用于获取到SQL中的分组语句条件    |
| order  | v1.0 | 用于获取到所有需要排序的列名称     |
| limit  | v1.0 | 用于获取到本次查询中有关分页的信息   |

- 使用示例

```java
package strInfo;

import strInfo.parser.SelectParser;
import strInfo.result.SelectStatement;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 测试用例类
 *
 * @author zhao
 */
public class MAIN {
    public static void main(String[] args) {
        // 构建一个SQL语句
        String sql = "select name, avg(achievement) as a from student right join achievement inner join (select * from sub_zhao;) left join (select * from sub_zhao123;) WHERE student.id = achievement.id and true group by name order by a desc limit 1;";
        // 通过单例设计获取到SQL语句的解析器，并调用parserSQL函数解析SQL
        SelectStatement selectStatement = SelectParser.getInstance().parseSql(sql);
        // 通过返回的结果对象，获取到解析出来的结果数据
        System.out.println("查询的表\t：" + selectStatement.getTableName());
        System.out.println("查询的字段\t：" + Arrays.toString(selectStatement.getFieldNames()));
        System.out.println("select 子句\t：" + selectStatement.getStatementStrByWord("select"));
        System.out.println("group 子句\t：" + selectStatement.getStatementStrByWord("group"));
        System.out.println("order 子句\t：" + selectStatement.getStatementStrByWord("order"));
        System.out.println("是否以正序排序\t：" + selectStatement.isAsc());
        System.out.println("查询的表(数组)\t：" + Arrays.toString(selectStatement.getStatementArrayByWord("from")));
        System.out.println("where中的条件(数组)\t：" + Arrays.toString(selectStatement.getStatementArrayByWord("where")));
        System.out.println("被group的字段(数组)\t：" + Arrays.toString(selectStatement.getStatementArrayByWord("group")));
        System.out.println("被order的字段(数组)\t：" + Arrays.toString(selectStatement.getStatementArrayByWord("order")));
        System.out.println("被解析的整个sql语句\t：" + selectStatement.getSqlStr());
        // 获取到所有的子查询语句
        ArrayList<SelectStatement> subQueryList = selectStatement.getSubQueryList();
        for (SelectStatement statement : subQueryList) {
            System.out.println("子查询表：" + statement.getTableName() + "\t子查询语句：" + statement.getSqlStr());
        }
    }
}
```

- 运行结果

```
查询的表	： student right join achievement inner join , sub_zhao, sub_zhao123
查询的字段	：[name, avg(achievement) as a]
select 子句	：name, avg(achievement) as a
group 子句	：name
order 子句	：a 
是否以正序排序	：-1
查询的表(数组)	：[student, achievement, sub_zhao, sub_zhao123]
where中的条件(数组)	：[ student.id = achievement.id, and,  student.id = achievement.id]
被group的字段(数组)	：[name]
被order的字段(数组)	：[a ]
被解析的整个sql语句	：select name, avg(achievement) as a from student right join achievement inner join (select * from sub_zhao;) left join (select * from sub_zhao123;) WHERE student.id = achievement.id and true group by name order by a desc limit 1;
子查询表： sub_zhao	子查询语句：(select * from sub_zhao;)
子查询表： sub_zhao123	子查询语句：(select * from sub_zhao123;)
```

#### strInfo.parser.InsertParser 的解析词

该类是针对 insert 语句的解析结果进行存储的类，在类中有诸多解析词可以使用，下面就是有关该类解析词的所有解释与说明。

| 解析词 | 加入版本 | 作用  |
|-----|------|-----|
|     |      |     |
|     |      |     |
|     |      |     |

- 使用示例

```java

```

- 运行结果

```

```

#### strInfo.parser.CreateParser 的解析词

该类是针对 create 语句的解析结果进行存储的类，在类中有诸多解析词可以使用，下面就是有关该类解析词的所有解释与说明。

| 解析词 | 加入版本 | 作用  |
|-----|------|-----|
|     |      |     |
|     |      |     |
|     |      |     |

- 使用示例

```java

```

- 运行结果

```

```

#### strInfo.parser.AlterParser 的解析词

该类是针对 alter 语句的解析结果进行存储的类，在类中有诸多解析词可以使用，下面就是有关该类解析词的所有解释与说明。

| 解析词 | 加入版本 | 作用  |
|-----|------|-----|
|     |      |     |
|     |      |     |
|     |      |     |

- 使用示例

```java

```

- 运行结果

```

```