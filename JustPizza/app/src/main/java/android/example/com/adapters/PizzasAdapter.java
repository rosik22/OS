package android.example.com.adapters;

import android.content.Context;
import android.example.com.model.Pizza;
import android.example.com.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PizzasAdapter extends ArrayAdapter<Pizza> {

    public PizzasAdapter(Context context, List<Pizza> pizzas){
        super(context, 0, pizzas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.pizza_list_item, parent, false);
        }

        Pizza currentPizza = getItem(position);

        TextView nameView = listItemView.findViewById(R.id.name);
        nameView.setText(currentPizza.getName());

        TextView ingredientsView = listItemView.findViewById(R.id.ingredients);
        ingredientsView.setText(currentPizza.getIngredients());

        TextView priceView = listItemView.findViewById(R.id.price);
        String text = "Цена: " + NumberFormat.getCurrencyInstance(new Locale("bg", "BG"))
                .format(currentPizza.getPrice());
        priceView.setText(text);

        return listItemView;
    }
}
