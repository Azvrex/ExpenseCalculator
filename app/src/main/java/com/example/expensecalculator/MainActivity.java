package com.example.expensecalculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements functionIntefaces {

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
    private PieChart pieChart;
    private CardView cardChartExpenses;
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
        setPieChartExpenses();

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
        if (totalNet>0){
            txtNetTotal.setTextColor(ContextCompat.getColor(this, R.color.green));
        }else{
            txtNetTotal.setTextColor(ContextCompat.getColor(this, R.color.red));
        }

        dbHelper.close();
    }

    @Override
    public void setPieChartExpenses() {
        pieChart = findViewById(R.id.pieChartExpenses);
        dbHelper = new DBHelper(this);
        cardChartExpenses = findViewById(R.id.cardChartExpenses);
        ArrayList<PieEntry> expensesList = new ArrayList<>();

        HashMap<String, Double> aggregatedExpenses = dbHelper.getExpenses();
        if(!aggregatedExpenses.isEmpty()) {
            cardChartExpenses.setVisibility(View.VISIBLE);
            double totalCost=0;
            /*for (Map.Entry<String, Double> entry : aggregatedExpenses.entrySet()) {
                expensesList.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
            }*/
            // Calculate the total cost
            for (Map.Entry<String, Double> entry : aggregatedExpenses.entrySet()) {
                totalCost += entry.getValue();
            }
            // Create PieEntry objects and calculate percentages
            for (Map.Entry<String, Double> entry : aggregatedExpenses.entrySet()) {
                double cost = entry.getValue();
                float percentage = (float) ((cost / totalCost) * 100.0);

                // Create PieEntry with label as "Name (Percentage%)"
                expensesList.add(new PieEntry((float) cost, entry.getKey() + " (" + String.format("%.2f", percentage) + "%)"));
            }
        }else{
            cardChartExpenses.setVisibility(View.GONE);
        }

        PieDataSet dataSet = new PieDataSet(expensesList, "Expenses");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);
        dataSet.setValueFormatter(new PercentFormatter(pieChart)); // Set the percentage formatter
        PieData data = new PieData(dataSet);


        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleRadius(20f);
        pieChart.setTransparentCircleRadius(25f);

        pieChart.animateY(1000);
        // Refresh the chart
        pieChart.invalidate();
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