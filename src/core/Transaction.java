package core;

import java.sql.Connection;
import java.sql.SQLException;

import pool.Pool;

public class Transaction {

	Connection	connexion	= Pool.getInstance().getConnexion();
	boolean			rollback;

	public void begin() {
		try {
			rollback = false;
			connexion.setAutoCommit(false);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void end() {
		try {
			if (rollback) connexion.rollback();
			else connexion.commit();
			connexion.setAutoCommit(true);
			Pool.getInstance().release(connexion);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean save(Hermes object) {
		try {
			Updater.save(object, connexion);
		}
		catch (SQLException e) {
			rollback = true;
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean execute(String sql) {
		try {
			Updater.executeSql(sql, null, connexion);
		}
		catch (SQLException e) {
			rollback = true;
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean delete(Hermes object) {
		try {
			Updater.delete(object, connexion);
			object.setId(0);
		}
		catch (SQLException e) {
			rollback = true;
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void finalize() {
		try {
			connexion.setAutoCommit(true);
			Pool.getInstance().release(connexion);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
