package com.kalus.tdengineorm.wrapper;

import cn.hutool.core.util.StrUtil;
import com.kalus.tdengineorm.enums.TdTwoParamsSelectFuncEnum;
import com.kalus.tdengineorm.enums.TdWindFuncTypeEnum;
import com.klaus.fd.constant.SqlConstant;
import lombok.EqualsAndHashCode;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Tdengine查询包装
 *
 * @author Klaus
 * @date 2024/05/11
 */
public class TdQueryWrapper<T> extends AbstractTdQueryWrapper<T> {

    public TdQueryWrapper(Class<T> entityClass) {
        super(entityClass);
    }

    public TdQueryWrapper<T> selectAll() {
        doSelectAll();
        return this;
    }

    public TdQueryWrapper<T> select(String... columnNames) {
        addColumnNames(columnNames);
        return this;
    }

    public final TdQueryWrapper<T> selectFunc(TdTwoParamsSelectFuncEnum selectFuncEnum, String... columnNames) {
        String[] array = Arrays.stream(columnNames)
                .map(columnName -> buildAggregationFunc(selectFuncEnum, columnName, columnName))
                .toArray(String[]::new);
        addColumnNames(array);
        return this;
    }

    public TdQueryWrapper<T> selectFunc(TdTwoParamsSelectFuncEnum selectFuncEnum, String columnName) {
        addColumnName(buildAggregationFunc(selectFuncEnum, columnName, columnName));
        return this;
    }

    public TdQueryWrapper<T> selectFunc(TdTwoParamsSelectFuncEnum selectFuncEnum, String columnName, String aliasColumnName) {
        addColumnName(buildAggregationFunc(selectFuncEnum, columnName, aliasColumnName));
        return this;
    }

    public TdQueryWrapper<T> from(String tbName) {
        super.tbName = tbName;
        return this;
    }

    public TdQueryWrapper<T> eq(String columnName, Object value) {
        if (StrUtil.isNotBlank(where)) {
            where.append(SqlConstant.AND);
        }
        String paramName = genParamName();
        this.where.append(columnName).append(SqlConstant.EQUAL).append(SqlConstant.COLON).append(paramName);
        getParamsMap().put(paramName, value);
        return this;
    }

    public TdQueryWrapper<T> and() {
        this.where.append(SqlConstant.BLANK).append(SqlConstant.AND).append(SqlConstant.BLANK);
        return this;
    }

    public TdQueryWrapper<T> and(Consumer<TdQueryWrapper<T>> consumer) {
        consumer.accept(this);
        return this;
    }

    public TdQueryWrapper<T> or(String columnName, Object value) {
        this.where.append(SqlConstant.BLANK)
                .append(SqlConstant.OR)
                .append(SqlConstant.BLANK);
        return this;
    }

    public TdQueryWrapper<T> ne(String columnName, Object value) {
        addWhereParam(value, columnName, columnName, SqlConstant.NE);
        return this;
    }


    public TdQueryWrapper<T> notNull(String columnName, Object value) {
        addWhereParam(value, columnName, columnName, SqlConstant.IS_NOT_NULL);
        return this;
    }

    public TdQueryWrapper<T> intervalWindow(String interval) {
        doWindowFunc(TdWindFuncTypeEnum.INTERVAL, interval);
        return this;
    }


    public TdQueryWrapper<T> stateWindow(String column) {
        doWindowFunc(TdWindFuncTypeEnum.STATE_WINDOW, column);
        return this;
    }


    public TdQueryWrapper<T> orderByAsc(String columnName) {
        if (StrUtil.isNotBlank(orderBy)) {
            orderBy.append(SqlConstant.COMMA);
        }
        orderBy.append(columnName);
        return this;
    }


    public TdQueryWrapper<T> orderByDesc(String columnName) {
        if (StrUtil.isNotBlank(orderBy)) {
            orderBy.append(SqlConstant.COMMA);
        }
        orderBy.append(SqlConstant.ORDER_BY)
                .append(columnName)
                .append(SqlConstant.BLANK)
                .append(SqlConstant.DESC);
        return this;
    }


    public TdQueryWrapper<T> innerQueryWrapper(Consumer<TdQueryWrapper<T>> innerQueryWrapperConsumer) {
        TdQueryWrapper<T> innerWrapper = TdWrappers.queryWrapper(getEntityClass());
        innerQueryWrapperConsumer.accept(innerWrapper);
        doInnerWrapper(innerWrapper);
        return this;
    }

    public TdQueryWrapper<T> limit(int count) {
        doLimit(SqlConstant.LIMIT + count);
        return this;
    }

    /**
     * 分页
     *
     * @param pageNo   页码, 起始为1
     * @param pageSize 页大小
     * @return {@link TdQueryWrapper}<{@link T}>
     */
    public TdQueryWrapper<T> limit(int pageNo, int pageSize) {
        doLimit(pageNo, pageSize);
        return this;
    }
}
