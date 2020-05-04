package com.aputech.dora.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.aputech.dora.Adpater.ContactInterface;
import com.aputech.dora.Adpater.PillAdapter;
import com.aputech.dora.Adpater.SAdapter;
import com.aputech.dora.Adpater.contactAdapter;
import com.aputech.dora.Model.User;
import com.aputech.dora.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;

public class SelectUser extends AppCompatActivity implements ContactInterface {
    RecyclerView recycler_basket;
    RecyclerView recycler_collected;
    ArrayList<String> IDFollowing= new ArrayList<>();
    ArrayList<User> FollowingUsers=new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth=FirebaseAuth.getInstance();
    CollectionReference All=db.collection("Users");
    contactAdapter adapter;
    PillAdapter pillAdapter;

    CollectionReference collectionFollowing= db.collection("Users").document(auth.getUid()).collection("Following");
    private ArrayList<User> Final= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user);
        recycler_basket = findViewById(R.id.recycler_basket);
        recycler_basket.setHasFixedSize(true);
        recycler_basket.setLayoutManager(new LinearLayoutManager(SelectUser.this));
       recycler_collected = findViewById(R.id.recycler_collected);
        recycler_collected.setHasFixedSize(true);
        recycler_collected.setLayoutManager(new LinearLayoutManager(SelectUser.this,LinearLayoutManager.HORIZONTAL, false));


      collectionFollowing.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
          @Override
          public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
              for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                  IDFollowing.add(documentSnapshot.getId());
              }
              All.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                  @Override
                  public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                      for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                          User usr=documentSnapshot.toObject(User.class);
                          if (IDFollowing.contains(usr.getUserid())){
                              FollowingUsers.add(usr);
                          }
                      }
                      adapter.notifyDataSetChanged();
                  }
              });
          }
      });
      pillAdapter = new PillAdapter(Final);
      recycler_collected.setAdapter(pillAdapter);
        adapter = new contactAdapter(FollowingUsers,SelectUser.this,SelectUser.this);
        recycler_basket.setAdapter(adapter);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_from_top,R.anim.slide_in_top);
    }

    @Override
    public void onClick(User value) {
        boolean alreadyadded=false;
        for (int x=0;x<pillAdapter.getItemCount();x++){
            User user = pillAdapter.getItem(x);
            if (user.getUserid().equals(value.getUserid())){
                alreadyadded=true;
                break;
            }else{
                alreadyadded=false;
            }
        }
        if (!alreadyadded){
            Final.add(value);
            pillAdapter.notifyDataSetChanged();
        }

    }
//    private void filter(String text) {
//        ArrayList<User> filteredList = new ArrayList<>();
//
//        for (User item : users) {
//            if (item.getUserName().toLowerCase().contains(text.toLowerCase())) {
//                filteredList.add(item);
//            }
//        }
//        adapter.filterList(filteredList);
//        if (adapter.getItemCount()==0){
//            noresult.setVisibility(View.VISIBLE);
//            recyclerView.setVisibility(View.INVISIBLE);
//        }else {
//            recyclerView.setVisibility(View.VISIBLE);
//            noresult.setVisibility(View.INVISIBLE);
//        }
//    }
}
