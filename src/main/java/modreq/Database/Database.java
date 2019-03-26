package modreq.Database;

import modreq.Modreq;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {
	public static String getLastId() {
		ResultSet rs = null;
		try {
			rs = Modreq.statement.executeQuery("SELECT PK_idm FROM modreqs ORDER BY PK_idm desc LIMIT 1");
			while(rs.next()) {
				return rs.getString("PK_idm");
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return "0";
	}

	public static String InsertModreq(String uuid, String text, String world, String x, String y, String z) {
		try {
			Modreq.statement.execute("INSERT INTO modreqs (uuid,text, world, x,y, z) VALUES('" + uuid + "','" + text + "','" + world + "','" + x + "','" + y + "','" + z + "')");
		} catch(Exception e) {
			e.printStackTrace();
		}

		return Database.getLastId();
	}

	public static ResultSet getTicketId(String id) {
		try {
			return Modreq.statement.executeQuery("SELECT * FROM modreqs WHERE PK_idm =" + id);
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ResultSet getTickets(String limit, String offset, String status) {
		try {
			return Modreq.statement.executeQuery("SELECT * FROM modreqs WHERE status = '" + status + "' LIMIT " + limit + " OFFSET " + offset + "");
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void ClaimTicket(String id, String claim_uuid) {
		try {
			Modreq.statement.execute("UPDATE modreqs SET claim_uuid='" + claim_uuid + "' WHERE PK_idm=" + id);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void UnClaimTicket(String id) {
		try {
			Modreq.statement.execute("UPDATE modreqs SET claim_uuid=null WHERE PK_idm=" + id);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void CloseTicket(String id, String answer) {
		try {
			Modreq.statement.execute("UPDATE modreqs SET status='close', answer='" + answer + "' WHERE PK_idm=" + id);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static ResultSet getOwnTickets(String uuid) {
		try {
			return Modreq.statement.executeQuery("SELECT * FROM modreqs WHERE uuid ='" + uuid + "'");
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ResultSet getOwnTicketsNotSend(String uuid) {
		try {
			return Modreq.statement.executeQuery("SELECT * FROM modreqs WHERE uuid ='" + uuid + "' AND send =0");
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ResultSet UpdateSend(String id) {
		try {
			Modreq.statement.execute("UPDATE modreqs SET send=1 WHERE PK_idm ='" + id + "'");
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ResultSet UpdateNotSend(String id) {
		try {
			Modreq.statement.execute("UPDATE modreqs SET send=0 WHERE PK_idm ='" + id + "'");
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
