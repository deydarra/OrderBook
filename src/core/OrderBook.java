package core;

public class OrderBook implements Comparable<OrderBook> {
    private  int price;
    private  int size;
    public int getPrice(){return price;}
    public void setPrice(int price){this.price = price;}
    public int getSize(){return size;}
    public void setSize(int size){this.size = size;}

    @Override
    public int compareTo(OrderBook o) {
        return this.getPrice()/(this.getSize()+1) - o.getPrice()/(o.getSize()+1);
    }

    public String toString(){
        return String.format(getPrice() + "," + getSize() + "\n");
    }


}

