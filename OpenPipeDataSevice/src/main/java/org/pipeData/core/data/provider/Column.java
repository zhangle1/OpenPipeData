package org.pipeData.core.data.provider;


import lombok.Data;
import org.pipeData.core.base.consts.ValueType;

import java.io.Serializable;
import java.util.List;

@Data
public class Column implements Serializable {

    private String [] name;

    private ValueType type;

    private String fmt;

    private List<ForeignKey> foreignKeys;


    public Column(String[] name, ValueType type) {
        this.name = name;
        this.type = type;
    }

    public Column() {
    }

    public static Column of(ValueType type, String... names) {
        return new Column(names, type);
    }

    public String columnName() {
        return name[name.length - 1];
    }

    public String tableName() {
        if (name.length == 1) {
            return null;
        } else {
            return name[name.length - 2];
        }
    }

    public String columnKey() {
        return String.join(".", name);
    }

    public void setName(String... name) {
        this.name = name;
    }


}
