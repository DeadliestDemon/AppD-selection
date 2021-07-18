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

import javax.microedition.khronos.egl.EGLDisplay;


public class IncomeFragment extends Fragment {

    //Firebase database;

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabse;

    // Recycler View

    private RecyclerView recyclerView;

    // Textview income result

    private TextView totalIncomeTxt;

    // Update edit text

    private EditText edtAmt;
    private EditText edtType;
    private EditText edtNote;

    // button for update

    private Button btnUpdate;
    private Button btnDelete;

    // data item view

    private String type;
    private String note;
    private int amount;

    private String post_key;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View myview = inflater.inflate(R.layout.fragment_income, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();

        String uid = mUser.getUid();

        mIncomeDatabse = FirebaseDatabase.getInstance().getReference().child("IncomeDatabase").child(uid);

        totalIncomeTxt = myview.findViewById(R.id.income_txt_result);

        recyclerView = myview.findViewById(R.id.recycler_id_income);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mIncomeDatabse.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {

                int totalIncome = 0;

                for (DataSnapshot mysnapshot: snapshot.getChildren())
                {
                    Data data = mysnapshot.getValue(Data.class);

                    totalIncome += data.getAmount();
                    String stTotalIncome = String.valueOf(totalIncome);
                    totalIncomeTxt.setText(stTotalIncome+".00");

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

        FirebaseRecyclerAdapter<Data,MyViewHolder>adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(
                Data.class,
                R.layout.income_recycler_data,
                MyViewHolder.class,
                mIncomeDatabse
        ) {
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, Data model, int i) {

                viewHolder.setType(model.getType());
                viewHolder.setNote(model.getNote());
                viewHolder.setDate(model.getDate());
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

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        private void setType(String type)
        {
            TextView mType = mView.findViewById(R.id.type_txt_income);
            mType.setText(type);

        }

        private void setNote(String note)
        {
            TextView mNote = mView.findViewById(R.id.note_txt_income);
            mNote.setText(note);
        }

        private void setDate(String date)
        {
            TextView mDate = mView.findViewById(R.id.date_txt_income);
            mDate.setText(date);
        }

        private void setAmt(int amtInt)
        {
            TextView mAmt = mView.findViewById(R.id.amt_txt_income);
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

        edtAmt = myview.findViewById(R.id.amt_edit);
        edtType = myview.findViewById(R.id.type_edt);
        edtNote = myview.findViewById(R.id.note_edt);

        // set data to edit

        edtType.setText(type);
        edtType.setSelection(type.length());

        edtNote.setText(note);
        edtNote.setSelection(note.length());

        edtAmt.setText(String.valueOf(amount));
        edtAmt.setSelection(String.valueOf(amount).length());


        btnUpdate = myview.findViewById(R.id.btn_update);
        btnDelete = myview.findViewById(R.id.btn_delete);

        AlertDialog dialog = mydialog.create();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                type = edtType.getText().toString().trim();
                note = edtNote.getText().toString().trim();

                String strAmount = String.valueOf(amount);
                strAmount = edtAmt.getText().toString().trim();

                int myAmt = Integer.parseInt(strAmount);

                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(myAmt,type,note,post_key,mDate);

                mIncomeDatabse.child(post_key).setValue(data);
                dialog.dismiss();


            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mIncomeDatabse.child(post_key).removeValue();

                dialog.dismiss();
            }
        });

        dialog.show();

    }

}