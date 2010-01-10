package core;

public class HasOne {

  private Hermes parent;
  private Hermes child;
  private String attributeName;
  private String fkName;
  private Integer fkValue = null;
  private boolean cascadeDelete = false;

  //Constuctors
  public HasOne(String attribute, String dependency) {
    attributeName = attribute;
    fkName = Inflector.foreignKeyName(attribute);
    cascadeDelete = dependency.equals("dependent:destroy");
  }

  // Getters & Setters
  public String getAttributeName() {
    return attributeName;
  }

  public void setAttributeName(String attributeName) {
    this.attributeName = attributeName;
  }

  public Hermes getChild() {
    return child;
  }

  public void setChild(Hermes child) {
    this.child = child;
  }

  public String getFkName() {
    return fkName;
  }

  public void setFkName(String fkName) {
    this.fkName = fkName;
  }

  public Hermes getParent() {
    return parent;
  }

  public void setParent(Hermes parent) {
    this.parent = parent;
  }

  public boolean isCascadeDelete() {
    return cascadeDelete;
  }

  public void setCascadeDelete(boolean cascadeDelete) {
    this.cascadeDelete = cascadeDelete;
  }

  public Integer getFkValue() {
    return fkValue;
  }

  public void setFkValue(Integer fkValue) {
    this.fkValue = fkValue;
  }
}
