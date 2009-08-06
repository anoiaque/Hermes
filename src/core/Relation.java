package core;

public class Relation {

    public class Cascade {
        public static final String DELETE = "delete";
    }

    private String foreignKeyName;
    private Integer foreignKeyValue=-1;
    private boolean cascadeDelete = false;
    private boolean lazy = false;
    private boolean nullable = true;
    private Jointure jointure;
    
    public Relation() {
    }

    public Relation(String cascade) {
        cascadeDelete = cascade.equals(Cascade.DELETE);
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

    public void setJointure(Jointure jointure) {
        this.jointure = jointure;
    }

    public Jointure getJointure() {
        return jointure;
    }
}
