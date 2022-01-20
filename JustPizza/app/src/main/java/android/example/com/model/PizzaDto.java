package android.example.com.model;

public class PizzaDto {
    String name;
    int qty;

    public PizzaDto(String name, int qty){
        this.name = name;
        this.qty = qty;
    }

    public String getName(){
        return name;
    }

    public int getQty(){
        return qty;
    }

    @Override
    public String toString(){
        return "\n" + name + " х " + qty;
    }
}
