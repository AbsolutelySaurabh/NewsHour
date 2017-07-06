package com.example.absolutelysaurabh.counterapp;


import android.icu.text.NumberFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.Locale;

/**
 * This app displays an order form to order coffee.
 */
public class MainActivity extends AppCompatActivity {

    int quantity=0;
    int price = 0;
    int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void increase(View view){

        // quantity = 0;
        quantity+=1;
        display(quantity);
    }

    public void decrease(View view){

        //int quantity = 0;
        quantity-=1;
        if(quantity<0){


        }
        display(quantity);
    }

    public void check(View view){

        boolean ans = ((CheckBox)view).isChecked();
        if(ans){
            flag = 1;
        }
    }

    /**
     * This method is called when the order button is clicked.
     */
    public void submitOrder(View view) {
        //int quantity = 0;
        //display(quantity);
        price = 5*quantity;
        displayPrice(price);
    }

    /**
     * This method displays the given quantity value on the screen.
     */
    private void display(int number) {
        TextView quantityTextView = (TextView) findViewById(R.id.quantity_text_view);
        quantityTextView.setText(""+number);
    }

    /**
     * This method displays the given price on the screen.
     *We can even do without the NumberFormat like we did in the display method
     * But using NumberFormat we'll discuss in lesson-3, which sets the local currency sign automatically.
     */
    private void displayPrice(int number) {
        TextView priceTextView = (TextView) findViewById(R.id.price);

        if(flag==1){

            price+=10;
        }


        priceTextView.setText(NumberFormat.getCurrencyInstance(Locale.US).format(number));
        TextView message = (TextView)findViewById(R.id.message);
        message.setText("Order Summary "+"\n"+"Total Items : "+quantity+"\n"+"Price : "+NumberFormat.getCurrencyInstance(Locale.US).format(quantity*5));
    }
}