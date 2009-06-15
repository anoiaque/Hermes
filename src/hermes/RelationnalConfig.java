package hermes;

public class RelationnalConfig {
    private String  foreignKeyName;
    private int     foreignKeyValue = 0;
    private boolean cascadeDelete   = false;
    private boolean lazy            = false;
    private boolean nullable        = true;

    public RelationnalConfig() {
    }

    public RelationnalConfig(boolean cascade_delete) {
        this.cascadeDelete = cascade_delete;
    }

    public String getForeignKeyName() {
        return foreignKeyName;
    }

    public void setForeignKeyName(String foreignKeyName) {
        this.foreignKeyName = foreignKeyName;
    }

    public int getForeignKeyValue() {
        return foreignKeyValue;
    }

    public void setForeignKeyValue(int foreignKeyValue) {
        this.foreignKeyValue = foreignKeyValue;
    }

    public boolean isLazy() {
        return lazy;
    }

    public void setLazy(boolean lazy) {
        this.lazy = lazy;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public void setCascadeDelete(boolean cascadeDelete) {
        this.cascadeDelete = cascadeDelete;
    }

    public boolean isCascadeDelete() {
        return cascadeDelete;
    }
}
