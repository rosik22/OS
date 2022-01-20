package android.example.com.adapters;

import android.content.Context;
import android.example.com.model.PizzaDto;
import android.example.com.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CartPizzaAdapter extends ArrayAdapter<PizzaDto> {

    public CartPizzaAdapter(Context context, List<PizzaDto> pizzas) {
        super(context, 0, pizzas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.cart_pizza_list_item, parent, false);
        }

        PizzaDto currentPizza = getItem(position);

        TextView nameView = listItemView.findViewById(R.id.name);
        nameView.setText(currentPizza.getName());

        TextView qtyView = listItemView.findViewById(R.id.quantity);
        String text = "Количество: " + currentPizza.getQty();
        qtyView.setText(text);

        return listItemView;
    }
}