package pl.lewica.lewicapl.android.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class LewicaPLDAO {

	private Context context;
	protected SQLiteDatabase database;
	protected LewicaPLSQLiteOpenHelper dbHelper;


	public LewicaPLDAO(Context context) {
		this.context	= context;
	}


	public void open()
			throws SQLException {
		dbHelper	= new LewicaPLSQLiteOpenHelper(context);
		database	= dbHelper.getReadableDatabase();
	}


	public void close() {
		dbHelper.close();
	}
}
