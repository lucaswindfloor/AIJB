package org.jeecg.common.util.sqlparse;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import org.jeecg.common.util.sqlparse.vo.SelectSqlInfo;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * jsqlparser AllTable anager
 *
 * @author: jeecg-boot
 */
public class JSqlParserAllTableManager {

    private final Map<String, SelectSqlInfo> allSelectTables = new HashMap<>();

    public JSqlParserAllTableManager(String selectSql) throws JSQLParserException {
        // 1、创建解析器
        CCJSqlParserManager mgr = new CCJSqlParserManager();
        // 2、使用解析器解析sql生成具有层次结构的java类
        Statement stmt = mgr.parse(new StringReader(selectSql));
        if (stmt instanceof Select) {
            Select selectStatement = (Select) stmt;
            // 3、解析select查询sql的信息
            this.parseSelectBody(selectStatement.getSelectBody());
        }
    }

    /**
     * 解析 select 查询sql的信息
     *
     * @param selectBody
     * @return
     */
    private void parseSelectBody(SelectBody selectBody) {
        // 判断是否使用了union等操作
        if (selectBody instanceof SetOperationList) {
            // 如果使用了union等操作
            List<SelectBody> selectBodyList = ((SetOperationList) selectBody).getSelects();
            for (SelectBody body : selectBodyList) {
                this.parseSelectBody(body);
            }
            return;
        }
        // 简单的select查询
        if (selectBody instanceof PlainSelect) {
            PlainSelect plainSelect = (PlainSelect) selectBody;
            FromItem fromItem = plainSelect.getFromItem();
            // 解析 from
            this.parseFromItem(fromItem);
            // 解析 joins
            List<Join> joins = plainSelect.getJoins();
            if (joins != null && joins.size() > 0) {
                for (Join join : joins) {
                    this.parseFromItem(join.getRightItem());
                }
            }
            // 解析 select a,b,c
            List<SelectItem> selectItems = plainSelect.getSelectItems();
            for (SelectItem selectItem : selectItems) {
                this.parseSelectItem(selectItem);
            }
        }
    }

    private void parseFromItem(FromItem fromItem) {
        // 解析 表名
        if (fromItem instanceof Table) {
            // 通过表名的方式from
            Table fromTable = (Table) fromItem;
            String aliasName = null;
            if (fromItem.getAlias() != null) {
                aliasName = fromItem.getAlias().getName();
            }
            this.allSelectTables.put(fromTable.getName(), new SelectSqlInfo(fromTable.getName(), aliasName));
        } else if (fromItem instanceof SubSelect) {
            // 通过子查询的方式from
            SubSelect fromSubSelect = (SubSelect) fromItem;
            this.parseSelectBody(fromSubSelect.getSelectBody());
        }
    }

    private void parseSelectItem(SelectItem selectItem) {
        if (selectItem instanceof AllColumns || selectItem instanceof AllTableColumns) {
            // 全部字段
        } else if (selectItem instanceof SelectExpressionItem) {
            // 获取单个查询字段名
            SelectExpressionItem selectExpressionItem = (SelectExpressionItem) selectItem;
            Expression expression = selectExpressionItem.getExpression();
            Alias alias = selectExpressionItem.getAlias();
            this.handleExpression(expression, alias);
        }
    }

    /**
     * 处理查询字段表达式
     *
     * @param expression
     * @param alias      是否有别名，无传null
     */
    private void handleExpression(Expression expression, Alias alias) {
        // 处理函数式字段  CONCAT(name,'(',age,')')
        if (expression instanceof Function) {
            this.handleFunctionExpression((Function) expression);
            return;
        }
        // 处理字段上的子查询
        if (expression instanceof SubSelect) {
            SubSelect subSelect = (SubSelect) expression;
            this.parseSelectBody(subSelect.getSelectBody());
            return;
        }
        // 获取真实字段名
        if (expression instanceof Column) {
            Column column = (Column) expression;
            String tableName = column.getTable().getName();
            SelectSqlInfo selectSqlInfo = this.allSelectTables.get(tableName);
            if (selectSqlInfo != null) {
                String realSelectField = column.getColumnName();
                String selectField = realSelectField;
                // 判断是否有别名
                if (alias != null) {
                    selectField = alias.getName();
                }
                selectSqlInfo.addSelectField(selectField, realSelectField);
            }
        }
    }

    /**
     * 处理函数式字段
     *
     * @param functionExp
     */
    private void handleFunctionExpression(Function functionExp) {
        if (functionExp.getParameters() != null) {
            List<Expression> expressions = functionExp.getParameters().getExpressions();
            for (Expression expression : expressions) {
                this.handleExpression(expression, null);
            }
        }
    }

    /**
     * 解析sql
     */
    public Map<String, SelectSqlInfo> parse() {
        return this.allSelectTables;
    }

}
