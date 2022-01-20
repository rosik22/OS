package android.example.com.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.example.com.model.Pizza;
import android.example.com.R;
import android.example.com.db.DBHandler;
import android.example.com.utils.PizzaUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.text.NumberFormat;
import java.util.Locale;

public class PizzaExtrasFragment extends Fragment implements View.OnClickListener {
    private View fragment;

    private DBHandler dbHandler;
    private String pizzaName;
    private RadioGroup pizza_with_extras;
    private TextView quantityTextView, priceTextView, pizzaNameTextView;
    private int basePrice, price = 0, quantity = 1;

    private CheckBox mushroomsCheckBox, cornCheckBox, picklesCheckBox, extrasCheckBox;
    private boolean isExtrasChecked, isMushroomsChecked, isCornChecked, isPicklesChecked;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            quantity = savedInstanceState.getInt("quantity");
            price = savedInstanceState.getInt("price");
            basePrice = savedInstanceState.getInt("basePrice");
            pizzaName = savedInstanceState.getString("pizzaName");
            isExtrasChecked = savedInstanceState.getBoolean("isExtrasChecked");
            isMushroomsChecked = savedInstanceState.getBoolean("isMushroomsChecked");
            isCornChecked = savedInstanceState.getBoolean("isCornChecked");
            isPicklesChecked = savedInstanceState.getBoolean("isPicklesChecked");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.pizza_extras_fragment, container, false);

        // load buttons
        pizza_with_extras = fragment.findViewById(R.id.additives_group);
        quantityTextView = fragment.findViewById(R.id.quantity_text_view);
        priceTextView = fragment.findViewById(R.id.price_text_view);
        pizzaNameTextView = fragment.findViewById(R.id.pizza_name_extras_view);

        extrasCheckBox = fragment.findViewById(R.id.pizza_with_add);
        mushroomsCheckBox = fragment.findViewById(R.id.mushrooms);
        cornCheckBox = fragment.findViewById(R.id.corn);
        picklesCheckBox = fragment.findViewById(R.id.pickles);

        display(quantity);
        displayMessage(basePrice);

        pizzaNameTextView.setText(pizzaName);
        pizza_with_extras.setVisibility(GONE);
        getActivity().findViewById(R.id.list).setVisibility(GONE);

        if(savedInstanceState != null){
            extrasCheckBox.setChecked(isExtrasChecked);
            if(isExtrasChecked){
                pizza_with_extras.setVisibility(VISIBLE);
            }
            mushroomsCheckBox.setChecked(isMushroomsChecked);
            cornCheckBox.setChecked(isCornChecked);
            picklesCheckBox.setChecked(isPicklesChecked);
            displayMessage(price);
        }

        // assign a listener to all buttons
        fragment.findViewById(R.id.back_button).setOnClickListener(this::onBack);
        extrasCheckBox.setOnClickListener(this);
        fragment.findViewById(R.id.decrement_button).setOnClickListener(this);
        fragment.findViewById(R.id.increment_button).setOnClickListener(this);
        mushroomsCheckBox.setOnClickListener(this);
        cornCheckBox.setOnClickListener(this);
        picklesCheckBox.setOnClickListener(this);
        fragment.findViewById(R.id.add_button).setOnClickListener(this);

        return fragment;
    }

    public void setPizza(Pizza pizza) {
        this.pizzaName = pizza.getName();
        this.basePrice = pizza.getPrice();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("quantity", quantity);
        savedInstanceState.putInt("price", price);
        savedInstanceState.putInt("basePrice", basePrice);
        savedInstanceState.putString("pizzaName", pizzaName);
        savedInstanceState.putBoolean("isExtrasChecked", extrasCheckBox.isChecked());
        savedInstanceState.putBoolean("isMushroomsChecked", mushroomsCheckBox.isChecked());
        savedInstanceState.putBoolean("isCornChecked", cornCheckBox.isChecked());
        savedInstanceState.putBoolean("isPicklesChecked", picklesCheckBox.isChecked());

        super.onSaveInstanceState(savedInstanceState);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.pizza_with_add:
                onExtrasButtonClicked(view);
                price = calculatePrice();
                break;
            case R.id.decrement_button:
                decrement(view);
                price = calculatePrice();
                break;
            case R.id.increment_button:
                increment(view);
                price = calculatePrice();
                break;
            case R.id.add_button:
                addToOrder(view);
                price = calculatePrice();
                Toast.makeText(getActivity(), "Добавихте " + pizzaName, Toast.LENGTH_SHORT).show();
                onBack(view);
                break;
            case R.id.mushrooms:
            case R.id.corn:
            case R.id.pickles:
                price = calculatePrice();
                break;
        }

        displayMessage(price);
    }

    public void onExtrasButtonClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        if (view.getId() == R.id.pizza_with_add) {
            if (checked)
                fragment.findViewById(R.id.additives_group).setVisibility(VISIBLE);
            else
                fragment.findViewById(R.id.additives_group).setVisibility(GONE);
        }
    }

    public void decrement(View view) {
        if (quantity == 1) {
            Toast.makeText(getActivity(), "Не може да добавите по-малко от една пица", Toast.LENGTH_SHORT).show();
            return;
        }
        quantity = quantity - 1;
        display(quantity);
    }

    public void increment(View view) {
        if (quantity == 100) {
            Toast.makeText(getActivity(), "Не може да добавите повече от 100 пици", Toast.LENGTH_SHORT).show();
            return;
        }
        quantity = quantity + 1;
        display(quantity);
    }

    public void addToOrder(View view) {
        PizzaUtil.total += calculatePrice();

        dbHandler = new DBHandler(getActivity());
        int currentQty = dbHandler.getPizzaQty(pizzaName);
        if (currentQty == 0) {
            dbHandler.addNewPizza(pizzaName, quantity);
        } else {
            dbHandler.updatePizza(pizzaName, quantity + currentQty);
        }
    }

    public void onBack(View view) {
        getActivity().findViewById(R.id.list).setVisibility(VISIBLE);
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    private int calculatePrice() {
        mushroomsCheckBox = fragment.findViewById(R.id.mushrooms);
        boolean hasMushrooms = mushroomsCheckBox.isChecked();

        cornCheckBox = fragment.findViewById(R.id.corn);
        boolean hasCorn = cornCheckBox.isChecked();

        picklesCheckBox = fragment.findViewById(R.id.pickles);
        boolean hasPickles = picklesCheckBox.isChecked();

        int localPrice = basePrice;
        extrasCheckBox = fragment.findViewById(R.id.pizza_with_add);
        if (extrasCheckBox.isChecked()) {

            if (hasMushrooms) {
                localPrice += 1;
            }

            if (hasCorn) {
                localPrice += 2;
            }

            if (hasPickles) {
                localPrice += 3;
            }
        }

        return quantity * localPrice;
    }

    // displays the quantity
    private void display(int number) {
        quantityTextView.setText("" + number);
    }

    // displays the total pizza price
    private void displayMessage(int price) {
        String message = getString(R.string.order_summary_price) + " " + NumberFormat.getCurrencyInstance(new Locale("bg", "BG")).format(price);
        priceTextView.setText(message);
    }
}