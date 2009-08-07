package core;

public class Table {

  public static String nameFor(Class<? extends Hermes> model) {
    String tableName = "";
    try {
      tableName = (String) model.getMethod("getTableName").invoke(model.newInstance());
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return tableName;
  }
}
