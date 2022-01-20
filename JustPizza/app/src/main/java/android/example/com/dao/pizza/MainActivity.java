package android.example.com.dao.pizza;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.example.com.utils.HttpHandler;
import android.example.com.model.Pizza;
import android.example.com.R;
import android.example.com.adapters.PizzasAdapter;
import android.example.com.dao.cart.CartActivity;
import android.example.com.fragments.PizzaExtrasFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();
    private final String fragmentTag = "pizzaFragmentTag";
    PizzaExtrasFragment pizzaExtrasFragment;
    List<Pizza> pizzaList;
    private ListView lv;
    private PizzasAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pizzas);

        pizzaList = new ArrayList<>();
        lv = findViewById(R.id.list);
        new GetPizzas().execute();

        getSupportActionBar();
    }

    // menu initializer
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cart_menu_item) {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("StaticFieldLeak")
    private class GetPizzas extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // loading pizza info
        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String url = "https://raw.githubusercontent.com/rosik22/OS/main/pizzas.json";
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray pizzas = jsonObj.getJSONArray("pizzas");

                    JSONObject c;
                    String name, ingredients;
                    int price;

                    for (int i = 0; i < pizzas.length(); i++) {
                        c = pizzas.getJSONObject(i);
                        name = c.getString("name");
                        ingredients = c.getString("ingredients");
                        price = Integer.parseInt(c.getString("price"));

                        Pizza itemPizza = new Pizza(name, ingredients, price);
                        pizzaList.add(itemPizza);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(),
                            "Json parsing error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show());

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(() -> Toast.makeText(getApplicationContext(),
                        "Couldn't get json from server. Check LogCat for possible errors!",
                        Toast.LENGTH_LONG).show());
            }

            return null;
        }

        // on item selected listener
        // navigates to pizza fragment
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            adapter = new PizzasAdapter(
                    MainActivity.this, pizzaList);
            lv.setAdapter(adapter);

            lv.setOnItemClickListener((parent, view, position, id) -> {
                        Pizza currentPizza = adapter.getItem(position);

                        pizzaExtrasFragment = new PizzaExtrasFragment();
                        pizzaExtrasFragment.setPizza(currentPizza);
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_id, pizzaExtrasFragment, fragmentTag).commit();
                    }
            );
        }
    }

}