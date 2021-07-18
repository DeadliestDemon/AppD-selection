package com.example.expense_manager;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expense_manager.model.Data;
import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;


public class DashBoardFragment extends Fragment {

    // Floating Button

    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;

    // Floating button textview

    private TextView fab_income_text;
    private TextView fab_expense_text;

    // Boolean

    private boolean isOpen = false;

    // animation

    private Animation FadOpen, FadClose;

    // dashboard total text

    private TextView totalIncomeRes;
    private TextView totalExpenseRes;

    // Recycler view

    private RecyclerView mRecyclerIncome;
    private RecyclerView mRecyclerExpense;

    // Firebase

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_dash_board, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();

        String uid = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeDatabase").child(uid);
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);

        // COnnect floating btn to layout

        fab_main_btn = myview.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn = myview.findViewById(R.id.income_ft_btn);
        fab_expense_btn = myview.findViewById(R.id.expense_ft_btn);

        // connect text

        fab_income_text = myview.findViewById(R.id.income_ft_text);
        fab_expense_text= myview.findViewById(R.id.expense_ft_text);

        // total income and expense

        totalExpenseRes = myview.findViewById(R.id.expense_set_result);
        totalIncomeRes = myview.findViewById(R.id.income_set_result);

        // Recycler

        mRecyclerIncome = myview.findViewById(R.id.recycler_income_dash);
        mRecyclerExpense = myview.findViewById(R.id.recycler_expense_dash);

        // connect animation

        FadOpen = AnimationUtils.loadAnimation(getActivity(),R.anim.fade_open);
        FadClose = AnimationUtils.loadAnimation(getActivity(),R.anim.fade_close);

        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addData();


            if (isOpen)
            {
                fab_income_btn.startAnimation(FadClose);
                fab_expense_btn.startAnimation(FadClose);
                fab_income_btn.setClickable(false);
                fab_expense_btn.setClickable(false);

                fab_income_text.startAnimation(FadClose);
                fab_expense_text.startAnimation(FadClose);
                fab_income_text.setClickable(false);
                fab_expense_text.setClickable(false);
                isOpen = false;

            }

            else
            {
                fab_income_btn.startAnimation(FadOpen);
                fab_expense_btn.startAnimation(FadOpen);
                fab_income_btn.setClickable(true);
                fab_expense_btn.setClickable(true);

                fab_income_text.startAnimation(FadOpen);
                fab_expense_text.startAnimation(FadOpen);
                fab_income_text.setClickable(true);
                fab_expense_text.setClickable(true);
                isOpen = true;

            }

            }
        });

        // Calc total Income

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {

                int totalIncomeint = 0;

                for (DataSnapshot mysnapshot : snapshot.getChildren())
                {
                    Data data = mysnapshot.getValue(Data.class);

                    totalIncomeint += data.getAmount();
                    String strTotalIncome = String.valueOf(totalIncomeint);
                    totalIncomeRes.setText(strTotalIncome+".00");




                }

            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        });

        // Calc total Expense

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {

                int totalExpenseint = 0;

                for (DataSnapshot mysnapshot : snapshot.getChildren())
                {
                    Data data = mysnapshot.getValue(Data.class);

                    totalExpenseint += data.getAmount();
                    String strTotalExpense = String.valueOf(totalExpenseint);
                    totalExpenseRes.setText(strTotalExpense+".00");




                }

            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        });

        // Recycler

        LinearLayoutManager layoutManagerIncome = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);

        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(layoutManagerIncome);

        LinearLayoutManager layoutManagerExpense = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);

        layoutManagerExpense.setStackFromEnd(true);
        layoutManagerExpense.setReverseLayout(true);
        mRecyclerExpense.setHasFixedSize(true);
        mRecyclerExpense.setLayoutManager(layoutManagerExpense);



        return myview;

    }

    // floating btn animation

    private void ftAnimation()
    {

        if (isOpen)
        {
            fab_income_btn.startAnimation(FadClose);
            fab_expense_btn.startAnimation(FadClose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);

            fab_income_text.startAnimation(FadClose);
            fab_expense_text.startAnimation(FadClose);
            fab_income_text.setClickable(false);
            fab_expense_text.setClickable(false);
            isOpen = false;

        }

        else
        {
            fab_income_btn.startAnimation(FadOpen);
            fab_expense_btn.startAnimation(FadOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_text.startAnimation(FadOpen);
            fab_expense_text.startAnimation(FadOpen);
            fab_income_text.setClickable(true);
            fab_expense_text.setClickable(true);
            isOpen = true;

        }
    }

    private void addData()
    {
        // Fab Button income..
        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                incomeDataInsert();


            }
        });

        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                expenseDataInsert();

            }
        });

    }

    public void incomeDataInsert()
    {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myview = inflater.inflate(R.layout.custom_layout_for_insertdata,null);
        mydialog.setView(myview);

        AlertDialog dialog = mydialog.create();

        EditText edtAmt = myview.findViewById(R.id.amt_edit);
        EditText edtType = myview.findViewById(R.id.type_edt);
        EditText edtNote = myview.findViewById(R.id.note_edt);

        Button btnSave = myview.findViewById(R.id.btn_save);
        Button btnCancel = myview.findViewById(R.id.btn_cancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String type = edtType.getText().toString().trim();
                String amount = edtAmt.getText().toString().trim();
                String note = edtNote.getText().toString().trim();

                if (TextUtils.isEmpty(type))
                {
                    edtType.setError("Required Field..");
                    return;
                }
                if (TextUtils.isEmpty(amount))
                {
                    edtAmt.setError("Required Field..");
                    return;
                }

                if (TextUtils.isEmpty(note))
                {
                    edtNote.setError("Required Field..");
                    return;
                }

                int ourAmtInt = Integer.parseInt(amount);

                String id = mIncomeDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(ourAmtInt,type,note,id,mDate);
                mIncomeDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(), "Data Added", Toast.LENGTH_SHORT).show();

                ftAnimation();
                dialog.dismiss();



            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftAnimation();
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public void expenseDataInsert()
    {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myview = inflater.inflate(R.layout.custom_layout_for_insertdata,null);

        mydialog.setView(myview);

        final AlertDialog dialog =mydialog.create();

        EditText amount = myview.findViewById(R.id.amt_edit);
        EditText type = myview.findViewById(R.id.type_edt);
        EditText note = myview.findViewById(R.id.note_edt);

        Button btnSave = myview.findViewById(R.id.btn_save);
        Button btnCancel = myview.findViewById(R.id.btn_cancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tmAmount = amount.getText().toString().trim();
                String tmType = type.getText().toString().trim();
                String tmNote= note.getText().toString().trim();

                if (TextUtils.isEmpty(tmAmount))
                {
                    amount.setError("Required Field");
                    return;
                }

                int amtInt = Integer.parseInt(tmAmount);

                if (TextUtils.isEmpty(tmType))
                {
                    type.setError("Required Field");
                    return;
                }
                if (TextUtils.isEmpty(tmNote))
                {
                    note.setError("Required Field");
                    return;
                }

                String id = mExpenseDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(amtInt,tmType,tmNote,id,mDate);
                mExpenseDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(), "Data Added", Toast.LENGTH_SHORT).show();

                ftAnimation();
                dialog.dismiss();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Data, IncomeViewHolder> incomeAdapter = new FirebaseRecyclerAdapter<Data, IncomeViewHolder>(

                Data.class,
                R.layout.dashboard_income,
                DashBoardFragment.IncomeViewHolder.class,
                mIncomeDatabase

        ) {
            @Override
            protected void populateViewHolder(IncomeViewHolder viewHolder, Data model, int i) {

                viewHolder.setIncomeType(model.getType());
                viewHolder.setIncomeAmount(model.getAmount());
                viewHolder.setIncomeDate(model.getDate());



            }
        };

        mRecyclerIncome.setAdapter(incomeAdapter);

        FirebaseRecyclerAdapter<Data, ExpenseViewHolder> expenseAdapter = new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>(
                Data.class,
                R.layout.dashboard_expense,
                DashBoardFragment.ExpenseViewHolder.class,
                mExpenseDatabase

        ) {
            @Override
            protected void populateViewHolder(ExpenseViewHolder viewHolder, Data model, int i) {

                viewHolder.setExpenseType(model.getType());
                viewHolder.setExpenseAmount(model.getAmount());
                viewHolder.setExpenseDate(model.getDate());




            }
        };

        mRecyclerExpense.setAdapter(expenseAdapter);

    }

    // For income Data

    public static class IncomeViewHolder extends RecyclerView.ViewHolder {

        View mIncomeView;

        public IncomeViewHolder( View itemView) {
            super(itemView);
            mIncomeView = itemView;
        }

        public void setIncomeType(String type)
        {
            TextView mType = mIncomeView.findViewById(R.id.type_income_ds);
            mType.setText(type);

        }

        public void setIncomeAmount(int amnt)
        {
            TextView mAmt = mIncomeView.findViewById(R.id.amt_income_ds);
            String strAmnt = String.valueOf(amnt);
            mAmt.setText(strAmnt);


        }

        public void setIncomeDate (String date)
        {
            TextView mDate = mIncomeView.findViewById(R.id.date_income_ds);
            mDate.setText(date);

        }

    }

    // For expense Data

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder{

        View mExpenseView;

        public ExpenseViewHolder( View itemView) {
            super(itemView);
            mExpenseView = itemView;
        }

        public void setExpenseType(String type)
        {
            TextView mType = mExpenseView.findViewById(R.id.type_expense_ds);
            mType.setText(type);

        }

        public void setExpenseAmount(int amnt)
        {
            TextView mAmt = mExpenseView.findViewById(R.id.amt_expense_ds);
            String strAmnt = String.valueOf(amnt);
            mAmt.setText(strAmnt);


        }

        public void setExpenseDate (String date)
        {
            TextView mDate = mExpenseView.findViewById(R.id.date_expense_ds);
            mDate.setText(date);

        }


    }


}