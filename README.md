# ![image](https://user-images.githubusercontent.com/113756063/210125879-995e2094-101c-4383-be74-8db99d0200bb.png) SQLStringInFo

## introduce

SQLStringInFo is an SQL command parsing library that focuses on parsing SQL command statements. The library provides a
parser for SQL command syntax. Through this library, you can quickly and accurately analyze and process SQL statements.

- MAVEN depends on coordinates

  Now it is supported to add the parsing library to your project using MAVEN. Of course, you can also use gradle to
  connect the maven coordinates!

```xml

<dependency>
    <groupId>io.github.BeardedManZhao</groupId>
    <artifactId>SQLStringInFo</artifactId>
    <version>1.1</version>
</dependency>
```

### Parsers in the framework

The parser is the implementation of SQL statement data processing. It stores specific logic about SQL parsing. In the
library, all parsers implement the strInfo.parser.SQLParser interface, which contains the most basic functions to obtain
data. The following is the information about all current parsers in the library.

| Parser Type                 | Parser Name | Supported Library Versions | Parser function                     |
|-----------------------------|-------------|----------------------------|-------------------------------------|
| strInfo.parser.SQLParser    | null        | v1.0                       | Unify the interfaces of all parsers |
| strInfo.parser.SelectParser | select      | v1.0                       | Parse the "Select" statement        |
| strInfo.parser.InsertParser | insert      | v1.0                       | Parse the "insert" statement        |
| strInfo.parser.CreateParser | create      | v1.0                       | Parse the "create" statement        |
| strInfo.parser.AlterParser  | alter       | v1.0                       | Parse the "alter"statement          |

### The return value of the parser in the framework

The return value is the result storage object after the parser parses an SQL statement. All the result statement objects
are implemented in the "strInfo. result. Statement" abstract class. In the result object, all the data corresponding to
this word can be obtained by parsing words. The parser itself will also provide some functions for people to use.

| Statement result type          | Supported Library Versions | Supported Parsing Words                                                   |
|--------------------------------|----------------------------|---------------------------------------------------------------------------|
| strInfo.result.Statement       | v1.0                       | null                                                                      |
| strInfo.result.SelectStatement | v1.0                       | select from where group order limit                                       |
| strInfo.result.InsertStatement | v1.0                       | insert table value values                                                 |
| strInfo.result.CreateStatement | v1.0                       | create table field option like as                                         |
| strInfo.result.AlterStatement  | v1.0                       | alter mod addINFO table add drop rename change index primary unique field |

#### Parser for strInfo.results.SelectStatement

This class is used to store the parsing results of select statements. There are many parsing words available in the
class. The following is all the explanations and explanations of this class of parsing words.

| Analytic word | Supported Library Versions | Effect                                                    |
|---------------|----------------------------|-------------------------------------------------------|
| select        | v1.0                       | String used to get the select clause                  |
| from          | v1.0                       | The table name used to obtain the query               |
| where         | v1.0                       | Used to obtain the data of where or on clauses        |
| group         | v1.0                       | Used to obtain grouping statement conditions in SQL   |
| order         | v1.0                       | Used to obtain the names of all columns to be sorted  |
| limit         | v1.0                       | Used to obtain information about paging in this query |

- Example of use

```java
package strInfo;

import strInfo.parser.SelectParser;
import strInfo.result.SelectStatement;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * ???????????????
 *
 * @author zhao
 */
public class MAIN {
    public static void main(String[] args) {
        // ????????????SQL??????
        String sql = "select name, avg(achievement) as a from student right join achievement inner join (select * from sub_zhao;) left join (select * from sub_zhao123;) WHERE student.id = achievement.id and true group by name order by a desc limit 1;";
        // ???????????????????????????SQL??????????????????????????????parserSQL????????????SQL
        SelectStatement selectStatement = SelectParser.getInstance().parseSql(sql);
        // ??????????????????????????????????????????????????????????????????
        System.out.println("????????????\t???" + selectStatement.getTableName());
        System.out.println("???????????????\t???" + Arrays.toString(selectStatement.getFieldNames()));
        System.out.println("select ??????\t???" + selectStatement.getStatementStrByWord("select"));
        System.out.println("group ??????\t???" + selectStatement.getStatementStrByWord("group"));
        System.out.println("order ??????\t???" + selectStatement.getStatementStrByWord("order"));
        System.out.println("?????????????????????\t???" + selectStatement.isAsc());
        System.out.println("????????????(??????)\t???" + Arrays.toString(selectStatement.getStatementArrayByWord("from")));
        System.out.println("where????????????(??????)\t???" + Arrays.toString(selectStatement.getStatementArrayByWord("where")));
        System.out.println("???group?????????(??????)\t???" + Arrays.toString(selectStatement.getStatementArrayByWord("group")));
        System.out.println("???order?????????(??????)\t???" + Arrays.toString(selectStatement.getStatementArrayByWord("order")));
        System.out.println("??????????????????sql??????\t???" + selectStatement.getSqlStr());
        // ?????????????????????????????????
        ArrayList<SelectStatement> subQueryList = selectStatement.getSubQueryList();
        for (SelectStatement statement : subQueryList) {
            System.out.println("???????????????" + statement.getTableName() + "\t??????????????????" + statement.getSqlStr());
        }
    }
}
```

- ????????????

```
????????????	??? student right join achievement inner join , sub_zhao, sub_zhao123
???????????????	???[name, avg(achievement) as a]
select ??????	???name, avg(achievement) as a
group ??????	???name
order ??????	???a 
?????????????????????	???-1
????????????(??????)	???[student, achievement, sub_zhao, sub_zhao123]
where????????????(??????)	???[ student.id = achievement.id, and,  student.id = achievement.id]
???group?????????(??????)	???[name]
???order?????????(??????)	???[a ]
??????????????????sql??????	???select name, avg(achievement) as a from student right join achievement inner join (select * from sub_zhao;) left join (select * from sub_zhao123;) WHERE student.id = achievement.id and true group by name order by a desc limit 1;
??????????????? sub_zhao	??????????????????(select * from sub_zhao;)
??????????????? sub_zhao123	??????????????????(select * from sub_zhao123;)
```

#### Parser for strInfo.parser.InsertParser

This class is used to store the parsing results of insert statements. There are many parsing words available in the
class. The following is all the explanations and explanations of this class of parsing words.

| Analytic word | Supported Library Versions | Effect                                                    |
|---------------|----------------------------|-----------------------------------------------------------|
| insert        | v1.0                       | Get the fields corresponding to all inserted data         |
| table         | v1.0                       | Get the Effect table of the insert statement              |
| value         | v1.0                       | Get the inserted data                                     |
| values        | v1.0                       | Get the inserted data, which has the same effect as value |

- Example of use

```java
package strInfo;

import strInfo.parser.InsertParser;
import strInfo.result.InsertStatement;

import java.util.Arrays;

/**
 * ???????????????
 *
 * @author zhao
 */
public class MAIN {
    public static void main(String[] args) {
        String insertSql = "insert into zhao (name, age) values (\"zhao\", 18), (\"tangYuan\", 19);";
        InsertStatement insertStatement = InsertParser.getInstance().parseSql(insertSql);
        System.out.println("??????????????????????????????\t???" + insertStatement.getTableName());
        System.out.println("???????????????????????????\t???" + Arrays.toString(insertStatement.getFieldNames()));
        System.out.println("???????????????????????????\t???" + insertStatement.getStatementStrByWord("value"));
        System.out.println("???????????????????????????(??????)\t???" + Arrays.toString(insertStatement.getStatementArrayByWord("insert")));
        System.out.println("???????????????????????????(??????)\t???" + Arrays.toString(insertStatement.getStatementArrayByWord("values")));
    }
}
```

- ????????????

```
??????????????????????????????	???zhao 
???????????????????????????	???[name, age]
???????????????????????????	???("zhao", 18), ("tangYuan", 19)
???????????????????????????(??????)	???[name, age]
???????????????????????????(??????)	???["zhao", 18, "tangYuan", 19]
```

#### Parser for strInfo.parser.CreateParser

This class is used to store the parsing results of the create statement. There are many parsing words available in the
class. The following is all the explanations and explanations of this class of parsing words.

| Analytic word | Supported Library Versions | Effect                                                                                               |
|---------------|----------------------------|------------------------------------------------------------------------------------------------------|
| create        | v1.0                       | Get the clause of the 'create' statement                                                             |
| table         | v1.0                       | Get the table created by "create"                                                                    |
| field         | v1.0                       | Get the fields in the creation table                                                                 |
| option        | v1.0                       | Get other options when the table is created, such as the storage engine.                             |
| like          | v1.0                       | Get other reference tables when the table is created. If other tables are used.                      |
| as            | v1.0                       | Get the sub query statement when the table is created. If the sub query is used to create the table. |

- Example of use

```java
package strInfo;

import strInfo.parser.CreateParser;
import strInfo.result.CreateStatement;
import strInfo.result.SelectStatement;

import java.util.Arrays;

/**
 * ???????????????
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
        System.out.println("????????????\t???" + createStatement.getTableName());
        System.out.println("??????????????????\t???" + createStatement.isTemporary());
        System.out.println("???????????????\t???" + Arrays.toString(createStatement.getFieldNames()));
        System.out.println("???????????????create??????\t???" + createStatement.getStatementStrByWord("create"));
        System.out.println("????????????????????????\t???" + createStatement.getStatementStrByWord("table"));
        System.out.println("???????????????????????????\t???" + createStatement.getStatementStrByWord("field"));
        System.out.println("???????????????????????????\t???" + createStatement.getStatementStrByWord("option"));
        System.out.println("???????????????????????????(??????)\t???" + Arrays.toString(createStatement.getStatementArrayByWord("option")));
        System.out.println("???????????????like???\t???" + createStatement.getStatementStrByWord("like"));
        System.out.println("sql???????????????" + createStatement.getSqlStr());
    }
}
```

- ????????????

```
????????????	        ???zhao
??????????????????	???true
???????????????	???[name varchar(20), age int]
???????????????create?????? ???temporary table zhao 
????????????????????????     ???zhao
??????????????????????????? ???
    name varchar(20),
    age int

???????????????????????????	???ENGINE=MyISAM DEFAULT CHARSET=utf8
???????????????????????????(??????)	???[ENGINE=MyISAM, DEFAULT, CHARSET=utf8]
???????????????like???	???null
sql???????????? ???create temporary table zhao (
    name varchar(20),
    age int
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
??????????????????????????????	???zhao 
???????????????????????????	???[name, age]
???????????????????????????	???("zhao", 18), ("tangYuan", 19)
???????????????????????????(??????)	???[name, age]
???????????????????????????(??????)	???["zhao", 18, "tangYuan", 19]
```

#### Parser for strInfo.parser.AlterParser

This class is used to store the parsing results of alter statements. There are many parsing words available in the
class. The following is all the explanations and explanations of this kind of parsing words.

| Analytic word | Supported Library Versions | Effect                                                                                     |
|---------------|----------------------------|--------------------------------------------------------------------------------------------|
| alter         | v1.0                       | Get the data of the "alter" clause in the alter statement                                  |
| mod           | v1.0                       | Get the modification mode in the "alter" statement                                         |
| addINFO       | v1.0                       | If the modification mode is "add", this represents the added mode                          |
| table         | v1.0                       | Get the changed table name                                                                 |
| add           | v1.0                       | Get the clause of "add"                                                                    |
| drop          | v1.0                       | If the drop mode is modified, the deleted table is obtained here                           |
| rename        | v1.0                       | If the modification mode is "rename", the new table name after renaming is obtained here   |
| change        | v1.0                       | If the modification mode is "change", the new column name after renaming is obtained here  |
| index         | v1.0                       | If the increase mode is "index", the index name to be added is obtained here               |
| primary       | v1.0                       | If the increase mode is "primary" key, the column name set as primary key is obtained here |
| unique        | v1.0                       | If the increase mode is "unique", the column name with unique constraint is obtained here  |
| field         | v1.0                       | This is the name of all modified columns                                                   |

- Example of use

```java
package strInfo;

import strInfo.parser.AlterParser;
import strInfo.result.AlterStatement;

import java.util.Arrays;

/**
 * ???????????????
 *
 * @author zhao
 */
public class MAIN {
    public static void main(String[] args) {
        String sql5 = "alter table zhao add index indexName (name, age1);";
/*     TODO ??????????????????alter???sql?????????????????????????????????
        String sql1 = "alter table zhao1 add test varchar(20);";
        String sql2 = "alter table zhao1 drop test;";
        String sql3 = " alter table zhao1 change age age1 int;";
        String sql4 = "alter table zhao1 rename zhao;";    
        String sql6 = "alter table zhao add primary key (name);";
        String sql7 = "alter table zhao add unique (name);";
*/
        AlterParser instance = AlterParser.getInstance();
        AlterStatement alterStatement1 = instance.parseSql(sql5);
        System.out.println("alter???????????????:" + alterStatement1.getStatementStrByWord("alter"));
        System.out.println("alterEffect????????? :" + alterStatement1.getStatementStrByWord("table"));
        System.out.println("alter??????????????? :" + alterStatement1.getStatementStrByWord("add"));
        System.out.println("alter??????????????? :" + alterStatement1.getStatementStrByWord("drop"));
        System.out.println("alter??????????????? :" + alterStatement1.getStatementStrByWord("index"));
        System.out.println("???????????????????????? :" + alterStatement1.getStatementStrByWord("field"));
        System.out.println("alter??????????????? :" + alterStatement1.getStatementStrByWord("rename"));
        System.out.println("alter??????????????? :" + alterStatement1.getStatementStrByWord("change"));
        System.out.println("alter??????????????? :" + alterStatement1.getStatementStrByWord("mod"));
        System.out.println("alter??????????????? :" + alterStatement1.getStatementStrByWord("addINFO"));
        System.out.println(alterStatement1.getSqlStr());
        // ???????????????????????????????????????
        System.out.println("???????????????????????? :" + Arrays.toString(alterStatement1.getStatementArrayByWord("field")));
    }
}
```

- ????????????

```
alter???????????????: table zhao add
alterEffect????????? :zhao
alter??????????????? :add index  add index indexName (name, age1);
alter??????????????? :null
alter??????????????? :indexName
???????????????????????? :name, age1
alter??????????????? :null
alter??????????????? :null
alter??????????????? :add
alter??????????????? :index
alter table zhao add index indexName (name, age1);
???????????????????????? :[name, age1]
```

<hr>

- Switch to [????????????](https://github.com/BeardedManZhao/SQLStringInFo/blob/core/README-Chinese.md)
- date : 2022-12-31
