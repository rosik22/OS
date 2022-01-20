package android.example.com.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.example.com.model.PizzaDto;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "pizzadb";

    private static final int DB_VERSION = 1;

    private static final String TABLE_NAME = "pizzas";

    private static final String NAME_COL = "name";

    private static final String QTY_COL = "qty";

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + NAME_COL + " TEXT UNIQUE,"
                + QTY_COL + " INTEGER)";

        db.execSQL(query);
    }

    public void addNewPizza(String pizzaName, int pizzaQty) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(NAME_COL, pizzaName);
        values.put(QTY_COL, pizzaQty);

        db.insert(TABLE_NAME, null, values);

        db.close();
    }

    public List<PizzaDto> getPizzas() {
        List<PizzaDto> pizzas = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorPizzas = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursorPizzas.moveToFirst()) {
            do {
                pizzas.add(new PizzaDto(cursorPizzas.getString(0), cursorPizzas.getInt(1)));
            } while (cursorPizzas.moveToNext());
        }
        cursorPizzas.close();

        return pizzas;
    }

    public void updatePizza(String pizzaName, int pizzaQty) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(QTY_COL, pizzaQty);

        db.update(TABLE_NAME, values, "name=?", new String[]{pizzaName});
        db.close();
    }

    public int getPizzaQty(String pizzaName) {
        int qty = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorPizzas = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursorPizzas.moveToFirst()) {
            do {
                if (cursorPizzas.getString(0).equals(pizzaName)) {
                    qty = cursorPizzas.getInt(1);
                }
            } while (cursorPizzas.moveToNext());
        }
        cursorPizzas.close();

        return qty;
    }

    public void clearTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}