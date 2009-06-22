package core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import pool.Pool;

public class Jointure extends Hermes {
    private int leftId;
    private int rightId;

    public Jointure(){
        
    }
    public Jointure(String jointableName) {
        this.setTableName(jointableName);
        createJoinTable();
    }

    private void createJoinTable() {
        Connection connexion = null;
        Pool pool = Pool.getInstance();
        String sql = "create  table if not exists " + this.getTableName() + "(" + "leftId int default null,rightId int default null); ";
        ResultSet rs = null;
        try {
            connexion = pool.getConnexion();
            PreparedStatement statement = connexion.prepareStatement(sql);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                pool.release(connexion);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public int getLeftId() {
        return leftId;
    }

    public void setLeftId(int leftId) {
        this.leftId = leftId;
    }

    public int getRightId() {
        return rightId;
    }

    public void setRightId(int rightId) {
        this.rightId = rightId;
    }
}
