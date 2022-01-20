package android.example.com.dao.cart;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.example.com.adapters.CartPizzaAdapter;
import android.example.com.dao.pizza.MainActivity;
import android.example.com.model.PizzaDto;
import android.example.com.R;
import android.example.com.db.DBHandler;
import android.example.com.utils.PizzaUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    List<PizzaDto> pizzaList;
    CartPizzaAdapter adapter;
    String citySelected, dateSelected, timeSelected;
    Button buttonDate, buttonTime;
    private ListView lv;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private TextView textViewDate, textViewTime, textViewCity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            dateSelected = savedInstanceState.getString("date");
            timeSelected = savedInstanceState.getString("time");
        }
        setContentView(R.layout.cart);

        lv = findViewById(R.id.cart_list);
        textViewCity = findViewById(R.id.city_text_view);
        buttonDate = findViewById(R.id.buttonDate);
        textViewDate = findViewById(R.id.textViewDate);
        buttonTime = findViewById(R.id.buttonTime);
        textViewTime = findViewById(R.id.textViewTime);

        findViewById(R.id.order_button).setOnClickListener(this::onOrder);

        if (savedInstanceState != null) {
            textViewDate.setText(dateSelected);
            textViewTime.setText(timeSelected);
        } else {
            setInitialDate();
            setInitialTime();
        }

        displayPrice();
        setSpinner();
        setDateAndTimeListener();

        getSupportActionBar();
        new GetPizzas().execute();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("city", citySelected);
        savedInstanceState.putString("time", timeSelected);
        savedInstanceState.putString("date", dateSelected);

        super.onSaveInstanceState(savedInstanceState);
    }

    // loads menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.pizza_menu_item) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    // initializes the spinner
    private void setSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.cities_spinner);

        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.cities_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    // gets the selected value from the spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        citySelected = (String) parent.getItemAtPosition(pos);
        textViewCity.setText(citySelected);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    private void setDateAndTimeListener() {
        buttonDate.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);

            datePickerDialog = new DatePickerDialog(CartActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        dateSelected = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                        textViewDate.setText(dateSelected);
                    }, year, month, day);
            datePickerDialog.show();
        });

        buttonTime.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            int hour = cldr.get(Calendar.HOUR_OF_DAY);
            int minute = cldr.get(Calendar.MINUTE);

            timePickerDialog = new TimePickerDialog(CartActivity.this,
                    (view, hour1, minute1) -> {
                        timeSelected = hour1 + ":" + minute1;
                        textViewTime.setText(timeSelected);
                    }, hour, minute, true);
            timePickerDialog.show();
        });

    }

    private void setInitialDate() {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        dateSelected = day + "/" + (month + 1) + "/" + year;
        textViewDate.setText(dateSelected);
    }

    private void setInitialTime() {
        final Calendar cldr = Calendar.getInstance();
        int hour = cldr.get(Calendar.HOUR_OF_DAY);
        int minute = cldr.get(Calendar.MINUTE);
        timeSelected = hour + ":" + minute;
        textViewTime.setText(timeSelected);
    }

    public void onOrder(View view) {
        if (PizzaUtil.total == 0) {
            Toast.makeText(this, "Поръчката Ви трябва да надвишава 0лв!", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Желаете ли да продължите напред?")
                .setTitle("Приключване на поръчката")
                .setCancelable(false)
                .setPositiveButton("Да", (dialog, id) -> {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);

                    intent.setData(Uri.parse("mailto:"));
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"rosinator99@abv.bg"});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Just Pizza order for " + "Rosi");
                    String emailContent = "Потвърждение на доствка!" +
                            "\nГрад за доставка: " + citySelected +
                            "\nДата на доставка: " + textViewDate.getText() +
                            "\nЧас на доставка: " + textViewTime.getText() +
                            "\nСъдържание: " + pizzaList.toString();
                    intent.putExtra(Intent.EXTRA_TEXT, emailContent);

                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }

                    dialog.cancel();

                    DBHandler dbHandler = new DBHandler(CartActivity.this);
                    dbHandler.clearTable();

                    PizzaUtil.total = 0;

                    finish();
                })
                .setNegativeButton("Не", (dialog, id) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void displayPrice() {
        TextView cartTextView = findViewById(R.id.price_text_view);
        String message = getString(R.string.order_summary_price) + " " + NumberFormat.getCurrencyInstance(new Locale("bg", "BG")).format(PizzaUtil.total);
        cartTextView.setText(message);
    }

    // thread for loading the order data (pizzas & quantity)
    @SuppressLint("StaticFieldLeak")
    private class GetPizzas extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            DBHandler dbHandler = new DBHandler(CartActivity.this);
            pizzaList = dbHandler.getPizzas();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            adapter = new CartPizzaAdapter(
                    CartActivity.this, pizzaList);
            lv.setAdapter(adapter);
        }
    }
}
