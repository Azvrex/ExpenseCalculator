package com.example.expensecalculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements onItemDeleteListener{

    private RadioButton radbtnBudget, radbtnExpense;
    private RadioGroup radGroup;
    private EditText edtItemName, edtCost;
    private Button btnAdd;
    private Spinner spnrItemName;
    private String[] expensesData;
    private RecyclerView recyclerView;
    private List<Item> itemList;
    private MyAdapter adapterRecycleView;
    private ItemDBHelper itemDBHelper;
    private String category;
    private DBHelper dbHelper;
    private TextView txtIncome, txtExpenses, txtNetTotal;
    //private ImageButton imgBtnClose;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        radbtnBudget = findViewById(R.id.radbtnBudget);
        radbtnExpense = findViewById(R.id.radbtnExpense);
        radGroup = findViewById(R.id.radGroup);
        edtItemName = findViewById(R.id.edtItemName);
        edtCost = findViewById(R.id.edtCost);
        btnAdd = findViewById(R.id.btnAdd);
        spnrItemName = findViewById(R.id.spnrItemName);
        //imgBtnClose = findViewById(R.id.imgBtnClose);

        expensesData = new String[] { "Utilities", "Transportation", "Food", "Debt Payments", "Insurance", "Personal Care", "Health & Wellness", "Education", "Entertainment/Recreation", "Childcare", "Miscellaneous" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, expensesData);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrItemName.setAdapter(adapter);

        itemDBHelper = new ItemDBHelper(this);
        itemList = new ArrayList<>();
        adapterRecycleView = new MyAdapter(itemList, this, this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapterRecycleView);

        // Load existing items from the database
        itemList.addAll(itemDBHelper.getAllItems());
        adapterRecycleView.notifyDataSetChanged();
        onItemDeleted();

        radGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (radbtnBudget.isChecked()){
                    edtItemName.setVisibility(View.VISIBLE);
                    spnrItemName.setVisibility(View.GONE);
                }else{
                    edtItemName.setVisibility(View.GONE);
                    spnrItemName.setVisibility(View.VISIBLE);
                }
            }
        });



    }

    public void onBtnAddClicked(View view){
        String itemName;
        if(radbtnBudget.isChecked()) {
            itemName = edtItemName.getText().toString().trim();
            category = "Budget";
        }else{
            itemName = spnrItemName.getSelectedItem().toString().trim();
            category = "Expenses";
        }
        String costString = edtCost.getText().toString().trim();


        if (edtItemName.getText().toString().isEmpty() && radbtnBudget.isChecked()){
            edtItemName.setError("This field must not be empty");
        } else if (edtCost.getText().toString().isEmpty()) {
            edtCost.setError("This field must not be empty");
        } else{
            float cost = Float.parseFloat(costString);
            Item newItem = new Item();
            newItem.setItemName(itemName);
            newItem.setCost(cost);
            newItem.setCategory(category);

            itemDBHelper.addItem(newItem);
            itemList.add(newItem);
            adapterRecycleView.notifyDataSetChanged();

            edtItemName.setText("");
            edtCost.setText("");
            onItemDeleted();
        }
    }

    @Override
    public void onItemDeleted() {
        txtIncome = findViewById(R.id.txtIncome);
        txtExpenses = findViewById(R.id.txtExpenses);
        txtNetTotal = findViewById(R.id.txtNetTotal);

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        dbHelper = new DBHelper(this);
        float totalBudgetCost = dbHelper.getTotalCostForCategory("Budget");
        totalBudgetCost = Float.parseFloat(decimalFormat.format(totalBudgetCost));
        float totalExpensesCost = dbHelper.getTotalCostForCategory("Expenses");
        totalExpensesCost = Float.parseFloat(decimalFormat.format(totalExpensesCost));
        float totalNet = totalBudgetCost-totalExpensesCost;

        txtIncome.setText(String.valueOf(totalBudgetCost));
        txtExpenses.setText(String.valueOf(totalExpensesCost));

        txtNetTotal.setText(String.valueOf(totalNet));
        if (totalExpensesCost>0){
            txtNetTotal.setTextColor(ContextCompat.getColor(this, R.color.green));
        }else{
            txtNetTotal.setTextColor(ContextCompat.getColor(this, R.color.red));
        }

        dbHelper.close();
    }



    /*public void onBtnAddClicked(View view){
        String itemName;
        if(radbtnBudget.isChecked()) {
            itemName = edtItemName.getText().toString().trim();
        }else{
            itemName = spnrItemName.getSelectedItem().toString().trim();
        }
        String costString = edtCost.getText().toString().trim();


        if (edtItemName.getText().toString().isEmpty() && radbtnBudget.isChecked()){
            edtItemName.setError("This field must not be empty");
        } else if (edtCost.getText().toString().isEmpty()) {
            edtCost.setError("This field must not be empty");
        } else{
            float cost = Float.parseFloat(costString);
            ModelItem newItem = new ModelItem(itemName, cost);
            itemList.add(newItem);
            adapterRecycleView.notifyDataSetChanged();
            edtItemName.setText("");
            edtCost.setText("");
        }
    }*/



}