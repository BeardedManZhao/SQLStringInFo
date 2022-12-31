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

| 解析词    | 加入版本 | 作用                   |
|--------|------|----------------------|
| insert | v1.0 | 获取到插入的所有数据对应的字段      |
| table  | v1.0 | 获取到插入语句的作用表          |
| value  | v1.0 | 获取到被插入的数据            |
| values | v1.0 | 获取到被插入的数据，与value效果一样 |

- 使用示例

```java
package strInfo;

import strInfo.parser.InsertParser;
import strInfo.result.InsertStatement;

import java.util.Arrays;

/**
 * 测试用例类
 *
 * @author zhao
 */
public class MAIN {
    public static void main(String[] args) {
        String insertSql = "insert into zhao (name, age) values (\"zhao\", 18), (\"tangYuan\", 19);";
        InsertStatement insertStatement = InsertParser.getInstance().parseSql(insertSql);
        System.out.println("插入数据的目标数据表\t：" + insertStatement.getTableName());
        System.out.println("插入数据的目标字段\t：" + Arrays.toString(insertStatement.getFieldNames()));
        System.out.println("插入的所有字段数据\t：" + insertStatement.getStatementStrByWord("value"));
        System.out.println("插入的所有目标字段(数组)\t：" + Arrays.toString(insertStatement.getStatementArrayByWord("insert")));
        System.out.println("插入的所有字段数据(数组)\t：" + Arrays.toString(insertStatement.getStatementArrayByWord("values")));
    }
}
```

- 运行结果

```
插入数据的目标数据表	：zhao 
插入数据的目标字段	：[name, age]
插入的所有字段数据	：("zhao", 18), ("tangYuan", 19)
插入的所有目标字段(数组)	：[name, age]
插入的所有字段数据(数组)	：["zhao", 18, "tangYuan", 19]
```

#### strInfo.parser.CreateParser 的解析词

该类是针对 create 语句的解析结果进行存储的类，在类中有诸多解析词可以使用，下面就是有关该类解析词的所有解释与说明。

| 解析词    | 加入版本 | 作用                              |
|--------|------|---------------------------------|
| create | v1.0 | 获取到create语句的子句                  |
| table  | v1.0 | 获取到create创建的表                   |
| field  | v1.0 | 获取到创建表中的字段                      |
| option | v1.0 | 获取到表创建时候的其它选项，例如存储引擎等。          |
| like   | v1.0 | 获取到表创建时候的其它引用表，如果使用了其它表的情况下。    |
| as     | v1.0 | 获取到表创建时候的子查询语句，如果使用了子查询创建表的情况下。 |

- 使用示例

```java
package strInfo;

import strInfo.parser.CreateParser;
import strInfo.result.CreateStatement;
import strInfo.result.SelectStatement;

import java.util.Arrays;

/**
 * 测试用例类
 *
 * @author zhao
 */
public class MAIN {
    public static void main(String[] args) {
        String createSQL = """
                create temporary table zhao (
                    name varchar(20),
                    age int
                ) ENGINE=MyISAM DEFAULT CHARSET=utf8;""";

        CreateStatement createStatement = CreateParser.getInstance().parseSql(createSQL);
        System.out.println("创建的表\t：" + createStatement.getTableName());
        System.out.println("是否是临时表\t：" + createStatement.isTemporary());
        System.out.println("表中的字段\t：" + Arrays.toString(createStatement.getFieldNames()));
        System.out.println("创建表时的create子句\t：" + createStatement.getStatementStrByWord("create"));
        System.out.println("创建表时的表名称\t：" + createStatement.getStatementStrByWord("table"));
        System.out.println("创建表时指定的字段\t：" + createStatement.getStatementStrByWord("field"));
        System.out.println("创建表时指定的选项\t：" + createStatement.getStatementStrByWord("option"));
        System.out.println("创建表时指定的选项(数组)\t：" + Arrays.toString(createStatement.getStatementArrayByWord("option")));
        System.out.println("创建表时的like表\t：" + createStatement.getStatementStrByWord("like"));
        System.out.println("sql语句数据：" + createStatement.getSqlStr());
    }
}
```

- 运行结果

```
创建的表	        ：zhao
是否是临时表	：true
表中的字段	：[name varchar(20), age int]
创建表时的create子句 ：temporary table zhao 
创建表时的表名称     ：zhao
创建表时指定的字段 ：
    name varchar(20),
    age int

创建表时指定的选项	：ENGINE=MyISAM DEFAULT CHARSET=utf8
创建表时指定的选项(数组)	：[ENGINE=MyISAM, DEFAULT, CHARSET=utf8]
创建表时的like表	：null
sql语句数据 ：create temporary table zhao (
    name varchar(20),
    age int
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
插入数据的目标数据表	：zhao 
插入数据的目标字段	：[name, age]
插入的所有字段数据	：("zhao", 18), ("tangYuan", 19)
插入的所有目标字段(数组)	：[name, age]
插入的所有字段数据(数组)	：["zhao", 18, "tangYuan", 19]
```

#### strInfo.parser.AlterParser 的解析词

该类是针对 alter 语句的解析结果进行存储的类，在类中有诸多解析词可以使用，下面就是有关该类解析词的所有解释与说明。

| 解析词     | 加入版本 | 作用                                  |
|---------|------|-------------------------------------|
| alter   | v1.0 | 获取到alter语句中alter子句的数据               |
| mod     | v1.0 | 获取到alter语句中的修改模式                    |
| addINFO | v1.0 | 如果修改模式为add，这里是代表添加的模式               |
| table   | v1.0 | 获取到发生变化的表名称                         |
| add     | v1.0 | 获取到add的子句                           |
| drop    | v1.0 | 如果修改模式drop，这里是获取到被删除的表              |
| rename  | v1.0 | 如果修改模式为rename，这里获取的是被重命名之后的新表名      |
| change  | v1.0 | 如果修改模式为change，这里获取的是被重命名之后的新列名      |
| index   | v1.0 | 如果增加模式为index，这里获取的是被添加的索引名称         |
| primary | v1.0 | 如果增加模式为 primary key，这里获取的是被设置为主键的列名 |
| unique  | v1.0 | 如果增加模式为 unique，这里获取的是被设置唯一约束的列名     |
| field   | v1.0 | 这里是获取到所有被修改的列名称                     |

- 使用示例

```java
package strInfo;

import strInfo.parser.AlterParser;
import strInfo.result.AlterStatement;

import java.util.Arrays;

/**
 * 测试用例类
 *
 * @author zhao
 */
public class MAIN {
    public static void main(String[] args) {
        String sql5 = "alter table zhao add index indexName (name, age1);";
/*     TODO 这里是其它的alter，sql语句，同样可以被库解析
        String sql1 = "alter table zhao1 add test varchar(20);";
        String sql2 = "alter table zhao1 drop test;";
        String sql3 = " alter table zhao1 change age age1 int;";
        String sql4 = "alter table zhao1 rename zhao;";    
        String sql6 = "alter table zhao add primary key (name);";
        String sql7 = "alter table zhao add unique (name);";
*/
        AlterParser instance = AlterParser.getInstance();
        AlterStatement alterStatement1 = instance.parseSql(sql5);
        System.out.println("alter的语句子句:" + alterStatement1.getStatementStrByWord("alter"));
        System.out.println("alter作用的表名 :" + alterStatement1.getStatementStrByWord("table"));
        System.out.println("alter增加的子句 :" + alterStatement1.getStatementStrByWord("add"));
        System.out.println("alter删除的表名 :" + alterStatement1.getStatementStrByWord("drop"));
        System.out.println("alter增加的索引 :" + alterStatement1.getStatementStrByWord("index"));
        System.out.println("发生变化的列名称 :" + alterStatement1.getStatementStrByWord("field"));
        System.out.println("alter更新后的表 :" + alterStatement1.getStatementStrByWord("rename"));
        System.out.println("alter更新后的列 :" + alterStatement1.getStatementStrByWord("change"));
        System.out.println("alter的修改模式 :" + alterStatement1.getStatementStrByWord("mod"));
        System.out.println("alter的增加模式 :" + alterStatement1.getStatementStrByWord("addINFO"));
        System.out.println(alterStatement1.getSqlStr());
        // 也可以获取到数组形式的数据
        System.out.println("发生变化的列名称 :" + Arrays.toString(alterStatement1.getStatementArrayByWord("field")));
    }
}
```

- 运行结果

```
alter的语句子句: table zhao add
alter作用的表名 :zhao
alter增加的子句 :add index  add index indexName (name, age1);
alter删除的表名 :null
alter增加的索引 :indexName
发生变化的列名称 :name, age1
alter更新后的表 :null
alter更新后的列 :null
alter的修改模式 :add
alter的增加模式 :index
alter table zhao add index indexName (name, age1);
发生变化的列名称 :[name, age1]
```

<hr>

- 切换到 [English Document]()
- date : 2022-12-31