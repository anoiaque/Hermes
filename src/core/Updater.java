package core;

import adaptors.Adaptor;
import java.util.HashMap;

public class Updater {

  public static int save(HashMap<String, Object> attributes_values, Class<? extends Hermes> model) {
    return Adaptor.get().save(attributes_values, model);
  }



}
