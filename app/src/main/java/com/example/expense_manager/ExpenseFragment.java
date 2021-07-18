package com.example.expense_manager;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.contentcapture.DataRemovalRequest;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.expense_manager.model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;


public class ExpenseFragment extends Fragment {

    // Firebase database

    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseDatabase;

    // Recycler view

    private RecyclerView recyclerView;

    // total text result

    private TextView expenseTotalResult;

    // edit data item

    private EditText edtAmount;
    private EditText editNote;
    private EditText editType;

    private Button btnUpdate;
    private Button btnDelete;

    // Data variable

    private String type;
    private String note;
    private int amount;

    private String post_key;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_expense, container, false);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);

        expenseTotalResult = myview.findViewById(R.id.expense_txt_result);

        recyclerView = myview.findViewById(R.id.recycler_id_expense);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                int totalExpenseint = 0;

                for (DataSnapshot mysnapshot: snapshot.getChildren())
                {
                    Data data = mysnapshot.getValue(Data.class);
                    totalExpenseint += data.getAmount();

                    String strExpensetotal = String.valueOf(totalExpenseint);

                    expenseTotalResult.setText(strExpensetotal+".00");


                }

            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        });




        return myview;

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Data, MyViewHolder>adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(

                Data.class,
                R.layout.expense_recycler_data,
                MyViewHolder.class,
                mExpenseDatabase

        ) {
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, Data model, int i) {

                viewHolder.setDate(model.getDate());
                viewHolder.setNote(model.getNote());
                viewHolder.setType(model.getType());
                viewHolder.setAmt(model.getAmount());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        post_key = getRef(i).getKey();
                        type = model.getType();
                        note = model.getNote();
                        amount = model.getAmount();




                        updateDataItem();
                    }
                });

            }
        };

        recyclerView.setAdapter(adapter);

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public MyViewHolder( View itemView) {
            super(itemView);
            mView = itemView;
        }

        private void setDate(String date)
        {
            TextView mDate = mView.findViewById(R.id.date_txt_expense);
            mDate.setText(date);
        }

        private void setType(String type)
        {
            TextView mType = mView.findViewById(R.id.type_txt_expense);
            mType.setText(type);

        }

        private void setNote(String note)
        {
            TextView mNote = mView.findViewById(R.id.note_txt_expense);
            mNote.setText(note);
        }


        private void setAmt(int amtInt)
        {
            TextView mAmt = mView.findViewById(R.id.amt_txt_expense);
            String strAmt = String.valueOf(amtInt);
            mAmt.setText(strAmt);


        }


    }

    private void updateDataItem()
    {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.update_data_item,null);
        mydialog.setView(myview);

        edtAmount = myview.findViewById(R.id.amt_edit);
        editNote = myview.findViewById(R.id.note_edt);
        editType = myview.findViewById(R.id.type_edt);

        editType.setText(type);
        editType.setSelection(type.length());

        editNote.setText(note);
        editNote.setSelection(note.length());

        edtAmount.setText(String.valueOf(amount));
        edtAmount.setSelection(String.valueOf(amount).length());

        btnDelete = myview.findViewById(R.id.btn_delete);
        btnUpdate = myview.findViewById(R.id.btn_update);

        AlertDialog dialog = mydialog.create();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                type = editType.getText().toString().trim();
                note = editNote.getText().toString().trim();

                String strAmt = String.valueOf(amount);
                strAmt = edtAmount.getText().toString().trim();

                int intAmt = Integer.parseInt(strAmt);

                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(intAmt,type,note,post_key,mDate);

                mExpenseDatabase.child(post_key).setValue(data);

                dialog.dismiss();

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mExpenseDatabase.child(post_key).removeValue();

                dialog.dismiss();
            }
        });
        dialog.show();

    }


}